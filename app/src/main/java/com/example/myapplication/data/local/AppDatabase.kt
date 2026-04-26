package com.example.myapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myapplication.data.local.dao.OrderDao
import com.example.myapplication.data.local.dao.ProductDao
import com.example.myapplication.data.local.entity.OrderEntity
import com.example.myapplication.data.local.entity.ProductEntity

@Database(entities = [ProductEntity::class, OrderEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
}
