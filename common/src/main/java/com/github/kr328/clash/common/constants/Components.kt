package com.github.kr328.clash.common.constants

import android.content.ComponentName
import com.github.kr328.clash.common.util.packageName

object Components {
    private const val COMPONENTS_PACKAGE_NAME = "com.github.kr328.clash"

    val MAIN_ACTIVITY = ComponentName(packageName, "$COMPONENTS_PACKAGE_NAME.MainActivity")
}
