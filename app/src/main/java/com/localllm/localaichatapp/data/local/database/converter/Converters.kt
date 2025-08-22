package com.localllm.localaichatapp.data.local.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.localllm.localaichatapp.domain.model.ChatSender

class Converters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromChatSender(sender: ChatSender): String = sender.name
    
    @TypeConverter
    fun toChatSender(senderString: String): ChatSender = ChatSender.valueOf(senderString)
    
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
}