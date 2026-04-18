package com.chang.nf.android.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chang.nf.android.R
import com.chang.nf.android.data.model.AppInfo

class AppListAdapter(private val appList: List<AppInfo>) :
    RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    interface OnNetworkRuleChangedListener {
        fun onWifiRuleChanged(buttonView: CompoundButton, appInfo: AppInfo, isChecked: Boolean)
        fun onMobileRuleChanged(buttonView: CompoundButton, appInfo: AppInfo, isChecked: Boolean)
    }

    private var listener: OnNetworkRuleChangedListener? = null

    fun setOnNetworkRuleChangedListener(listener: OnNetworkRuleChangedListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appInfo = appList[position]
        holder.appName.text = appInfo.appName
        holder.appIcon.setImageDrawable(appInfo.appIcon)
        holder.wifiCheckBox.isChecked = appInfo.isWifiEnabled
        holder.mobileCheckBox.isChecked = appInfo.isMobileEnabled

        holder.wifiCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!buttonView.isPressed) {
                return@setOnCheckedChangeListener
            }
            listener?.onWifiRuleChanged(buttonView, appInfo, isChecked)
        }

        holder.mobileCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!buttonView.isPressed) {
                return@setOnCheckedChangeListener
            }
            listener?.onMobileRuleChanged(buttonView, appInfo, isChecked)
        }
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appIcon: ImageView = itemView.findViewById(R.id.iv_app_icon)
        val appName: TextView = itemView.findViewById(R.id.tv_app_name)
        val wifiCheckBox: CheckBox = itemView.findViewById(R.id.cb_wifi)
        val mobileCheckBox: CheckBox = itemView.findViewById(R.id.cb_mobile)
    }
}
