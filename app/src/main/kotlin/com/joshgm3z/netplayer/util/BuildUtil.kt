package com.joshgm3z.netplayer.util

import com.joshgm3z.netplayer.BuildConfig

@Suppress("KotlinConstantConditions")
val isDevBuild
    get() = BuildConfig.FLAVOR != "prod"