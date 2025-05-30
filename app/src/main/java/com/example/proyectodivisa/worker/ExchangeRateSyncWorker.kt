package com.example.proyectodivisa.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.proyectodivisa.data.repository.ExchangeRateRepository

/**
 * 🔄 WORKER DE SINCRONIZACIÓN AUTOMÁTICA CADA HORA
 */
class ExchangeRateSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    
    companion object {
        const val TAG = "ExchangeRateSyncWorker"
        const val WORK_NAME = "exchange_rate_sync_work"
    }
    
    override suspend fun doWork(): Result {
        Log.d(TAG, "⏰ Ejecutando sincronización automática programada")
        
        return try {
            val repository = ExchangeRateRepository(applicationContext)
            
            // Sincronizar con MXN como base (peso mexicano)
            val success = repository.syncExchangeRates("MXN")
            
            if (success) {
                val recordCount = repository.getRecordCount()
                Log.d(TAG, "✅ Sincronización automática exitosa - $recordCount registros en BD")
                Result.success()
            } else {
                Log.w(TAG, "⚠️ Sincronización automática falló - Reintentando...")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "💥 Error en sincronización automática", e)
            Result.failure()
        }
    }
}
