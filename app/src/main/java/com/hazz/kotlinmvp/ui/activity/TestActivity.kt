package com.hazz.kotlinmvp.ui.activity

import android.util.Log
import android.widget.TextView
import com.fxq.lib.widget.colorpicker.OnColorChangeListener
import com.hazz.kotlinmvp.R
import com.hazz.kotlinmvp.base.BaseActivity
import kotlinx.android.synthetic.main.activity_test.cpv_color_picker
import kotlinx.android.synthetic.main.activity_test.tv_test

/**
 * Author: Fanxq
 * Date: 2021/2/5
 */
class TestActivity : BaseActivity() {

  override fun layoutId(): Int {
    return R.layout.activity_test
  }

  override fun initData() {
  }

  override fun initView() {
    cpv_color_picker.setOnColorChangeListener(object : OnColorChangeListener {
      override fun colorChanged(color: Int) {
        Log.e("fxq", "color = $color")
        tv_test.setBackgroundColor(color)
      }
    })
  }

  override fun start() {
  }
}