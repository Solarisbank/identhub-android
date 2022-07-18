package de.solarisbank.sdk.logger

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import okio.GzipSource
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.util.concurrent.TimeUnit

class LoggerHttpInterceptor : Interceptor {
    companion object {
        private const val SHOULD_LOG_BODY = false
        private val OMITTED_URLS = listOf(
            "sdk_logging"
        )
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBody = request.body
        val connection = chain.connection()

        for (omitted in OMITTED_URLS) {
            if (request.url.toString().contains(omitted, ignoreCase = true)) {
                return chain.proceed(request)
            }
        }

        var requestStartMessage =
            ("--> ${request.method} ${request.url}${if (connection != null) " " + connection.protocol() else ""}")

        if (requestBody != null) {
            requestStartMessage += " (${requestBody.contentLength()}-byte body)"
        }

        IdLogger.info(requestStartMessage, IdLogger.Category.Api)
        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            IdLogger.error("<-- HTTP FAILED: $e", IdLogger.Category.Api)
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        val responseBody = response.body!!
        val contentLength = responseBody.contentLength()
        IdLogger.info(
            "<-- ${response.code}${if (response.message.isEmpty()) "" else ' ' + response.message} ${response.request.url} (${tookMs}ms)",
            IdLogger.Category.Api
        )

        val headers = response.headers
        IdLogger.info("[request-id: ${headers["x-request-id"]}]", IdLogger.Category.Api)

        if (bodyHasUnknownEncoding(response.headers) || !SHOULD_LOG_BODY) {
            IdLogger.info("<-- END HTTP (encoded body omitted)", IdLogger.Category.Api)
        } else {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            var buffer = source.buffer

            var gzippedLength: Long? = null
            if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
                gzippedLength = buffer.size
                GzipSource(buffer.clone()).use { gzippedResponseBody ->
                    buffer = Buffer()
                    buffer.writeAll(gzippedResponseBody)
                }
            }

            val contentType = responseBody.contentType()
            val charset: Charset = contentType?.charset(UTF_8) ?: UTF_8

            if (!buffer.isProbablyUtf8()) {
                IdLogger.info("<-- END HTTP (binary ${buffer.size}-byte body omitted)", IdLogger.Category.Api)
                return response
            }

            if (contentLength != 0L) {
                IdLogger.debug(buffer.clone().readString(charset), IdLogger.Category.Api)
            }

            if (gzippedLength != null) {
                IdLogger.info("<-- END HTTP (${buffer.size}-byte, $gzippedLength-gzipped-byte body)", IdLogger.Category.Api)
            } else {
                IdLogger.info("<-- END HTTP (${buffer.size}-byte body)", IdLogger.Category.Api)
            }
        }

        return response
    }
}

private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
    val contentEncoding = headers["Content-Encoding"] ?: return false
    return !contentEncoding.equals("identity", ignoreCase = true) &&
            !contentEncoding.equals("gzip", ignoreCase = true)
}

private fun Buffer.isProbablyUtf8(): Boolean {
    try {
        val prefix = Buffer()
        val byteCount = size.coerceAtMost(64)
        copyTo(prefix, 0, byteCount)
        for (i in 0 until 16) {
            if (prefix.exhausted()) {
                break
            }
            val codePoint = prefix.readUtf8CodePoint()
            if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                return false
            }
        }
        return true
    } catch (_: EOFException) {
        return false // Truncated UTF-8 sequence.
    }
}

