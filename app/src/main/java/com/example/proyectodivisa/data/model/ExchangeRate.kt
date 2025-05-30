package com.example.proyectodivisa.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 💾 ENTIDAD DE BASE DE DATOS
 */
@Entity(tableName = "exchange_rates")
data class ExchangeRate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val baseCurrency: String,
    val targetCurrency: String,
    val rate: Double,
    val lastUpdate: Long,
    val syncTimestamp: Long = System.currentTimeMillis()
)
