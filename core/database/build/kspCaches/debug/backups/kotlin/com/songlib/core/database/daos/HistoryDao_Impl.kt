package com.songlib.core.database.daos

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performBlocking
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.songlib.core.database.model.HistoryEntity
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
public class HistoryDao_Impl(
  __db: RoomDatabase,
) : HistoryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfHistoryEntity: EntityInsertAdapter<HistoryEntity>

  private val __deleteAdapterOfHistoryEntity: EntityDeleteOrUpdateAdapter<HistoryEntity>

  private val __updateAdapterOfHistoryEntity: EntityDeleteOrUpdateAdapter<HistoryEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfHistoryEntity = object : EntityInsertAdapter<HistoryEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `histories` (`id`,`song`,`created`) VALUES (nullif(?, 0),?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: HistoryEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.song.toLong())
        statement.bindText(3, entity.created)
      }
    }
    this.__deleteAdapterOfHistoryEntity = object : EntityDeleteOrUpdateAdapter<HistoryEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `histories` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: HistoryEntity) {
        statement.bindLong(1, entity.id.toLong())
      }
    }
    this.__updateAdapterOfHistoryEntity = object : EntityDeleteOrUpdateAdapter<HistoryEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `histories` SET `id` = ?,`song` = ?,`created` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: HistoryEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.song.toLong())
        statement.bindText(3, entity.created)
        statement.bindLong(4, entity.id.toLong())
      }
    }
  }

  public override suspend fun insert(history: HistoryEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfHistoryEntity.insert(_connection, history)
  }

  public override fun delete(history: HistoryEntity): Unit = performBlocking(__db, false, true) { _connection ->
    __deleteAdapterOfHistoryEntity.handle(_connection, history)
  }

  public override fun update(history: HistoryEntity): Unit = performBlocking(__db, false, true) { _connection ->
    __updateAdapterOfHistoryEntity.handle(_connection, history)
  }

  public override fun getAll(): List<HistoryEntity> {
    val _sql: String = "SELECT * FROM histories"
    return performBlocking(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSong: Int = getColumnIndexOrThrow(_stmt, "song")
        val _columnIndexOfCreated: Int = getColumnIndexOrThrow(_stmt, "created")
        val _result: MutableList<HistoryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: HistoryEntity
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpSong: Int
          _tmpSong = _stmt.getLong(_columnIndexOfSong).toInt()
          val _tmpCreated: String
          _tmpCreated = _stmt.getText(_columnIndexOfCreated)
          _item = HistoryEntity(_tmpId,_tmpSong,_tmpCreated)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: Int) {
    val _sql: String = "DELETE FROM histories WHERE id = ?"
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
    val _sql: String = "DELETE FROM histories"
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
