package com.songlib.core.database.daos

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performBlocking
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.songlib.core.database.model.BookEntity
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
public class BookDao_Impl(
  __db: RoomDatabase,
) : BookDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBookEntity: EntityInsertAdapter<BookEntity>

  private val __deleteAdapterOfBookEntity: EntityDeleteOrUpdateAdapter<BookEntity>

  private val __updateAdapterOfBookEntity: EntityDeleteOrUpdateAdapter<BookEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfBookEntity = object : EntityInsertAdapter<BookEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `books` (`bookId`,`bookNo`,`created`,`enabled`,`position`,`songs`,`subTitle`,`title`,`user`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BookEntity) {
        statement.bindLong(1, entity.bookId.toLong())
        statement.bindLong(2, entity.bookNo.toLong())
        statement.bindText(3, entity.created)
        val _tmp: Int = if (entity.enabled) 1 else 0
        statement.bindLong(4, _tmp.toLong())
        statement.bindLong(5, entity.position.toLong())
        statement.bindLong(6, entity.songs.toLong())
        statement.bindText(7, entity.subTitle)
        statement.bindText(8, entity.title)
        statement.bindLong(9, entity.user.toLong())
      }
    }
    this.__deleteAdapterOfBookEntity = object : EntityDeleteOrUpdateAdapter<BookEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `books` WHERE `bookId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: BookEntity) {
        statement.bindLong(1, entity.bookId.toLong())
      }
    }
    this.__updateAdapterOfBookEntity = object : EntityDeleteOrUpdateAdapter<BookEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `books` SET `bookId` = ?,`bookNo` = ?,`created` = ?,`enabled` = ?,`position` = ?,`songs` = ?,`subTitle` = ?,`title` = ?,`user` = ? WHERE `bookId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: BookEntity) {
        statement.bindLong(1, entity.bookId.toLong())
        statement.bindLong(2, entity.bookNo.toLong())
        statement.bindText(3, entity.created)
        val _tmp: Int = if (entity.enabled) 1 else 0
        statement.bindLong(4, _tmp.toLong())
        statement.bindLong(5, entity.position.toLong())
        statement.bindLong(6, entity.songs.toLong())
        statement.bindText(7, entity.subTitle)
        statement.bindText(8, entity.title)
        statement.bindLong(9, entity.user.toLong())
        statement.bindLong(10, entity.bookId.toLong())
      }
    }
  }

  public override suspend fun insert(book: BookEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfBookEntity.insert(_connection, book)
  }

  public override suspend fun insertAll(books: List<BookEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfBookEntity.insert(_connection, books)
  }

  public override fun delete(book: BookEntity): Unit = performBlocking(__db, false, true) { _connection ->
    __deleteAdapterOfBookEntity.handle(_connection, book)
  }

  public override fun update(book: BookEntity): Unit = performBlocking(__db, false, true) { _connection ->
    __updateAdapterOfBookEntity.handle(_connection, book)
  }

  public override fun getAll(): List<BookEntity> {
    val _sql: String = "SELECT * FROM books"
    return performBlocking(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfBookId: Int = getColumnIndexOrThrow(_stmt, "bookId")
        val _columnIndexOfBookNo: Int = getColumnIndexOrThrow(_stmt, "bookNo")
        val _columnIndexOfCreated: Int = getColumnIndexOrThrow(_stmt, "created")
        val _columnIndexOfEnabled: Int = getColumnIndexOrThrow(_stmt, "enabled")
        val _columnIndexOfPosition: Int = getColumnIndexOrThrow(_stmt, "position")
        val _columnIndexOfSongs: Int = getColumnIndexOrThrow(_stmt, "songs")
        val _columnIndexOfSubTitle: Int = getColumnIndexOrThrow(_stmt, "subTitle")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfUser: Int = getColumnIndexOrThrow(_stmt, "user")
        val _result: MutableList<BookEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BookEntity
          val _tmpBookId: Int
          _tmpBookId = _stmt.getLong(_columnIndexOfBookId).toInt()
          val _tmpBookNo: Int
          _tmpBookNo = _stmt.getLong(_columnIndexOfBookNo).toInt()
          val _tmpCreated: String
          _tmpCreated = _stmt.getText(_columnIndexOfCreated)
          val _tmpEnabled: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfEnabled).toInt()
          _tmpEnabled = _tmp != 0
          val _tmpPosition: Int
          _tmpPosition = _stmt.getLong(_columnIndexOfPosition).toInt()
          val _tmpSongs: Int
          _tmpSongs = _stmt.getLong(_columnIndexOfSongs).toInt()
          val _tmpSubTitle: String
          _tmpSubTitle = _stmt.getText(_columnIndexOfSubTitle)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpUser: Int
          _tmpUser = _stmt.getLong(_columnIndexOfUser).toInt()
          _item = BookEntity(_tmpBookId,_tmpBookNo,_tmpCreated,_tmpEnabled,_tmpPosition,_tmpSongs,_tmpSubTitle,_tmpTitle,_tmpUser)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: Int) {
    val _sql: String = "DELETE FROM books WHERE bookId = ?"
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
    val _sql: String = "DELETE FROM books"
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
