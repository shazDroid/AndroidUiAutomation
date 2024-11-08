package com.shazdroid.automation.uiautomation.presentation.components

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlFile
import com.shazdroid.automation.uiautomation.data.source.ConfigService
import com.shazdroid.automation.uiautomation.presentation.services.impl.BlocklyServiceImpl
import com.shazdroid.automation.uiautomation.presentation.view.BrowserManager
import javax.swing.Icon

class XmlGutterHandler : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        val file = element.containingFile as? XmlFile ?: return null
        val document = file.viewProvider.document ?: return null
        val firstLineStartOffset = document.getLineStartOffset(0)

        // Only show the icon if the element is on the first line of an XML layout file
        if (element.textOffset != firstLineStartOffset) return null

        return object : LineMarkerInfo<PsiElement>(
            element,
            element.textRange,
            AllIcons.Actions.Dump,
            { "Scan Layout" },
            null,
            GutterIconRenderer.Alignment.LEFT,
            { "Scan this layout" } // accessibleNameProvider as a String
        ) {
            override fun createGutterRenderer(): GutterIconRenderer? {
                return CustomGutterIconRenderer(element.project, file, element)
            }
        }
    }
}

class CustomGutterIconRenderer(
    private val project: Project,
    private val file: XmlFile,
    element: PsiElement
) : LineMarkerInfo.LineMarkerGutterIconRenderer<PsiElement>(
    LineMarkerInfo(
        element,
        element.textRange,
        AllIcons.Actions.Dump,
        { "Scan Layout" },
        null,
        GutterIconRenderer.Alignment.LEFT
    )
) {

    override fun getIcon(): Icon = AllIcons.Actions.Dump

    override fun getTooltipText(): String = "Scan Layout"

    override fun getClickAction(): AnAction {
        return object : AnAction() {
            override fun actionPerformed(e: AnActionEvent) {
                performScanAction(file)
            }
        }
    }

    private fun performScanAction(file: XmlFile) {
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Blockly")
        toolWindow?.show {
            executeBlocklyScan(file)
        }
    }

    private fun executeBlocklyScan(file: XmlFile) {
        val layoutFilePath = file.virtualFile.path

        // Adjust the path to your config.json
        val configFilePath = "${file.project.basePath}/config.json" // Replace with the actual path

        val configService = ConfigService(configFilePath)
        val blocklyService = BlocklyServiceImpl()

        // Generate the block definitions and get the list of block types
        val (blocklyCode, blockTypeNames) = blocklyService.generateBlocklyBlocks(layoutFilePath, configService)

        // Inject the generated block definitions into the Blockly workspace
        val injectScript = "addGeneratedBlocks(`$blocklyCode`);"
        BrowserManager.executeJavaScript(injectScript)

        // Create the XML blocks to load into the workspace
        val xmlBlocks = """
        <xml xmlns="https://developers.google.com/blockly/xml">
            ${
            blockTypeNames.joinToString("\n") { blockTypeName ->
                """<block type="$blockTypeName"></block>"""
            }
        }
        </xml>
        """.trimIndent()

        // Inject the XML blocks into the Blockly workspace
        val renderScript = "loadBlocks(`$xmlBlocks`);"
        BrowserManager.executeJavaScript(renderScript)
    }
}
