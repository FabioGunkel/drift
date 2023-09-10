package com.gunkel.android.drift.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle


class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("http://www.drift.com.br/map")
        startActivity(intent)
    }
}