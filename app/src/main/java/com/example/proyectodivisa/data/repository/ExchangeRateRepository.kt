package com.example.proyectodivisa.data.repository

import android.content.Context
import android.util.Log
import com.example.proyectodivisa.data.api.RetrofitClient
import com.example.proyectodivisa.data.database.ExchangeRateDatabase
import com.example.proyectodivisa.data.model.ExchangeRate
import kotlinx.coroutines.flow.Flow

/**
 * 🏪 REPOSITORIO - VERSIÓN SIMPLIFICADA PARA BACKGROUND
 */
class ExchangeRateRepository(context: Context) {
    
    private val database = ExchangeRateDatabase.getDatabase(context)
    private val exchangeRateDao = database.exchangeRateDao()
    private val apiService = RetrofitClient.exchangeRateApiService
    
    companion object {
        private const val TAG = "ExchangeRateRepository"
        private const val PAGE_SIZE = 20
    }
    
    fun getAllExchangeRates(): Flow<List<ExchangeRate>> {
        return exchangeRateDao.getAllExchangeRates()
    }
    
    fun getExchangeRatesByBase(baseCurrency: String): Flow<List<ExchangeRate>> {
        return exchangeRateDao.getExchangeRatesByBase(baseCurrency)
    }
    
    /**
     * 📄 OBTENER DATOS PAGINADOS
     */
    suspend fun getExchangeRatesPaginated(page: Int): List<ExchangeRate> {
        val offset = page * PAGE_SIZE
        return exchangeRateDao.getExchangeRatesPaginated(PAGE_SIZE, offset)
    }
    
    /**
     * 📊 OBTENER ESTADÍSTICAS DE UNA MONEDA
     */
    suspend fun getCurrencyStats(currency: String): CurrencyStats? {
        return exchangeRateDao.getCurrencyStats(currency)
    }
    
    /**
     * 📈 OBTENER HISTORIAL DE UNA MONEDA
     */
    suspend fun getCurrencyHistoryLastDays(currency: String, days: Int): List<ExchangeRate> {
        val timestamp = System.currentTimeMillis() - (days * 24L * 60L * 60L * 1000L)
        return exchangeRateDao.getCurrencyHistoryLastDays(currency, timestamp)
    }
    
    /**
     * 📊 OBTENER TASAS DIARIAS
     */
    suspend fun getDailyRatesHistory(currency: String, days: Int): List<ExchangeRate> {
        val timestamp = System.currentTimeMillis() - (days * 24L * 60L * 60L * 1000L)
        return exchangeRateDao.getDailyRatesHistory(currency, timestamp)
    }
    
    /**
     * 🔄 SINCRONIZACIÓN PRINCIPAL
     */
    suspend fun syncExchangeRates(baseCurrency: String = "MXN"): Boolean {
        return try {
            Log.d(TAG, "🚀 Iniciando sincronización silenciosa para $baseCurrency")
            
            val response = apiService.getExchangeRates(baseCurrency)
            
            if (response.isSuccessful) {
                val exchangeRateResponse = response.body()
                
                if (exchangeRateResponse != null && exchangeRateResponse.result == "success") {
                    Log.d(TAG, "📡 API exitosa - ${exchangeRateResponse.conversionRates.size} divisas recibidas")
                    
                    val exchangeRates = exchangeRateResponse.conversionRates.map { (currency, rate) ->
                        ExchangeRate(
                            baseCurrency = baseCurrency,
                            targetCurrency = currency,
                            rate = rate,
                            lastUpdate = exchangeRateResponse.timeLastUpdateUnix,
                            syncTimestamp = System.currentTimeMillis()
                        )
                    }
                    
                    // 💾 GUARDAR EN BASE DE DATOS
                    exchangeRateDao.insertExchangeRates(exchangeRates)
                    
                    val totalRecords = exchangeRateDao.getRecordCount()
                    Log.d(TAG, "✅ ${exchangeRates.size} divisas guardadas - Total en BD: $totalRecords")
                    
                    // 🗑️ LIMPIAR REGISTROS ANTIGUOS (más de 30 días)
                    val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24L * 60L * 60L * 1000L)
                    val deletedCount = exchangeRateDao.deleteOldRecords(thirtyDaysAgo)
                    if (deletedCount > 0) {
                        Log.d(TAG, "🗑️ $deletedCount registros antiguos eliminados")
                    }
                    
                    val finalCount = exchangeRateDao.getRecordCount()
                    Log.d(TAG, "📊 Total final en BD: $finalCount registros")
                    
                    true
                } else {
                    Log.e(TAG, "❌ Respuesta de API inválida")
                    false
                }
            } else {
                Log.e(TAG, "🌐 Error HTTP: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "💥 Error durante sincronización", e)
            false
        }
    }
    
    suspend fun getRecordCount(): Int {
        return exchangeRateDao.getRecordCount()
    }
    
    suspend fun getRecordCountByBase(baseCurrency: String): Int {
        return exchangeRateDao.getRecordCountByBase(baseCurrency)
    }
    
    suspend fun getLastSyncTime(baseCurrency: String = "MXN"): Long? {
        return exchangeRateDao.getLastSyncTime(baseCurrency)
    }
}

/**
 * 📊 CLASE DE ESTADÍSTICAS
 */
data class CurrencyStats(
    val currency: String,
    val currentRate: Double,
    val minRate: Double,
    val maxRate: Double,
    val avgRate: Double,
    val change: Double,
    val changePercent: Double,
    val totalRecords: Int
)
