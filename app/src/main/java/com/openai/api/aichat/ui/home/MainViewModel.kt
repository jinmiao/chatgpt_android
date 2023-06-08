package com.openai.api.aichat.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.openai.api.aichat.ChatApplication.Companion.app
import com.openai.api.aichat.common.model.UserInfo

class MainViewModel(chatApp: Application) : AndroidViewModel(chatApp) {

    val newChat: MutableLiveData<Boolean> = MutableLiveData()
    val newChatForImage: MutableLiveData<Boolean> = MutableLiveData()
    val drawerOpened: MutableLiveData<Boolean> = MutableLiveData()

}