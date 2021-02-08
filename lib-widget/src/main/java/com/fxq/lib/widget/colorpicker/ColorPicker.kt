package com.fxq.lib.widget.colorpicker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.fxq.lib.widget.R

/**
 * Author: Fanxq
 * Date: 2021/2/5
 */
class ColorPicker : RelativeLayout {

  private lateinit var mLLColorProgress: View
  private lateinit var mColorBarDot: View
  private lateinit var mLLProgress: LinearLayout
  private lateinit var mLocation: View
  private lateinit var mFakeBgColor: View
  private lateinit var mBgColor: CardView
  private lateinit var colorBarLayoutParams: LayoutParams
  var onColorChangeListener: OnColorChangeListener? = null
  private lateinit var vLocationLayoutParams: LayoutParams
  private val mCurrentHSV = FloatArray(3)
  private val mBgHSV = floatArrayOf(1f, 1f, 1f)

  constructor(context: Context) : super(context) {
    initView(context)
  }

  constructor(
    context: Context,
    attrs: AttributeSet?
  ) : super(context, attrs) {
    initView(context)
  }

  constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
  ) : super(context, attrs, defStyleAttr) {
    initView(context)
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun initView(context: Context) {
    val view: View = LayoutInflater.from(context).inflate(R.layout.view_color_picker, this)
    mBgColor = view.findViewById(R.id.fl_color)
    mFakeBgColor = view.findViewById(R.id.rl_fake_color)
    mLLProgress = view.findViewById(R.id.ll_progress)
    mLocation = view.findViewById(R.id.view_location)
    vLocationLayoutParams = mLocation.layoutParams as LayoutParams
    mLLColorProgress = findViewById(R.id.ll_color_progress)
    mColorBarDot = view.findViewById(R.id.view_color_bar_dot)
    colorBarLayoutParams = mColorBarDot.layoutParams as LayoutParams

    /*调整颜色*/
    mLLColorProgress.setOnTouchListener { _, event ->
      val width = mLLColorProgress.width
      val leftMargin = event.x
      var x = 0f
      if (leftMargin < mColorBarDot.width / 2.0f) {
        colorBarLayoutParams.leftMargin = 0
      } else if (leftMargin > width - mColorBarDot.width / 2.0f) {
        x = 100f
        colorBarLayoutParams.leftMargin = width - mColorBarDot.width
      } else {
        x = event.x / width * 100
        colorBarLayoutParams.leftMargin = (leftMargin - mColorBarDot.width / 2).toInt()
      }
      mColorBarDot.layoutParams = colorBarLayoutParams
      onProgressChanged(x)
      true
    }

    /*调整颜色明暗*/
    mFakeBgColor.setOnTouchListener { _, event ->
      val width = mFakeBgColor.width
      val height = mFakeBgColor.height
      val action = event.action
      val leftMargin: Int
      val topMargin: Int
      when (action) {
        MotionEvent.ACTION_DOWN -> {
        }
        MotionEvent.ACTION_MOVE -> {
          //防止越界处理
          leftMargin = if (event.x > width - mLocation.width / 2f) {
            width - mLocation.width
          } else if (event.x < mLocation.width / 2f) {
            0
          } else {
            (event.x - mLocation.width / 2f).toInt()
          }
          topMargin = if (event.y > height - mLocation.height / 2f) {
            height - mLocation.height
          } else if (event.y <= mLocation.height / 2f) {
            0
          } else {
            (event.y - mLocation.height / 2f).toInt()
          }
          vLocationLayoutParams.leftMargin = leftMargin
          vLocationLayoutParams.topMargin = topMargin
          mLocation.layoutParams = vLocationLayoutParams
          changeColor()
        }
        MotionEvent.ACTION_UP -> {
        }
      }
      true
    }
  }

  /**
   * 颜色值调整
   *
   * @param progressColor
   */
  @SuppressLint("WrongConstant")
  private fun onProgressChanged(progressColor: Float) {
    var x = progressColor
    if (x == 0f) {
      x = 0.001f
    }
    var colorHue = 360f * x / 100f
    setColorHue(colorHue)
    mBgColor.setCardBackgroundColor(Color.HSVToColor(mBgHSV))
    showColorBarDot()
    changeColor()
  }

  private fun showColorBarDot() {
    val gd = GradientDrawable()
    gd.setColor(Color.HSVToColor(mBgHSV))
    gd.cornerRadius = 24f
    gd.setStroke(9, Color.parseColor("#ffffff")) //描边的颜色和宽度
    mColorBarDot.background = gd
  }

  /**
   * 颜色明暗度调整
   */
  private fun changeColor() {
    var hPercent = mLocation.x / (mFakeBgColor.width - mLocation.width)
    var vPercent = mLocation.y / (mFakeBgColor.height - mLocation.height)
    setColorSat(1f * hPercent) //颜色深浅
    setColorVal(1f - 1f * vPercent) //颜色明暗
    val color = Color.HSVToColor(mCurrentHSV)

    onColorChangeListener?.colorChanged(color)
  }

  fun initColor(color: Int) {
    Color.colorToHSV(color, mCurrentHSV)
    mBgHSV[0] = mCurrentHSV[0]
    showColorBarDot()
    mBgColor.setCardBackgroundColor(Color.HSVToColor(mBgHSV))
    mLocation.post {
      colorBarLayoutParams.leftMargin = (mLLColorProgress.width * mCurrentHSV[0] / 360f).toInt()
      mColorBarDot.layoutParams = colorBarLayoutParams
      vLocationLayoutParams.leftMargin = ((mFakeBgColor.width - mLocation.width) * mCurrentHSV[1]).toInt()
      vLocationLayoutParams.topMargin = ((mFakeBgColor.height - mLocation.height) * (1f- mCurrentHSV[2])).toInt()
      mLocation.layoutParams = vLocationLayoutParams
    }
  }


  /**
   * 设置色彩
   * @param color
   */
  private fun setColorHue(color: Float) {
    mBgHSV[0] = color
    mCurrentHSV[0] = color
  }

  /**
   * 设置颜色深浅
   */
  private fun setColorSat(color: Float) {
    mCurrentHSV[1] = color
  }

  /**
   * 设置颜色明暗
   */
  private fun setColorVal(color: Float) {
    mCurrentHSV[2] = color
  }

  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    parent.requestDisallowInterceptTouchEvent(true)
    return super.dispatchTouchEvent(ev)
  }
}