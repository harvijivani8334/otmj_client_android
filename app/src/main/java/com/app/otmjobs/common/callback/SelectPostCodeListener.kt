package com.app.otmjobs.common.callback

import com.app.otmjobs.common.data.model.PostCodeInfo

interface SelectPostCodeListener {
    fun onSelectPostCode(info: PostCodeInfo, tag: Int)
}