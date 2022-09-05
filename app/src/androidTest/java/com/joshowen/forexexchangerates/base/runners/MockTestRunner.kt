package com.joshowen.forexexchangerates.base.runners

import android.app.Application
import android.content.Context
import android.os.StrictMode
import androidx.test.runner.AndroidJUnitRunner
import com.joshowen.forexexchangerates.base.base.BaseApplicationTest_Application

class MockTestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        return super.newApplication(cl, BaseApplicationTest_Application::class.java.name, context)
    }
}