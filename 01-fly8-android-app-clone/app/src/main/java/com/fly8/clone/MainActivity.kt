package com.fly8.clone

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Monitor
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fly8.clone.ui.AboutScreen
import com.fly8.clone.ui.ConnectScreen
import com.fly8.clone.ui.DashboardScreen
import com.fly8.clone.ui.Fly8Background
import com.fly8.clone.ui.Fly8Muted
import com.fly8.clone.ui.Fly8Primary
import com.fly8.clone.ui.Fly8Surface
import com.fly8.clone.ui.Fly8Theme
import com.fly8.clone.ui.Fly8ViewModel
import com.fly8.clone.ui.SessionsScreen

enum class Tab(val label: String) { DASHBOARD("Dashboard"), SESSIONS("Sessions"), ABOUT("About") }

class MainActivity : ComponentActivity() {
    private val vm: Fly8ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent { Fly8Theme { Fly8App(vm) } }
    }

    override fun onResume() {
        super.onResume()
        vm.refreshStats()
    }
}

@Composable
private fun Fly8App(vm: Fly8ViewModel) {
    var tab by remember { mutableStateOf(Tab.DASHBOARD) }
    var showConnect by remember { mutableStateOf(false) }
    val snackbarHost = remember { SnackbarHostState() }
    val message by vm.snackbar.collectAsStateWithLifecycle()

    LaunchedEffect(message) {
        message?.let { snackbarHost.showSnackbar(it); vm.dismissSnackbar() }
    }

    Scaffold(
        containerColor = Fly8Background,
        snackbarHost = { SnackbarHost(snackbarHost) },
        bottomBar = { if (!showConnect) BottomBar(tab) { tab = it } }
    ) { inner ->
        Box(Modifier.fillMaxSize().padding(inner)) {
            if (showConnect) {
                ConnectScreen(vm, onBack = { showConnect = false })
            } else {
                AnimatedContent(
                    targetState = tab,
                    transitionSpec = {
                        val forward = targetState.ordinal > initialState.ordinal
                        val dir = if (forward) 1 else -1
                        (slideInHorizontally { it / 6 * dir } + fadeIn()) togetherWith
                            (slideOutHorizontally { -it / 6 * dir } + fadeOut()) using SizeTransform(clip = false)
                    },
                    label = "tab"
                ) { current ->
                    when (current) {
                        Tab.DASHBOARD -> DashboardScreen(vm, onConnect = { showConnect = true })
                        Tab.SESSIONS -> SessionsScreen(vm, onConnect = { showConnect = true })
                        Tab.ABOUT -> AboutScreen(vm)
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomBar(current: Tab, onSelect: (Tab) -> Unit) {
    NavigationBar(containerColor = Fly8Surface, tonalElevation = 0.dp) {
        Tab.entries.forEach { tab ->
            NavigationBarItem(
                selected = current == tab,
                onClick = { onSelect(tab) },
                icon = {
                    Icon(
                        when (tab) {
                            Tab.DASHBOARD -> Icons.Outlined.Dashboard
                            Tab.SESSIONS -> Icons.Outlined.Monitor
                            Tab.ABOUT -> Icons.Outlined.Info
                        },
                        contentDescription = tab.label
                    )
                },
                label = { Text(tab.label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Fly8Primary,
                    selectedTextColor = Fly8Primary,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = Fly8Muted,
                    unselectedTextColor = Fly8Muted
                )
            )
        }
    }
}
