package com.omni.support.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author 邱永恒
 *
 * @time 2019/10/11 10:25
 *
 * @desc 时间选择工具类
 *
 */
object PickerUtils {
    const val YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm"
    const val HH_MM = "HH:mm"

    /**
     * 日期时间选择器
     */
    fun showDateTimePickerDialog(
        context: Context,
        pattern: String,
        listener: OnTimeSelectListener?
    ) {
        val now = Calendar.getInstance()

        // 显示日期选择器
        val datePickerDialog =
            DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val c = Calendar.getInstance()
                c.set(Calendar.YEAR, year)
                c.set(Calendar.MONTH, month)
                c.set(Calendar.DAY_OF_MONTH, day)

                // 显示时间选择器
                val timePickerDialog = TimePickerDialog(
                    context,
                    TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                        c.set(Calendar.HOUR_OF_DAY, hour)
                        c.set(Calendar.MINUTE, minute)
                        val df = SimpleDateFormat(pattern, Locale.getDefault())
                        val format = df.format(c.time)

                        listener?.onTimeSet(format, c)
                    },
                    c.get(Calendar.HOUR_OF_DAY),
                    c.get(Calendar.MINUTE),
                    true
                )

                timePickerDialog.show()
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    /**
     * 时间选择器
     */
    fun showTimePickerDialog(context: Context, pattern: String, listener: OnTimeSelectListener?) {
        val c = Calendar.getInstance()

        // 显示时间选择器
        val timePickerDialog =
            TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                c.set(Calendar.HOUR_OF_DAY, hour)
                c.set(Calendar.MINUTE, minute)
                val df = SimpleDateFormat(pattern, Locale.getDefault())
                val format = df.format(c.time)

                listener?.onTimeSet(format, c)
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true)

        timePickerDialog.show()
    }

    interface OnTimeSelectListener {
        fun onTimeSet(time: String, calendar: Calendar)
    }
}