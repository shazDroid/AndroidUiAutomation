package com.shazdroid.automation.uiautomation.domain.usecase

import com.intellij.psi.PsiFile
import com.shazdroid.automation.uiautomation.data.model.UIComponent
import com.shazdroid.automation.uiautomation.data.source.ConfigService
import com.shazdroid.automation.uiautomation.presentation.services.impl.XmlServiceImpl

class MatchComponentsToConfigUseCase(
    private val xmlService: XmlServiceImpl,
    private val configService: ConfigService
) {
//    fun execute(file: PsiFile): Map<UIComponent, List<String>> {
//        val components = xmlService.scanXmlFile(file)
//        val matchedActions = mutableMapOf<UIComponent, List<String>>()
//
//        components.forEach { component ->
//            // Retrieve actions for the component type from the config
//            val actions = configService.getActionsForComponent(component.type) ?: emptyList()
//
//            // Only add to matchedActions if there are valid actions for this component
//            if (actions.isNotEmpty()) {
//                matchedActions[component] = actions
//            }
//        }
//
//        return matchedActions
//    }
}
