package com.songlib.data.sample

import com.songlib.data.models.Book
import com.songlib.domain.entities.Selectable

val SampleBooks = listOf(
    Book(
        bookId = 1,
        user = 1,
        title = "Songs of Worship",
        subTitle = "worship",
        songs = 750,
        position = 1,
        bookNo = 1,
        enabled = true,
        created = ""
    ),
    Book(
        bookId = 2,
        user = 1,
        title = "Nyimbo za Injili",
        subTitle = "injili",
        songs = 213,
        position = 2,
        bookNo = 2,
        enabled = true,
        created = ""
    ),
    Book(
        bookId = 3,
        user = 1,
        title = "Redemption Songs",
        subTitle = "redemption",
        songs = 712,
        position = 3,
        bookNo = 3,
        enabled = true,
        created = ""
    )
)

val SampleSelectableBooks = listOf(
    Selectable(
        Book(
            bookId = 1,
            bookNo = 1,
            created = "",
            enabled = true,
            position = 1,
            songs = 750,
            subTitle = "worship",
            title = "Songs of Worship",
            user = 1
        ),
    ),
    Selectable(
        Book(
            bookId = 2,
            bookNo = 2,
            created = "",
            enabled = true,
            position = 2,
            songs = 220,
            subTitle = "injili",
            title = "Nyimbo za Injili",
            user = 1
        ),
        isSelected = true
    ),
    Selectable(
        Book(
            bookId = 3,
            bookNo = 3,
            created = "",
            enabled = true,
            position = 2,
            songs = 600,
            subTitle = "kikuyu",
            title = "Nyimbo cia Kunira Ngai",
            user = 1
        ),
        isSelected = true
    ),
    Selectable(
        Book(
            bookId = 4,
            bookNo = 4,
            created = "",
            enabled = true,
            position = 4,
            songs = 200,
            subTitle = "gusii",
            title = "Amatero Y'enchiri",
            user = 1
        ),
        isSelected = false
    ),
)