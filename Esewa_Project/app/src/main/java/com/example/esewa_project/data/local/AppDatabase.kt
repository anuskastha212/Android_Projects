package com.example.esewa_project.data.local

import android.content.Context
import android.icu.text.RelativeDateTimeFormatter
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.esewa_project.data.local.dao.CartDao
import com.example.esewa_project.data.local.dao.FavouriteDao
import com.example.esewa_project.data.local.dao.ProductDao
import com.example.esewa_project.data.local.entity.CartEntity
import com.example.esewa_project.data.local.entity.FavouriteEntity
import com.example.esewa_project.data.local.entity.ProductEntity

@Database(
    entities = [
        ProductEntity::class,
        CartEntity::class,
        FavouriteEntity::class],
    version = 2,
    exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun productDao(): ProductDao
    abstract fun favouriteDao(): FavouriteDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase?=null

        fun getDatabase(context: Context): AppDatabase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "esewa_market_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}