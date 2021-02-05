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
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.fxq.lib.widget.R

/**
 * Author: Fanxq
 * Date: 2021/2/5
 */
class ColorPicker : LinearLayout {

  private lateinit var llColorProgress: View
  private lateinit var vColorBarDot: View
  private var red = 255
  private var green = 0
  private var blue = 0
  private var index = 0
  private lateinit var llProgress: LinearLayout
  private lateinit var vLocation: View
  private lateinit var vBgColor: CardView
  private lateinit var colorBarLayoutParams: RelativeLayout.LayoutParams
  private var onColorChangeListener: OnColorChangeListener? = null
  private lateinit var vLocationLayoutParams: FrameLayout.LayoutParams
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

  private fun initView(context: Context) {
    val view: View = LayoutInflater.from(context).inflate(R.layout.view_color_picker, this)
    vBgColor = view.findViewById(R.id.fl_color)
    llProgress = view.findViewById(R.id.ll_progress)
    vLocation = view.findViewById(R.id.view_location)
    vLocationLayoutParams = vLocation.getLayoutParams() as FrameLayout.LayoutParams
    llColorProgress = findViewById(R.id.ll_color_progress)
    vColorBarDot = view.findViewById(R.id.view_color_bar_dot)
    colorBarLayoutParams = vColorBarDot.getLayoutParams() as RelativeLayout.LayoutParams

    /*调整颜色*/
    llColorProgress.setOnTouchListener({ v, event ->
      val width = llColorProgress?.getWidth()
      val leftMargin = event.x
      var x = 0f
      if (leftMargin < vColorBarDot?.getWidth() / 2.0f) {
        colorBarLayoutParams?.leftMargin = 0
      } else if (leftMargin > width - vColorBarDot.getWidth() / 2.0f) {
        x = 100f
        colorBarLayoutParams?.leftMargin = width - vColorBarDot.getWidth()
      } else {
        x = event.x / width * 100
        colorBarLayoutParams?.leftMargin = (leftMargin - vColorBarDot.getWidth() / 2).toInt()
      }
      vColorBarDot.setLayoutParams(colorBarLayoutParams)
      onProgressChanged(x.toInt())
      true
    })

    /*调整颜色明暗*/
    vBgColor.setOnTouchListener { v, event ->
      val width = vBgColor?.width
      val height = vBgColor?.height
      val action = event?.action
      val leftMargin: Int
      val topMargin: Int
      when (action) {
        MotionEvent.ACTION_DOWN -> {
        }
        MotionEvent.ACTION_MOVE -> {
          //防止越界处理
          leftMargin = if (event.x > width - vLocation.width / 2f) {
            width - vLocation.width
          } else if (event.x < vLocation.width / 2f) {
            0
          } else {
            (event.x - vLocation.width / 2f).toInt()
          }
          topMargin = if (event.y > height - vLocation.height / 2f) {
            height - vLocation.height
          } else if (event.y <= vLocation.height / 2f) {
            0
          } else {
            (event.y - vLocation.height / 2f).toInt()
          }
          vLocationLayoutParams.leftMargin = leftMargin
          vLocationLayoutParams.topMargin = topMargin
          vLocation.layoutParams = vLocationLayoutParams
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
    vBgColor?.setCardBackgroundColor(Color.rgb(red, green, blue))
    showColorBarDot()
    changeColor()
  }

  private fun showColorBarDot() {
    val gd = GradientDrawable()
    gd.setColor(Color.rgb(red, green, blue))
    gd.cornerRadius = 24f
    gd.setStroke(9, Color.parseColor("#ffffff")) //描边的颜色和宽度
    vColorBarDot.setBackground(gd)
  }

  /**
   * 颜色明暗度调整
   */
  private fun changeColor() {
    var tempRed = red
    var tempGreen = green
    var tempBlue = blue
    val hPercent =
      1 - vLocation?.x / (vBgColor?.width - vLocation?.width)
    val vPercent = vLocation?.y / (vBgColor?.height - vLocation?.height)
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
    val layoutParams = vLocation.layoutParams as FrameLayout.LayoutParams
    layoutParams.leftMargin = vBgColor.width - vLocation.width
    vLocation.layoutParams = layoutParams
    colorBarLayoutParams.leftMargin = llColorProgress.width - vColorBarDot.width
    vColorBarDot.layoutParams = colorBarLayoutParams
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

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    vBgColor.post {
      val layoutParams = vBgColor.layoutParams as LayoutParams
      layoutParams.height = h - llProgress.height
      vBgColor.layoutParams = layoutParams
    }
  }

  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    parent.requestDisallowInterceptTouchEvent(true)
    return super.dispatchTouchEvent(ev)
  }
}