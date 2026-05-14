package com.songlib.core.database.daos

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performBlocking
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.songlib.core.database.model.SongEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
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
public class SongDao_Impl(
  __db: RoomDatabase,
) : SongDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSongEntity: EntityInsertAdapter<SongEntity>

  private val __deleteAdapterOfSongEntity: EntityDeleteOrUpdateAdapter<SongEntity>

  private val __updateAdapterOfSongEntity: EntityDeleteOrUpdateAdapter<SongEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfSongEntity = object : EntityInsertAdapter<SongEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `songs` (`songId`,`alias`,`book`,`content`,`created`,`liked`,`likes`,`songNo`,`title`,`views`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SongEntity) {
        statement.bindLong(1, entity.songId.toLong())
        statement.bindText(2, entity.alias)
        statement.bindLong(3, entity.book.toLong())
        statement.bindText(4, entity.content)
        statement.bindText(5, entity.created)
        val _tmp: Int = if (entity.liked) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        statement.bindLong(7, entity.likes.toLong())
        statement.bindLong(8, entity.songNo.toLong())
        statement.bindText(9, entity.title)
        statement.bindLong(10, entity.views.toLong())
      }
    }
    this.__deleteAdapterOfSongEntity = object : EntityDeleteOrUpdateAdapter<SongEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `songs` WHERE `songId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: SongEntity) {
        statement.bindLong(1, entity.songId.toLong())
      }
    }
    this.__updateAdapterOfSongEntity = object : EntityDeleteOrUpdateAdapter<SongEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `songs` SET `songId` = ?,`alias` = ?,`book` = ?,`content` = ?,`created` = ?,`liked` = ?,`likes` = ?,`songNo` = ?,`title` = ?,`views` = ? WHERE `songId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: SongEntity) {
        statement.bindLong(1, entity.songId.toLong())
        statement.bindText(2, entity.alias)
        statement.bindLong(3, entity.book.toLong())
        statement.bindText(4, entity.content)
        statement.bindText(5, entity.created)
        val _tmp: Int = if (entity.liked) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        statement.bindLong(7, entity.likes.toLong())
        statement.bindLong(8, entity.songNo.toLong())
        statement.bindText(9, entity.title)
        statement.bindLong(10, entity.views.toLong())
        statement.bindLong(11, entity.songId.toLong())
      }
    }
  }

  public override suspend fun insert(song: SongEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSongEntity.insert(_connection, song)
  }

  public override suspend fun insertAll(songs: List<SongEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSongEntity.insert(_connection, songs)
  }

  public override fun delete(song: SongEntity): Unit = performBlocking(__db, false, true) { _connection ->
    __deleteAdapterOfSongEntity.handle(_connection, song)
  }

  public override fun update(song: SongEntity): Unit = performBlocking(__db, false, true) { _connection ->
    __updateAdapterOfSongEntity.handle(_connection, song)
  }

  public override fun getAll(): List<SongEntity> {
    val _sql: String = "SELECT * FROM songs"
    return performBlocking(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfSongId: Int = getColumnIndexOrThrow(_stmt, "songId")
        val _columnIndexOfAlias: Int = getColumnIndexOrThrow(_stmt, "alias")
        val _columnIndexOfBook: Int = getColumnIndexOrThrow(_stmt, "book")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfCreated: Int = getColumnIndexOrThrow(_stmt, "created")
        val _columnIndexOfLiked: Int = getColumnIndexOrThrow(_stmt, "liked")
        val _columnIndexOfLikes: Int = getColumnIndexOrThrow(_stmt, "likes")
        val _columnIndexOfSongNo: Int = getColumnIndexOrThrow(_stmt, "songNo")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfViews: Int = getColumnIndexOrThrow(_stmt, "views")
        val _result: MutableList<SongEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SongEntity
          val _tmpSongId: Int
          _tmpSongId = _stmt.getLong(_columnIndexOfSongId).toInt()
          val _tmpAlias: String
          _tmpAlias = _stmt.getText(_columnIndexOfAlias)
          val _tmpBook: Int
          _tmpBook = _stmt.getLong(_columnIndexOfBook).toInt()
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpCreated: String
          _tmpCreated = _stmt.getText(_columnIndexOfCreated)
          val _tmpLiked: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfLiked).toInt()
          _tmpLiked = _tmp != 0
          val _tmpLikes: Int
          _tmpLikes = _stmt.getLong(_columnIndexOfLikes).toInt()
          val _tmpSongNo: Int
          _tmpSongNo = _stmt.getLong(_columnIndexOfSongNo).toInt()
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpViews: Int
          _tmpViews = _stmt.getLong(_columnIndexOfViews).toInt()
          _item = SongEntity(_tmpSongId,_tmpAlias,_tmpBook,_tmpContent,_tmpCreated,_tmpLiked,_tmpLikes,_tmpSongNo,_tmpTitle,_tmpViews)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getSong(songId: Int): SongEntity {
    val _sql: String = "SELECT * FROM songs WHERE songId = ?"
    return performBlocking(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, songId.toLong())
        val _columnIndexOfSongId: Int = getColumnIndexOrThrow(_stmt, "songId")
        val _columnIndexOfAlias: Int = getColumnIndexOrThrow(_stmt, "alias")
        val _columnIndexOfBook: Int = getColumnIndexOrThrow(_stmt, "book")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfCreated: Int = getColumnIndexOrThrow(_stmt, "created")
        val _columnIndexOfLiked: Int = getColumnIndexOrThrow(_stmt, "liked")
        val _columnIndexOfLikes: Int = getColumnIndexOrThrow(_stmt, "likes")
        val _columnIndexOfSongNo: Int = getColumnIndexOrThrow(_stmt, "songNo")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfViews: Int = getColumnIndexOrThrow(_stmt, "views")
        val _result: SongEntity
        if (_stmt.step()) {
          val _tmpSongId: Int
          _tmpSongId = _stmt.getLong(_columnIndexOfSongId).toInt()
          val _tmpAlias: String
          _tmpAlias = _stmt.getText(_columnIndexOfAlias)
          val _tmpBook: Int
          _tmpBook = _stmt.getLong(_columnIndexOfBook).toInt()
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpCreated: String
          _tmpCreated = _stmt.getText(_columnIndexOfCreated)
          val _tmpLiked: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfLiked).toInt()
          _tmpLiked = _tmp != 0
          val _tmpLikes: Int
          _tmpLikes = _stmt.getLong(_columnIndexOfLikes).toInt()
          val _tmpSongNo: Int
          _tmpSongNo = _stmt.getLong(_columnIndexOfSongNo).toInt()
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpViews: Int
          _tmpViews = _stmt.getLong(_columnIndexOfViews).toInt()
          _result = SongEntity(_tmpSongId,_tmpAlias,_tmpBook,_tmpContent,_tmpCreated,_tmpLiked,_tmpLikes,_tmpSongNo,_tmpTitle,_tmpViews)
        } else {
          error("The query result was empty, but expected a single row to return a NON-NULL object of type 'com.songlib.core.database.model.SongEntity'.")
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: Int) {
    val _sql: String = "DELETE FROM songs WHERE book = ?"
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
    val _sql: String = "DELETE FROM songs"
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
