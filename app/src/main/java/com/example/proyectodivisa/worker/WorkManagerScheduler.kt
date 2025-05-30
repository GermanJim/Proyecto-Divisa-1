package com.example.proyectodivisa.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * 📅 PROGRAMADOR DE TRABAJOS AUTOMÁTICOS
 */
class WorkManagerScheduler(private val context: Context) {
    
    companion object {
        private const val TAG = "WorkManagerScheduler"
    }
    
    /**
     * ⏰ PROGRAMAR SINCRONIZACIÓN CADA HORA
     */
    fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(false) // Permitir incluso con batería baja
            .build()
        
        val syncWorkRequest = PeriodicWorkRequestBuilder<ExchangeRateSyncWorker>(
            1, TimeUnit.HOURS,  // Cada hora
            15, TimeUnit.MINUTES // Ventana de flexibilidad
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            ExchangeRateSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Mantener el trabajo existente
            syncWorkRequest
        )
        
        Log.d(TAG, "⏰ Sincronización automática cada hora programada")
    }
    
    /**
     * 🚀 EJECUTAR SINCRONIZACIÓN INMEDIATA
     */
    fun scheduleImmediateSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val syncWorkRequest = OneTimeWorkRequestBuilder<ExchangeRateSyncWorker>()
            .setConstraints(constraints)
            .build()
        
        WorkManager.getInstance(context).enqueue(syncWorkRequest)
        
        Log.d(TAG, "🚀 Sincronización inmediata programada")
    }
    
    /**
     * ❌ CANCELAR SINCRONIZACIÓN
     */
    fun cancelSync() {
        WorkManager.getInstance(context).cancelUniqueWork(ExchangeRateSyncWorker.WORK_NAME)
        Log.d(TAG, "❌ Sincronización automática cancelada")
    }
}
