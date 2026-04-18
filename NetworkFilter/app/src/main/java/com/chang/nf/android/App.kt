package com.chang.nf.android

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.os.Build
import android.util.Log
import android.view.WindowManager
import com.chang.nf.android.utils.BaseUtils.isMainProcess
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.properties.Delegates

/**
 * Home application.
 * <p>
 * Created by Nicholas Sean on 2024/8/26 18:57.
 *
 * @version 1.0
 */
class App : Application() {

    companion object {
        private const val TAG = "App"

        var sInstance: Context by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        if (isMainProcess(this)) {
            sInstance = this
            printAppInfo()
            printDeviceConfiguration()
        }
    }

    private fun printAppInfo() {
        Log.i(
            TAG, "onCreate()" +
                    "\nBUILD    TYPE = " + Build.TYPE +
                    "\nBUILD      ID = " + BuildConfig.COMPILE_ID +
                    "\nVER      NAME = " + BuildConfig.VERSION_NAME +
                    "\nVER      CODE = " + BuildConfig.VERSION_CODE
        )
    }

    private fun printDeviceConfiguration() {
        var x = 0
        var y = 0
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bounds = windowManager.currentWindowMetrics.bounds
            x = bounds.right
            y = bounds.bottom
        } else {
            val point = Point()
            windowManager.defaultDisplay.getRealSize(point)
            x = point.x
            y = point.y
        }
        Log.i(
            TAG, "DeviceConfiguration()" +
                    "\nPhysical size           : ${x}x${y}" +
                    "\nPhysical density        : ${resources.displayMetrics.densityDpi}" +
                    "\nScreen inches           : ${
                        sqrt(
                            resources.displayMetrics.widthPixels.toDouble().pow(2.0) +
                                    resources.displayMetrics.heightPixels.toDouble().pow(2.0)
                        ) / resources.displayMetrics.densityDpi}" +
                    "\nSmallest screen widthDp : ${resources.configuration.smallestScreenWidthDp}" +
                    "\nOrientation             : ${if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) "LANDSCAPE" else "LANDSCAPE"}"
        )
    }
}