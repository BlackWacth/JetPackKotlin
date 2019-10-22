package com.bruce.jetpackkotlin.binding

import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment

/**
 *
 * Copyright (c) 2019, 四川绿源集科技有限公司 All rights reserved.
 * author：HuaZhongWei
 * date：2019/10/22
 * description：
 *
 **/
class FragmentDataBindingComponent(fragment: Fragment): DataBindingComponent {

    private val adapter = FragmentBindingAdapters(fragment)

    override fun getFragmentBindingAdapters() = adapter

}