package cn.flyself.updateapp.ui

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import cn.flyself.updateapp.*
import cn.flyself.updateapp.UpdateApp.Companion.DOWNLOAD_BY_APP
import kotlinx.android.synthetic.main.activity_update_app.*
import java.io.File

class UpdateAppActivity : AppCompatActivity() {
    private var title = ""
    private var apkSize = ""
    private var message = ""
    private var keepForeground = false
    private var enableBackKey = true
    private var downloadBy: Int? = null

    companion object {
        const val TAG = "UpdateAppActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_app)
        val intent = intent
        if (intent != null) {
            title = intent.getStringExtra("title")
            apkSize = intent.getStringExtra("apkSize")
            message = intent.getStringExtra("message")
            downloadBy = intent.getIntExtra("downloadBy", DOWNLOAD_BY_APP)
            keepForeground = intent.getBooleanExtra("keepForeground", false)
            enableBackKey = intent.getBooleanExtra("enableBackKey", true)
        }
        initView()

        update_app_cancel.setOnClickListener {
            if (UpdateManager.dialogListener != null) {
                UpdateManager.dialogListener!!.onClickCancel()
            }
            finish()
        }
        update_app_allow.setOnClickListener {
            onClickAllow()
            if (UpdateManager.dialogListener != null) {
                UpdateManager.dialogListener!!.onClickAllow()
            }
            if (!keepForeground) {
                finish()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && !enableBackKey) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        update_app_title.text = getString(R.string.update_app_title)
        update_app_new_version.text = getString(R.string.update_app_new_version) + title
        update_app_apk_size.text = getString(R.string.update_app_apk_size) + apkSize + "MB"
        update_app_message.text = getString(R.string.update_app_message) + message
        update_app_download_progress.visibility = View.GONE
    }

    private fun onClickAllow() {
        update_app_download_progress.visibility = View.VISIBLE
        update_app_allow.isClickable = false
        update_app_cancel.isClickable = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            update_app_allow.setTextColor(getColor(R.color.update_app_grey))
            update_app_cancel.setTextColor(getColor(R.color.update_app_grey))
        } else {
            @Suppress("DEPRECATION")
            update_app_allow.setTextColor(resources.getColor(R.color.update_app_grey))
            @Suppress("DEPRECATION")
            update_app_cancel.setTextColor(resources.getColor(R.color.update_app_grey))
        }
        val listener = object : OnDownloadListener {
            override fun onDownloadSuccess(file: File) {
                Log.w(TAG, "UpdateApp Download Success!")
                finish()
            }

            override fun onDownloading(progress: Int) {
                Log.w(TAG, progress.toString())
                update_app_download_progress.progress = progress
            }

            override fun onDownloadFailed() {
                Log.e(TAG, "UpdateApp Download Failed!")
                finish()
            }
        }
        UpdateManager.setOnDownloadListener(listener)
    }
}