package com.fxq.kotlin.mvvm.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hazz.kotlinmvp.mvp.model.HomeModel
import com.hazz.kotlinmvp.mvp.model.bean.HomeBean
import io.reactivex.disposables.CompositeDisposable

/**
 * Author: Fanxq
 * Date: 2020/12/23
 */
class HomeViewModel : ViewModel() {

  private var mTitle: String? = null
  var num: Int = 1
  var loadingMore = false
  var isRefresh = false
  private val mCompositeDispose: CompositeDisposable = CompositeDisposable()
  private val homeModel: HomeModel by lazy {
    HomeModel()
  }

  private var bannerHomeBean: HomeBean? = null
  private val mBannerHomeBean: MutableLiveData<HomeBean> = MutableLiveData()
  private val mItemList: MutableLiveData<ArrayList<HomeBean.Issue.Item>> = MutableLiveData();
  private var nextPageUrl: String? = null     //加载首页的Banner 数据+一页数据合并后，nextPageUrl没 add


  fun geBannerHomeBeanLiveData() = mBannerHomeBean
  fun getHomeListBeanLiveData() = mItemList

  fun loadHomeData(num: Int) {
    val disposable = homeModel.requestHomeData(num)
      .flatMap { homeBean ->
        //过滤掉 Banner2(包含广告,等不需要的 Type), 具体查看接口分析
        val bannerItemList = homeBean.issueList[0].itemList

        bannerItemList.filter { item ->
          item.type == "banner2" || item.type == "horizontalScrollCard"
        }.forEach { item ->
          //移除 item
          bannerItemList.remove(item)
        }

        bannerHomeBean = homeBean //记录第一页是当做 banner 数据

        //根据 nextPageUrl 请求下一页数据
        homeModel.loadMoreData(homeBean.nextPageUrl)
      }
      .subscribe({ homeBean ->

        nextPageUrl = homeBean.nextPageUrl
        //过滤掉 Banner2(包含广告,等不需要的 Type), 具体查看接口分析
        val newBannerItemList = homeBean.issueList[0].itemList

        newBannerItemList.filter { item ->
          item.type == "banner2" || item.type == "horizontalScrollCard"
        }.forEach { item ->
          //移除 item
          newBannerItemList.remove(item)
        }
        // 重新赋值 Banner 长度
        bannerHomeBean!!.issueList[0].count = bannerHomeBean!!.issueList[0].itemList.size

        //赋值过滤后的数据 + banner 数据
        bannerHomeBean?.issueList!![0].itemList.addAll(newBannerItemList)
        mBannerHomeBean.postValue(bannerHomeBean)

      }, { t ->
        mBannerHomeBean.postValue(null)
      })
    if (disposable != null) {
      mCompositeDispose.add(disposable)
    }
  }

  fun loadMoreData() {
    val disposable = nextPageUrl?.let {
      homeModel.loadMoreData(it)
        .subscribe({ homeBean ->
          //过滤掉 Banner2(包含广告,等不需要的 Type), 具体查看接口分析
          val newItemList = homeBean.issueList[0].itemList

          newItemList.filter { item ->
            item.type == "banner2" || item.type == "horizontalScrollCard"
          }.forEach { item ->
            //移除 item
            newItemList.remove(item)
          }

          nextPageUrl = homeBean.nextPageUrl
          loadingMore = false
          mItemList.postValue(newItemList)
        }, { t ->
          mItemList.postValue(null)
        })

    }
    if (disposable != null) {
      mCompositeDispose.add(disposable)
    }
  }

  override fun onCleared() {
    super.onCleared()
    mCompositeDispose.dispose()
  }
}