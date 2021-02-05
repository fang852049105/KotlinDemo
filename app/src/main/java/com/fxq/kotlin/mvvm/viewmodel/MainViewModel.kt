package com.fxq.kotlin.mvvm.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.flyco.tablayout.listener.CustomTabEntity
import com.hazz.kotlinmvp.R
import java.util.ArrayList

/**
 * Author: Fanxq
 * Date: 2020/12/21
 */
class MainViewModel : ViewModel() {

  private val mTitles = arrayOf("每日精选", "发现", "热门", "我的")
  // 未被选中的图标
  private val mIconUnSelectIds = intArrayOf(
    R.mipmap.ic_home_normal, R.mipmap.ic_discovery_normal, R.mipmap.ic_hot_normal, R.mipmap.ic_mine_normal)
  // 被选中的图标
  private val mIconSelectIds = intArrayOf(R.mipmap.ic_home_selected, R.mipmap.ic_discovery_selected, R.mipmap.ic_hot_selected, R.mipmap.ic_mine_selected)
  //默认为0
  private var mIndex = 0

  private val mTabEntities = ArrayList<CustomTabEntity>()

  private var mExitTime: Long = 0

  fun getIndex() = mIndex

  fun getTitles() = mTitles

  fun getIconUnSelectIds() = mIconUnSelectIds

  fun getIconSelectIds() = mIconSelectIds

  fun getTabEntities() = mTabEntities

  fun getExitTime() = mExitTime

  fun setIndx(index: Int) {
    mIndex = index
  }

  fun setExitTime(time: Long) {
    mExitTime = time
  }
}