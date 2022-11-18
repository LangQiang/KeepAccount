package com.atool.apm.dokit.ui.debugpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.atool.apm.R
import com.lazylite.mod.App
import com.lazylite.mod.config.ConfMgr
import com.lazylite.mod.utils.DeviceInfo
import com.lazylite.mod.utils.SoftKeyboardHelper
import com.lazylite.mod.utils.toast.KwToast
import com.lazylite.mod.widget.BaseFragment
import android.app.PendingIntent

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.godq.deeplink.DeepLinkUtils
import com.lazylite.mod.receiver.network.NetworkStateUtil


class DebugFragment : BaseFragment() {

    private val schemeKey = "debug_scheme"

    var schemeEditET: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return View.inflate(context, R.layout.debug_fragment_layout, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        schemeEditET = view.findViewById(R.id.scheme_et)
        schemeEditET?.setText(ConfMgr.getStringValue("", schemeKey, ""))
        view.findViewById<View>(R.id.scheme_btn).setOnClickListener {
            schemeJump()
        }
        view.findViewById<View>(R.id.device_info_btn).setOnClickListener {
            showDeviceInfo()
        }
        val alarmMgr = context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        val intent = Intent(context, NetworkStateUtil::class.java)
        val alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

        alarmMgr?.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() +
                    60 * 1000, alarmIntent
        )
    }

    private fun showDeviceInfo() {
        val info = String.format("设备性能评级：%.2f", DeviceInfo.devicePerformanceScore())
        KwToast.showSysToast(info)
    }

    private fun schemeJump() {
        val scheme = schemeEditET?.text?.toString()
        ConfMgr.setStringValue("", schemeKey, scheme, false)
        DeepLinkUtils.load(scheme).execute()
        SoftKeyboardHelper.hideKeyboard(App.getInstance())
    }
}