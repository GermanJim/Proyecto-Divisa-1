package com.example.proyectodivisa

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyectodivisa.data.repository.ExchangeRateRepository
import com.example.proyectodivisa.worker.WorkManagerScheduler
import kotlinx.coroutines.launch

/**
 * 📱 ACTIVIDAD PRINCIPAL - PANTALLA EN BLANCO CON SINCRONIZACIÓN
 * 
 * Solo configura la sincronización automática y mantiene una pantalla en blanco.
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var repository: ExchangeRateRepository
    private lateinit var workManagerScheduler: WorkManagerScheduler
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 🎨 LAYOUT COMPLETAMENTE EN BLANCO
        setContentView(R.layout.activity_main)
        
        // 🏗️ INICIALIZAR COMPONENTES
        repository = ExchangeRateRepository(this)
        workManagerScheduler = WorkManagerScheduler(this)
        
        Log.d(TAG, "📱 App iniciada - Configurando sincronización automática")
        
        // ⏰ CONFIGURAR SINCRONIZACIÓN AUTOMÁTICA CADA HORA
        setupAutomaticSync()
        
        // 🚀 EJECUTAR SINCRONIZACIÓN INMEDIATA AL ABRIR LA APP
        executeImmediateSync()
    }
    
    /**
     * ⏰ CONFIGURAR SINCRONIZACIÓN AUTOMÁTICA CADA HORA
     */
    private fun setupAutomaticSync() {
        Log.d(TAG, "⏰ Configurando sincronización automática cada hora")
        workManagerScheduler.schedulePeriodicSync()
        Log.d(TAG, "✅ Sincronización automática configurada")
    }
    
    /**
     * 🚀 EJECUTAR SINCRONIZACIÓN INMEDIATA AL ABRIR
     */
    private fun executeImmediateSync() {
        Log.d(TAG, "🚀 Ejecutando sincronización inmediata...")
        
        lifecycleScope.launch {
            try {
                // Sincronizar con MXN como base (peso mexicano)
                val success = repository.syncExchangeRates("MXN")
                
                if (success) {
                    val recordCount = repository.getRecordCount()
                    Log.d(TAG, "✅ Sincronización inmediata exitosa - $recordCount registros guardados")
                } else {
                    Log.w(TAG, "⚠️ Sincronización inmediata falló - WorkManager tomará el control")
                    workManagerScheduler.scheduleImmediateSync()
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error en sincronización inmediata", e)
                workManagerScheduler.scheduleImmediateSync()
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "📱 App en primer plano - Sincronización automática activa")
    }
    
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "📱 App en segundo plano - Sincronización automática continúa")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "📱 App cerrada - WorkManager sigue funcionando en segundo plano")
    }
}
