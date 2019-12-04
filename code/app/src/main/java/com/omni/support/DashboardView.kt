package com.omni.support

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.ImageView

/**
 * @author 邱永恒
 *
 * @time 2019/11/19 16:18
 *
 * @desc
 *
 */
class DashboardView : FrameLayout {
    private lateinit var ivPointer: ImageView


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        LayoutInflater.from(context).inflate(R.layout.widget_dashboard, this)
        initView()
    }

    private fun initView() {
        ivPointer = findViewById(R.id.iv_pointer)

        val ra = RotateAnimation(
            0f, -90f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 1f
        )
        ra.duration = 2100
        ra.fillAfter = true

        ivPointer.startAnimation(ra)

//        ivPointer.post {
//            ivPointer.rotationX = width / 2f
//            ivPointer.rotationY = 0f
//            ivPointer.rotation = 45f
//        }


    }

}