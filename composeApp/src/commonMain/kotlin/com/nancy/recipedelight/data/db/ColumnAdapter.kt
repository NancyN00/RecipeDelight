package com.nancy.recipedelight.data.db

import app.cash.sqldelight.ColumnAdapter

val listAdapter = object : ColumnAdapter<List<String>, String> {
    override fun decode(databaseValue: String): List<String> =
        if (databaseValue.isEmpty()) emptyList() else databaseValue.split(",")

    override fun encode(value: List<String>): String =
        value.joinToString(separator = ",")
}