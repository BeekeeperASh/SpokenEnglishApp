package com.example.spokenenglishapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.spokenenglishapp.app_screens.SubScreen
import com.example.spokenenglishapp.navigation.MainScreen
import com.example.spokenenglishapp.ui.theme.SpokenEnglishAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpokenEnglishAppTheme {
                //val stringArray = resources.getStringArray(R.array.dialog_test)
                MainScreen()
            }
        }
    }

    companion object{
        const val RecordAudioRequestCode = 1
        val textLists = listOf<Int>(R.array.dialog_travel, R.array.dialog_2)
        val soundLists = listOf<List<Int>>(
            listOf(R.raw.travel_1_1, R.raw.travel_2_1, R.raw.travel_1_2, R.raw.travel_2_2, R.raw.travel_1_3, R.raw.travel_2_3, R.raw.travel_1_4,
                R.raw.travel_2_4, R.raw.travel_1_5, R.raw.travel_2_5, R.raw.travel_1_6, R.raw.travel_2_6, R.raw.travel_1_7, R.raw.travel_2_7),
            listOf(R.raw.test1, R.raw.test2, R.raw.test3, R.raw.test4)
        )
        val imagesList = listOf<List<String>>(
            listOf("https://catsareontop.com/wp-content/uploads/2018/10/vacation-feature.jpg",
                "https://assets.change.org/photos/3/av/bu/GRAVBUXefiKAAgb-800x450-noPad.jpg?1522984091"),
            listOf("https://catsareontop.com/wp-content/uploads/2018/10/vacation-feature.jpg",
                "https://assets.change.org/photos/3/av/bu/GRAVBUXefiKAAgb-800x450-noPad.jpg?1522984091")
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



