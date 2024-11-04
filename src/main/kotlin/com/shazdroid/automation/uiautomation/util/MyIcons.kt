package com.shazdroid.automation.uiautomation.util

import com.intellij.openapi.util.IconLoader

object MyIcons {
    fun getIcon(iconName: String) = IconLoader.getIcon(iconName, javaClass)
}