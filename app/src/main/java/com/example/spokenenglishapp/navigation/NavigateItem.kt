package com.example.spokenenglishapp.navigation

import com.example.spokenenglishapp.R

sealed class NavigateItem(val title: String, val iconId: Int, val route: String){
    object Screen1: NavigateItem("Main", R.drawable.home, "chat_levels")
    object Screen2: NavigateItem("Talk", R.drawable.headset_mic, "sub_screen")
    object Screen3: NavigateItem("Practice", R.drawable.hearing, "custom_exercise")
    object Screen5: NavigateItem("???", R.drawable.star, "sub_screen")
    object Account: NavigateItem("Account", R.drawable.account_circle, "sign_in")
    object Auth: NavigateItem("Account", R.drawable.account_circle, "auth")
}
