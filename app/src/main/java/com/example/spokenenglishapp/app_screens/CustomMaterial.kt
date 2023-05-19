package com.example.spokenenglishapp.app_screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.datastore.dataStore
import com.example.spokenenglishapp.R
import com.example.spokenenglishapp.datastore.StoreUserInfo
import com.example.spokenenglishapp.ui.theme.Purple400
import com.example.spokenenglishapp.ui.theme.Purple500
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

@Composable
fun CustomExercise() {
    val context = LocalContext.current
    val accuracy = remember {
        mutableStateOf(0)
    }
    val clipboardManager = LocalClipboardManager.current
    var text by remember { mutableStateOf(TextFieldValue()) }
    var speed by remember { mutableStateOf(1.0f) }
    var stateOfTTS by remember {
        mutableStateOf(false)
    }
    val tts = remember {
        TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                stateOfTTS = true
            }
        }
    }
    var selectedVoice by remember {
        mutableStateOf(true)
    }
    if (stateOfTTS) {
        val availableVoices = tts.voices
        if (selectedVoice) {
            tts.voice = availableVoices.find { it.name == "en-GB-default" }
        } else {
            tts.voice = availableVoices.find { it.name == "en-US-default" }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(text = accuracy.value.toString())
        Box(
            modifier = Modifier
                .weight(5f)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                SelectionContainer {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 18.sp),
                        label = { Text("Введите текст:") }
                    )
                }

                Button(
                    onClick = {
                        val clipText =
                            clipboardManager.getText() //.getPrimaryClip()?.getItemAt(0)?.text
                        clipText?.let {
                            text = TextFieldValue(
                                text = text.text + it.toString(),
                            )
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Вставить текст из буфера обмена")
                }
                Button(
                    onClick = {
                        text = TextFieldValue(
                            text = "",
                        )
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Очистить поле ввода")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row() {
                    Button(
                        onClick = { selectedVoice = true },
                        colors = ButtonDefaults.buttonColors(backgroundColor = if (selectedVoice) Color.Green else Color.Gray)
                    ) {
                        Text(
                            modifier = Modifier.background(if (selectedVoice) Color.Green else Color.Gray),
                            text = "en-GB",
                        )
                    }
                    Button(
                        onClick = { selectedVoice = false },
                        colors = ButtonDefaults.buttonColors(backgroundColor = if (selectedVoice) Color.Gray else Color.Green)
                    ) {
                        Text(
                            modifier = Modifier.background(if (selectedVoice) Color.Gray else Color.Green),
                            text = "en-US",
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Скорость речи: ${String.format("%.1f", speed)}x", fontSize = 16.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = speed,
                    onValueChange = { speed = it },
                    valueRange = 0.5f..2.0f,
                    steps = 15,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = {
                    tts.setSpeechRate(speed)
                    tts.speak(text.text, TextToSpeech.QUEUE_FLUSH, null, null)
                }) {
                    Text("Озвучить текст")
                }
            }
        }
        speechRecognition(
            modifier = Modifier.weight(1f),
            originalString = text.text,
            accuracy = accuracy
        )
        Spacer(modifier = Modifier.height(32.dp))
    }

    DisposableEffect(tts) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }
}

@Composable
fun TextInput() {

    val context = LocalContext.current
    val gson = Gson()
    val scope = rememberCoroutineScope()
    val dataStore = StoreUserInfo(context)

    val savedText = dataStore.getText.collectAsState(initial = "")
    val arrayTutorialType = object : TypeToken<ArrayList<String>>() {}.type
    val savedArrayOfTexts: ArrayList<String> = gson.fromJson(savedText.value?:"", arrayTutorialType)?: ArrayList()
    //savedArrayOfTexts.forEachIndexed{ _, text -> Log.d("MyLog", text)}

    var textValue by remember { mutableStateOf(TextFieldValue()) }

    val clipboardManager = LocalClipboardManager.current

    var flag = remember {
        mutableStateOf(true)
    }
    flag.value = savedArrayOfTexts.isEmpty()
    if (flag.value) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Текст для практики", color = MaterialTheme.colors.primary, style = MaterialTheme.typography.h3, textAlign = TextAlign.Center)
            SelectionContainer {
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .heightIn(max = 280.dp),
                    textStyle = TextStyle(fontSize = 18.sp),
                    label = { Text("Введите текст:") }
                )
            }

            Button(
                onClick = {
                    val clipText =
                        clipboardManager.getText() //.getPrimaryClip()?.getItemAt(0)?.text
                    clipText?.let {
                        textValue = TextFieldValue(
                            text = textValue.text + it.toString(),
                        )
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Вставить код из буфера обмена")
            }
            Button(
                onClick = {
                    val clipText =
                        clipboardManager.getText() //.getPrimaryClip()?.getItemAt(0)?.text
                    clipText?.let {
                        textValue = TextFieldValue(
                            text = "",
                        )
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Очистить поле ввода")
            }
            Button(
                onClick = {
                    val arrayOfTexts = divideString(textValue.text)
                    val json = gson.toJson(arrayOfTexts)
                    scope.launch {
                        dataStore.saveText(json)
                    }
                    flag.value = false

                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Применить")
            }
        }
    } else {
        TextRepetition(savedArrayOfTexts, flag)
    }

}

@Composable
fun speechRecognition(
    modifier: Modifier,
    originalString: String,
    accuracy: MutableState<Int>
) {

    val infiniteTransition = rememberInfiniteTransition()
    val pulsate by infiniteTransition.animateFloat(
        initialValue = 50f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse)
    )

    var speechRecognizer: SpeechRecognizer? = null
    val flag = remember {
        mutableStateOf(false)
    }

    val context: Context = LocalContext.current

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) !=
        PackageManager.PERMISSION_GRANTED
    ) {
        checkPermissions(context)
    }
    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

    val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    speechRecognizerIntent.putExtra(
        RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
    )
    speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
    speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Something")

    speechRecognizer!!.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}

        override fun onBeginningOfSpeech() {
        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            flag.value = false
        }

        override fun onError(error: Int) {
            Toast.makeText(context, "Please repeat", Toast.LENGTH_SHORT).show()
            flag.value = false
        }

        override fun onResults(bundle: Bundle?) {
            //Toast.makeText(context, "Results", Toast.LENGTH_SHORT).show()
            val data = bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val percent = levenshteinDistancePercentage(data?.get(0) ?: "", originalString).toInt()
            accuracy.value = percent
        }

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}
    })

    Box(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .heightIn(100.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        IconButton(
            onClick = {
                if (flag.value) {
                    speechRecognizer.stopListening()
                } else {
                    speechRecognizer.startListening(
                        speechRecognizerIntent
                    )
                    flag.value = true
                }
            },
        ) {
            Icon(
                painter = painterResource(id = if (flag.value) R.drawable.mic else R.drawable.mic_off),
                contentDescription = "icon",
                modifier = Modifier
                    .size(if (flag.value) pulsate.dp else 60.dp)
                    .background(MaterialTheme.colors.surface)
                    .border(4.dp, Purple500, CircleShape)
                    //.offset(x = 10.dp, y = 10.dp)
                ,
                tint = Purple400,
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer.destroy()
        }
    }
}

fun divideString(
    input: String
): ArrayList<String> {
    val result = ArrayList<String>()
    //val sentenceRegex = "(?<!\\w\\.\\w.)(?<![A-Z][a-z]\\.)(?<=\\.|\\?)\\s".toRegex()
    val sentenceRegex = "(?<!\\w\\.\\w.)(?<![A-Z][a-z]\\.)(?<=\\.|\\?|[\\n\\r])\\s".toRegex()

    val sentences = input.split(sentenceRegex)
    var currentSubstring = StringBuilder()

    for (sentence in sentences) {
        if (currentSubstring.length + sentence.length <= 100) {
            currentSubstring.append(sentence)
            if (currentSubstring.length >= 50) {
                result.add(currentSubstring.toString())
                currentSubstring = StringBuilder()
            }
        } else {
//            val remainingChars = 100 - currentSubstring.length
//            currentSubstring.append(sentence.substring(0, remainingChars))
            result.add(currentSubstring.toString())
//            currentSubstring = StringBuilder(sentence.substring(remainingChars))
            currentSubstring = StringBuilder(sentence)
        }
    }

    if (currentSubstring.isNotEmpty()) {
        result.add(currentSubstring.toString())
    }

    return result
}

@Composable
fun TextRepetition(
    savedArrayOfTexts: ArrayList<String>,
    flag: MutableState<Boolean>
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = StoreUserInfo(context)
    Column() {
        IconButton(
            onClick = {
                flag.value = true
                scope.launch {
                    dataStore.saveText("")
                }
            }
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
        }
        Spacer(
            modifier = Modifier
                .height(4.dp)
                .background(Color.Red)
        )
        LazyColumn() {
            itemsIndexed(savedArrayOfTexts) { index, item ->
                if (item.isNotEmpty()) TextItem(item)
                if (index == savedArrayOfTexts.size-1) {
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        }
    }
    
}

@Composable
fun TextItem(text: String) {
    val accuracy = rememberSaveable {
        mutableStateOf(0)
    }
    val context = LocalContext.current
    var speed by rememberSaveable { mutableStateOf(1.0f) }
    var stateOfTTS by remember {
        mutableStateOf(false)
    }
    val tts = remember {
        TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                stateOfTTS = true
            }
        }
    }
    var selectedVoice by rememberSaveable {
        mutableStateOf(true)
    }
    if (stateOfTTS) {
        val availableVoices = tts.voices
        if (selectedVoice) {
            tts.voice = availableVoices.find { it.name == "en-GB-default" }
        } else {
            tts.voice = availableVoices.find { it.name == "en-US-default" }
        }
    }


    Column(
        Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = CircleShape,
            border = BorderStroke(4.dp, MaterialTheme.colors.primary)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    modifier = Modifier
                        .weight(3f)
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    speechRecognition(
                        Modifier.padding(top = 12.dp),
                        originalString = text,
                        accuracy = accuracy
                    )
                    Text(
                        text = "${accuracy.value} %",
                        modifier = Modifier.padding(bottom = 12.dp),
                        style = MaterialTheme.typography.h6
                    )
                }

            }
        }
        Row() {
            Button(
                onClick = { selectedVoice = true },
                colors = ButtonDefaults.buttonColors(backgroundColor = if (selectedVoice) Color.Green else Color.Gray)
            ) {
                Text(
                    modifier = Modifier.background(if (selectedVoice) Color.Green else Color.Gray),
                    text = "en-GB",
                )
            }
            Button(
                onClick = { selectedVoice = false },
                colors = ButtonDefaults.buttonColors(backgroundColor = if (selectedVoice) Color.Gray else Color.Green)
            ) {
                Text(
                    modifier = Modifier.background(if (selectedVoice) Color.Gray else Color.Green),
                    text = "en-US",
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Скорость речи: ${String.format("%.1f", speed)}x", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = speed,
            onValueChange = { speed = it },
            valueRange = 0.5f..2.0f,
            steps = 15,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))


        Row() {
            Button(onClick = {
                tts.setSpeechRate(speed)
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }) {
                Text("Озвучить текст")
            }
            IconButton(onClick = {
                tts.stop()
            }) {
                Icon(painter = painterResource(id = R.drawable.replay), contentDescription = "")
            }
        }
    }

    DisposableEffect(tts) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

}