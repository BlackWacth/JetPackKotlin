package com.bruce.jetpackkotlin.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.bruce.jetpackkotlin.App
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector

/**
 *
 * Copyright (c) 2019, 四川绿源集科技有限公司 All rights reserved.
 * author：HuaZhongWei
 * date：2019/10/21
 * description：自动注入帮助类
 * 实现了HasSupportFragmentInjector的Activity，自动注入
 * 实现了Injectable的Fragment，自动注入
 *
 **/
object AppInjector {

    fun init(app: App) {
        DaggerAppComponent.builder().application(app)
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                handleActivity(activity)
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }

        })
    }

    private fun handleActivity(activity: Activity) {
        if (activity is HasSupportFragmentInjector) {
            AndroidInjection.inject(activity)
        }
        if (activity is FragmentActivity) {
            activity.supportFragmentManager
                .registerFragmentLifecycleCallbacks(object :
                    FragmentManager.FragmentLifecycleCallbacks() {

                    override fun onFragmentCreated(
                        fm: FragmentManager,
                        f: Fragment,
                        savedInstanceState: Bundle?
                    ) {
                        if (f is Injectable) {
                            AndroidSupportInjection.inject(f)
                        }
                    }
                }, true)
        }
    }
}