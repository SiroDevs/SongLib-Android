package com.songlib.core.database.daos

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performBlocking
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.songlib.core.database.model.ListingEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ListingDao_Impl(
  __db: RoomDatabase,
) : ListingDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfListingEntity: EntityInsertAdapter<ListingEntity>

  private val __deleteAdapterOfListingEntity: EntityDeleteOrUpdateAdapter<ListingEntity>

  private val __updateAdapterOfListingEntity: EntityDeleteOrUpdateAdapter<ListingEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfListingEntity = object : EntityInsertAdapter<ListingEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `listings` (`id`,`parent`,`title`,`song`,`created`,`modified`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ListingEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.parent.toLong())
        statement.bindText(3, entity.title)
        statement.bindLong(4, entity.song.toLong())
        statement.bindText(5, entity.created)
        statement.bindText(6, entity.modified)
      }
    }
    this.__deleteAdapterOfListingEntity = object : EntityDeleteOrUpdateAdapter<ListingEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `listings` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ListingEntity) {
        statement.bindLong(1, entity.id.toLong())
      }
    }
    this.__updateAdapterOfListingEntity = object : EntityDeleteOrUpdateAdapter<ListingEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `listings` SET `id` = ?,`parent` = ?,`title` = ?,`song` = ?,`created` = ?,`modified` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ListingEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.parent.toLong())
        statement.bindText(3, entity.title)
        statement.bindLong(4, entity.song.toLong())
        statement.bindText(5, entity.created)
        statement.bindText(6, entity.modified)
        statement.bindLong(7, entity.id.toLong())
      }
    }
  }

  public override suspend fun insert(listing: ListingEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfListingEntity.insert(_connection, listing)
  }

  public override fun delete(listing: ListingEntity): Unit = performBlocking(__db, false, true) { _connection ->
    __deleteAdapterOfListingEntity.handle(_connection, listing)
  }

  public override fun update(listing: ListingEntity): Unit = performBlocking(__db, false, true) { _connection ->
    __updateAdapterOfListingEntity.handle(_connection, listing)
  }

  public override fun getAll(parent: Int): List<ListingEntity> {
    val _sql: String = "SELECT * FROM listings WHERE parent = ? ORDER BY modified DESC"
    return performBlocking(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, parent.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfParent: Int = getColumnIndexOrThrow(_stmt, "parent")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfSong: Int = getColumnIndexOrThrow(_stmt, "song")
        val _columnIndexOfCreated: Int = getColumnIndexOrThrow(_stmt, "created")
        val _columnIndexOfModified: Int = getColumnIndexOrThrow(_stmt, "modified")
        val _result: MutableList<ListingEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ListingEntity
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpParent: Int
          _tmpParent = _stmt.getLong(_columnIndexOfParent).toInt()
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpSong: Int
          _tmpSong = _stmt.getLong(_columnIndexOfSong).toInt()
          val _tmpCreated: String
          _tmpCreated = _stmt.getText(_columnIndexOfCreated)
          val _tmpModified: String
          _tmpModified = _stmt.getText(_columnIndexOfModified)
          _item = ListingEntity(_tmpId,_tmpParent,_tmpTitle,_tmpSong,_tmpCreated,_tmpModified)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countSongs(parentId: Int): Int {
    val _sql: String = "SELECT COUNT(*) FROM listings WHERE parent = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, parentId.toLong())
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: Int) {
    val _sql: String = "DELETE FROM listings WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id.toLong())
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAll() {
    val _sql: String = "DELETE FROM listings"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
