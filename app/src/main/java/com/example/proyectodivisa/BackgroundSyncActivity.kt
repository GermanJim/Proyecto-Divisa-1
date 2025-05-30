package com.example.proyectodivisa

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.example.proyectodivisa.worker.WorkManagerScheduler

/**
 * 🔄 ACTIVIDAD MÍNIMA PARA SINCRONIZACIÓN EN SEGUNDO PLANO
 * 
 * Esta actividad se ejecuta solo para inicializar la sincronización automática
 * y luego se cierra inmediatamente. No tiene interfaz visual.
 */
class BackgroundSyncActivity : Activity() {
    
    companion object {
        private const val TAG = "BackgroundSyncActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d(TAG, "🚀 Iniciando aplicación de sincronización en segundo plano")
        
        // 📅 Programar sincronización automática cada hora
        val workManagerScheduler = WorkManagerScheduler(this)
        workManagerScheduler.schedulePeriodicSync()
        
        Log.d(TAG, "✅ Sincronización programada. La app funcionará en segundo plano.")
        Log.d(TAG, "💡 Puedes cerrar la app, la sincronización continuará cada hora.")
        
        // 🔄 Ejecutar una sincronización inmediata para probar
        workManagerScheduler.scheduleImmediateSync()
        
        // ⏰ Cerrar la actividad después de 3 segundos
        android.os.Handler(mainLooper).postDelayed({
            Log.d(TAG, "🏁 Cerrando actividad. Sincronización activa en segundo plano.")
            finish()
        }, 3000)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "📱 Actividad cerrada. WorkManager sigue funcionando en segundo plano.")
    }
}
