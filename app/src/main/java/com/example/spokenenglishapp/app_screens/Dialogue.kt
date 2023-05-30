package com.example.spokenenglishapp.app_screens

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.spokenenglishapp.R
import com.example.spokenenglishapp.app_tools.MessageItem
import com.example.spokenenglishapp.firebase.Resources
import com.example.spokenenglishapp.firebase.StorageRepository
import com.example.spokenenglishapp.firebase.UserData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun f(text: Array<String>, sound: List<Int>): ArrayList<MessageItem> {
    val listSub: ArrayList<MessageItem> = ArrayList()

    for (i in (2 until (text.size / 2 + 1))) {
        listSub.add(
            MessageItem(
                i % 2 == 0,
                text[i],
                text[(text.size - 2) / 2 + i],
                text[i % 2],
                sound[(i - 2)]
            )
        )
    }
    //Log.d("MyLog", listSub.toString())
    return listSub
}

@Composable
fun dialogue(
    navController: NavController,
    text: Int,
    sound: List<Int>,
    links: List<String>
) {
//    val testList = remember {
//        mutableStateOf(listOf(MessageItem()))
//    }

    val len = rememberSaveable {
        mutableStateOf(1)
    }
    val textList = stringArrayResource(id = text)
    val listX = remember {
        mutableStateListOf<MessageItem>().apply {
            addAll(f(textList, sound))
        }
    }
    val accuracy = remember {
        mutableStateOf(0)
    }
    val finalAccuracy = remember {
        mutableStateOf(0)
    }
    val isEnd = remember {
        mutableStateOf(false)
    }
//    val messageList = remember {
//        mutableStateListOf<MessageItem>(
//            MessageItem(
//                true,
//                textEn = textList[2],
//                textRu = textList[(textList.size) / 2 + 1],
//                name = textList[0],
//                sound = sound[0]
//            )
//        )
//    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isEnd.value) {
            OnComplete(
                len.value,
                {

                    navController.navigate("chat_levels")
                },
                finalAccuracy.value,
                isEnd
            )
        }
        TopPanel(
            navController = navController,
            modifier = Modifier.weight(1.5f),
            links[0],
            links[1],
            accuracy.value
        )
        MessageList(
            //messageItems = messageList,
            messageItems = listX,
            modifier = Modifier.weight(6f),
            len = len.value,
            maxLen = listX.size
        )
        SpeechRecognitionScreen(
            modifier = Modifier.weight(1f),
            listX[len.value - 1].textEn,
            len = len,
            maxLen = listX.size,
            accuracy,
            isEnd,
            finalAccuracy
            //messageList,
            //testList.value,
            //textList,
            //sound
        )
    }
}


@Composable
fun TopPanel(
    navController: NavController,
    modifier: Modifier = Modifier,
    link1: String,
    link2: String,
    accuracy: Int
) {
    Column(modifier = modifier) {
        IconButton(onClick = {
            //navController.navigate("chat_levels")
            navController.navigateUp()
        }) {

            Icon(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = "buttonBack",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                model = link1,
                contentDescription = "Translated description of what the image contains",
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Accuracy",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = "$accuracy %",
                    color = if (accuracy < 50) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center
                )
            }
            AsyncImage(
                model = link2,
                contentDescription = "Translated description of what the image contains",
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    messageItems: MutableList<MessageItem>,
    len: Int,
    maxLen: Int
) {
    //val immutableList = messageItems.reversed()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            reverseLayout = true
        ) {
            itemsIndexed(messageItems.reversed()) { i, message ->
                if (i > maxLen - len - 1) MessageCard(message)
            }
        }
    }
}

