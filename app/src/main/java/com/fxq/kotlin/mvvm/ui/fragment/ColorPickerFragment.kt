package com.fxq.kotlin.mvvm.ui.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
    mColorPicker.onColorChangeListener = mOnColorChangeListener
    mColorPicker.initColor(colorId)
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
    dialog.setCanceledOnTouchOutside(true)
    dialog.setCancelable(true)
    dialog.window
      ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

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