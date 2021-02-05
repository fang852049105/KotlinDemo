package com.fxq.kotlin.mvvm.ui.activity

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.FragmentTransaction
import android.view.KeyEvent
import com.flyco.tablayout.listener.OnTabSelectListener
import com.fxq.kotlin.mvvm.ui.fragment.HomeVMFragment
import com.fxq.kotlin.mvvm.viewmodel.MainViewModel
import com.hazz.kotlinmvp.R
import com.hazz.kotlinmvp.base.BaseActivity
import com.hazz.kotlinmvp.mvp.model.bean.TabEntity
import com.hazz.kotlinmvp.showToast
import com.hazz.kotlinmvp.ui.fragment.DiscoveryFragment
import com.hazz.kotlinmvp.ui.fragment.HomeFragment
import com.hazz.kotlinmvp.ui.fragment.HotFragment
import com.hazz.kotlinmvp.ui.fragment.MineFragment
import kotlinx.android.synthetic.main.activity_main.tab_layout

/**
 * Author: Fanxq
 * Date: 2020/12/21
 */
class MainMVActivity : BaseActivity() {

  private lateinit var mMainViewModel: MainViewModel

  //private var mHomeFragment: HomeFragment? = null
  private var mHomeFragment: HomeVMFragment? = null
  private var mDiscoveryFragment: DiscoveryFragment? = null
  private var mHotFragment: HotFragment? = null
  private var mMineFragment: MineFragment? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mMainViewModel = ViewModelProvider(viewModelStore, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[MainViewModel::class.java]
    if (savedInstanceState != null) {
      mMainViewModel.setIndx(savedInstanceState.getInt("currTabIndex"))
    }
    initTab()
    tab_layout.currentTab = mMainViewModel.getIndex()
    switchFragment(mMainViewModel.getIndex())
  }


  //初始化底部菜单
  private fun initTab() {
    (0 until mMainViewModel.getTitles().size)
      .mapTo(mMainViewModel.getTabEntities()) { TabEntity( mMainViewModel.getTitles()[it], mMainViewModel.getIconSelectIds()[it], mMainViewModel.getIconUnSelectIds()[it]) }
    //为Tab赋值
    tab_layout.setTabData(mMainViewModel.getTabEntities())
    tab_layout.setOnTabSelectListener(object : OnTabSelectListener {
      override fun onTabSelect(position: Int) {
        //切换Fragment
        switchFragment(position)
      }

      override fun onTabReselect(position: Int) {

      }
    })
  }

  /* 切换Fragment
  * @param position 下标
  */
  private fun switchFragment(position: Int) {
    val transaction = supportFragmentManager.beginTransaction()
    hideFragments(transaction)
    when (position) {
      0 // 首页
      -> mHomeFragment?.let {
        transaction.show(it)
      } ?: HomeVMFragment.getInstance(mMainViewModel.getTitles()[position]).let {
        mHomeFragment = it
        transaction.add(R.id.fl_container, it, "home")
      }
      1  //发现
      -> mDiscoveryFragment?.let {
        transaction.show(it)
      } ?: DiscoveryFragment.getInstance(mMainViewModel.getTitles()[position]).let {
        mDiscoveryFragment = it
        transaction.add(R.id.fl_container, it, "discovery") }
      2  //热门
      -> mHotFragment?.let {
        transaction.show(it)
      } ?: HotFragment.getInstance(mMainViewModel.getTitles()[position]).let {
        mHotFragment = it
        transaction.add(R.id.fl_container, it, "hot") }
      3 //我的
      -> mMineFragment?.let {
        transaction.show(it)
      } ?: MineFragment.getInstance(mMainViewModel.getTitles()[position]).let {
        mMineFragment = it
        transaction.add(R.id.fl_container, it, "mine") }

      else -> {

      }
    }

    mMainViewModel.setIndx(position)
    tab_layout.currentTab = mMainViewModel.getIndex()
    transaction.commitAllowingStateLoss()
  }

  /**
   * 隐藏所有的Fragment
   * @param transaction transaction
   */
  private fun hideFragments(transaction: FragmentTransaction) {
    mHomeFragment?.let { transaction.hide(it) }
    mDiscoveryFragment?.let { transaction.hide(it) }
    mHotFragment?.let { transaction.hide(it) }
    mMineFragment?.let { transaction.hide(it) }
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    super.onSaveInstanceState(outState)
    if (tab_layout != null) {
      outState?.putInt("currTabIndex", mMainViewModel.getIndex())
    }
  }

  override fun layoutId(): Int {
    return R.layout.activity_main
  }

  override fun initData() {
  }

  override fun initView() {
  }

  override fun start() {
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if (System.currentTimeMillis().minus(mMainViewModel.getExitTime()) <= 2000) {
        finish()
      } else {
        mMainViewModel.setExitTime(System.currentTimeMillis())
        showToast("再按一次退出程序")
      }
      return true
    }
    return super.onKeyDown(keyCode, event)
  }
}