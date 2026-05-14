package com.songlib.core.database

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.songlib.core.database.daos.BookDao
import com.songlib.core.database.daos.BookDao_Impl
import com.songlib.core.database.daos.HistoryDao
import com.songlib.core.database.daos.HistoryDao_Impl
import com.songlib.core.database.daos.ListingDao
import com.songlib.core.database.daos.ListingDao_Impl
import com.songlib.core.database.daos.SearchDao
import com.songlib.core.database.daos.SearchDao_Impl
import com.songlib.core.database.daos.SongDao
import com.songlib.core.database.daos.SongDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _bookDao: Lazy<BookDao> = lazy {
    BookDao_Impl(this)
  }

  private val _historyDao: Lazy<HistoryDao> = lazy {
    HistoryDao_Impl(this)
  }

  private val _listingDao: Lazy<ListingDao> = lazy {
    ListingDao_Impl(this)
  }

  private val _searchDao: Lazy<SearchDao> = lazy {
    SearchDao_Impl(this)
  }

  private val _songDao: Lazy<SongDao> = lazy {
    SongDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2, "8a5e9ad8949d12eba135e9e8908b7070", "3502120eb29ff68fa114ab71adc66799") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `books` (`bookId` INTEGER NOT NULL, `bookNo` INTEGER NOT NULL, `created` TEXT NOT NULL, `enabled` INTEGER NOT NULL, `position` INTEGER NOT NULL, `songs` INTEGER NOT NULL, `subTitle` TEXT NOT NULL, `title` TEXT NOT NULL, `user` INTEGER NOT NULL, PRIMARY KEY(`bookId`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_books_bookId` ON `books` (`bookId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `histories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `song` INTEGER NOT NULL, `created` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `listings` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `parent` INTEGER NOT NULL, `title` TEXT NOT NULL, `song` INTEGER NOT NULL, `created` TEXT NOT NULL, `modified` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `searches` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `created` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `songs` (`songId` INTEGER NOT NULL, `alias` TEXT NOT NULL, `book` INTEGER NOT NULL, `content` TEXT NOT NULL, `created` TEXT NOT NULL, `liked` INTEGER NOT NULL, `likes` INTEGER NOT NULL, `songNo` INTEGER NOT NULL, `title` TEXT NOT NULL, `views` INTEGER NOT NULL, PRIMARY KEY(`songId`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_songs_songId` ON `songs` (`songId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '8a5e9ad8949d12eba135e9e8908b7070')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `books`")
        connection.execSQL("DROP TABLE IF EXISTS `histories`")
        connection.execSQL("DROP TABLE IF EXISTS `listings`")
        connection.execSQL("DROP TABLE IF EXISTS `searches`")
        connection.execSQL("DROP TABLE IF EXISTS `songs`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsBooks: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBooks.put("bookId", TableInfo.Column("bookId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("bookNo", TableInfo.Column("bookNo", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("created", TableInfo.Column("created", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("enabled", TableInfo.Column("enabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("position", TableInfo.Column("position", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("songs", TableInfo.Column("songs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("subTitle", TableInfo.Column("subTitle", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("title", TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("user", TableInfo.Column("user", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBooks: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBooks: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesBooks.add(TableInfo.Index("index_books_bookId", true, listOf("bookId"), listOf("ASC")))
        val _infoBooks: TableInfo = TableInfo("books", _columnsBooks, _foreignKeysBooks, _indicesBooks)
        val _existingBooks: TableInfo = read(connection, "books")
        if (!_infoBooks.equals(_existingBooks)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |books(com.songlib.core.database.model.BookEntity).
              | Expected:
              |""".trimMargin() + _infoBooks + """
              |
              | Found:
              |""".trimMargin() + _existingBooks)
        }
        val _columnsHistories: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsHistories.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistories.put("song", TableInfo.Column("song", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistories.put("created", TableInfo.Column("created", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysHistories: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesHistories: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoHistories: TableInfo = TableInfo("histories", _columnsHistories, _foreignKeysHistories, _indicesHistories)
        val _existingHistories: TableInfo = read(connection, "histories")
        if (!_infoHistories.equals(_existingHistories)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |histories(com.songlib.core.database.model.HistoryEntity).
              | Expected:
              |""".trimMargin() + _infoHistories + """
              |
              | Found:
              |""".trimMargin() + _existingHistories)
        }
        val _columnsListings: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsListings.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsListings.put("parent", TableInfo.Column("parent", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsListings.put("title", TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsListings.put("song", TableInfo.Column("song", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsListings.put("created", TableInfo.Column("created", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsListings.put("modified", TableInfo.Column("modified", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysListings: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesListings: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoListings: TableInfo = TableInfo("listings", _columnsListings, _foreignKeysListings, _indicesListings)
        val _existingListings: TableInfo = read(connection, "listings")
        if (!_infoListings.equals(_existingListings)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |listings(com.songlib.core.database.model.ListingEntity).
              | Expected:
              |""".trimMargin() + _infoListings + """
              |
              | Found:
              |""".trimMargin() + _existingListings)
        }
        val _columnsSearches: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSearches.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSearches.put("title", TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSearches.put("created", TableInfo.Column("created", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSearches: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSearches: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoSearches: TableInfo = TableInfo("searches", _columnsSearches, _foreignKeysSearches, _indicesSearches)
        val _existingSearches: TableInfo = read(connection, "searches")
        if (!_infoSearches.equals(_existingSearches)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |searches(com.songlib.core.database.model.SearchEntity).
              | Expected:
              |""".trimMargin() + _infoSearches + """
              |
              | Found:
              |""".trimMargin() + _existingSearches)
        }
        val _columnsSongs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSongs.put("songId", TableInfo.Column("songId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSongs.put("alias", TableInfo.Column("alias", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSongs.put("book", TableInfo.Column("book", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSongs.put("content", TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSongs.put("created", TableInfo.Column("created", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSongs.put("liked", TableInfo.Column("liked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSongs.put("likes", TableInfo.Column("likes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSongs.put("songNo", TableInfo.Column("songNo", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSongs.put("title", TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSongs.put("views", TableInfo.Column("views", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSongs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSongs: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesSongs.add(TableInfo.Index("index_songs_songId", true, listOf("songId"), listOf("ASC")))
        val _infoSongs: TableInfo = TableInfo("songs", _columnsSongs, _foreignKeysSongs, _indicesSongs)
        val _existingSongs: TableInfo = read(connection, "songs")
        if (!_infoSongs.equals(_existingSongs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |songs(com.songlib.core.database.model.SongEntity).
              | Expected:
              |""".trimMargin() + _infoSongs + """
              |
              | Found:
              |""".trimMargin() + _existingSongs)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "books", "histories", "listings", "searches", "songs")
  }

  public override fun clearAllTables() {
    super.performClear(false, "books", "histories", "listings", "searches", "songs")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(BookDao::class, BookDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(HistoryDao::class, HistoryDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ListingDao::class, ListingDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(SearchDao::class, SearchDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(SongDao::class, SongDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun booksDao(): BookDao = _bookDao.value

  public override fun historiesDao(): HistoryDao = _historyDao.value

  public override fun listingsDao(): ListingDao = _listingDao.value

  public override fun searchesDao(): SearchDao = _searchDao.value

  public override fun songsDao(): SongDao = _songDao.value
}
