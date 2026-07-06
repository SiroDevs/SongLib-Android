package com.songlib.core.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.songlib.core.database.AppDatabase
import com.songlib.core.database.daos.BookDao
import com.songlib.core.database.daos.DraftDao
import com.songlib.core.database.daos.EditDao
import com.songlib.core.database.daos.HistoryDao
import com.songlib.core.database.daos.ListingDao
import com.songlib.core.database.daos.SearchDao
import com.songlib.core.database.daos.SongDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) { }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS feature (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                draftId INTEGER NOT NULL DEFAULT 0,
                title TEXT NOT NULL,
                content TEXT NOT NULL DEFAULT '',
                songNo INTEGER,
                book INTEGER,
                userId INTEGER NOT NULL DEFAULT 0,
                created TEXT NOT NULL,
                updated TEXT,
                synced INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS edits (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                editId INTEGER NOT NULL DEFAULT 0,
                songId INTEGER NOT NULL,
                title TEXT NOT NULL,
                content TEXT NOT NULL DEFAULT '',
                userId INTEGER NOT NULL DEFAULT 0,
                status TEXT NOT NULL DEFAULT 'pending',
                created TEXT NOT NULL,
                updated TEXT,
                synced INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())

        db.execSQL("ALTER TABLE searches ADD COLUMN hits INTEGER NOT NULL DEFAULT 1")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_searches_title ON searches(title)")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) { }
}

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase =
        Room.databaseBuilder(appContext, AppDatabase::class.java, "SongLib")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .build()

    @Provides fun provideBookDao(db: AppDatabase): BookDao = db.booksDao()
    @Provides fun provideHistoryDao(db: AppDatabase): HistoryDao = db.historiesDao()
    @Provides fun provideListingDao(db: AppDatabase): ListingDao = db.listingsDao()
    @Provides fun provideSearchDao(db: AppDatabase): SearchDao = db.searchesDao()
    @Provides fun provideSongDao(db: AppDatabase): SongDao = db.songsDao()
    @Provides fun provideDraftDao(db: AppDatabase): DraftDao = db.draftsDao()
    @Provides fun provideEditDao(db: AppDatabase): EditDao = db.editsDao()
}
