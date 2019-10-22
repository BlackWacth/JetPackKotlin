package com.bruce.jetpackkotlin.ui.common

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * Copyright (c) 2019, 四川绿源集科技有限公司 All rights reserved.
 * author：HuaZhongWei
 * date：2019/10/22
 * description：
 *
 **/
class DataBoundViewHolder<out T : ViewDataBinding> constructor(
    val binding: T
) : RecyclerView.ViewHolder(binding.root)