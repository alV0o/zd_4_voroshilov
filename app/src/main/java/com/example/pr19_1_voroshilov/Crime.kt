package com.example.pr19_1_voroshilov

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class Crime (@PrimaryKey val id: UUID = UUID.randomUUID()){
    var date = Date()
    var title = ""
    var isSolved = false
    var suspect = ""
}