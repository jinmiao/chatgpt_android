package com.openai.api.aichat.ui.base

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding, VM : AndroidViewModel> : AppCompatActivity() {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!

    private var _viewModel: VM? = null
    protected val viewModel: VM
        get() = _viewModel!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = getViewBinding()
        setContentView(binding.root)
        setSupportActionBar(getToolBar())
        supportActionBar?.setDisplayHomeAsUpEnabled(isDisplayHomeAsUpEnabled())

        _viewModel = ViewModelProvider(this)[getViewModelClass()]
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _viewModel = null
    }

    abstract fun getViewBinding(): VB
    abstract fun getViewModelClass(): Class<VM>
    open fun isDisplayHomeAsUpEnabled(): Boolean = true
    abstract fun getToolBar(): Toolbar

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}