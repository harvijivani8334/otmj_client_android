package com.app.otmjobs.common.utils

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.os.Bundle
import com.app.otmjobs.common.callback.LifeCycleDelegate


 class AppLifecycleHandler(private val lifecycleDelegate: LifeCycleDelegate) :
    Application.ActivityLifecycleCallbacks, ComponentCallbacks2 // <-- Implement these
{
    private var appInForeground = false

    // Override from Application.ActivityLifecycleCallbacks
    override fun onActivityResumed(p0: Activity) {
        if (!appInForeground) {
            appInForeground = true
            lifecycleDelegate.onAppForegrounded()
        }
    }

    // Override from ComponentCallbacks2
    override fun onTrimMemory(level: Int) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            // lifecycleDelegate instance was passed in on the constructor
            appInForeground = false
            lifecycleDelegate.onAppBackgrounded()
        }
    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

    override fun onConfigurationChanged(newConfig: Configuration) {

    }

    override fun onLowMemory() {

    }
}