@Composable
fun MessageCard(
    messageItem: MessageItem
) {

    val context: Context = LocalContext.current

    //val mp: MediaPlayer = MediaPlayer.create(context, R.raw.sound_test)
    //var mp by remember { mutableStateOf(MediaPlayer()) }

    val mp = MediaPlayer.create(context, messageItem.sound)

    var isPlaying by remember {
        mutableStateOf(false)
    }

    val isShow = rememberSaveable {
        mutableStateOf(false)
    }

    mp.setOnCompletionListener {
        isPlaying = false
    }

    DisposableEffect(Unit) {
        onDispose {
            mp.release()
            //Log.d("MyLog", "answer is 3")
        }
    }

    LaunchedEffect(Unit) {

        //Log.d("MyLog", "compose")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = when {
            messageItem.isSide -> Alignment.End
            else -> Alignment.Start
        },
    ) {
        Card(
            modifier = Modifier.widthIn(max = 400.dp),
            shape = cardShapeFor(messageItem),
            colors = CardDefaults.cardColors(
                when {
                    messageItem.isSide -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.tertiaryContainer
                }
            ),
//            backgroundColor = when {
//                messageItem.isSide -> MaterialTheme.colors.secondary
//                else -> MaterialTheme.colors.primary
//            },
        ) {

            Row(
                horizontalArrangement = when {
                    messageItem.isSide -> Arrangement.Start
                    else -> Arrangement.End
                }
            ) {
                if (!messageItem.isSide) {
                    Column(
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(8.dp)
                                .widthIn(max = 280.dp),
                            text = messageItem.textEn,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        AnimatedVisibility(isShow.value) {
                            Text(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .widthIn(max = 280.dp),
                                text = messageItem.textRu,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    IconButton(onClick = {
                        if (isPlaying) {
                            mp.pause()
                            isPlaying = false
                        } else {
                            mp.start()
                            isPlaying = true
                        }
                    }
                    ) {
                        Icon(
                            painter = painterResource(id = if (isPlaying) R.drawable.pause_circle else R.drawable.play_circle),
                            contentDescription = "iconPlay",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (isPlaying) {
                        IconButton(onClick = {
                            mp.stop()
                            mp.prepare()
                            isPlaying = false
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.stop_circle),
                                contentDescription = "iconStop",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                } else {
                    IconButton(onClick = {
                        if (isPlaying) {
                            mp.pause()
                            isPlaying = false
                        } else {
                            mp.start()
                            isPlaying = true
                        }
                    }
                    ) {
                        Icon(
                            painter = painterResource(id = if (isPlaying) R.drawable.pause_circle else R.drawable.play_circle),
                            contentDescription = "iconPlay",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (isPlaying) {
                        IconButton(onClick = {
                            mp.stop()
                            mp.prepare()
                            isPlaying = false
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.stop_circle),
                                contentDescription = "iconStop",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Column(
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(8.dp)
                                .widthIn(max = 280.dp),
                            text = messageItem.textEn,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        AnimatedVisibility(isShow.value) {
                            Text(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .widthIn(max = 280.dp),
                                text = messageItem.textRu,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (messageItem.isSide) {
                IconButton(
                    onClick = {
                        isShow.value = !isShow.value
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.googletranslate),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = messageItem.name,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                Text(
                    text = messageItem.name,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(20.dp))
                IconButton(
                    onClick = {
                        isShow.value = !isShow.value
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.googletranslate),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

    }
}

@Composable
fun cardShapeFor(messageItem: MessageItem): Shape {
    val roundedCorners = RoundedCornerShape(16.dp)
    return when {
        messageItem.isSide -> roundedCorners.copy(bottomEnd = CornerSize(0))
        else -> roundedCorners.copy(bottomStart = CornerSize(0))
    }
}

@Composable
fun OnComplete(
    len: Int,
    onSubmit: () -> Unit,
    finalAccuracy: Int,
    isEnd: MutableState<Boolean>,
    repository: StorageRepository = StorageRepository()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var userInf: List<UserData>? = emptyList()
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
                        //Log.d("MyLog", "userInfo")
                        delay(1000)
                    }
                    is Resources.Error -> {
                        val throwable = resources.throwable // Ошибка получения данных
                        // Обработка ошибки при получении данных
                    }
                }
            }
            //delay(1000)
        }
    }
    if (repository.hasUser()) {
        userInfo()
    }
    AlertDialog(
        onDismissRequest = {
            if (repository.hasUser()) {
                //userInfo()
                repository.addElementToMap(userInf!![0].documentId, finalAccuracy / len) {
                    //Log.d("MyLog", "update")
                }
            }
            isEnd.value = false
            onSubmit()

        },
        confirmButton = {
            TextButton(onClick = {
                if (repository.hasUser()) {
                    //userInfo()
                    repository.addElementToMap(userInf!![0].documentId, finalAccuracy / len) {
                        //Log.d("MyLog", "update")
                    }
                }
                isEnd.value = false
                onSubmit()

            }) {
                Text(text = "OK")
            }
        },
//        dismissButton = {
//            TextButton(onClick = { isEnd.value = false }) {
//                Text(text = "Cancel")
//            }
//        },
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Final accuracy ${finalAccuracy / len} %",
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center
                )
                Row() {
                    repeat((finalAccuracy / len - 40) / 10) {
                        Icon(
                            painter = painterResource(id = R.drawable.star),
                            contentDescription = "",
                            tint = Color.Yellow,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
            }
        },
        shape = MaterialTheme.shapes.medium
    )

//    DisposableEffect(Unit) {
//        onDispose {
//            if (repository.hasUser()) userInfo()
//        }
//    }

}



