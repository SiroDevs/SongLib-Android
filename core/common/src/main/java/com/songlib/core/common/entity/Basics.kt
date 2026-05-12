package com.songlib.core.common.entity

data class Selectable<T>(
    val data: T,
    val isSelected: Boolean = false
)
