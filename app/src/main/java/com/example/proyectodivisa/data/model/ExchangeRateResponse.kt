package com.example.proyectodivisa.data.model

import com.google.gson.annotations.SerializedName

/**
 * 📡 MODELO DE RESPUESTA DE LA API
 */
data class ExchangeRateResponse(
    @SerializedName("result")
    val result: String,
    
    @SerializedName("documentation")
    val documentation: String,
    
    @SerializedName("terms_of_use")
    val termsOfUse: String,
    
    @SerializedName("time_last_update_unix")
    val timeLastUpdateUnix: Long,
    
    @SerializedName("time_last_update_utc")
    val timeLastUpdateUtc: String,
    
    @SerializedName("time_next_update_unix")
    val timeNextUpdateUnix: Long,
    
    @SerializedName("time_next_update_utc")
    val timeNextUpdateUtc: String,
    
    @SerializedName("base_code")
    val baseCode: String,
    
    @SerializedName("conversion_rates")
    val conversionRates: Map<String, Double>
)
