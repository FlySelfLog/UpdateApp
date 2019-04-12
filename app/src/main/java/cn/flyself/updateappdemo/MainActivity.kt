package cn.flyself.updateappdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import cn.flyself.updateapp.UpdateApp
import cn.flyself.updateapp.UpdateDisplay
import cn.flyself.updateapp.ui.OnDialogClickListener
import cn.flyself.updateapp.ui.UpdateAppNotification
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn1.setOnClickListener {
            UpdateDisplay.get()
                .setOnDialogClickListener(object : OnDialogClickListener {
                    override fun onClickCancel() {
                        Toast.makeText(applicationContext, "onClickCancel", Toast.LENGTH_SHORT).show()
                    }

                    override fun onClickAllow() {
                        Toast.makeText(applicationContext, "onClickAllow", Toast.LENGTH_SHORT).show()
                    }
                })
                .setVersionName("标题")
                .setApkSize("提示")
                .setMessage("消息")
                .setKeepForeground(true)
                .showDialog()
        }

        btn2.setOnClickListener {
            val notify = UpdateAppNotification(this)
            notify.showNotification("", "", 30, R.drawable.ic_launcher_foreground)
        }

        btn3.setOnClickListener {
            val updateApp = UpdateApp.build()
                .checkBy(UpdateApp.CHECK_BY_VERSION_CODE)
                .downloadBy(UpdateApp.DOWNLOAD_BY_APP)//下载方式，应用内下载or使用浏览器下载
                .setUpdateJsonUrl("https://flyself.gitee.io/update/UpdateAppDemo/update.json")
                .setSavePath("download/cn.flyself.updateappdemo/apk")
                .isForce(false)//是否强制更新
                .showNotification(true)//在通知栏显示进度
                .setKeepForeground(keepForeground = true, enableBackKey = false)//保持窗口始终在前面
                .update()
            if (!updateApp.hasUpdate) {
                Toast.makeText(applicationContext, "没有更新", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
