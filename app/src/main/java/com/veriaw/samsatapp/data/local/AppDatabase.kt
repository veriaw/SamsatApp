package com.veriaw.kriptografiapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.veriaw.kriptografiapp.data.entity.UserEntity
import com.veriaw.samsatapp.data.entity.KeyEntity
import com.veriaw.samsatapp.data.entity.SubmissionEntity
import com.veriaw.samsatapp.data.local.KeyDao
import com.veriaw.samsatapp.data.local.SubmissionDao

@Database(entities = [UserEntity::class,SubmissionEntity::class,KeyEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun submissionDao(): SubmissionDao
    abstract fun keyDao(): KeyDao

    companion object{
        @Volatile
        private var instance: AppDatabase? = null
        fun getInstance(context: Context):AppDatabase =
            instance?: synchronized(this){
                instance?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,"kripto.db"
                ).build()
            }
    }
}