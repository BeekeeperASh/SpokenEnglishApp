package com.example.spokenenglishapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.spokenenglishapp.app_screens.ChatLevels
import com.example.spokenenglishapp.app_screens.SubScreen
import com.example.spokenenglishapp.app_screens.dialogue
import com.example.spokenenglishapp.app_tools.Level
import com.example.spokenenglishapp.firebase.GoogleAuthUiClient
import com.example.spokenenglishapp.firebase.SignInScreen
import com.example.spokenenglishapp.firebase.SignInViewModel
import com.example.spokenenglishapp.firebase.login.LoginViewModel
import com.example.spokenenglishapp.navigation.*
import com.example.spokenenglishapp.ui.theme.SpokenEnglishAppTheme
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val loginViewModel = viewModel(modelClass = LoginViewModel::class.java)
            SpokenEnglishAppTheme {
                //val stringArray = resources.getStringArray(R.array.dialog_test)
                MainScreen(loginViewModel)
            }
        }
    }

    companion object{
        const val RecordAudioRequestCode = 1
        val textLists = listOf<Int>(R.array.dialog_travel, R.array.dialog_museum, R.array.dialog_2)
        val soundLists = listOf<List<Int>>(
            listOf(R.raw.travel_1_1, R.raw.travel_2_1, R.raw.travel_1_2, R.raw.travel_2_2, R.raw.travel_1_3, R.raw.travel_2_3, R.raw.travel_1_4,
                R.raw.travel_2_4, R.raw.travel_1_5, R.raw.travel_2_5, R.raw.travel_1_6, R.raw.travel_2_6, R.raw.travel_1_7, R.raw.travel_2_7),
            listOf(R.raw.museum_a1, R.raw.museum_b1, R.raw.museum_a2, R.raw.museum_b2, R.raw.museum_a3, R.raw.museum_b3, R.raw.museum_a4, R.raw.musuem_b4, R.raw.museum_a5,
                R.raw.museum_b5, R.raw.museum_a6, R.raw.museum_b6, R.raw.musuem_a7, R.raw.museum_b7, R.raw.museum_b8),
            listOf(R.raw.test1, R.raw.test2, R.raw.test3, R.raw.test4)
        )
        val imagesList = listOf<List<String>>(
            listOf("https://catsareontop.com/wp-content/uploads/2018/10/vacation-feature.jpg",
                "https://assets.change.org/photos/3/av/bu/GRAVBUXefiKAAgb-800x450-noPad.jpg?1522984091"),
            listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQpfLa3QIyY1HTMvZrLiDbVeaxEdie061ILb22jI3vB81bljD6x6LYwOnkuo2p-wR_8Jjw&usqp=CAU",
                "https://mf.b37mrtl.ru/rbthmedia/images/2022.06/original/62aa019232b67266905dd4ce.jpg"),
            listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQpfLa3QIyY1HTMvZrLiDbVeaxEdie061ILb22jI3vB81bljD6x6LYwOnkuo2p-wR_8Jjw&usqp=CAU",
                "https://mf.b37mrtl.ru/rbthmedia/images/2022.06/original/62aa019232b67266905dd4ce.jpg")
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RecordAudioRequestCode
            && grantResults.isNotEmpty()
        ){
            Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show()
        }
    }

}



