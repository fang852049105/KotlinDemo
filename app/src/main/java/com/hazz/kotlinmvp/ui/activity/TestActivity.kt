package com.hazz.kotlinmvp.ui.activity

import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.fxq.kotlin.mvvm.ui.fragment.ColorPickerFragment
import com.fxq.lib.widget.colorpicker.OnColorChangeListener
import com.hazz.kotlinmvp.R
import com.hazz.kotlinmvp.base.BaseActivity
import kotlinx.android.synthetic.main.activity_test.tv_show

/**
 * Author: Fanxq
 * Date: 2021/2/5
 */
class TestActivity : BaseActivity() {

  private var colorPickerFragment: ColorPickerFragment?= null
  //private var currentColorId = -16711936
  //private var currentColorId = -16777216
  private var currentColorId = 0


  override fun layoutId(): Int {
    return R.layout.activity_test
  }

  override fun initData() {
  }

  override fun initView() {
    tv_show.setBackgroundColor(currentColorId)
    tv_show.setOnClickListener(View.OnClickListener {
      //cpv_color_picker.visibility = View.VISIBLE
      colorPickerFragment?.let {
        it.colorId = currentColorId
        it.show(supportFragmentManager, "colorPicker")
      } ?: ColorPickerFragment().let {
        colorPickerFragment = it
        it.mOnColorChangeListener = OnColorChangeListener { color,isEnd ->
          if (!isEnd) {
            return@OnColorChangeListener
          }
          if (currentColorId == color) {
            return@OnColorChangeListener
          }
          tv_show.setBackgroundColor(color)
          currentColorId = color
          Log.e("fxq", "color = $color")
        }
        it.colorId = currentColorId
        it.show(supportFragmentManager, "colorPicker")
      }
    })
  }

  override fun start() {
  }

  override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      finish()
    }
    return super.onKeyUp(keyCode, event)
  }
  //
  // override fun dispatchKeyEvent(event: KeyEvent): Boolean {
  //   return if (event.keyCode == KeyEvent.KEYCODE_SEARCH) {
  //     true
  //   } else super.dispatchKeyEvent(event)
  // }
}