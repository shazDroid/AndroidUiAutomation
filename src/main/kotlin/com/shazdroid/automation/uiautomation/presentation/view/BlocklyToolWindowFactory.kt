package com.shazdroid.automation.uiautomation.presentation.view

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBPanel
import com.intellij.ui.jcef.JBCefApp
import com.intellij.ui.jcef.JBCefBrowser
import com.shazdroid.automation.uiautomation.util.CustomSchemeHandlerFactory
import org.cef.CefApp
import java.awt.BorderLayout
import javax.swing.JLabel


class BlocklyToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = JBPanel<JBPanel<*>>().apply { layout = BorderLayout() }

        if (JBCefApp.isSupported()) {
            val browser = BrowserManager.browser
            registerAppSchemeHandler()
            Disposer.register(project, browser)
            panel.add(browser.component, BorderLayout.CENTER)
        } else {
            panel.add(JLabel("JCEF is not supported on this platform"), BorderLayout.CENTER)
        }

        toolWindow.contentManager.addContent(
            toolWindow.contentManager.factory.createContent(panel, "Blockly", false)
        )

        if (JBCefApp.isSupported()) {
            val browser = BrowserManager.browser.cefBrowser

            // Open DevTools
            browser.devTools?.let { devTools ->
                JBCefBrowser.createBuilder()
                    .setCefBrowser(devTools)
                    .setClient(BrowserManager.browser.jbCefClient)
                    .build()
                    .openDevtools()
            } ?: run {
                println("DevTools are not available.")
            }
        } else {
            println("JCEF is not supported on this platform.")
        }
    }
}

private fun registerAppSchemeHandler(): Unit {
    CefApp.getInstance()
        .registerSchemeHandlerFactory(
            "http",
            "myapp",
            CustomSchemeHandlerFactory()
        )
}