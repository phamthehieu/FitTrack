package com.example.fittrack.ui.navigation

import android.net.Uri

object FitTrackRoutes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val LOGIN_ROUTE = "login?email={email}"
    const val REGISTER = "register"
    const val HOME = "home"

    fun loginRoute(prefillEmail: String? = null): String {
        val encoded = Uri.encode(prefillEmail.orEmpty())
        return "login?email=$encoded"
    }
}
