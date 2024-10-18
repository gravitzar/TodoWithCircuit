package com.zerocode.todoappciruitwithroom

import androidx.compose.ui.window.ComposeUIViewController
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.rememberCircuitNavigator

fun MainViewController() = ComposeUIViewController {
    val backStack = rememberSaveableBackStack(root = TodoHomeScreen)
    val navigator = rememberCircuitNavigator(backStack, onRootPop = { /* do platform based back handling when root is popped */ })
    TodoApp(backStack, navigator)
}