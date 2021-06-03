package com.app.imagepickers.utils

class Constant {
    object ImageScaleType {
        const val CENTER_CROP = 1
        const val FIT_CENTER = 2
    }

    object TransformationType {
        const val BLUR_TRANSFORMATION = "BLUR_TRANSFORMATION"
        const val COLOR_FILTER_TRANSFORMATION = "COLOR_FILTER_TRANSFORMATION"
        const val GRAYSCALE_TRANSFORMATION = "GRAYSCALE_TRANSFORMATION"
        const val ROUNDED_CORNERS_TRANSFORMATION = "ROUNDED_CORNERS_TRANSFORMATION"
        const val CENTERCROP_TRANSFORM = "CENTERCROP_TRANSFORM"
        const val CIRCLECROP_TRANSFORM = "CIRCLECROP_TRANSFORM"
    }

    object ImagePicker {
        const val REQUEST_CAMERA_KITKAT = 0
        const val REQUEST_CAMERA = 1
        const val REQUEST_GALLARY = 2
        const val REQUEST_VIDEO = 3
        const val MULTIPLE_IMAGE_SELECT = 4
    }
}