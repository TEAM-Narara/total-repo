package com.ssafy.login.login.github

import android.content.Context

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.ssafy.login.BuildConfig

class GitHubLoginHelper {

    private val url = Uri.Builder().scheme("https").authority("github.com")
        .appendPath("login")
        .appendPath("oauth")
        .appendPath("authorize")
        .appendQueryParameter("client_id", BuildConfig.GIT_ID)
        .appendQueryParameter("scope", "user:email")
        .build()

    fun githubLogin(context: Context) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
        customTabsIntent.launchUrl(context, url)
    }
}
