package cn.flyself.updateapp

import java.io.File

interface OnDownloadListener {
    /**
     * 下载成功
     */
    fun onDownloadSuccess(file: File)

    /**
     * @param progress
     * 下载进度
     */
    fun onDownloading(progress: Int)

    /**
     * 下载失败
     */
    fun onDownloadFailed()
}