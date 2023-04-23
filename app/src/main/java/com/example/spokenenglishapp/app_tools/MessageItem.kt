package com.example.spokenenglishapp.app_tools

import com.example.spokenenglishapp.R

data class MessageItem(
    val isSide: Boolean = true,
    val textEn: String = "",
    val textRu: String = "",
    val name: String = "",
    val sound: Int = R.raw.sound_test,

)
