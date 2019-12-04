package com.omni.support

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.zxing.client.android.activity.CaptureActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initListener()
    }

    private fun initView() {

    }

    private fun initListener() {
        btn_scan.setOnClickListener {
            startActivity(Intent(this, CaptureActivity::class.java))
        }

        btn_welcome.setOnClickListener {
            startActivity(Intent(this, MyBaseWelcomeActivity::class.java))
        }

        btn_dashboard.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        btn_ble.setOnClickListener {
            startActivity(Intent(this, BikeLockTestActivity::class.java))
        }

        btn_scooter.setOnClickListener {
            startActivity(Intent(this, ScooterTestActivity::class.java))
        }

        btn_wc.setOnClickListener {
            startActivity(Intent(this, WcTestActivity::class.java))
        }

        btn_keybox.setOnClickListener {
            startActivity(Intent(this, KeyboxTestActivity::class.java))
        }

        btn_ulock.setOnClickListener {
            startActivity(Intent(this, ULockTestActivity::class.java))
        }

        btn_carport.setOnClickListener {
            startActivity(Intent(this, CarportTestActivity::class.java))
        }

        btn_box.setOnClickListener{
            startActivity(Intent(this, BoxTestActivity::class.java))
        }

        btn_bsj.setOnClickListener{
            startActivity(Intent(this, BSJTestActivity::class.java))
        }

        btn_hj.setOnClickListener {
            startActivity(Intent(this, HJTestActivity::class.java))
        }

        btn_bloom.setOnClickListener {
            startActivity(Intent(this, BloomTestActivity::class.java))
        }

        btn_stem.setOnClickListener {
            startActivity(Intent(this, StemTestActivity::class.java))
        }
    }
}
