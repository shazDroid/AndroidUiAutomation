package com.shazdroid.automation.uiautomation.util

import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefCookieAccessFilter
import org.cef.handler.CefResourceHandler
import org.cef.handler.CefResourceRequestHandler
import org.cef.misc.BoolRef
import org.cef.misc.StringRef
import org.cef.network.CefRequest
import org.cef.network.CefResponse
import org.cef.network.CefURLRequest

class BlocklyResourceRequestHandler(private val resourceHandler: BlocklyResourceHandler) : CefResourceRequestHandler {
    override fun getCookieAccessFilter(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?
    ): CefCookieAccessFilter {
        TODO("Not yet implemented")
    }

    override fun onBeforeResourceLoad(browser: CefBrowser?, frame: CefFrame?, request: CefRequest?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getResourceHandler(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?
    ): CefResourceHandler? {
        return resourceHandler.getResourceHandler(request)
    }

    override fun onResourceRedirect(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?,
        response: CefResponse?,
        new_url: StringRef?
    ) {
        TODO("Not yet implemented")
    }

    override fun onResourceResponse(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?,
        response: CefResponse?
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun onResourceLoadComplete(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?,
        response: CefResponse?,
        status: CefURLRequest.Status?,
        receivedContentLength: Long
    ) {
        TODO("Not yet implemented")
    }

    override fun onProtocolExecution(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?,
        allowOsExecution: BoolRef?
    ) {
        TODO("Not yet implemented")
    }

}