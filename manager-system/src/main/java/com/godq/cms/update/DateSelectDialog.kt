package com.godq.cms.update

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.godq.cms.R
import com.godq.cms.databinding.DialogDateSelectLayoutBinding
import com.lazylite.mod.dialogqueue.IDialogQueue
import com.lazylite.mod.dialogqueue.OnDialogQueueDismissListener
import com.lazylite.mod.utils.DeviceInfo
import com.lazylite.mod.utils.ScreenUtility
import timber.log.Timber
import java.util.*

class DateSelectDialog(private val activity: Activity, private val date: String?)
    : Dialog(activity, R.style.CommonDialogTheme), IDialogQueue {

    var onSelectCallback: ((dateStr: String?) -> Unit)? = null

    var dateStr: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        initView().apply {
            setContentView(this)
        }

        setOnDismissListener {
            mOnDialogQueueDismissListener?.onDialogQueueDismiss()
        }
    }

    private fun initView(): View {
        val binding =  DialogDateSelectLayoutBinding.inflate(LayoutInflater.from(context))
        binding.dialog = this

        val calendar = Calendar.getInstance()
        var y: Int
        var m: Int
        var d: Int
        if (date.isNullOrEmpty()) {
            y = calendar.get(Calendar.YEAR)
            m = calendar.get(Calendar.MONTH)
            d = calendar.get(Calendar.DAY_OF_MONTH)
            dateStr = String.format("%d-%02d-%02d", y, m + 1, d)
        } else {
            try {
                val splits = date.split('-')
                dateStr = date
                y = splits[0].toInt()
                m = splits[1].toInt() - 1
                d = splits[2].toInt()
            } catch (e: Exception) {
                y = calendar.get(Calendar.YEAR)
                m = calendar.get(Calendar.MONTH)
                d = calendar.get(Calendar.DAY_OF_MONTH)
                dateStr = String.format("%d-%02d-%02d", y, m + 1, d)
            }

        }
        binding.datePickerView.init(y, m, d) { _, year, monthOfYear, dayOfMonth ->
            Timber.tag("date").e("year:$year month:$monthOfYear day:$dayOfMonth ")
            dateStr = String.format("%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth)
        }
        return binding.root
    }

    override fun show() {
        if (activity.isFinishing || isShowing) {
            return
        }
        super.show()
        //修改系统menu菜单不能全屏显示问题
        window?.apply {
            this.decorView.setPadding(0, 0, 0, 0)
            val layoutParams = this.attributes
            layoutParams.width = DeviceInfo.WIDTH - ScreenUtility.dip2px(76f)
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            this.attributes = layoutParams
        }
    }

    fun confirm() {
        onSelectCallback?.invoke(dateStr)
        dismiss()
    }

    override fun dismiss() {
        if (activity.isFinishing) {
            return
        }
        try {
            super.dismiss()
        } catch (e:Exception) {

        }
    }

    override fun showDialog() {
        show()
    }

    private var mOnDialogQueueDismissListener : OnDialogQueueDismissListener? = null


    override fun setOnDialogQueueDismissListener(onDialogQueueDismissListener: OnDialogQueueDismissListener?) {
        mOnDialogQueueDismissListener = onDialogQueueDismissListener
    }

}