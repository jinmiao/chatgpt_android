package com.openai.api.aichat.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.aallam.openai.api.BetaOpenAI
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.openai.api.aichat.ChatApplication.Companion.app
import com.openai.api.aichat.R
import com.openai.api.aichat.common.utils.LogUtil
import com.openai.api.aichat.common.utils.startActivitySafe
import com.openai.api.aichat.databinding.ActivityMainBinding
import com.openai.api.aichat.ui.base.BaseActivity
import com.openai.api.aichat.ui.home.history.HistoryChatActivity
import com.openai.api.aichat.ui.home.newchat.NewChatFragment
import com.openai.api.aichat.ui.settings.SettingsActivity

@BetaOpenAI
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun getToolBar(): Toolbar {
        return binding.contentMain.toolbar
    }

    override fun isDisplayHomeAsUpEnabled() = false

    private lateinit var toggle: ActionBarDrawerToggle

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState()
    }

    private val newChatFragment = NewChatFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFragment("", newChatFragment)
        initActionBarDrawerToggle()
        initNavigation()

        // 个人Key设置
        initSetKeys()
    }

    @SuppressLint("CheckResult")
    private fun initSetKeys() {
        if (app.sharedPrefHelper.openAIAPIKey.isNullOrEmpty()) {
            MaterialDialog(this).show {
                title(text = "输入你的 OpenAI API Key")
                message(text = "安全提示：Key 只会存在本地，不会上传到服务器。")
                input(hint = "openai api key") { dialog, text ->
                    app.sharedPrefHelper.openAIAPIKey = text.toString()
                }
            }
        }
    }

    override fun onDestroy() {
        binding.drawerLayout.removeDrawerListener(drawerListener)
        super.onDestroy()
    }

    private fun initActionBarDrawerToggle() {
        toggle = ActionBarDrawerToggle(this,
            binding.drawerLayout,
            getToolBar(),
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close)
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.white)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    @SuppressLint("CheckResult")
    private fun initNavigation() {
        binding.newChat.setOnClickListener {
            viewModel.newChat.value = true
            viewModel.newChatForImage.value = false
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        binding.newChatImage.setOnClickListener {
            viewModel.newChat.value = false
            viewModel.newChatForImage.value = true
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        binding.settings.setOnClickListener {
            startActivitySafe(SettingsActivity::class)
        }

        binding.history.setOnClickListener {
            startActivitySafe(HistoryChatActivity::class)
        }

        binding.drawerLayout.addDrawerListener(drawerListener)
    }

    private val drawerListener = object : DrawerLayout.DrawerListener {
        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            LogUtil.i(TAG, "onDrawerSlide")
        }

        override fun onDrawerOpened(drawerView: View) {
            LogUtil.i(TAG, "onDrawerOpened")
            viewModel.drawerOpened.value = true
        }

        override fun onDrawerClosed(drawerView: View) {
            LogUtil.i(TAG, "onDrawerClosed")
        }

        override fun onDrawerStateChanged(newState: Int) {
            LogUtil.i(TAG, "onDrawerStateChanged")
        }
    }

    private fun showFragment(at: String, fragment: Fragment) {
        if (at.isNotEmpty()) title = at
        supportFragmentManager.beginTransaction().replace(R.id.fragment_content_main, fragment)
            .commit()

        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onPause() {
        super.onPause()
        if (binding.drawerLayout.isOpen) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isOpen) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}