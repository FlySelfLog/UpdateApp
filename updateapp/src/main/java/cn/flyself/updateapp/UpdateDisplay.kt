package cn.flyself.updateapp

import android.content.Intent
import cn.flyself.updateapp.ui.OnDialogClickListener
import cn.flyself.updateapp.ui.UpdateAppActivity


class UpdateDisplay {
    private var versionName = ""
    private var apkSize = ""
    private var message = ""
    private var keepForeground = false
    private var enableBackKey = true

    companion object {
        @JvmStatic
        private var updateDisplay: UpdateDisplay? = null

        @JvmStatic
        fun get(): UpdateDisplay {
            if (updateDisplay == null) {
                updateDisplay = UpdateDisplay()
            }
            return updateDisplay!!
        }
    }

    /**
     * 设置dialog监听，自定义allow、cancel按钮响应事件
     * @param listener 监听事件实例
     * @return UpdateDisplay
     * */
    fun setOnDialogClickListener(listener: OnDialogClickListener): UpdateDisplay {
        UpdateManager.setOnDialogClickListener(listener)
        return this
    }

    /***/
    fun setVersionName(versionName: String): UpdateDisplay {
        this.versionName = versionName
        return this
    }

    /***/
    fun setApkSize(apkSize: String): UpdateDisplay {
        this.apkSize = apkSize
        return this
    }

    /***/
    fun setMessage(message: String): UpdateDisplay {
        this.message = message
        return this
    }

    /**
     * @param keepForeground
     * @return UpdateDisplay
     * */
    fun setKeepForeground(keepForeground: Boolean): UpdateDisplay {
        this.keepForeground = keepForeground
        return this
    }

    /**
     * @param enableBackKey
     * @return
     * */
    fun enableBackKey(enableBackKey: Boolean): UpdateDisplay {
        this.enableBackKey = enableBackKey
        return this
    }

    /**
     * 显示dialog
     * @param
     * @return UpdateDisplay
     * */
    fun showDialog(): UpdateDisplay {
        val context = UpdateManager.application!!.applicationContext
        val intent = Intent(context, UpdateAppActivity::class.java)
        intent.putExtra("title", versionName)
        intent.putExtra("apkSize", apkSize)
        intent.putExtra("message", message)
        intent.putExtra("keepForeground", keepForeground)
        intent.putExtra("enableBackKey", enableBackKey)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK//避免在Android6.0中崩溃
        context.startActivity(intent)
        return this
    }
}