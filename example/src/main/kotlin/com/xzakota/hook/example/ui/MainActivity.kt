package com.xzakota.hook.example.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import com.xzakota.hook.example.databinding.LayoutActivityMainBinding
import com.xzakota.hook.example.utils.HookUtils
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper

@SuppressLint("SetTextI18n")
class MainActivity : Activity() {
    private lateinit var isActivatedTextView : TextView
    private lateinit var xposedApiVersionTextView : TextView

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = LayoutActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isActivatedTextView = binding.isActivatedText
        xposedApiVersionTextView = binding.xposedApiVersionText

        XposedServiceHelper.registerListener(object : XposedServiceHelper.OnServiceListener {
            override fun onServiceBind(service : XposedService) {
                HookUtils.isSelfModuleActivated = true
                HookUtils.xposedAPIVersion = service.apiVersion

                syncTextView()
            }

            override fun onServiceDied(p0 : XposedService) {}
        })

        syncTextView()
    }

    private fun syncTextView() {
        isActivatedTextView.text = HookUtils.isSelfModuleActivated.toString()
        xposedApiVersionTextView.text = HookUtils.xposedAPIVersion.toString()
    }
}
