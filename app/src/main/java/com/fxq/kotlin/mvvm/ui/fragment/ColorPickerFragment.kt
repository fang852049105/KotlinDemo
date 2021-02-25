package com.fxq.kotlin.mvvm.ui.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import com.fxq.lib.widget.colorpicker.ColorPicker
import com.fxq.lib.widget.colorpicker.OnColorChangeListener
import com.hazz.kotlinmvp.R

/**
 * Author: Fanxq
 * Date: 2021/2/7
 */
class ColorPickerFragment : DialogFragment() {

  var mOnColorChangeListener: OnColorChangeListener? = null
  var colorId = -3987159
  private lateinit var mColorPicker: ColorPicker
  private lateinit var mHandleView: FrameLayout
  private lateinit var mTopView: View

  private var mLastYPosition = 0f
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val activity: Activity = activity!!
    val view = getDialogView(activity!!)
    val builder = AlertDialog.Builder(activity)
      .setView(view)
    return builder.create()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    updatePosition()
    return super.onCreateView(inflater, container, savedInstanceState)
  }

  private fun getDialogView(activity: Activity): View? {
    val view: View = LayoutInflater.from(activity).inflate(R.layout.fragment_color_picker, null)
    mColorPicker = view.findViewById(R.id.cpv_color_picker)
    mTopView = view.findViewById(R.id.top_view)
    mTopView.setOnClickListener {
      dismissAllowingStateLoss()
    }
    mHandleView = view.findViewById(R.id.board_top)
    mHandleView.setOnTouchListener({ view, event ->
      when (event.action) {
        MotionEvent.ACTION_DOWN -> {
          mLastYPosition = event.y

        }
        MotionEvent.ACTION_MOVE -> {
          Log.e("fxq", "mLastYPosition = $mLastYPosition")
          Log.e("fxq", "event.y = " + event.y)
          if (event.y - mLastYPosition > 15) {
            dismissAllowingStateLoss()
          }
        }
        MotionEvent.ACTION_UP -> {
        }
      }
      true
    })
    mColorPicker.onColorChangeListener = mOnColorChangeListener
    mColorPicker.initColor(colorId)
    mHandleView.setOnClickListener {
      dismissAllowingStateLoss()
    }
    return view
  }

  override fun onStart() {
    super.onStart()
    val window = dialog.window
    val windowParams = window!!.attributes
    windowParams.dimAmount = 0.0f
    window.attributes = windowParams
    val manager = window.windowManager
    val outMetrics = DisplayMetrics()
    manager.defaultDisplay.getMetrics(outMetrics)
    window.decorView.setPadding(0, 0, 0, 0)
    window.setBackgroundDrawableResource(android.R.color.transparent)
    dialog.setCancelable(false)
    dialog.window
      ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    dialog.setOnKeyListener { dialog, keyCode, event ->
      if (keyCode === KeyEvent.KEYCODE_BACK) {
        mHandleView.performClick()
        return@setOnKeyListener true
      }
      false
    }

  }



  fun updatePosition() {
    val dialog = dialog ?: return
    val window = dialog.window
    val layoutParams = window!!.attributes
    layoutParams.gravity = Gravity.BOTTOM
    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
    window.attributes = layoutParams
  }
}