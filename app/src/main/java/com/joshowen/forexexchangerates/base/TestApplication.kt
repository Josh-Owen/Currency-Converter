package com.joshowen.forexexchangerates.base

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

open class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}