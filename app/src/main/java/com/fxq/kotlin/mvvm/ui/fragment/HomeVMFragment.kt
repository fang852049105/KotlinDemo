package com.fxq.kotlin.mvvm.ui.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.fxq.kotlin.mvvm.viewmodel.HomeViewModel
import com.hazz.kotlinmvp.R
import com.hazz.kotlinmvp.base.BaseFragment
import com.hazz.kotlinmvp.mvp.model.bean.HomeBean
import com.hazz.kotlinmvp.net.exception.ErrorStatus
import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.hazz.kotlinmvp.showToast
import com.hazz.kotlinmvp.ui.activity.SearchActivity
import com.hazz.kotlinmvp.ui.adapter.HomeAdapter
import com.hazz.kotlinmvp.utils.StatusBarUtil
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.header.MaterialHeader
import kotlinx.android.synthetic.main.fragment_home.iv_search
import kotlinx.android.synthetic.main.fragment_home.mRecyclerView
import kotlinx.android.synthetic.main.fragment_home.mRefreshLayout
import kotlinx.android.synthetic.main.fragment_home.multipleStatusView
import kotlinx.android.synthetic.main.fragment_home.toolbar
import kotlinx.android.synthetic.main.fragment_home.tv_header_title
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Author: Fanxq
 * Date: 2020/12/23
 */
class HomeVMFragment : BaseFragment() {

  private var mHomeAdapter: HomeAdapter? = null
  private var mMaterialHeader: MaterialHeader? = null
  private lateinit var mHomeViewModel: HomeViewModel
  private var mTitle: String? = null

  companion object {
    fun getInstance(title: String): HomeVMFragment {
      val fragment = HomeVMFragment()
      val bundle = Bundle()
      fragment.arguments = bundle
      fragment.mTitle = title
      return fragment
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    mHomeViewModel = ViewModelProvider(viewModelStore, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))[HomeViewModel::class.java]
    super.onCreate(savedInstanceState)
  }
  override fun getLayoutId(): Int {
    return R.layout.fragment_home
  }

  private val linearLayoutManager by lazy {
    LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
  }

  private val simpleDateFormat by lazy {
    SimpleDateFormat("- MMM. dd, 'Brunch' -", Locale.ENGLISH)
  }


  override fun initView() {
    mRefreshLayout.setEnableHeaderTranslationContent(true)
    mRefreshLayout.setOnRefreshListener {
      mHomeViewModel.isRefresh = true
      showLoading()
      mHomeViewModel.loadHomeData(mHomeViewModel.num)
    }
    mMaterialHeader = mRefreshLayout.refreshHeader as MaterialHeader?
    //打开下拉刷新区域块背景:
    mMaterialHeader?.setShowBezierWave(true)
    //设置下拉刷新主题颜色
    mRefreshLayout.setPrimaryColorsId(R.color.color_light_black, R.color.color_title_bg)
    mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
          val childCount = mRecyclerView.childCount
          val itemCount = mRecyclerView.layoutManager.itemCount
          val firstVisibleItem = (mRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
          if (firstVisibleItem + childCount == itemCount) {
            if (!mHomeViewModel.loadingMore) {
              mHomeViewModel.loadingMore = true;
              mHomeViewModel.loadMoreData()
            }
          }
        }
      }

      //RecyclerView滚动的时候调用
      override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val currentVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
        if (currentVisibleItemPosition == 0) {
          //背景设置为透明
          toolbar.setBackgroundColor(getColor(R.color.color_translucent))
          iv_search.setImageResource(R.mipmap.ic_action_search_white)
          tv_header_title.text = ""
        } else {
          if (mHomeAdapter?.mData!!.size > 1) {
            toolbar.setBackgroundColor(getColor(R.color.color_title_bg))
            iv_search.setImageResource(R.mipmap.ic_action_search_black)
            val itemList = mHomeAdapter!!.mData
            val item = itemList[currentVisibleItemPosition + mHomeAdapter!!.bannerItemSize - 1]
            if (item.type == "textHeader") {
              tv_header_title.text = item.data?.text
            } else {
              tv_header_title.text = simpleDateFormat.format(item.data?.date)
            }
          }
        }
      }
    })

    iv_search.setOnClickListener { openSearchActivity() }

    mLayoutStatusView = multipleStatusView

    //状态栏透明和间距处理
    activity?.let { StatusBarUtil.darkMode(it) }
    activity?.let { StatusBarUtil.setPaddingSmart(it, toolbar) }
  }

  private fun openSearchActivity() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      val options = activity?.let { ActivityOptionsCompat.makeSceneTransitionAnimation(it, iv_search, iv_search.transitionName) }
      startActivity(Intent(activity, SearchActivity::class.java), options?.toBundle())
    } else {
      startActivity(Intent(activity, SearchActivity::class.java))
    }
  }


  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    mHomeViewModel.geBannerHomeBeanLiveData().observe(this, Observer<HomeBean> {homebean ->
      dismissLoading()
      if (homebean == null) {
        showError(ExceptionHandle.handleException(Throwable()), ExceptionHandle.errorCode)
      } else {
        mLayoutStatusView?.showContent()
        Logger.d(homebean)

        // Adapter
        mHomeAdapter = activity?.let { HomeAdapter(it, homebean!!.issueList[0].itemList) }
        //设置 banner 大小
        mHomeAdapter?.setBannerSize(homebean!!.issueList[0].count)

        mRecyclerView.adapter = mHomeAdapter
        mRecyclerView.layoutManager = linearLayoutManager
        mRecyclerView.itemAnimator = DefaultItemAnimator()
      }
    })
    mHomeViewModel.getHomeListBeanLiveData().observe(this, Observer<ArrayList<HomeBean.Issue.Item>> {
      if (it != null) {
        mHomeAdapter?.addItemData(it)
      }
    })
  }


  /**
   * 显示错误信息
   */
  fun showError(msg: String, errorCode: Int) {
    showToast(msg)
    if (errorCode == ErrorStatus.NETWORK_ERROR) {
      mLayoutStatusView?.showNoNetwork()
    } else {
      mLayoutStatusView?.showError()
    }
  }

  override fun lazyLoad() {
    showLoading()
    mHomeViewModel.loadHomeData(mHomeViewModel.num)
  }

  /**
   * 显示 Loading （下拉刷新的时候不需要显示 Loading）
   */
  fun showLoading() {
    if (!mHomeViewModel.isRefresh) {
      mLayoutStatusView?.showLoading()
    }
  }

  /**
   * 隐藏 Loading
   */
   fun dismissLoading() {
    mRefreshLayout.finishRefresh()
  }

  fun getColor(colorId: Int): Int {
    return resources.getColor(colorId)
  }
}