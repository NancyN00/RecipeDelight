package com.nancy.recipedelight.domain.models

data class Meal(
    val id: String,
    val name: String,
    val category: String? = null,
    val area: String? = null,
    val instructions: String? = null,
    val thumb: String? = null,
    val tags: List<String> = emptyList(),
    val youtube: String? = null,
    val ingredients: List<String> = emptyList()
)