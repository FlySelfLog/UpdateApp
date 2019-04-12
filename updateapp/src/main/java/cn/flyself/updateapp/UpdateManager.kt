package cn.flyself.updateapp

import android.app.Application
import cn.flyself.updateapp.ui.OnDialogClickListener

class UpdateManager {
    companion object {
        @JvmStatic
        var application: Application? = null
            private set
        @JvmStatic
        var dialogListener: OnDialogClickListener? = null
            private set
        @JvmStatic
        var downloadListener: OnDownloadListener? = null
            private set

        /**
         * 初始化UpdateApp
         * @param application 全局application对象
         * @return
         * */
        @JvmStatic
        fun init(application: Application) {
            this.application = application
        }

        /**
         * 设置全局监听窗口allow、cancel点击事件
         * @param listener 监听对象
         * @return
         * */
        @JvmStatic
        fun setOnDialogClickListener(listener: OnDialogClickListener) {
            this.dialogListener = listener
        }

        /**
         * 设置全局监听下载事件
         * @param listener 监听对象
         * @return
         * */
        @JvmStatic
        fun setOnDownloadListener(listener: OnDownloadListener) {
            this.downloadListener = listener
        }
    }
}