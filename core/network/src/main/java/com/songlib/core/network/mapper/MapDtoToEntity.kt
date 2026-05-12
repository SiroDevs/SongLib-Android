package com.songlib.core.network.mapper

import com.songlib.core.database.model.IdiomEntity
import com.songlib.core.database.model.ProverbEntity
import com.songlib.core.database.model.SayingEntity
import com.songlib.core.database.model.WordEntity
import com.songlib.core.network.dtos.IdiomDto
import com.songlib.core.network.dtos.ProverbDto
import com.songlib.core.network.dtos.SayingDto
import com.songlib.core.network.dtos.WordDto

object MapDtoToEntity {
    fun mapToEntity(entity: IdiomDto): IdiomEntity {
        return IdiomEntity(
            rid = entity.rid,
            title = entity.title,
            meaning = entity.meaning,
            views = entity.views,
            likes = entity.likes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }

    fun mapToEntity(entity: ProverbDto): ProverbEntity {
        return ProverbEntity(
            rid = entity.rid,
            title = entity.title,
            synonyms = entity.synonyms,
            meaning = entity.meaning,
            conjugation = entity.conjugation,
            views = entity.views,
            likes = entity.likes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }

    fun mapToEntity(entity: SayingDto): SayingEntity {
        return SayingEntity(
            rid = entity.rid,
            title = entity.title,
            meaning = entity.meaning,
            views = entity.views,
            likes = entity.likes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }

    fun mapToEntity(entity: WordDto): WordEntity {
        return WordEntity(
            rid = entity.rid,
            title = entity.title,
            synonyms = entity.synonyms,
            meaning = entity.meaning,
            conjugation = entity.conjugation,
            views = entity.views,
            likes = entity.likes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }
}