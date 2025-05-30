package com.example.proyectodivisa.data.api

import com.example.proyectodivisa.data.model.ExchangeRateResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 🌐 INTERFAZ DE LA API
 */
interface ExchangeRateApiService {
    
    @GET("v6/3ecb08227c209b51e9f9a9b3/latest/{base_currency}")
    suspend fun getExchangeRates(@Path("base_currency") baseCurrency: String): Response<ExchangeRateResponse>
    
    companion object {
        const val BASE_URL = "https://v6.exchangerate-api.com/"
    }
}
