package com.shazdroid.automation.uiautomation.util

import com.intellij.openapi.diagnostic.Logger
import org.cef.callback.CefCallback
import org.cef.handler.CefRequestHandlerAdapter
import org.cef.handler.CefResourceHandler
import org.cef.misc.IntRef
import org.cef.misc.StringRef
import org.cef.network.CefRequest
import org.cef.network.CefResponse
import java.io.InputStream
import java.nio.ByteBuffer

class BlocklyResourceHandler : CefRequestHandlerAdapter(), CefResourceHandler {
    private val logger = Logger.getInstance(BlocklyResourceHandler::class.java)
    private var resourceStream: InputStream? = null
    private var mimeType: String? = null
    private var responseData: ByteBuffer? = null

    fun getResourceHandler(request: CefRequest?): CefResourceHandler? {
        val url = request?.url
        logger.info("Received request for URL: $url")

        if (url == null || !url.startsWith("blockly://")) {
            logger.warn("Invalid or null URL: $url")
            return null
        }

        // Convert the URL to a resource path within the project resources
        val resourcePath = url.replace("blockly://", "/webview/")
        resourceStream = BlocklyResourceHandler::class.java.getResourceAsStream(resourcePath)

        if (resourceStream == null) {
            logger.error("Failed to load resource: $resourcePath")
            return null
        }

        mimeType = when {
            resourcePath.endsWith(".html") -> "text/html"
            resourcePath.endsWith(".js") -> "application/javascript"
            resourcePath.endsWith(".css") -> "text/css"
            else -> "application/octet-stream"
        }

        // Read the entire file content into a ByteBuffer for JCEF
        responseData = ByteBuffer.wrap(resourceStream!!.readAllBytes())
        logger.info("Loaded resource: $resourcePath with MIME type: $mimeType")
        return this
    }

    override fun processRequest(request: CefRequest, callback: CefCallback): Boolean {
        callback.Continue()
        return true
    }

    override fun getResponseHeaders(response: CefResponse?, responseLength: IntRef?, redirectUrl: StringRef?) {
        response?.mimeType = mimeType
        response?.status = 200
        responseLength?.set(responseData?.remaining() ?: 0)
    }

    override fun readResponse(
        dataOut: ByteArray?,
        bytesToRead: Int,
        bytesRead: IntRef?,
        callback: CefCallback?
    ): Boolean {
        responseData?.let {
            val remaining = it.remaining()
            val bytesToCopy = minOf(bytesToRead, remaining)
            dataOut?.let { buffer ->
                it.get(buffer, 0, bytesToCopy)
                bytesRead?.set(bytesToCopy)
            }
            return bytesToCopy > 0
        }
        bytesRead?.set(0)
        return false
    }

    override fun cancel() {
        resourceStream?.close()
    }
}
