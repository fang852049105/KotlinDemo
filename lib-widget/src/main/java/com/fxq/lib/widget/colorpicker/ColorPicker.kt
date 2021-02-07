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
  private var red = 255
  private var green = 0
  private var blue = 0
  private var index = 0
  private lateinit var mLLProgress: LinearLayout
  private lateinit var mLocation: View
  private lateinit var mFakeBgColor:View
  private lateinit var mBgColor: CardView
  private lateinit var colorBarLayoutParams: LayoutParams
  private var onColorChangeListener: OnColorChangeListener? = null
  private lateinit var vLocationLayoutParams: LayoutParams
  private val transValue = 255 //透明度

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
      onProgressChanged(x.toInt())
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
  private fun onProgressChanged(progressColor: Int) {
    red = 0
    green = 0
    blue = 0
    index = (progressColor / (100 / 6f)).toInt()
    val v = progressColor % (100 / 6f) / (100 / 6f)
    when (index) {
      0 -> {
        red = 255
        green = (255 * v).toInt()
      }
      1 -> {
        red = (255 * (1 - v)).toInt()
        green = 255
      }
      2 -> {
        green = 255
        blue = (255 * v).toInt()
      }
      3 -> {
        green = (255 * (1 - v)).toInt()
        blue = 255
      }
      4 -> {
        blue = 255
        red = (255 * v).toInt()
      }
      5 -> {
        blue = (255 * (1 - v)).toInt()
        red = 255
      }
      else -> red = 255
    }
    mBgColor.setCardBackgroundColor(Color.rgb(red, green, blue))
    showColorBarDot()
    changeColor()
  }

  private fun showColorBarDot() {
    val gd = GradientDrawable()
    gd.setColor(Color.rgb(red, green, blue))
    gd.cornerRadius = 24f
    gd.setStroke(9, Color.parseColor("#ffffff")) //描边的颜色和宽度
    mColorBarDot.background = gd
  }

  /**
   * 颜色明暗度调整
   */
  private fun changeColor() {
    var tempRed = red
    var tempGreen = green
    var tempBlue = blue
    val hPercent =
      1 - mLocation.x / (mFakeBgColor.width - mLocation.width)
    val vPercent = mLocation.y / (mFakeBgColor.height - mLocation.height)
    when (index) {
      0 -> {
        tempGreen = (green + hPercent * (255 - green)).toInt()
        tempBlue = (blue + hPercent * (255 - blue)).toInt()
      }
      1 -> {
        tempRed = (red + hPercent * (255 - red)).toInt()
        tempBlue = (blue + hPercent * (255 - blue)).toInt()
      }
      2 -> {
        tempRed = (red + hPercent * (255 - red)).toInt()
        tempBlue = (blue + hPercent * (255 - blue)).toInt()
      }
      3 -> {
        tempRed = (red + hPercent * (255 - red)).toInt()
        tempGreen = (green + hPercent * (255 - green)).toInt()
      }
      4 -> {
        tempRed = (red + hPercent * (255 - red)).toInt()
        tempGreen = (green + hPercent * (255 - green)).toInt()
      }
      5, 6 -> {
        tempGreen = (green + hPercent * (255 - green)).toInt()
        tempBlue = (blue + hPercent * (255 - blue)).toInt()
      }
    }
    tempRed = (tempRed - tempRed * vPercent).toInt()
    tempGreen = (tempGreen - tempGreen * vPercent).toInt()
    tempBlue = (tempBlue - tempBlue * vPercent).toInt()
    val color = Color.argb(transValue, tempRed, tempGreen, tempBlue)
    onColorChangeListener?.colorChanged(color)
  }

  override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
    super.onWindowFocusChanged(hasWindowFocus)
    val layoutParams = mLocation.layoutParams as LayoutParams
    layoutParams.leftMargin = mFakeBgColor.width - mLocation.width
    mLocation.layoutParams = layoutParams
    mLocation.post { changeColor() }
    colorBarLayoutParams.leftMargin = mLLColorProgress.width - mColorBarDot.width
    mColorBarDot.layoutParams = colorBarLayoutParams
    showColorBarDot()
  }

  /**
   * 设置该方法，颜色改变的时候会回调颜色值
   *
   * @param onColorChangeListener
   */
  fun setOnColorChangeListener(onColorChangeListener: OnColorChangeListener?) {
    this.onColorChangeListener = onColorChangeListener
  }

  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    parent.requestDisallowInterceptTouchEvent(true)
    return super.dispatchTouchEvent(ev)
  }
}