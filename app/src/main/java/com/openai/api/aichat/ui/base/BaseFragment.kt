package com.openai.api.aichat.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding, VM : AndroidViewModel> : Fragment() {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!

    private var _viewModel: VM? = null
    protected val viewModel: VM
        get() = _viewModel!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = getViewBinding()
        _viewModel = ViewModelProvider(this)[getViewModelClass()]

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _viewModel = null
    }

    abstract fun getViewBinding(): VB
    abstract fun getViewModelClass(): Class<VM>
}