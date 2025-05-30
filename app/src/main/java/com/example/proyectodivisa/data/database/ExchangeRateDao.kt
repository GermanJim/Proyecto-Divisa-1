package com.example.proyectodivisa.data.database

import androidx.room.*
import com.example.proyectodivisa.data.model.ExchangeRate
import com.example.proyectodivisa.data.repository.CurrencyStats
import kotlinx.coroutines.flow.Flow

/**
 * 🗃️ DATA ACCESS OBJECT - VERSIÓN COMPLETA
 */
@Dao
interface ExchangeRateDao {
    
    @Query("SELECT * FROM exchange_rates ORDER BY syncTimestamp DESC")
    fun getAllExchangeRates(): Flow<List<ExchangeRate>>
    
    @Query("SELECT * FROM exchange_rates WHERE baseCurrency = :baseCurrency ORDER BY syncTimestamp DESC")
    fun getExchangeRatesByBase(baseCurrency: String): Flow<List<ExchangeRate>>
    
    @Query("SELECT * FROM exchange_rates WHERE baseCurrency = :baseCurrency AND targetCurrency = :targetCurrency ORDER BY syncTimestamp DESC LIMIT 1")
    suspend fun getLatestExchangeRate(baseCurrency: String, targetCurrency: String): ExchangeRate?
    
    /**
     * 📄 PAGINACIÓN
     */
    @Query("SELECT * FROM exchange_rates ORDER BY syncTimestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getExchangeRatesPaginated(limit: Int, offset: Int): List<ExchangeRate>
    
    /**
     * 📊 ESTADÍSTICAS DE UNA MONEDA
     */
    @Query("""
        SELECT 
            targetCurrency as currency,
            (SELECT rate FROM exchange_rates e2 WHERE e2.targetCurrency = :currency ORDER BY syncTimestamp DESC LIMIT 1) as currentRate,
            MIN(rate) as minRate,
            MAX(rate) as maxRate,
            AVG(rate) as avgRate,
            0.0 as change,
            0.0 as changePercent,
            COUNT(*) as totalRecords
        FROM exchange_rates 
        WHERE targetCurrency = :currency
    """)
    suspend fun getCurrencyStats(currency: String): CurrencyStats?
    
    /**
     * 📈 HISTORIAL DE UNA MONEDA
     */
    @Query("SELECT * FROM exchange_rates WHERE targetCurrency = :currency AND syncTimestamp >= :timestamp ORDER BY syncTimestamp ASC")
    suspend fun getCurrencyHistoryLastDays(currency: String, timestamp: Long): List<ExchangeRate>
    
    /**
     * 📊 TASAS DIARIAS
     */
    @Query("SELECT * FROM exchange_rates WHERE targetCurrency = :currency AND syncTimestamp >= :timestamp ORDER BY syncTimestamp ASC")
    suspend fun getDailyRatesHistory(currency: String, timestamp: Long): List<ExchangeRate>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchangeRate(exchangeRate: ExchangeRate)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchangeRates(exchangeRates: List<ExchangeRate>)
    
    @Query("DELETE FROM exchange_rates WHERE syncTimestamp < :timestamp")
    suspend fun deleteOldRecords(timestamp: Long): Int
    
    @Query("SELECT COUNT(*) FROM exchange_rates")
    suspend fun getRecordCount(): Int
    
    @Query("SELECT COUNT(*) FROM exchange_rates WHERE baseCurrency = :baseCurrency")
    suspend fun getRecordCountByBase(baseCurrency: String): Int
    
    @Query("SELECT MAX(syncTimestamp) FROM exchange_rates WHERE baseCurrency = :baseCurrency")
    suspend fun getLastSyncTime(baseCurrency: String): Long?
}
