package com.ssafy.network.auth

import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.auth.AuthManager
import com.ssafy.network.source.auth.AuthDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val authDataSource: AuthDataSource
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.addToken().also { apiResponse ->
            apiResponse.saveToken()
        }

        // TODO 리프레시 토큰도 만료 된 경우에 대한 로직 처리 필요
        if(false){
            AuthManager.sendNoAuthEvent()
        }

        if (response.code == UNAUTHORIZED) {
            getNewToken()
            return chain.addToken()
        } else {
            return response
        }
    }

    private fun Interceptor.Chain.addToken(): Response {
        val token = runBlocking { dataStoreRepository.getAccessToken() }
        val request = request().newBuilder()
            .addHeader(TOKEN, token)
            .build()

        return proceed(request)
    }

    private fun Response.saveToken() = runBlocking {
        header(TOKEN)?.let { newToken ->
            dataStoreRepository.saveAccessToken(newToken)
        }

        header(REFRESH_TOKEN)?.let { newRefreshToken ->
            dataStoreRepository.saveRefreshToken(newRefreshToken)
        }
    }

    private fun getNewToken() = runBlocking {
        val refreshToken = dataStoreRepository.getRefreshToken()
        authDataSource.reissue(refreshToken).raw().saveToken()
    }

    companion object {
        const val TOKEN = "Authorization"
        const val REFRESH_TOKEN = "Refresh-Token"
        const val UNAUTHORIZED = 401
    }
}