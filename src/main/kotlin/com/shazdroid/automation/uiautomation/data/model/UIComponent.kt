package com.shazdroid.automation.uiautomation.data.model

data class UIComponent(
    val id: String?,
    val type: String,
    val parentId: String?,
    val children: MutableList<UIComponent> = mutableListOf(),
    val path: String
)
