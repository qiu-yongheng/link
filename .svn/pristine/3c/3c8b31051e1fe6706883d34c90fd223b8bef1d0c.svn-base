package com.omni.support.widget.ui.dialog

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.SPUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.omni.support.widget.R
import com.omni.support.widget.base.BaseDialogFragment
import kotlinx.android.synthetic.main.widget_dialog_scanning.*
import java.util.HashMap

/**
 * @author 邱永恒
 *
 * @time 2019/9/21 10:32
 *
 * @desc
 *
 */
class ScanningDialog : BaseDialogFragment(), BluetoothAdapter.LeScanCallback {
    private lateinit var adapter: DeviceAdapter
    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var listener: OnScanListener? = null
    private val map = HashMap<String, String>()
    private val handler = Handler()
    var filterRSSI: Int = 0
    var filterName: String? = null

    override fun getLayoutId(): Int {
        return R.layout.widget_dialog_scanning
    }

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        recycler_view.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        adapter = DeviceAdapter()
        adapter.bindToRecyclerView(recycler_view)

        val progress = SPUtils.getInstance().getInt("rssi_progress", 30)
        sb_rssi.max = 60
        filterRSSI = -(progress + 40)
        tv_rssi.text = "${filterRSSI}db"
        sb_rssi.progress = progress
    }

    override fun initListener() {
        bluetoothManager = context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager?.adapter
        bluetoothAdapter?.enable()
        SystemClock.sleep(100)
        startScan()
    }

    private fun startScan() {
        PermissionUtils.permission(PermissionConstants.LOCATION)
            .callback(object : PermissionUtils.SimpleCallback {
                override fun onGranted() {
                    map.clear()
                    adapter.setNewData(null)
                    bluetoothAdapter?.startLeScan(this@ScanningDialog)
                    btn_start.isEnabled = false
                    btn_stop.isEnabled = true
                    handler.postDelayed({
                        bluetoothAdapter?.stopLeScan(this@ScanningDialog)
                        btn_start.isEnabled = true
                        btn_stop.isEnabled = true
                    }, 10000)
                }

                override fun onDenied() {
                    dismiss()
                }
            })
            .request()
    }

    override fun onLeScan(bluetoothDevice: BluetoothDevice, rssi: Int, bytes: ByteArray) {
        Log.d("=====", "搜索到设备: " + bluetoothDevice.name + ", " + bluetoothDevice.address)
        val name = bluetoothDevice.name
        val address = bluetoothDevice.address
        if (name == null || address == null ||  rssi < filterRSSI || (!TextUtils.isEmpty(filterName) && !name.contains(filterName!!))) {
            return
        }
        if (map[address] == null) {
            map[address] = address
            adapter.addData(Device(bluetoothDevice, rssi))
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        bluetoothAdapter?.stopLeScan(this)
        handler.removeCallbacksAndMessages(null)
    }

    fun setOnScanListener(listener: OnScanListener) {
        this.listener = listener
    }

    interface OnScanListener {
        fun onChoose(device: Device)
    }

}

data class Device(val device: BluetoothDevice, val rssi: Int)

class DeviceAdapter : BaseQuickAdapter<Device, BaseViewHolder>(R.layout.widget_item_scanning) {
    override fun convert(helper: BaseViewHolder, item: Device) {
        val device = item.device
        val name = device.name
        val address = device.address
        val rssi = item.rssi
        helper.setText(R.id.tv_name, name)
            .setText(R.id.tv_mac, address)
            .setText(R.id.tv_rssi, "${rssi}db")
    }
}

