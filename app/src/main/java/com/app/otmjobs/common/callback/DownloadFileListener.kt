package com.app.otmjobs.common.callback

interface DownloadFileListener {
    fun onFileDownloaded(directory: String,extension:String, action: Int)
}