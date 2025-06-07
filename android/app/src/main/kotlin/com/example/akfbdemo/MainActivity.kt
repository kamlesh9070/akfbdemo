package com.example.akfbdemo

import android.util.Log
import io.flutter.embedding.android.FlutterActivity

class MainActivity : FlutterActivity() {

    init {
        val ok = Item(name = "Hello")
        Log.d("MainActivity", ": ok: $ok")
    }


}
