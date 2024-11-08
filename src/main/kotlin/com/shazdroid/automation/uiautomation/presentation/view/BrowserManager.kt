package com.shazdroid.automation.uiautomation.presentation.view

import com.intellij.ui.jcef.JBCefBrowser

object BrowserManager {
    val browser: JBCefBrowser by lazy {
        JBCefBrowser("http://myapp/index.html") // Set the initial URL or leave it blank
    }

    fun executeJavaScript(code: String) {
        browser.cefBrowser.executeJavaScript(code, browser.cefBrowser.url, 0)
    }
}
