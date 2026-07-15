package com.songlib.core.network.dtos

import androidx.annotation.Keep

@Keep
data class PaginationMeta(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int,
    val hasMore: Boolean
)

@Keep
data class PagedSongsResponse(
    val data: List<com.songlib.core.database.model.SongEntity>,
    val pagination: PaginationMeta
)

@Keep
data class UserDto(
    val userId: Int = 0,
    val username: String,
    val email: String,
    val name: String? = null,
    val photoUrl: String? = null,
    val googleId: String? = null,
    val selectedBooks: String? = null,
    val role: String = "user",
    val created: String? = null,
    val updated: String? = null
)

@Keep
data class DraftDto(
    val draftId: Int = 0,
    val title: String,
    val content: String? = null,
    val songNo: Int? = null,
    val book: Int? = null,
    val userId: Int = 0,
    val created: String? = null,
    val updated: String? = null
)

@Keep
data class EditDto(
    val editId: Int = 0,
    val songId: Int,
    val book: Int = 0,
    val songNo: Int = 0,
    val title: String,
    val alias: String? = null,
    val content: String? = null,
    val userId: Int = 0,
    val status: String = "pending",
    val created: String? = null,
    val updated: String? = null
)

/** Body for admin approve/reject — reject carries an optional reason. */
@Keep
data class EditRejectRequest(val reason: String? = null)

/** Thin wrapper the approve/reject endpoints return. */
@Keep
data class EditActionResponse(
    val message: String,
    val edit: EditDto
)

@Keep
data class SongReportRequest(
    val songId: Int,
    val bookId: Int,
    val songNo: Int,
    val songTitle: String,
    val reportType: String,
    val description: String,
    val reportedBy: String? = null
)

@Keep
data class SongReportResponse(
    val message: String,
    val reportId: Int
)

@Keep
data class LikeToggleRequest(val userId: Int, val songId: Int)
@Keep
data class LikeToggleResponse(val userId: Int, val songId: Int, val liked: Boolean)
@Keep
data class LikedSongsResponse(val userId: Int, val likedSongIds: List<Int>)

@Keep
data class OrganisationDto(
    val orgId: Int = 0,
    val title: String,
    val description: String? = null,
    val userId: Int = 0,
    val created: String? = null
)

@Keep
data class ListingDto(
    val listingId: Int = 0,
    val title: String,
    val songs: List<Int> = emptyList(),
    val userId: Int = 0,
    val created: String? = null
)
