package com.github.kr328.golang;

import com.android.build.gradle.BaseExtension;
import com.android.build.gradle.api.BaseVariant;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;

import java.io.File;
import java.util.*;

public abstract class GolangBuildTask extends Exec {
    private static Map<String, String> environmentOf(File ndk, String abi, int sdkVersion) {
        HashMap<String, String> environment = new HashMap<>();
        ArrayList<String> paths = new ArrayList<>();

        paths.add(ndk.getAbsolutePath());
        paths.add("toolchains");
        paths.add("llvm");
        paths.add("prebuilt");

        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            paths.add("windows-x86_64");
        } else if (Os.isFamily(Os.FAMILY_MAC)) {
            paths.add("darwin-x86_64");
        } else if (Os.isFamily(Os.FAMILY_UNIX)) {
            paths.add("linux-x86_64");
        } else {
            throw new IllegalArgumentException("Unsupported platform: " + System.getProperty("os.name"));
        }

        paths.add("bin");

        final String compilerPrefix;
        final String goArch;
        final String goArm;
        switch (abi) {
            case "arm64-v8a":
                compilerPrefix = "aarch64-linux-android";
                goArch = "arm64";
                goArm = "";
                break;
            case "armeabi-v7a":
                compilerPrefix = "armv7a-linux-androideabi";
                goArch = "arm";
                goArm = "7";
                break;
            case "x86":
                compilerPrefix = "i686-linux-android";
                goArch = "386";
                goArm = "";
                break;
            case "x86_64":
                compilerPrefix = "x86_64-linux-android";
                goArch = "amd64";
                goArm = "";
                break;
            default:
                throw new IllegalArgumentException("Unsupported abi: " + abi);
        }

        paths.add(compilerPrefix + sdkVersion + "-clang");

        environment.put("CC", String.join(File.separator, paths));
        environment.put("GOOS", "android");
        environment.put("GOARCH", goArch);
        environment.put("GOARM", goArm);
        environment.put("CGO_ENABLED", "1");
        environment.put("CFLAGS", "-O3 -Werror");

        return environment;
    }

    public GolangBuildTask applyFor(
            BaseExtension base,
            BaseVariant variant,
            String abi, File source,
            File output,
            String fileName,
            Collection<String> tags,
            String packageName
    ) {
        getGolangSource().set(source);
        getGolangOutput().set(output);
        getTags().set(tags);

        ArrayList<String> commands = new ArrayList<>();

        commands.add("go");
        commands.add("build");
        commands.add("-buildmode");
        commands.add("c-shared");
        commands.add("-trimpath");
        commands.add("-o");
        commands.add(getGolangOutput().file(fileName).get().getAsFile().getAbsolutePath());

        ArrayList<String> prependTags = new ArrayList<>(getTags().get());
//        if (variant.getBuildType().isDebuggable()) {
//            prependTags.add("debug");
//        }

        if (!prependTags.isEmpty()) {
            commands.add("-tags");
            commands.add(String.join(",", prependTags));
        }

        if (variant.getBuildType().isDebuggable()) {
            commands.add("-ldflags=-extldflags=-Wl,-z,max-page-size=16384");
        } else {
            commands.add("-ldflags=-s -w -extldflags=-Wl,-z,max-page-size=16384");
        }

        if (!packageName.isEmpty()) {
            commands.add(packageName);
        }

        environment(environmentOf(base.getNdkDirectory(), abi, Objects.requireNonNull(base.getDefaultConfig().getMinSdk())));
        workingDir(getGolangSource());
        commandLine(commands);

        return this;
    }

    @InputDirectory
    public abstract DirectoryProperty getGolangSource();

    @OutputDirectory
    public abstract DirectoryProperty getGolangOutput();

    @Input
    public abstract ListProperty<String> getTags();
}
