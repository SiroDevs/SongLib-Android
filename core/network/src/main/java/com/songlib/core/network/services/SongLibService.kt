package com.songlib.core.network.services

import androidx.annotation.Keep
import com.songlib.core.common.utils.ApiConstants
import com.songlib.core.database.model.BookEntity
import com.songlib.core.database.model.SongEntity
import com.songlib.core.network.dtos.DraftDto
import com.songlib.core.network.dtos.EditDto
import com.songlib.core.network.dtos.LikeToggleRequest
import com.songlib.core.network.dtos.LikeToggleResponse
import com.songlib.core.network.dtos.LikedSongsResponse
import com.songlib.core.network.dtos.ListingDto
import com.songlib.core.network.dtos.OrganisationDto
import com.songlib.core.network.dtos.PagedSongsResponse
import com.songlib.core.network.dtos.SongReportRequest
import com.songlib.core.network.dtos.SongReportResponse
import com.songlib.core.network.dtos.UserDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

@Keep
interface SongLibService {
    @GET(ApiConstants.BOOKS)
    suspend fun getBooks(): List<BookEntity>

    @GET("${ApiConstants.BOOKS}/{ids}")
    suspend fun getBooksByIds(@Path("ids") ids: String): List<BookEntity>

    @GET("${ApiConstants.SONGS}/books/{bookIds}")
    suspend fun getSongsPage(
        @Path("bookIds") bookIds: String,
        @Query("page")   page: Int = 1,
        @Query("limit")  limit: Int = 500,
        @Query("since")  since: String? = null
    ): PagedSongsResponse

    @GET("${ApiConstants.SONGS}/{songId}")
    suspend fun getSongById(@Path("songId") songId: Int): SongEntity

    // ── Drafts ────────────────────────────────────────────────────────────
    @GET(ApiConstants.DRAFTS)
    suspend fun getDrafts(): List<DraftDto>

    @GET("${ApiConstants.DRAFTS}/{draftId}")
    suspend fun getDraft(@Path("draftId") draftId: Int): DraftDto

    @POST(ApiConstants.DRAFTS)
    suspend fun createDraft(@Body draft: DraftDto): DraftDto

    @PUT("${ApiConstants.DRAFTS}/{draftId}")
    suspend fun updateDraft(@Path("draftId") draftId: Int, @Body draft: DraftDto): DraftDto

    @DELETE("${ApiConstants.DRAFTS}/{draftId}")
    suspend fun deleteDraft(@Path("draftId") draftId: Int): Map<String, String>

    // ── Edits ─────────────────────────────────────────────────────────────
    @GET(ApiConstants.EDITS)
    suspend fun getEdits(): List<EditDto>

    @GET("${ApiConstants.EDITS}/{editId}")
    suspend fun getEdit(@Path("editId") editId: Int): EditDto

    @POST(ApiConstants.EDITS)
    suspend fun createEdit(@Body edit: EditDto): EditDto

    @PUT("${ApiConstants.EDITS}/{editId}")
    suspend fun updateEdit(@Path("editId") editId: Int, @Body edit: EditDto): EditDto

    @DELETE("${ApiConstants.EDITS}/{editId}")
    suspend fun deleteEdit(@Path("editId") editId: Int): Map<String, String>

    // ── Reports ───────────────────────────────────────────────────────────
    @POST(ApiConstants.REPORTS)
    suspend fun submitReport(@Body report: SongReportRequest): SongReportResponse

    // ── Users ─────────────────────────────────────────────────────────────
    @GET("${ApiConstants.USERS}/{userId}")
    suspend fun getUser(@Path("userId") userId: Int): UserDto

    @POST(ApiConstants.USERS)
    suspend fun createUser(@Body user: UserDto): UserDto

    @PUT("${ApiConstants.USERS}/{userId}")
    suspend fun updateUser(@Path("userId") userId: Int, @Body user: UserDto): UserDto

    // ── Organisations ─────────────────────────────────────────────────────
    @GET(ApiConstants.ORGANISATIONS)
    suspend fun getOrganisations(): List<OrganisationDto>

    @GET("${ApiConstants.ORGANISATIONS}/{orgId}")
    suspend fun getOrganisation(@Path("orgId") orgId: Int): OrganisationDto

    // ── Listings ──────────────────────────────────────────────────────────
    @GET(ApiConstants.LISTINGS)
    suspend fun getRemoteListings(): List<ListingDto>

    @POST(ApiConstants.LISTINGS)
    suspend fun createRemoteListing(@Body listing: ListingDto): ListingDto

    @PUT("${ApiConstants.LISTINGS}/{listingId}")
    suspend fun updateRemoteListing(
        @Path("listingId") listingId: Int,
        @Body listing: ListingDto
    ): ListingDto

    // ── Likes ─────────────────────────────────────────────────────────────
    @POST(ApiConstants.LIKES_TOGGLE)
    suspend fun toggleLike(@Body body: LikeToggleRequest): LikeToggleResponse

    @GET("${ApiConstants.LIKES_USER}/{userId}")
    suspend fun getLikedSongs(@Path("userId") userId: Int): LikedSongsResponse
}
