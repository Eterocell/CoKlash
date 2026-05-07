package com.github.kr328.golang;

import com.android.build.api.variant.AndroidComponentsExtension;
import com.android.build.api.variant.ExternalNativeBuild;
import com.android.build.api.variant.Variant;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskProvider;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

public class GolangPlugin implements Plugin<Project> {
    public static String taskNameOf(String variantName, String abi) {
        return String.format("externalGolangBuild%s%s", capitalize(variantName), capitalize(abi));
    }

    private static String capitalize(String str) {
        return Arrays.stream(str.split("[-_]"))
                .filter(s -> !s.isEmpty())
                .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
                .collect(Collectors.joining());
    }

    public static File outputDirOf(Project project, String variantName, String abi) {
        return project.getLayout().getBuildDirectory()
                .dir(outputPathOf(variantName, abi))
                .get()
                .getAsFile();
    }

    @Deprecated
    public static File outputDirOf(Project project, Object variant, String abi) {
        String variantName = null;
        if (variant instanceof String) {
            variantName = (String) variant;
        } else if (variant != null) {
            // Compatibility for old callers passing AGP variant-like objects without importing legacy AGP APIs.
            variantName = nameOf(variant);
        }

        return outputDirOf(project, variantName, abi);
    }

    private static String nameOf(Object variant) {
        try {
            Method getName = variant.getClass().getMethod("getName");
            Object name = getName.invoke(variant);
            return name == null ? null : name.toString();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Unsupported variant object: " + variant, e);
        }
    }

    private static String outputPathOf(String variantName, String abi) {
        return Stream.of("outputs", "golang", variantName, abi)
                .filter(Objects::nonNull)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(File.separator));
    }

    @Override
    public void apply(@Nonnull Project target) {
        target.getExtensions().create("golang", GolangExtension.class);

        target.getPlugins().withId("com.android.application", plugin -> configureAndroid(target));
        target.getPlugins().withId("com.android.library", plugin -> configureAndroid(target));
        target.getPlugins().withId("com.android.dynamic-feature", plugin -> {
            throw new GradleException("golang-android does not support com.android.dynamic-feature");
        });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void configureAndroid(Project target) {
        AndroidComponentsExtension androidComponents =
                target.getExtensions().getByType(AndroidComponentsExtension.class);
        Provider<Directory> ndkDirectory = androidComponents.getSdkComponents().getNdkDirectory();

        androidComponents.onVariants(
                androidComponents.selector().all(),
                (Action<Variant>) variant -> decorateVariant(target, variant, ndkDirectory)
        );
    }

    private void decorateVariant(Project target, Variant variant, Provider<Directory> ndkDirectory) {
        GolangExtension extension = target.getExtensions().getByType(GolangExtension.class);
        GolangSourceSet sourceSet = findSourceSet(extension, variant);
        if (sourceSet == null) {
            return;
        }

        ExternalNativeBuild externalNativeBuild = variant.getExternalNativeBuild();
        if (externalNativeBuild == null) {
            throw new GradleException(
                    "No external native build configured for " + target.getPath() + " variant " + variant.getName()
                            + ". Configure android.externalNativeBuild and external native ABI filters."
            );
        }

        Set<String> abis = new TreeSet<>(externalNativeBuild.getAbiFilters().get());
        if (abis.isEmpty()) {
            throw new GradleException(
                    "No external native ABI filters configured for " + target.getPath() + " variant " + variant.getName()
                            + ". Configure android.defaultConfig.externalNativeBuild.cmake.abiFilters."
            );
        }

        TaskProvider<GolangJniLibsTask> jniLibsTaskProvider = target.getTasks().register(
                "externalGolangJniLibs" + capitalize(variant.getName()),
                GolangJniLibsTask.class,
                task -> task.getGolangOutputRoot().set(
                        target.getLayout().getBuildDirectory().dir(outputPathOf(variant.getName(), null))
                )
        );

        variant.getSources().getJniLibs().addGeneratedSourceDirectory(
                jniLibsTaskProvider,
                GolangJniLibsTask::getGolangOutputRoot
        );
        variant.getLifecycleTasks().registerPreBuild(jniLibsTaskProvider);

        abis.forEach(abi -> {
            String taskName = taskNameOf(variant.getName(), abi);
            File output = outputDirOf(target, variant.getName(), abi);
            File source = sourceSet.getSrcDir().get().getAsFile();
            List<String> tags = sourceSet.getTags().getOrElse(Collections.emptyList());
            String packageName = sourceSet.getPackageName().getOrElse("");

            TaskProvider<GolangBuildTask> taskProvider = target.getTasks().register(taskName, GolangBuildTask.class, task ->
                    task.applyFor(
                            abi,
                            ndkDirectory,
                            variant.getMinSdk().getApiLevel(),
                            variant.getDebuggable(),
                            source,
                            output,
                            sourceSet.getFileName().get(),
                            tags,
                            packageName
                    )
            );

            jniLibsTaskProvider.configure(task -> task.dependsOn(taskProvider));
            variant.getLifecycleTasks().registerPreBuild(taskProvider);
        });
    }

    private GolangSourceSet findSourceSet(GolangExtension extension, Variant variant) {
        GolangSourceSet sourceSet = extension.getSourceSets().findByName(variant.getName());
        if (sourceSet == null) {
            sourceSet = extension.getSourceSets().findByName(variant.getFlavorName());
        }
        if (sourceSet == null) {
            sourceSet = extension.getSourceSets().findByName("main");
        }
        return sourceSet;
    }

    public abstract static class GolangJniLibsTask extends DefaultTask {
        @Internal
        public abstract DirectoryProperty getGolangOutputRoot();
    }
}
