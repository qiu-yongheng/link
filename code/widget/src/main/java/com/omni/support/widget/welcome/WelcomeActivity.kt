package com.omni.support.widget.welcome

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.blankj.utilcode.util.AppUtils
import com.omni.support.widget.R
import com.omni.support.widget.base.BaseActivity
import kotlinx.android.synthetic.main.widget_activity_welcome.*

/**
 * @author 邱永恒
 *
 * @time 2019/8/3 16:12
 *
 * @desc
 *
 */
abstract class WelcomeActivity : BaseActivity() {
    var duration: Long = 1000

    override fun getLayoutId(): Int {
        return R.layout.widget_activity_welcome
    }

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        tv_version.text = "v${AppUtils.getAppVersionName()}"

        initAnim()
    }

    private fun initAnim() {
        val alphaAnimation = AlphaAnimation(0.5f, 1f)
        alphaAnimation.duration = duration
        iv_icon.startAnimation(alphaAnimation)

        alphaAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                onAnimationEnd()
            }

            override fun onAnimationRepeat(animation: Animation) {
            }
        })
    }

    abstract fun getImgId(): Int
    abstract fun onAnimationEnd()

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, WelcomeActivity::class.java)
            context.startActivity(intent)
        }
    }
}