package com.example.fittrack.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fittrack.data.local.db.DatabaseProvider
import com.example.fittrack.data.repository.FitTrackRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = FitTrackRepository(DatabaseProvider.get(app.applicationContext))

    val fullName: StateFlow<String> =
        repo.observeProfiles()
            .map { profiles -> profiles.firstOrNull()?.name.orEmpty() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = "",
            )

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(app) as T
            }
            error("Unknown ViewModel class: $modelClass")
        }
    }
}

