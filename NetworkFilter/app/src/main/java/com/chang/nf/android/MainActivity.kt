package com.chang.nf.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.databinding.DataBindingUtil
import com.chang.nf.android.databinding.ActivityMainBinding

/**
 * 主页
 * <p>
 * Created by Nicholas Sean on 2025/8/26 18:50.
 *
 * @version 1.0
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }
}