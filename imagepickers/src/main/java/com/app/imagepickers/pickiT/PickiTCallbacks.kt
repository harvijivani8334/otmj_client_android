package com.app.imagepickers.pickiT

import java.util.*

public interface PickiTCallbacks {
    fun PickiTonUriReturned()
    fun PickiTonStartListener()
    fun PickiTonProgressUpdate(progress: Int)
    fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    )

    fun PickiTonMultipleCompleteListener(
        paths: ArrayList<String>,
        wasSuccessful: Boolean,
        Reason: String?
    )
}