package com.example.spokenenglishapp.app_screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.spokenenglishapp.app_tools.Level
import com.example.spokenenglishapp.navigation.example.MainScreen
import com.example.spokenenglishapp.ui.theme.Purple200

@Composable
fun ChatLevels(list: List<Level>, navController: NavController, index: MutableState<Int>) {
    LazyColumn(modifier = Modifier.fillMaxSize()){
        itemsIndexed(list){i, item ->
            LevelItem(level = item, navController = navController, index, i)
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LevelItem(level: Level, navController: NavController, index: MutableState<Int>, i: Int){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = if(i == 13) 64.dp else 4.dp),
        backgroundColor = Purple200,
        elevation = 0.dp,
        shape = RoundedCornerShape(4.dp),
        onClick = {
            index.value = i
            navController.navigate(level.route)
        }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(88.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = level.imageResource,
                contentDescription = "description of what the image contains",
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 4.dp)) {
                Text(text = level.title)
                Text(text = level.description, color = Color.White)
            }

        }
    }
}

//, isMain: MutableState<Boolean>