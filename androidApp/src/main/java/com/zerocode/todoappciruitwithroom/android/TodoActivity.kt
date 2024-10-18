package com.zerocode.todoappciruitwithroom.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.zerocode.todoappciruitwithroom.TodoApp
import com.zerocode.todoappciruitwithroom.TodoEditScreen
import com.zerocode.todoappciruitwithroom.TodoEditScreenUI
import com.zerocode.todoappciruitwithroom.TodoHomeScreen
import com.zerocode.todoappciruitwithroom.TodoHomeScreenUI
import com.zerocode.todoappciruitwithroom.TodoItem
import com.zerocode.todoappciruitwithroom.storage.Todo
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

class TodoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set edge to edge + dark status bar icons
        enableEdgeToEdge()
        window?.let { WindowCompat.getInsetsController(it, window.decorView) }?.isAppearanceLightStatusBars = true

        setContent {
            val backStack = rememberSaveableBackStack(root = TodoHomeScreen)
            val navigator = rememberCircuitNavigator(backStack) // back handling is automatically configured for android when root is pop out
            TodoApp(backStack = backStack, navigator = navigator)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTodoItemList() {
    TodoItem(
        todo = Todo(
            1,
            title = "Todo $1",
            content = "Todo Content $1",
            completed = Random.nextBoolean(),
            createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        ),
        onClick = {}
    )
}

@Preview
@Composable
fun PreviewTodoHomeScreen() {
    TodoHomeScreenUI(
        state = TodoHomeScreen.TodoHomeScreenState(
            todos = (1..100).map {
                Todo(
                    it.toLong(),
                    title = "Todo $it",
                    content = "Todo Content $it",
                    completed = Random.nextBoolean(),
                    createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                )
            },
            eventSink = { },
        )
    )
}

@Preview
@Composable
fun PreviewTodoEditScreen() {
    TodoEditScreenUI(
        state = TodoEditScreen.TodoEditScreenState(
            todo = Todo(
                id = 1,
                title = "Hello World!",
                content = "How are the things going on there...",
                completed = false,
                createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            ),
            eventSink = { },
        ), modifier = Modifier
    )
}