package com.nordix.chekly

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity

abstract class LoggedActivity : ComponentActivity() {
    private fun log(event: String) = Log.d("Lifecycle", "${javaClass.simpleName}: $event")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("onCreate (restored=${savedInstanceState != null})")
    }

    override fun onStart() { super.onStart(); log("onStart") }
    override fun onResume() { super.onResume(); log("onResume") }
    override fun onPause() { super.onPause(); log("onPause") }
    override fun onStop() { super.onStop(); log("onStop") }
    override fun onRestart() { super.onRestart(); log("onRestart") }
    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy (changingConfig=$isChangingConfigurations)")
    }
}