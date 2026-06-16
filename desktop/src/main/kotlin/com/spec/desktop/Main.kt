package com.spec.desktop

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Spec",
        state = rememberWindowState(size = DpSize(900.dp, 700.dp))
    ) {
        MaterialTheme {
            App()
        }
    }
}

@Composable
fun App() {
    // Navigation state
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Search) }

    when (currentScreen) {
        is Screen.Search -> SearchScreen(
            onProductSelected = { productId -> currentScreen = Screen.Dossier(productId) }
        )
        is Screen.Dossier -> DossierScreen(
            productId = (currentScreen as Screen.Dossier).productId,
            onBack = { currentScreen = Screen.Search }
        )
    }
}

sealed class Screen {
    data object Search : Screen()
    data class Dossier(val productId: String) : Screen()
}

@Composable
fun SearchScreen(onProductSelected: (String) -> Unit) {
    // TODO: implement
}

@Composable
fun DossierScreen(productId: String, onBack: () -> Unit) {
    // TODO: implement
}
