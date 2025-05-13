package com.songlib.data.models

class BookModel : ArrayList<BookModelItem>()

data class BookModelItem(
    val bookId: Int,
    val bookNo: Int,
    val created: String,
    val enabled: Boolean,
    val position: Int,
    val songs: Int,
    val subTitle: String,
    val title: String,
    val user: Int
)