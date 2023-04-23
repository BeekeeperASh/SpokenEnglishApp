package com.example.spokenenglishapp.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.spokenenglishapp.MainActivity.Companion.imagesList
import com.example.spokenenglishapp.MainActivity.Companion.soundLists
import com.example.spokenenglishapp.MainActivity.Companion.textLists
import com.example.spokenenglishapp.app_screens.ChatLevels
import com.example.spokenenglishapp.app_screens.dialogue
import com.example.spokenenglishapp.app_tools.Level

@Composable
fun NavGraph(
    navHostController: NavHostController,
    isSub: MutableState<Boolean>
) {
    val index = remember {
        mutableStateOf(0)
    }
    val backStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    isSub.value = currentRoute == "screen_dialogue"

    NavHost(navController = navHostController, startDestination = "chat_levels"){
        composable("chat_levels"){
            ChatLevels(listOf(
                Level("Travel and Tourism", description = "Путешествия и туризм", route = "screen_dialogue", imageResource = "https://thumbnails.production.thenounproject.com/ZsdSIWYJSgI5XqyOB7QbEK5XNhI=/fit-in/1000x1000/photos.production.thenounproject.com/photos/4EA4ED5A-4A0F-4F04-A854-297D1BDA9198.jpg"),
                Level("Dialog2", description = "Something", route = "screen_dialogue", imageResource = "https://replicate.delivery/pbxt/0faix0jVnsWfHEXk4hB00UPqqogMCI3kBcgUie4tJWtOUM5fA/out-0.png"),
                Level("", description = "Anything"),
                Level(route = "screen_3", imageResource = "https://cdn.discordapp.com/attachments/995431274267279440/1039324448790151228/Artlandis_a_majestic_colossal_ancient_tree_made_with_intricate__6ec2679d-18d4-42a0-bd23-78e3de632248.png"),
                Level(route = "screen_4"),
                Level(),
                Level(),
                Level(),
                Level(),
                Level(),
                Level(),
                Level(),
                Level(),
                Level()
                ), navHostController, index)
        }
        composable("screen_2"){
            Screen2()
        }
        composable("screen_3"){
            Screen3()
        }
        composable("screen_4"){
            Screen4()
        }
        composable("screen_5"){
            Screen5()
        }
        composable("screen_6"){
            Screen6()
        }
        composable("screen_dialogue"){
            dialogue(navHostController, textLists[index.value], soundLists[index.value], imagesList[0])
        }
    }
}