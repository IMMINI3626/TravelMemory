package com.example.travelmemory

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class TravelRecord(
    val id: Long = 0,
    val place: String,
    val visitDate: String,
    val memo: String,
    val photoUri: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "travel_memory.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "travel_records"
        private const val COLUMN_ID = "id"
        private const val COLUMN_PLACE = "place"
        private const val COLUMN_VISIT_DATE = "visit_date"
        private const val COLUMN_MEMO = "memo"
        private const val COLUMN_PHOTO_URI = "photo_uri"
        private const val COLUMN_LATITUDE = "latitude"
        private const val COLUMN_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PLACE TEXT NOT NULL,
                $COLUMN_VISIT_DATE TEXT NOT NULL,
                $COLUMN_MEMO TEXT,
                $COLUMN_PHOTO_URI TEXT,
                $COLUMN_LATITUDE REAL,
                $COLUMN_LONGITUDE REAL
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // INSERT
    fun insertTravel(record: TravelRecord): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PLACE, record.place)
            put(COLUMN_VISIT_DATE, record.visitDate)
            put(COLUMN_MEMO, record.memo)
            put(COLUMN_PHOTO_URI, record.photoUri)
            put(COLUMN_LATITUDE, record.latitude)
            put(COLUMN_LONGITUDE, record.longitude)
        }
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    // SELECT ALL
    fun getAllTravels(orderBy: String = "$COLUMN_VISIT_DATE DESC"): List<TravelRecord> {
        val db = readableDatabase
        val records = mutableListOf<TravelRecord>()
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, orderBy)
        with(cursor) {
            while (moveToNext()) {
                records.add(
                    TravelRecord(
                        id = getLong(getColumnIndexOrThrow(COLUMN_ID)),
                        place = getString(getColumnIndexOrThrow(COLUMN_PLACE)),
                        visitDate = getString(getColumnIndexOrThrow(COLUMN_VISIT_DATE)),
                        memo = getString(getColumnIndexOrThrow(COLUMN_MEMO)) ?: "",
                        photoUri = getString(getColumnIndexOrThrow(COLUMN_PHOTO_URI)),
                        latitude = if (isNull(getColumnIndexOrThrow(COLUMN_LATITUDE))) null
                        else getDouble(getColumnIndexOrThrow(COLUMN_LATITUDE)),
                        longitude = if (isNull(getColumnIndexOrThrow(COLUMN_LONGITUDE))) null
                        else getDouble(getColumnIndexOrThrow(COLUMN_LONGITUDE))
                    )
                )
            }
            close()
        }
        db.close()
        return records
    }

    // SELECT ONE
    fun getTravelById(id: Long): TravelRecord? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME, null,
            "$COLUMN_ID = ?", arrayOf(id.toString()),
            null, null, null
        )
        val record = if (cursor.moveToFirst()) {
            TravelRecord(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                place = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLACE)),
                visitDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VISIT_DATE)),
                memo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEMO)) ?: "",
                photoUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_URI)),
                latitude = if (cursor.isNull(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE))) null
                else cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                longitude = if (cursor.isNull(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))) null
                else cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))
            )
        } else null
        cursor.close()
        db.close()
        return record
    }

    // SEARCH
    fun searchTravel(keyword: String): List<TravelRecord> {
        val db = readableDatabase
        val records = mutableListOf<TravelRecord>()
        val cursor = db.query(
            TABLE_NAME, null,
            "$COLUMN_PLACE LIKE ?", arrayOf("%$keyword%"),
            null, null, "$COLUMN_VISIT_DATE DESC"
        )
        with(cursor) {
            while (moveToNext()) {
                records.add(
                    TravelRecord(
                        id = getLong(getColumnIndexOrThrow(COLUMN_ID)),
                        place = getString(getColumnIndexOrThrow(COLUMN_PLACE)),
                        visitDate = getString(getColumnIndexOrThrow(COLUMN_VISIT_DATE)),
                        memo = getString(getColumnIndexOrThrow(COLUMN_MEMO)) ?: "",
                        photoUri = getString(getColumnIndexOrThrow(COLUMN_PHOTO_URI)),
                        latitude = if (isNull(getColumnIndexOrThrow(COLUMN_LATITUDE))) null
                        else getDouble(getColumnIndexOrThrow(COLUMN_LATITUDE)),
                        longitude = if (isNull(getColumnIndexOrThrow(COLUMN_LONGITUDE))) null
                        else getDouble(getColumnIndexOrThrow(COLUMN_LONGITUDE))
                    )
                )
            }
            close()
        }
        db.close()
        return records
    }

    // UPDATE
    fun updateTravel(record: TravelRecord): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PLACE, record.place)
            put(COLUMN_VISIT_DATE, record.visitDate)
            put(COLUMN_MEMO, record.memo)
            put(COLUMN_PHOTO_URI, record.photoUri)
            put(COLUMN_LATITUDE, record.latitude)
            put(COLUMN_LONGITUDE, record.longitude)
        }
        val rows = db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(record.id.toString()))
        db.close()
        return rows
    }

    // DELETE
    fun deleteTravel(id: Long): Int {
        val db = writableDatabase
        val rows = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return rows
    }
}