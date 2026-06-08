package com.songlib.core.network.dtos

// ── Paginated wrapper ─────────────────────────────────────────────────────────
data class PaginationMeta(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int,
    val hasMore: Boolean
)

data class PagedSongsResponse(
    val data: List<com.songlib.core.database.model.SongEntity>,
    val pagination: PaginationMeta
)

// ── User ─────────────────────────────────────────────────────────────────────
data class UserDto(
    val userId: Int = 0,
    val username: String,
    val email: String,
    val name: String? = null,
    val photoUrl: String? = null,
    val googleId: String? = null,
    val selectedBooks: String? = null,
    val created: String? = null,
    val updated: String? = null
)

// ── Draft ─────────────────────────────────────────────────────────────────────
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

// ── Edit ─────────────────────────────────────────────────────────────────────
data class EditDto(
    val editId: Int = 0,
    val songId: Int,
    val title: String,
    val content: String? = null,
    val userId: Int = 0,
    val status: String = "pending",
    val created: String? = null,
    val updated: String? = null
)

// ── Report ────────────────────────────────────────────────────────────────────
data class SongReportRequest(
    val songId: Int,
    val bookId: Int,
    val songNo: Int,
    val songTitle: String,
    val reportType: String,
    val description: String,
    val reportedBy: String? = null
)

data class SongReportResponse(
    val message: String,
    val reportId: Int
)

// ── Like toggle ───────────────────────────────────────────────────────────────
data class LikeToggleRequest(val userId: Int, val songId: Int)
data class LikeToggleResponse(val userId: Int, val songId: Int, val liked: Boolean)
data class LikedSongsResponse(val userId: Int, val likedSongIds: List<Int>)

// ── Organisation ──────────────────────────────────────────────────────────────
data class OrganisationDto(
    val orgId: Int = 0,
    val title: String,
    val description: String? = null,
    val userId: Int = 0,
    val created: String? = null
)

// ── Listing ───────────────────────────────────────────────────────────────────
data class ListingDto(
    val listingId: Int = 0,
    val title: String,
    val songs: List<Int> = emptyList(),
    val userId: Int = 0,
    val created: String? = null
)
