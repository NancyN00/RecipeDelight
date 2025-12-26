package com.nancy.recipedelight.domain.models

data class Category(
    val id: String,
    val name: String,
    val thumb: String? = null,
    val description: String? = null
)