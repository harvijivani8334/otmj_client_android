package com.app.imagepickers.pickiT

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import java.io.File
import java.util.*

object SDUtil {
    public val EXTERNAL_STORAGE = System.getenv("EXTERNAL_STORAGE")
    public val SECONDARY_STORAGES = System.getenv("SECONDARY_STORAGE")
    public val EMULATED_STORAGE_TARGET = System.getenv("EMULATED_STORAGE_TARGET")

    public fun getStorageDirectories(context: Context): Array<String?> {
        val availableDirectoriesSet: MutableCollection<in String?> = HashSet()
        if (!TextUtils.isEmpty(EMULATED_STORAGE_TARGET)) {
            availableDirectoriesSet.add(getEmulatedStorageTarget())
        } else {
            availableDirectoriesSet.addAll(getExternalStorage(context)!!)
        }
        Collections.addAll(availableDirectoriesSet, *getAllSecondaryStorages())
        val storagesArray = arrayOfNulls<String?>(availableDirectoriesSet.size)
        return storagesArray;
    }

    public fun getExternalStorage(context: Context): Set<String>? {
        val availableDirectoriesSet: MutableSet<String> = HashSet()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val files = getExternalFilesDirs(context)
            for (file in files) {
                if (file != null) {
                    val applicationSpecificAbsolutePath = file.absolutePath
                    var rootPath = applicationSpecificAbsolutePath.substring(
                        9, applicationSpecificAbsolutePath.indexOf("Android/data")
                    )
                    rootPath = rootPath.substring(rootPath.indexOf("/storage/") + 1)
                    rootPath = rootPath.substring(0, rootPath.indexOf("/"))
                    if (rootPath != "emulated") {
                        availableDirectoriesSet.add(rootPath)
                    }
                }
            }
        } else {
            if (TextUtils.isEmpty(EXTERNAL_STORAGE)) {
                availableDirectoriesSet.addAll(getAvailablePhysicalPaths()!!)
            } else {
                availableDirectoriesSet.add(EXTERNAL_STORAGE)
            }
        }
        return availableDirectoriesSet
    }

    public fun getEmulatedStorageTarget(): String {
        var rawStorageId = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val path = Environment.getExternalStorageDirectory().absolutePath
            val folders = path.split(File.separator).toTypedArray()
            val lastSegment = folders[folders.size - 1]
            if (!TextUtils.isEmpty(lastSegment) && TextUtils.isDigitsOnly(lastSegment)) {
                rawStorageId = lastSegment
            }
        }
        return if (TextUtils.isEmpty(rawStorageId)) {
            EMULATED_STORAGE_TARGET
        } else {
            EMULATED_STORAGE_TARGET + File.separator + rawStorageId
        }
    }

    public fun getAllSecondaryStorages(): Array<String?> {
        if (!TextUtils.isEmpty(SECONDARY_STORAGES)) {
            if (SECONDARY_STORAGES != null) {
                return SECONDARY_STORAGES.split(File.pathSeparator).toTypedArray()
            }
        }
        return arrayOfNulls(0)
    }

    public fun getAvailablePhysicalPaths(): List<String>? {
        val availablePhysicalPaths: MutableList<String> = ArrayList()
        for (physicalPath in KNOWN_PHYSICAL_PATHS) {
            val file = File(physicalPath)
            if (file.exists()) {
                availablePhysicalPaths.add(physicalPath)
            }
        }
        return availablePhysicalPaths
    }

    public fun getExternalFilesDirs(context: Context): Array<File?> {
        return if (Build.VERSION.SDK_INT >= 19) {
            context.getExternalFilesDirs(null)
        } else {
            arrayOf(context.getExternalFilesDir(null))
        }
    }

    @SuppressLint("SdCardPath")
    public val KNOWN_PHYSICAL_PATHS = arrayOf(
        "/storage/sdcard0",
        "/storage/sdcard1",
        "/storage/extsdcard",
        "/storage/sdcard0/external_sdcard",
        "/mnt/extsdcard",
        "/mnt/sdcard/external_sd",
        "/mnt/sdcard/ext_sd",
        "/mnt/external_sd",
        "/mnt/media_rw/sdcard1",
        "/removable/microsd",
        "/mnt/emmc",
        "/storage/external_SD",
        "/storage/ext_sd",
        "/storage/removable/sdcard1",
        "/data/sdext",
        "/data/sdext2",
        "/data/sdext3",
        "/data/sdext4",
        "/sdcard1",
        "/sdcard2",
        "/storage/microsd"
    )
}