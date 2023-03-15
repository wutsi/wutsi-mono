package com.wutsi.platform.core.security.spring

import com.wutsi.platform.core.security.TokenProvider
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

class SpringAuthorizationRequestInterceptor(
    private val tokenProvider: TokenProvider,
) : ClientHttpRequestInterceptor {

    override fun intercept(request: HttpRequest, body: ByteArray, exec: ClientHttpRequestExecution): ClientHttpResponse {
        val token = tokenProvider.getToken()
        if (token != null) {
            request.headers["Authorization"] = listOf("Bearer $token")
        }

        return exec.execute(request, body)
    }
}
