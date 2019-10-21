package com.bruce.jetpackkotlin.di

import com.bruce.jetpackkotlin.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 *
 * Copyright (c) 2019, 四川绿源集科技有限公司 All rights reserved.
 * author：HuaZhongWei
 * date：2019/10/21
 * description：
 *
 **/
@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity

}