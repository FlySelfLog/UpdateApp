package cn.flyself.updateapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.FileProvider
import cn.flyself.updateapp.ui.OnDialogClickListener
import cn.flyself.updateapp.ui.UpdateAppNotification
import java.io.File
import kotlin.concurrent.thread

class UpdateApp {
    /**
     * 是否有更新
     * */
    var hasUpdate = false
        private set

    private var checkBy = CHECK_BY_VERSION_CODE
    private var downloadBy = DOWNLOAD_BY_APP
    private var updateJsonUrl: String? = null
    private var savePath = ""
    private var isForce = false
    private var keepForeground = false
    private var enableBackKey = true
    private var showNotification = true
    private var listener: OnDownloadListener? = null

    companion object {
        const val TAG = "UpdateApp"
        const val CHECK_BY_VERSION_CODE = 100
        const val CHECK_BY_VERSION_NAME = 101
        const val DOWNLOAD_BY_APP = 201
        const val DOWNLOAD_BY_BROWSER = 202

        @JvmStatic
        private var updateApp: UpdateApp? = null

        @JvmStatic
        fun build(): UpdateApp {
            if (updateApp == null) {
                updateApp = UpdateApp()
            }
            return updateApp!!
        }
    }

    /**
     * 设置检查更新的方式，默认为CHECK_BY_VERSION_CODE
     * CHECK_BY_VERSION_CODE: 通过versionCode检查
     * CHECK_BY_VERSION_NAME: 通过versionName检查
     * @param checkBy 检查更新的方式
     * @return UpdateApp
     * */
    fun checkBy(checkBy: Int): UpdateApp {
        this.checkBy = checkBy
        return this
    }

    /**
     * 下载方式：app下载、手机浏览器下载。默认app下载
     * DOWNLOAD_BY_APP: 通过App下载
     * DOWNLOAD_BY_BROWSER: 通过手机浏览器下载
     * @param downloadBy 设置下载方式
     * @return UpdateApp
     * */
    fun downloadBy(downloadBy: Int): UpdateApp {
        this.downloadBy = downloadBy
        return this
    }

    /**
     * 设置服务器端的更新信息json url
     * @param updateJsonUrl 服务器端的更新信息json url
     * @return UpdateParser
     * */
    fun setUpdateJsonUrl(updateJsonUrl: String): UpdateApp {
        this.updateJsonUrl = updateJsonUrl
        return this
    }

    /**
     * 设置下载文件保存路径
     * @param path 外部存储路径
     * @return UpdateApp
     * */
    fun setSavePath(path: String): UpdateApp {
        this.savePath = path
        return this
    }

    /**
     * 是否强制更新，默认false 强制更新情况下用户不同意更新则不能使用app
     * TODO
     * @param isForce 是否强制更新
     * @return UpdateApp
     * */
    fun isForce(isForce: Boolean): UpdateApp {
        this.isForce = isForce
        return this
    }

    /**
     * 是否在下载时保持dialog在最前
     * @param keepForeground 是否保持dialog在最前，默认为false
     * @return UpdateApp
     * */
    fun setKeepForeground(keepForeground: Boolean): UpdateApp {
        this.keepForeground = keepForeground
        return this
    }

    /**
     * 是否在下载时保持dialog在最前
     * @param keepForeground 是否保持dialog在最前，默认为false
     * @param enableBackKey 返回键是否可用,默认为true
     * @return UpdateApp
     * */
    fun setKeepForeground(keepForeground: Boolean, enableBackKey: Boolean): UpdateApp {
        this.keepForeground = keepForeground
        this.enableBackKey = enableBackKey
        return this
    }

    /**
     * 是否显示下载进度到通知栏，默认为true
     * @param
     * @return UpdateApp
     * */
    fun showNotification(isShow: Boolean): UpdateApp {
        this.showNotification = isShow
        return this
    }

