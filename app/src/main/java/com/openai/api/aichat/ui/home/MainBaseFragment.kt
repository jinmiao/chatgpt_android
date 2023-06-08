package com.openai.api.aichat.ui.home

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.AndroidViewModel
import androidx.viewbinding.ViewBinding
import com.openai.api.aichat.ui.base.BaseFragment

abstract class MainBaseFragment<VB : ViewBinding, VM : AndroidViewModel> : BaseFragment<VB, VM>() {

    val activityModel: MainViewModel by activityViewModels()

}