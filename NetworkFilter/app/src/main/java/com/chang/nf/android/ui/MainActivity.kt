package com.chang.nf.android.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import androidx.activity.ComponentActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.server.NetworkManagementService
import com.chang.nf.android.NetworkManager
import com.chang.nf.android.R
import com.chang.nf.android.data.model.AppInfo
import com.chang.nf.android.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject

/**
 * 主页
 * <p>
 * Created by Nicholas Sean on 2025/8/26 18:50.
 *
 * @version 1.0
 */
const val TAG = "NetworkFilter"
class MainActivity : ComponentActivity(), AppListAdapter.OnNetworkRuleChangedListener {

    private lateinit var appListAdapter: AppListAdapter
    private lateinit var binding: ActivityMainBinding
    private var suppressGlobalChange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        setupGlobalControls()

        appListAdapter = AppListAdapter(loadApps())
        binding.recyclerView.adapter = appListAdapter
        appListAdapter.setOnNetworkRuleChangedListener(this)
    }

    private fun setupGlobalControls() {
        // 初始化三行单选按钮状态
        suppressGlobalChange = true
        initGlobalRow(
            NetworkManagementService.NETWORK_TYPE_WIFI,
            binding.rgWifi,
            R.id.rb_wifi_none,
            R.id.rb_wifi_allow,
            R.id.rb_wifi_deny
        )
        initGlobalRow(
            NetworkManagementService.NETWORK_TYPE_MOBILE,
            binding.rgMobile,
            R.id.rb_mobile_none,
            R.id.rb_mobile_allow,
            R.id.rb_mobile_deny
        )
        initGlobalRow(
            NetworkManagementService.NETWORK_TYPE_ALL,
            binding.rgAll,
            R.id.rb_all_none,
            R.id.rb_all_allow,
            R.id.rb_all_deny
        )
        suppressGlobalChange = false

        // 监听器
        binding.rgWifi.setOnCheckedChangeListener { group, checkedId ->
            if (suppressGlobalChange) return@setOnCheckedChangeListener
            Log.i(TAG, "global, wifi check")
            handleGlobalChange(NetworkManagementService.NETWORK_TYPE_WIFI, checkedId,
                noneId = R.id.rb_wifi_none,
                allowId = R.id.rb_wifi_allow,
                denyId = R.id.rb_wifi_deny)
        }
        binding.rgMobile.setOnCheckedChangeListener { group, checkedId ->
            if (suppressGlobalChange) return@setOnCheckedChangeListener
            Log.i(TAG, "global, mobile check")
            handleGlobalChange(NetworkManagementService.NETWORK_TYPE_MOBILE, checkedId,
                noneId = R.id.rb_mobile_none,
                allowId = R.id.rb_mobile_allow,
                denyId = R.id.rb_mobile_deny)
        }
        binding.rgAll.setOnCheckedChangeListener { group, checkedId ->
            if (suppressGlobalChange) return@setOnCheckedChangeListener
            Log.i(TAG, "global, all check")
            handleGlobalChange(NetworkManagementService.NETWORK_TYPE_ALL, checkedId,
                noneId = R.id.rb_all_none,
                allowId = R.id.rb_all_allow,
                denyId = R.id.rb_all_deny)
        }
    }

    private fun initGlobalRow(networkType: Int, group: android.widget.RadioGroup, noneId: Int, allowId: Int, denyId: Int) {
        val status = NetworkManager.getGlobalNetworkRule(networkType) // -1 none, 0 deny, 1 allow
        when (status) {
            -1 -> group.check(noneId)
            0 -> group.check(denyId)
            1 -> group.check(allowId)
            else -> group.check(noneId)
        }
    }

    private fun handleGlobalChange(networkType: Int, checkedId: Int, noneId: Int, allowId: Int, denyId: Int) {
        when (checkedId) {
            noneId -> NetworkManager.removeGlobalNetworkRule(networkType)
            allowId -> NetworkManager.setGlobalNetworkRule(networkType, true)
            denyId -> NetworkManager.setGlobalNetworkRule(networkType, false)
        }
    }

    private fun loadApps(): ArrayList<AppInfo> {
        Log.i(TAG, "loadApps()")
        val appList = arrayListOf<AppInfo>()
        val pm = packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        Log.i(TAG, "Installed packages: ${packages.size}")
        for (app in packages) {
            if (pm.checkPermission(Manifest.permission.INTERNET, app.packageName) == PackageManager.PERMISSION_GRANTED) {
//                if (app.flags and ApplicationInfo.FLAG_SYSTEM == 0) { // Exclude system apps for now
                    val appName = app.loadLabel(pm).toString()
                    val appIcon = app.loadIcon(pm)
                    val packageName = app.packageName
                    val uid = app.uid
                    Log.i(TAG, "appName: $appName, packageName: $packageName, uid: $uid")

                    var wifiEnabled = true
                    var mobileEnabled = true

                    val rulesJson = NetworkManager.getAppNetworkRules(uid)
                    if (!rulesJson.isNullOrEmpty()) {
                        try {
                            val jsonArray = JSONArray(rulesJson)
                            for (i in 0 until jsonArray.length()) {
                                val obj: JSONObject = jsonArray.getJSONObject(i)
                                val ruleUid = obj.optInt("uid", -1)
                                if (ruleUid != uid) continue
                                val allow = obj.optBoolean("allow", true)
                                val networkType = obj.optInt("networkType", -1)
                                if (networkType == NetworkManagementService.NETWORK_TYPE_WIFI) {
                                    wifiEnabled = allow
                                } else if (networkType == NetworkManagementService.NETWORK_TYPE_MOBILE) {
                                    mobileEnabled = allow
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to parse rules JSON for uid=$uid: $rulesJson", e)
                        }
                    }

                    appList.add(AppInfo(appName, packageName, appIcon, uid, wifiEnabled, mobileEnabled))
//                }
            }
        }
        Log.d(TAG, "appList: ${appList.size}")
        return appList
    }

    override fun onWifiRuleChanged(buttonView: CompoundButton, appInfo: AppInfo, isChecked: Boolean) {
        Log.i(TAG, "onWifiRuleChanged() appName: ${appInfo.appName}, packageName: ${appInfo.packageName}, uid: ${appInfo.uid}, allow: $isChecked")
        if (NetworkManager.setAppNetworkRule(appInfo.uid, NetworkManagementService.NETWORK_TYPE_WIFI, isChecked)) {
            appInfo.isWifiEnabled = isChecked
        } else {
            buttonView.isChecked = !isChecked
        }
    }

    override fun onMobileRuleChanged(buttonView: CompoundButton, appInfo: AppInfo, isChecked: Boolean) {
        Log.i(TAG, "onMobileRuleChanged() appName: ${appInfo.appName}, packageName: ${appInfo.packageName}, uid: ${appInfo.uid}, allow: $isChecked")
        if (NetworkManager.setAppNetworkRule(appInfo.uid, NetworkManagementService.NETWORK_TYPE_MOBILE, isChecked)) {
            appInfo.isMobileEnabled = isChecked
        } else {
            buttonView.isChecked = !isChecked
        }
    }
}