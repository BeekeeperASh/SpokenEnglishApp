package com.example.spokenenglishapp.navigation

import com.example.spokenenglishapp.R

sealed class NavigateItem(val title: String, val iconId: Int, val route: String){
    object Screen1: NavigateItem("ChatLevels", R.drawable.home, "chat_levels")
    object Screen2: NavigateItem("Screen 2", R.drawable.headset_mic, "screen_2")
    object Screen3: NavigateItem("Screen 3", R.drawable.hearing, "screen_3")
    object Screen4: NavigateItem("Screen 4", R.drawable.mic, "screen_4")
    object Screen5: NavigateItem("Screen 5", R.drawable.mic, "screen_5")
    object Account: NavigateItem("Account", R.drawable.account_circle, "sign_in")
    object Auth: NavigateItem("Account", R.drawable.account_circle, "auth")
}
