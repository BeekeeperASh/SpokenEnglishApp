package com.example.spokenenglishapp.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.example.spokenenglishapp.R
import com.example.spokenenglishapp.app_screens.Graph
import com.example.spokenenglishapp.firebase.Resources
import com.example.spokenenglishapp.firebase.StorageRepository
import com.example.spokenenglishapp.firebase.UserData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@Composable
fun ProfileScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    repository: StorageRepository = StorageRepository()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var userInf: List<UserData>? = emptyList()
    val name = remember {
        mutableStateOf("")
    }
    val accuracyMap = remember {
        mutableStateOf(emptyMap<String, Any>())
    }

    fun userInfo() {
        lifecycleOwner.lifecycleScope.launch {
            repository.getUserData(repository.getUserId()).collect { resources ->
                when (resources) {
                    is Resources.Loading -> {
                        // Обработка загрузки данных
                    }
                    is Resources.Success -> {
                        userInf = resources.data // Данные пользователя
                        // Обработка успешного получения данных

                        if (userInf?.isEmpty() == true) {
                            //Log.d("MyLog", "Add user")
                            //Log.d("MyLog", userInf.toString())
                            repository.addUser(
                                repository.getUserId(),
                                repository.user?.email,
                                null
                            ) {
                                //Log.d("MyLog", "Add user")
                            }
                        } else {
                            name.value = userInf?.get(0)?.username.toString()
                            accuracyMap.value = userInf?.get(0)?.accuracyMap?.toMap() ?: emptyMap()
                        }

                        delay(800L)
                        //Log.d("MyLog", userInf.toString())
                        //ready = true
                    }
                    is Resources.Error -> {
                        val throwable = resources.throwable // Ошибка получения данных
                        // Обработка ошибки при получении данных
                    }
                }
            }
            //delay(800L)
        }
    }
    userInfo()
    //Log.d("MyLog", "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb")


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (repository.user?.uid != null) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (userData?.profilePictureUrl != null) {
                    AsyncImage(
                        model = userData.profilePictureUrl,
                        contentDescription = "Profile picture",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                        //.padding(end = 16.dp)
                        ,
                        contentScale = ContentScale.Crop
                    )
                    //Spacer(modifier = Modifier.height(16.dp))
                }
                if (userData?.username != null && userData.username != "") {
                    Text(
                        text = "${userData.username}",
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    //Spacer(modifier = Modifier.height(16.dp))
                } else {
                    //userInf?.get(0)?.username?.let { Log.d("MyLog", it) }
                    Text(
                        text = name.value,
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    //Spacer(modifier = Modifier.height(16.dp))
                }
                IconButton(onClick = { onSignOut() }, modifier = Modifier.padding(start = 16.dp)) {
                    androidx.compose.material3.Icon(
                        painter = painterResource(id = R.drawable.logout),
                        contentDescription = "buttonBack",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
        Text(text = "График точности произношения", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        if (accuracyMap.value.isNotEmpty()){
            var points = mutableListOf<Float>()
            val subList = mutableListOf<Int?>()
            for (key in accuracyMap.value.keys) {
                subList.add(sumNumbersFromString(key))
                points.add((accuracyMap.value[key].toString().toInt()).toFloat() + 10f)
            }
            points = sortArrayBasedOnAnother(points, subList)
            if (points.size>=10){
                points = points.subList(points.size-10, points.size)
            }
            Box(
                modifier = Modifier.weight(4f)
            ) {
                Graph(
                    modifier = Modifier
                        .fillMaxWidth()
                    //.height(500.dp)
                    ,
                    xValues = (0..9).map { it + 1},
                    yValues = (0..10).map { (it) * 10 },
                    points = points,
                    paddingSpace = 16.dp,
                    verticalStep = 10
                )
            }
            Spacer(modifier = Modifier.weight(1f))

        }


    }
    //Log.d("MyLog", "ccccccccccccccccccccccccccccccccccccccccccccccc")

}

fun sumNumbersFromString(input: String): Int {
    val pattern = Pattern.compile("\\d+")
    val matcher = pattern.matcher(input)
    var sum = 0
    var counter = 0

    while (matcher.find()) {
        val numberString = matcher.group()
        val number = numberString.toIntOrNull() ?: 0
        sum += if (counter == 0) number * 1440 else if (counter == 1) number * 10000 else if (counter == 2) number * 60 else number
        counter++
    }

    return sum
}

fun sortArrayBasedOnAnother(
    arrayToSort: MutableList<Float>,
    referenceArray: MutableList<Int?>
): MutableList<Float> {
    val sortedPairs = arrayToSort.zip(referenceArray).sortedBy { it.second }
    return sortedPairs.map { it.first }.toMutableList()
}
