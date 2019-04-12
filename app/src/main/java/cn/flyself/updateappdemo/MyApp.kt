package cn.flyself.updateappdemo

import android.app.Application
import cn.flyself.updateapp.UpdateManager

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        UpdateManager.init(this)
    }

}