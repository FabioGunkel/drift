package com.gunkel.map

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


class MapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapScreen()
        }
    }

    @Composable
    private fun MapScreen() {
        Text("Hello world!")
    }


    @Preview
    @Composable
    fun PreviewMapScreen() {
        MapScreen()
    }
}
