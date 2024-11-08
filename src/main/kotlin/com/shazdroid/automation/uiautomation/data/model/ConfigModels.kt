package com.shazdroid.automation.uiautomation.data.model
import com.google.gson.annotations.SerializedName

data class BlocklyConfig(
val actions: Map<String, ActionConfig>,
val components: Map<String, List<String>>
)


data class ActionConfig(
    @SerializedName("name") val name: String,
    @SerializedName("label") val label: String,
    @SerializedName("tooltip") val tooltip: String?,
    @SerializedName("fields") val fields: List<FieldConfig>?
)

data class FieldConfig(
    @SerializedName("type") val type: String,
    @SerializedName("name") val name: String,
    @SerializedName("placeholder") val placeholder: String,
    @SerializedName("options") val options: List<String>?
)
