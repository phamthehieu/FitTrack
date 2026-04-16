package com.example.fittrack

import android.app.Application
import android.content.pm.ApplicationInfo
import com.example.fittrack.data.local.db.DatabaseProvider
import com.example.fittrack.data.sync.FitTrackSyncScheduler
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

class FitTrackApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        val appCheck = FirebaseAppCheck.getInstance()
        val isDebuggable = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (isDebuggable) {
            // Khi chạy debug, Firebase sẽ log ra debug token trong Logcat để bạn thêm vào Firebase Console.
            appCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
        }

        // Warm-up DB + đảm bảo migration được kiểm tra sớm
        DatabaseProvider.get(this)
        // Lên lịch sync (có mạng sẽ tự chạy)
        FitTrackSyncScheduler.schedule(this)
    }
}

