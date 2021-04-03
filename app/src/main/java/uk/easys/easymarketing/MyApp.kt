package uk.easys.easymarketing

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication

class MyApp : MultiDexApplication() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
