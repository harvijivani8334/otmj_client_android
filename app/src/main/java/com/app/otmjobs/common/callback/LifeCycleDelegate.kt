package com.app.otmjobs.common.callback

interface LifeCycleDelegate {
    fun onAppBackgrounded()
    fun onAppForegrounded()
}