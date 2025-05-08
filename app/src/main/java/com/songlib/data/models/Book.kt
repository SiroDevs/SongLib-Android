package com.songlib.data.models

data class Book(
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

data class BookResp(
    val count: Int,
    val `data`: List<Book>
)
