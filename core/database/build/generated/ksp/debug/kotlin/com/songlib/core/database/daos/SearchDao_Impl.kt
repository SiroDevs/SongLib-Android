package com.songlib.core.database.daos

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performBlocking
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.songlib.core.database.model.SearchEntity
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
public class SearchDao_Impl(
  __db: RoomDatabase,
) : SearchDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSearchEntity: EntityInsertAdapter<SearchEntity>

  private val __deleteAdapterOfSearchEntity: EntityDeleteOrUpdateAdapter<SearchEntity>

  private val __updateAdapterOfSearchEntity: EntityDeleteOrUpdateAdapter<SearchEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfSearchEntity = object : EntityInsertAdapter<SearchEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `searches` (`id`,`title`,`created`) VALUES (nullif(?, 0),?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SearchEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.created)
      }
    }
    this.__deleteAdapterOfSearchEntity = object : EntityDeleteOrUpdateAdapter<SearchEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `searches` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: SearchEntity) {
        statement.bindLong(1, entity.id.toLong())
      }
    }
    this.__updateAdapterOfSearchEntity = object : EntityDeleteOrUpdateAdapter<SearchEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `searches` SET `id` = ?,`title` = ?,`created` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: SearchEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.created)
        statement.bindLong(4, entity.id.toLong())
      }
    }
  }

  public override suspend fun insert(search: SearchEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSearchEntity.insert(_connection, search)
  }

  public override fun delete(search: SearchEntity): Unit = performBlocking(__db, false, true) { _connection ->
    __deleteAdapterOfSearchEntity.handle(_connection, search)
  }

  public override fun update(search: SearchEntity): Unit = performBlocking(__db, false, true) { _connection ->
    __updateAdapterOfSearchEntity.handle(_connection, search)
  }

  public override fun getAll(): List<SearchEntity> {
    val _sql: String = "SELECT * FROM searches"
    return performBlocking(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfCreated: Int = getColumnIndexOrThrow(_stmt, "created")
        val _result: MutableList<SearchEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SearchEntity
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpCreated: String
          _tmpCreated = _stmt.getText(_columnIndexOfCreated)
          _item = SearchEntity(_tmpId,_tmpTitle,_tmpCreated)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: Int) {
    val _sql: String = "DELETE FROM searches WHERE id = ?"
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
    val _sql: String = "DELETE FROM searches"
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
