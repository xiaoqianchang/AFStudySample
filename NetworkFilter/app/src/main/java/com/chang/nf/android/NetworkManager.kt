package com.chang.nf.android

import android.os.INetworkManagementService
import android.os.ServiceManager
import android.util.Log
import android.widget.Toast

object NetworkManager {
    private const val TAG = "NetworkManager"
    private var networkManagementService: INetworkManagementService? = null

    init {
        try {
            val binder = ServiceManager.getService("network_management")
            networkManagementService = INetworkManagementService.Stub.asInterface(binder)
            if (networkManagementService != null) {
                Log.d(TAG, "INetworkManagementService not null!")
            } else {
                Log.d(TAG, "Fail to get NetworkManagementService")
            }
        } catch (e: Exception) {
            Log.e(TAG, "RemoteException while getting NetworkManagementService", e)
        }
    }

    fun getAppNetworkRules(uid: Int): String? {
        return try {
            networkManagementService?.getAppNetworkRules(uid)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting app network rules", e)
            null
        }
    }

    fun setAppNetworkRule(uid: Int, networkType: Int, allow: Boolean): Boolean {
        try {
            networkManagementService?.setAppNetworkRule(uid, networkType, allow)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error setting app network rule", e)
            Toast.makeText(App.sInstance, e.message, Toast.LENGTH_SHORT).show()
            return false
        }
    }

    fun removeAppNetworkRule(uid: Int): Boolean {
        try {
            networkManagementService?.removeAppNetworkRule(uid)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error removing app network rule", e)
            Toast.makeText(App.sInstance, e.message, Toast.LENGTH_SHORT).show()
            return false
        }
    }

    fun setGlobalNetworkRule(networkType: Int, allow: Boolean) {
        try {
            networkManagementService?.setGlobalNetworkRule(networkType, allow)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting global network rule", e)
            Toast.makeText(App.sInstance, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun removeGlobalNetworkRule(networkType: Int) {
        try {
            networkManagementService?.removeGlobalNetworkRule(networkType)
        } catch (e: Exception) {
            Log.e(TAG, "Error removing global network rule", e)
            Toast.makeText(App.sInstance, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun getGlobalNetworkRule(networkType: Int): Int {
        return try {
            networkManagementService?.getGlobalNetworkRule(networkType) ?: -1
        } catch (e: Exception) {
            Log.e(TAG, "Error getting global network rule", e)
            -1
        }
    }
}