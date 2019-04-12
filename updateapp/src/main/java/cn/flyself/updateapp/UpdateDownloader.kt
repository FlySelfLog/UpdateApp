package cn.flyself.updateapp

import android.os.Environment
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class UpdateDownloader {
    private val okHttpClient = OkHttpClient()
    private var listener: OnDownloadListener? = null

    //private var downloadFile: File? = null
    private var totalSize: Long = 0

    companion object {
        @JvmStatic
        private var updateDownloader: UpdateDownloader? = null

        @JvmStatic
        fun get(): UpdateDownloader {
            if (updateDownloader == null) {
                updateDownloader = UpdateDownloader()
            }
            return updateDownloader!!
        }
    }

    /**
     * 启动下载
     * @param url 下载连接
     * @param saveDir 储存下载文件的SDCard目录
     * @return UpdateDownloader
     */
    fun download(url: String, saveDir: String): UpdateDownloader {
        val request = Request.Builder().url(url).build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 下载失败
                listener!!.onDownloadFailed()
            }

            override fun onResponse(call: Call, response: Response) {
                var inputStream: InputStream? = null
                var fileOutputStream: FileOutputStream? = null
                val buf = ByteArray(2048)
                var len: Int
                // 储存下载文件的目录
                val savePath = isExistDir(saveDir)
                try {
                    inputStream = response.body()!!.byteStream()
                    val total: Long = if (totalSize == 0L) {
                        response.body()!!.contentLength()
                    } else {
                        totalSize
                    }
                    val downloadFile = File(savePath, getNameFromUrl(url))
                    fileOutputStream = FileOutputStream(downloadFile)
                    var sum: Long = 0

                    while (true) {
                        len = inputStream.read(buf)
                        if (len == -1) {
                            break
                        }
                        fileOutputStream.write(buf, 0, len)
                        sum += len
                        val progress = (sum * 1.0f / total * 100).toInt()
                        // 下载中
                        listener!!.onDownloading(progress)
                    }

                    fileOutputStream.flush()
                    // 下载完成
                    listener!!.onDownloadSuccess(downloadFile)
                } catch (e: Exception) {
                    e.printStackTrace()
                    listener!!.onDownloadFailed()
                } finally {
                    try {
                        inputStream?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    try {
                        fileOutputStream?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        })
        return this
    }

    /**
     * 设置监听
     * @param listener 下载监听
     * @return UpdateDownloader
     * */
    fun setOnDownloadListener(listener: OnDownloadListener): UpdateDownloader {
        this.listener = listener
        return this
    }

    /**
     * 设置文件总字节数
     * @param totalSize
     * @return UpdateDownloader
     * */
    fun setTotalSize(totalSize: Long): UpdateDownloader {
        this.totalSize = totalSize
        return this
    }

    /**
     * 判断下载目录是否存在
     * @param saveDir
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun isExistDir(saveDir: String): String {
        // 下载位置
        val downloadFile = File(Environment.getExternalStorageDirectory(), saveDir)
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile()
        }
        return downloadFile.absolutePath
    }

    /**
     * 从下载连接中解析出文件名
     * @param url
     * @return
     */
    private fun getNameFromUrl(url: String): String {
        return url.substring(url.lastIndexOf("/") + 1)
    }
}