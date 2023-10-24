package com.example.safecircle.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.safecircle.ui.screen.EnhancedMarkerState
import com.example.safecircle.ui.screen.PersonInfo

object GlobalState {
    var childList by mutableStateOf(listOf<PersonInfo>())
    var markers by mutableStateOf(mapOf<Int, EnhancedMarkerState>())
}