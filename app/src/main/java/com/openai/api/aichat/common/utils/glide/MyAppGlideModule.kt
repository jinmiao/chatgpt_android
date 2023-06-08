package com.openai.api.aichat.common.utils.glide

import android.content.Context
import cc.shinichi.library.glide.progress.ProgressManager.okHttpClient
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import java.io.InputStream

@GlideModule
class MyAppGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)

        // 替换底层网络框架为okhttp3，这步很重要！如果不添加会无法正常显示原图的加载百分比，或者卡在1%
        // 如果你的app中已经存在了自定义的 GlideModule，你只需要把这一行代码，添加到对应的重载方法中即可。
        registry.replace(GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(okHttpClient))
    }
}