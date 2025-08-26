package com.chang.nf.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Boot up receiver.
 * <p>
 * Created by Nicholas Sean on 2022/5/23 3:32 下午.
 *
 * @version 1.0
 */
class BootUpReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootUpReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive() action=${intent?.action}.")
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d(TAG, "onReceive() BOOT_COMPLETED.")
            }
        }
    }
}