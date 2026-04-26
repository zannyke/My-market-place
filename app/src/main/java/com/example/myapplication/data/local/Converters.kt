package com.example.myapplication.data.local

import androidx.room.TypeConverter
import com.example.myapplication.data.model.OrderStatus

class Converters {
    @TypeConverter
    fun fromOrderStatus(status: OrderStatus): String {
        return status.name
    }

    @TypeConverter
    fun toOrderStatus(status: String): OrderStatus {
        return OrderStatus.valueOf(status)
    }
}
