package com.shazdroid.automation.uiautomation.util

import org.cef.callback.CefCallback
import org.cef.handler.CefLoadHandler
import org.cef.handler.CefResourceHandler
import org.cef.misc.IntRef
import org.cef.misc.StringRef
import org.cef.network.CefRequest
import org.cef.network.CefResponse
import java.io.IOException
import java.io.InputStream
import java.net.URLConnection

class CustomResourceHandler : CefResourceHandler {
    private var state: ResourceHandlerState = ClosedConnection

    override fun processRequest(request: CefRequest, callback: CefCallback): Boolean {
        val url = request.url ?: return false
        val pathToResource = url.replace("http://myapp", "webview/")
        val newUrl = javaClass.classLoader.getResource(pathToResource)
        state = if (newUrl != null) {
            OpenedConnection(newUrl.openConnection())
        } else {
            ClosedConnection
        }
        callback.Continue()
        return true
    }

    override fun getResponseHeaders(response: CefResponse, responseLength: IntRef, redirectUrl: StringRef?) {
        state.getResponseHeaders(response, responseLength, redirectUrl)
    }

    override fun readResponse(dataOut: ByteArray, bytesToRead: Int, bytesRead: IntRef, callback: CefCallback): Boolean {
        return state.readResponse(dataOut, bytesToRead, bytesRead, callback)
    }

    override fun cancel() {
        state.close()
        state = ClosedConnection
    }
}

sealed interface ResourceHandlerState {
    fun getResponseHeaders(response: CefResponse, responseLength: IntRef, redirectUrl: StringRef?)
    fun readResponse(dataOut: ByteArray, bytesToRead: Int, bytesRead: IntRef, callback: CefCallback): Boolean
    fun close() {}
}

class OpenedConnection(private val connection: URLConnection) : ResourceHandlerState {
    private val inputStream: InputStream = connection.inputStream

    override fun getResponseHeaders(response: CefResponse, responseLength: IntRef, redirectUrl: StringRef?) {
        try {
            val url = connection.url.toString()
            response.mimeType = when {
                url.contains(".css") -> "text/css"
                url.contains(".js") -> "text/javascript"
                url.contains(".html") -> "text/html"
                else -> connection.contentType ?: "application/octet-stream"
            }
            responseLength.set(inputStream.available())
            response.status = 200
        } catch (e: IOException) {
            response.error = CefLoadHandler.ErrorCode.ERR_FILE_NOT_FOUND
            response.statusText = e.localizedMessage
            response.status = 404
        }
    }

    override fun readResponse(dataOut: ByteArray, bytesToRead: Int, bytesRead: IntRef, callback: CefCallback): Boolean {
        return try {
            val availableSize = inputStream.available()
            if (availableSize > 0) {
                val maxBytesToRead = availableSize.coerceAtMost(bytesToRead)
                bytesRead.set(inputStream.read(dataOut, 0, maxBytesToRead))
                true
            } else {
                inputStream.close()
                false
            }
        } catch (e: IOException) {
            inputStream.close()
            false
        }
    }

    override fun close() {
        inputStream.close()
    }
}

object ClosedConnection : ResourceHandlerState {
    override fun getResponseHeaders(response: CefResponse, responseLength: IntRef, redirectUrl: StringRef?) {
        response.status = 404
    }

    override fun readResponse(dataOut: ByteArray, bytesToRead: Int, bytesRead: IntRef, callback: CefCallback): Boolean {
        bytesRead.set(0)
        return false
    }
}

