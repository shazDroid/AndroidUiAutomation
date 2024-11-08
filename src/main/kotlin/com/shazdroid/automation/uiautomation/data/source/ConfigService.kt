package com.shazdroid.automation.uiautomation.data.source

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.shazdroid.automation.uiautomation.data.model.ActionConfig
import java.io.File

class ConfigService(private val configFilePath: String) {

    private val configData: ConfigData

    init {
        val configFile = File(configFilePath)
        val configJson = configFile.readText()
        val gson = Gson()
        configData = gson.fromJson(configJson, ConfigData::class.java)
    }

    fun getActionDetails(actionName: String): ActionConfig? {
        return configData.actions.values.flatten().find { it.name == actionName }
    }

    fun getActionsForComponentType(componentType: String): List<String> {
        return configData.actions[componentType]?.map { it.name } ?: emptyList()
    }

    data class ConfigData(
        val actions: Map<String, List<ActionConfig>>
    )
}