    /**
     * 开始更新
     * @param
     * @return UpdateApp
     * */
    fun update(): UpdateApp {
        val context = UpdateManager.application!!.applicationContext
        var updateData: UpdateEntity? = null
        thread {
            updateData = UpdateParser.get().setUpdateJsonUrl(updateJsonUrl!!).parser()
        }.join()

        //判断是否有更新
        if (checkBy == CHECK_BY_VERSION_CODE) {
            hasUpdate = UpdateChecker.get()
                .setServerVersionCode(updateData!!.versionCode)
                .setVersionCode(context)
                .checker()
        } else if (checkBy == CHECK_BY_VERSION_NAME) {
            hasUpdate = UpdateChecker.get()
                .setServerVersionName(updateData!!.versionName)
                .setVersionName(context)
                .checker()
        }
        if (!hasUpdate) {
            return this
        }

        //显示
        UpdateDisplay.get()
            .setVersionName(updateData!!.versionName)
            .setApkSize(String.format("%.2f", updateData!!.apkSize / 1024f / 1024f))
            .setMessage(updateData!!.versionInfo)
            .setKeepForeground(keepForeground)
            .enableBackKey(enableBackKey)
            .setOnDialogClickListener(object : OnDialogClickListener {
                override fun onClickCancel() {
                    Log.w(TAG, "cancel update")
                }

                override fun onClickAllow() {
                    listener = object : OnDownloadListener {
                        override fun onDownloadSuccess(file: File) {
                            Log.w(TAG, "UpdateApp Download Success!")
                            UpdateManager.downloadListener!!.onDownloadSuccess(file)
                            //发送通知
                            if (showNotification) {
                                val updateAppNotification = UpdateAppNotification(context)
                                updateAppNotification.clearNotification(UpdateAppNotification.TYPE_DOWNLOAD)
                                updateAppNotification.setIntent(getInstallIntent(context, file))
                                updateAppNotification.showNotification(
                                    "下载成功",
                                    "100%",
                                    -1,
                                    R.drawable.ic_launcher_foreground
                                )
                            }
                            //安装
                            toInstall(getInstallIntent(context, file))
                        }

                        override fun onDownloading(progress: Int) {
                            Log.w(TAG, progress.toString())
                            UpdateManager.downloadListener!!.onDownloading(progress)

                            //发送通知
                            if (showNotification) {
                                val updateAppNotification = UpdateAppNotification(context)
                                updateAppNotification.showNotification(
                                    "正在下载",
                                    "$progress%",
                                    progress,
                                    R.drawable.ic_launcher_foreground
                                )
                            }
                        }

                        override fun onDownloadFailed() {
                            Log.e(TAG, "UpdateApp Download Failed!")
                            UpdateManager.downloadListener!!.onDownloadFailed()

                            //发送通知
                            if (showNotification) {
                                val updateAppNotification = UpdateAppNotification(context)
                                updateAppNotification.clearNotification(UpdateAppNotification.TYPE_DOWNLOAD)
                                updateAppNotification.showNotification(
                                    "下载失败",
                                    "请重试",
                                    -1,
                                    R.drawable.ic_launcher_foreground
                                )
                            }
                        }
                    }
                    if (downloadBy == DOWNLOAD_BY_APP) {
                        thread {
                            UpdateDownloader.get()
                                .setOnDownloadListener(listener!!)
                                .setTotalSize(updateData!!.apkSize)
                                .download(updateData!!.downloadUrl, savePath)
                        }
                    } else if (downloadBy == DOWNLOAD_BY_BROWSER) {
                        // 跳转到浏览器下载
                        downloadFromWebView(context, updateData!!.downloadUrl)
                    }
                }
            })
            .showDialog()
        return this
    }


    /**
     * @param apkFile
     * @return
     */
    private fun getInstallIntent(context: Context, apkFile: File): Intent {
        val install = Intent(Intent.ACTION_VIEW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            install.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val contentUri = FileProvider.getUriForFile(context, context.packageName + ".fileProvider", apkFile)
            install.setDataAndType(contentUri, "application/vnd.android.package-archive")
        } else {
            install.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive")
        }
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return install
    }

    /**
     * 跳转安装
     */
    private fun toInstall(install: Intent) {
        UpdateManager.application!!.startActivity(install)
    }

    /**
     * @param context
     * @param url
     * @return
     * 通过浏览器下载APK包
     */
    private fun downloadFromWebView(context: Context, url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}