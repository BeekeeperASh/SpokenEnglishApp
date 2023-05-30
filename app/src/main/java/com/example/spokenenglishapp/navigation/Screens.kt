package com.example.spokenenglishapp.navigation

import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun Screen5() {
    val context = LocalContext.current
    var textToSpeak by remember { mutableStateOf("") }
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var voices: List<Voice> by remember { mutableStateOf(emptyList()) }
    var selectedVoice by remember { mutableStateOf<Voice?>(null) }

    // Get list of available voices when component is first rendered
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    voices = tts?.voices?.toList() ?: emptyList()
                }
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = textToSpeak,
            onValueChange = { textToSpeak = it },
            label = { Text("Text to speak") }
        )

        // Dropdown to select a voice
        Log.d("MyLog", voices.size.toString())
        DropdownMenu(
            expanded = selectedVoice != null,
            onDismissRequest = { selectedVoice = null },
        ) {
            voices.forEach { voice ->
                DropdownMenuItem(
                    onClick = { selectedVoice = voice },
                    content = { Text(voice.name) },
                )
            }
        }
        LazyColumn {
            itemsIndexed(voices) { _, item ->
                Text(text = item.name.toString())
                Button(onClick = {
                    Log.d("MyLog", item.toString())
                    tts?.voice = item
                    tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
                }) {
                    Text(text = "Button")
                }
            }
        }
        TextButton(
            onClick = {
                tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
            },
            enabled = textToSpeak.isNotBlank() && selectedVoice != null
        ) {
            Text("Speak")
        }


    }


}

