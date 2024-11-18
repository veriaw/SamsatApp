package com.veriaw.kriptografiapp.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "users")
@Parcelize
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int =0,

    @ColumnInfo(name = "username")
    var username: String? =null,

    @ColumnInfo(name = "password")
    var password: String? =null,

    @ColumnInfo(name = "email")
    var email: String? =null,

    @ColumnInfo(name = "role")
    var role: String? =null,
):Parcelable
