package com.example.spokenenglishapp.navigation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeechService
import android.speech.tts.Voice
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import com.example.spokenenglishapp.R
import com.example.spokenenglishapp.app_screens.checkPermissions
import com.example.spokenenglishapp.app_screens.levenshteinDistancePercentage
import com.example.spokenenglishapp.ui.theme.Purple400
import com.example.spokenenglishapp.ui.theme.Purple500
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

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

