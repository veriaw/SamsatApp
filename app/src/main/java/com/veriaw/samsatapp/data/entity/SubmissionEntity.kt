package com.veriaw.samsatapp.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "submissions")
@Parcelize
data class SubmissionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int =0,

    @ColumnInfo(name = "userid")
    var userid: Int =0,

    @ColumnInfo(name = "username")
    var username: String? =null,

    @ColumnInfo(name = "address")
    var address: String? =null,

    @ColumnInfo(name = "timestamp")
    var timestamp: String? =null,

    @ColumnInfo(name = "email")
    var email: String? =null,

    @ColumnInfo(name = "submissiontype")
    var submissiontype: String? =null,

    @ColumnInfo(name = "extendyear")
    var extendyear: Int? =null,

    @ColumnInfo(name = "photo")
    var photo: String? =null,

    @ColumnInfo(name = "file")
    var file: String? =null,

    @ColumnInfo(name = "status")
    var status: String? =null,

    @ColumnInfo(name = "price")
    var price: Int? =null,

    @ColumnInfo(name = "payment")
    var payment: String? =null,
): Parcelable