package com.songlib.data.models

data class Song(
    val __v: Int,
    val alias: String,
    val book: Int,
    val content: String,
    val created: String,
    val liked: Boolean,
    val likes: Int,
    val songId: Int,
    val songNo: Int,
    val title: String,
    val views: Int
)

data class SongResp(
    val count: Int,
    val `data`: List<Song>
)