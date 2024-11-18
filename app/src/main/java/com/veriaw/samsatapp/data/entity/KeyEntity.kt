package com.veriaw.samsatapp.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "keys")
@Parcelize
data class KeyEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int =0,

    @ColumnInfo(name = "submissionid")
    var submissionid: Int? =null,

    @ColumnInfo(name = "key")
    var key: String? =null,
): Parcelable