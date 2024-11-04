package com.shazdroid.automation.uiautomation.presentation.components

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlFile
import javax.swing.Icon

class XmlGutterHandler : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        // Check if the element is the first line of an XML file
        val file = element.containingFile as? XmlFile ?: return null
        val document = file.viewProvider.document ?: return null
        val firstLineStartOffset = document.getLineStartOffset(0)
        if (element.textOffset != firstLineStartOffset) return null

        // Ensure the file is an XML layout file
        val rootTag = file.rootTag ?: return null
        if (rootTag.name != "layout") return null

        // Use NavigationGutterIconBuilder to create the icon with tooltip and click action
        val icon: Icon = AllIcons.Actions.Dump
        return NavigationGutterIconBuilder.create(icon)
            .setAlignment(GutterIconRenderer.Alignment.LEFT)
            .setTooltipText("Scan Layout")
            .setTargets(element)
            .setPopupTitle("Layout Scanner")
            .setNamer { "Scan this layout" }
            .createLineMarkerInfo(element)
    }

    private fun performScanAction(file: XmlFile) {
        // Implement the action to perform when the icon is clicked
    }
}
