package cn.flyself.updateapp

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

class UpdateChecker {
    private var serverVersionCode: Long? = null
    private var serverVersionName: String? = null
    private var versionCode: Long? = null
    private var versionName: String? = null

    companion object {
        @JvmStatic
        private var updateChecker: UpdateChecker? = null

        @JvmStatic
        fun get(): UpdateChecker {
            if (updateChecker == null) {
                updateChecker = UpdateChecker()
            }
            return updateChecker!!
        }
    }

    /**
     * 检查是否可更新
     * @param
     * @return Boolean
     * */
    fun checker(): Boolean {
        if (serverVersionCode != null && versionCode != null) {
            return serverVersionCode!! > versionCode!!
        }
        if (serverVersionName != null && serverVersionName != null) {
            return serverVersionName != serverVersionName
        }
        return false
    }

    /**
     * 设置version code
     * @param serverVersionCode
     * @return UpdateChecker
     * */
    fun setServerVersionCode(serverVersionCode: Long): UpdateChecker {
        this.serverVersionCode = serverVersionCode
        return this
    }

    /**
     * 设置version name
     * @param serverVersionName
     * @return UpdateChecker
     * */
    fun setServerVersionName(serverVersionName: String): UpdateChecker {
        this.serverVersionName = serverVersionName
        return this
    }

    /**
     * 获取app的version code
     * @param context 上下文对象
     * @return UpdateChecker
     * */
    fun setVersionCode(context: Context): UpdateChecker {
        val manager = context.packageManager
        var versionCode: Long = 0
        try {
            val info = manager.getPackageInfo(context.packageName, 0)
            versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                info.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        this.versionCode = versionCode
        return this
    }

    /**
     * 获取app的version name
     * @param context 上下文对象
     * @return UpdateChecker
     * */
    fun setVersionName(context: Context): UpdateChecker {
        val manager = context.packageManager
        var versionName = ""
        try {
            val info = manager.getPackageInfo(context.packageName, 0)
            versionName = info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        this.versionName = versionName
        return this
    }
}