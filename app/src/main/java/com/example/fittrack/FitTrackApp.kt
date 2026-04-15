package com.example.fittrack

import android.app.Application
import com.example.fittrack.data.local.db.DatabaseProvider
import com.example.fittrack.data.sync.FitTrackSyncScheduler

class FitTrackApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Warm-up DB + đảm bảo migration được kiểm tra sớm
        DatabaseProvider.get(this)
        // Lên lịch sync (có mạng sẽ tự chạy)
        FitTrackSyncScheduler.schedule(this)
    }
}

