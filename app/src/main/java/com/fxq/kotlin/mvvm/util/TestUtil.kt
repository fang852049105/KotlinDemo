package com.fxq.kotlin.mvvm.util

import android.app.ActivityManager
import android.content.Context
import android.os.PowerManager
import com.hazz.kotlinmvp.MyApplication

/**
 * Author: Fanxq
 * Date: 2021/6/10
 */
object TestUtil {

  fun isScreenOn(context: Context): Boolean {
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return powerManager.isInteractive
  }

  fun isAppOnForegroud(context: Context): Boolean {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val appProcess = activityManager.runningAppProcesses ?: return false
    val packageName = context.packageName
    for (progress in appProcess) {
      if (progress.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        && progress.processName.equals(packageName)) {
        return true
      }
    }
    return false
  }
}