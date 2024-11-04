package com.shazdroid.automation.uiautomation.presentation.view

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBPanel
import com.intellij.ui.jcef.JBCefApp
import com.intellij.ui.jcef.JBCefBrowser
import com.shazdroid.automation.uiautomation.util.CustomResourceHandler
import com.shazdroid.automation.uiautomation.util.CustomSchemeHandlerFactory
import org.cef.CefApp
import java.awt.BorderLayout
import javax.swing.JLabel


class BlocklyToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = JBPanel<JBPanel<*>>().apply { layout = BorderLayout() }

        if (JBCefApp.isSupported()) {
            val browser = JBCefBrowser("http://myapp/index.html")

//            // Register the custom request handler
//            client.addRequestHandler(object : CefRequestHandler {
//                override fun onBeforeBrowse(
//                    browser: CefBrowser?,
//                    frame: CefFrame?,
//                    request: CefRequest?,
//                    user_gesture: Boolean,
//                    is_redirect: Boolean
//                ): Boolean {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onOpenURLFromTab(
//                    browser: CefBrowser?,
//                    frame: CefFrame?,
//                    target_url: String?,
//                    user_gesture: Boolean
//                ): Boolean {
//                    TODO("Not yet implemented")
//                }
//
//                override fun getResourceRequestHandler(
//                    browser: CefBrowser?,
//                    frame: CefFrame?,
//                    request: CefRequest?,
//                    isNavigation: Boolean,
//                    isDownload: Boolean,
//                    requestInitiator: String?,
//                    disableDefaultHandling: org.cef.misc.BoolRef?
//                ): CefResourceRequestHandler? {
//                    return object : CefResourceRequestHandler {
//                        override fun getResourceHandler(
//                            browser: CefBrowser?,
//                            frame: CefFrame?,
//                            request: CefRequest?
//                        ): CefResourceHandler? {
//                            return resourceHandler
//                        }
//
//                        override fun getCookieAccessFilter(
//                            browser: CefBrowser?,
//                            frame: CefFrame?,
//                            request: CefRequest?
//                        ) = null
//
//                        override fun onBeforeResourceLoad(
//                            browser: CefBrowser?,
//                            frame: CefFrame?,
//                            request: CefRequest?
//                        ) = false
//
//                        override fun onResourceRedirect(
//                            browser: CefBrowser?,
//                            frame: CefFrame?,
//                            request: CefRequest?,
//                            response: CefResponse?,
//                            new_url: org.cef.misc.StringRef?
//                        ) {
//                        }
//
//                        override fun onResourceResponse(
//                            browser: CefBrowser?,
//                            frame: CefFrame?,
//                            request: CefRequest?,
//                            response: CefResponse?
//                        ) = false
//
//                        override fun onResourceLoadComplete(
//                            browser: CefBrowser?,
//                            frame: CefFrame?,
//                            request: CefRequest?,
//                            response: CefResponse?,
//                            status: org.cef.network.CefURLRequest.Status?,
//                            receivedContentLength: Long
//                        ) {
//                        }
//
//                        override fun onProtocolExecution(
//                            browser: CefBrowser?,
//                            frame: CefFrame?,
//                            request: CefRequest?,
//                            allowOsExecution: org.cef.misc.BoolRef?
//                        ) {
//                        }
//                    }
//                }
//
//                override fun getAuthCredentials(
//                    browser: CefBrowser?,
//                    origin_url: String?,
//                    isProxy: Boolean,
//                    host: String?,
//                    port: Int,
//                    realm: String?,
//                    scheme: String?,
//                    callback: CefAuthCallback?
//                ): Boolean {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onCertificateError(
//                    browser: CefBrowser?,
//                    cert_error: CefLoadHandler.ErrorCode?,
//                    request_url: String?,
//                    sslInfo: CefSSLInfo?,
//                    callback: CefCallback?
//                ): Boolean {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onRenderProcessTerminated(
//                    browser: CefBrowser?,
//                    status: CefRequestHandler.TerminationStatus?
//                ) {
//                    TODO("Not yet implemented")
//                }
//            }, browser.cefBrowser)
//
            registerAppSchemeHandler()
            browser.loadURL("http://myapp/index.html")
            Disposer.register(project, browser)
            panel.add(browser.component, BorderLayout.CENTER)
        } else {
            panel.add(JLabel("JCEF is not supported on this platform"), BorderLayout.CENTER)
        }

        toolWindow.contentManager.addContent(
            toolWindow.contentManager.factory.createContent(panel, "Blockly", false)
        )
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