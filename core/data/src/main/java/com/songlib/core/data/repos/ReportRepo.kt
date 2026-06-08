package com.songlib.core.data.repos

import com.songlib.core.network.dtos.SongReportRequest
import com.songlib.core.network.dtos.SongReportResponse
import com.songlib.core.network.services.SongLibService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepo @Inject constructor(
    private val service: SongLibService
) {
    suspend fun submitReport(request: SongReportRequest): SongReportResponse =
        service.submitReport(request)
}
