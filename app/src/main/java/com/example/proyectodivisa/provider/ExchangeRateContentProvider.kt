package com.example.proyectodivisa.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.content.ContentUris
import com.example.proyectodivisa.data.database.ExchangeRateDatabase
import kotlinx.coroutines.runBlocking

/**
 * 🔒 CONTENT PROVIDER SEGURO
 */
class ExchangeRateContentProvider : ContentProvider() {
    
    companion object {
        const val AUTHORITY = "com.example.proyectodivisa.provider"
        val BASE_URI: Uri = Uri.parse("content://$AUTHORITY")
        
        const val EXCHANGE_RATES = 1
        const val EXCHANGE_RATE_ID = 2
        
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "exchange_rates", EXCHANGE_RATES)
            addURI(AUTHORITY, "exchange_rates/#", EXCHANGE_RATE_ID)
        }
        
        val EXCHANGE_RATES_URI: Uri = Uri.withAppendedPath(BASE_URI, "exchange_rates")
    }
    
    private lateinit var database: ExchangeRateDatabase
    
    override fun onCreate(): Boolean {
        database = ExchangeRateDatabase.getDatabase(context!!)
        return true
    }
    
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return when (uriMatcher.match(uri)) {
            EXCHANGE_RATES -> {
                val query = buildString {
                    append("SELECT ")
                    append(projection?.joinToString(", ") ?: "*")
                    append(" FROM exchange_rates")
                    
                    if (!selection.isNullOrEmpty()) {
                        append(" WHERE ")
                        append(selection)
                    }
                    
                    append(" ORDER BY ")
                    append(sortOrder ?: "syncTimestamp DESC")
                }
                
                runBlocking {
                    database.openHelper.readableDatabase.query(query, selectionArgs ?: emptyArray())
                }
            }
            EXCHANGE_RATE_ID -> {
                val id = ContentUris.parseId(uri)
                val query = buildString {
                    append("SELECT ")
                    append(projection?.joinToString(", ") ?: "*")
                    append(" FROM exchange_rates WHERE id = ?")
                    
                    if (!selection.isNullOrEmpty()) {
                        append(" AND ")
                        append(selection)
                    }
                    
                    append(" ORDER BY ")
                    append(sortOrder ?: "syncTimestamp DESC")
                }
                
                val args = mutableListOf(id.toString())
                selectionArgs?.let { args.addAll(it) }
                
                runBlocking {
                    database.openHelper.readableDatabase.query(query, args.toTypedArray())
                }
            }
            else -> throw IllegalArgumentException("❌ URI desconocida: $uri")
        }?.also { cursor ->
            cursor.setNotificationUri(context?.contentResolver, uri)
        }
    }
    
    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            EXCHANGE_RATES -> "vnd.android.cursor.dir/vnd.$AUTHORITY.exchange_rates"
            EXCHANGE_RATE_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.exchange_rates"
            else -> throw IllegalArgumentException("❌ URI desconocida: $uri")
        }
    }
    
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (callingPackage != context?.packageName) {
            throw SecurityException("🚫 Acceso no autorizado - Solo lectura permitida")
        }
        return null
    }
    
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        if (callingPackage != context?.packageName) {
            throw SecurityException("🚫 Acceso no autorizado - Solo lectura permitida")
        }
        return 0
    }
    
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        if (callingPackage != context?.packageName) {
            throw SecurityException("🚫 Acceso no autorizado - Solo lectura permitida")
        }
        return 0
    }
}
