package com.example.proyectodivisa.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.proyectodivisa.data.model.ExchangeRate

/**
 * 🏗️ CONFIGURACIÓN DE LA BASE DE DATOS
 */
@Database(
    entities = [ExchangeRate::class],
    version = 1,
    exportSchema = false
)
abstract class ExchangeRateDatabase : RoomDatabase() {
    
    abstract fun exchangeRateDao(): ExchangeRateDao
    
    companion object {
        @Volatile
        private var INSTANCE: ExchangeRateDatabase? = null
        
        fun getDatabase(context: Context): ExchangeRateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExchangeRateDatabase::class.java,
                    "exchange_rate_database_background"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
