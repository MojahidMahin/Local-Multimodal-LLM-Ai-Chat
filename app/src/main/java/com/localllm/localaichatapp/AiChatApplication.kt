package com.localllm.localaichatapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AiChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}