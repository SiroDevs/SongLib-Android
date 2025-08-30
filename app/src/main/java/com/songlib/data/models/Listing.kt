package com.songlib.data.models

import androidx.room.*
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

@Entity(tableName = "listings")
data class Listing(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val parent: Int,
    val title: String,
    val song: Int,
    val created: Date,
    val modified: Date,
    val songCount: Int = 0
) {
    val lastUpdate: String
        get() {
            val now = Date()
            val interval = now.time - modified.time

            return when {
                interval < TimeUnit.MINUTES.toMillis(1) -> {
                    "just now"
                }
                interval < TimeUnit.HOURS.toMillis(1) -> {
                    "${interval / TimeUnit.MINUTES.toMillis(1)} min ago"
                }
                interval < TimeUnit.DAYS.toMillis(1) -> {
                    "${interval / TimeUnit.HOURS.toMillis(1)} hr ago"
                }
                else -> {
                    val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                    formatter.format(modified)
                }
            }
        }
}
