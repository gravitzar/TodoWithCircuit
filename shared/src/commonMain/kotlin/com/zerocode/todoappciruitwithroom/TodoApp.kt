package com.zerocode.todoappciruitwithroom

import androidx.compose.runtime.Composable
import com.slack.circuit.backstack.SaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.overlay.ContentWithOverlays
import com.slack.circuit.runtime.Navigator
import com.slack.circuitx.gesturenavigation.GestureNavigationDecoration
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun TodoApp(backStack: SaveableBackStack, navigator: Navigator) {
    TodoTheme {
        KoinContext {
            val todoRepository: TodoRepository = koinInject<TodoRepository>()

            val circuit: Circuit =
                Circuit.Builder()
                    .addPresenterFactory(TodoPresenter.Factory(todoRepository))
                    .addUi<TodoHomeScreen, TodoHomeScreen.TodoHomeScreenState> { state, modifier ->
                        TodoHomeScreenUI(
                            state = state,
                            modifier = modifier
                        )
                    }
                    .addPresenterFactory(TodoEditPresenter.Factory(todoRepository))
                    .addUi<TodoEditScreen, TodoEditScreen.TodoEditScreenState> { state, modifier ->
                        TodoEditScreenUI(
                            state = state,
                            modifier = modifier
                        )
                    }
                    .build()

            CircuitCompositionLocals(circuit) {
                ContentWithOverlays {
                    NavigableCircuitContent(
                        navigator = navigator,
                        backStack = backStack,
                        decoration = GestureNavigationDecoration(
                            onBackInvoked = {
                                // Pop the back stack once the user has gone 'back'
                                navigator.pop()
                            }
                        ),
                    )
                }
            }
        }
    }
}
