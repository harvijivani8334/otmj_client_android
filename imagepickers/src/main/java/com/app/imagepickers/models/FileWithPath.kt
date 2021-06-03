package com.app.imagepickers.models

import android.net.Uri
import java.io.File

class FileWithPath {
    var id = 0
    lateinit var file: File
    var filePath: String = ""
    var uri: Uri? = null
}