package com.zerocode.todoappciruitwithroom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.zerocode.todoappciruitwithroom.storage.Todo
import com.zerocode.todoappciruitwithroom.util.Parcelize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

enum class TodoMode {
    CREATE, EDIT,
}

sealed class TodoEditEvent : CircuitUiEvent {
    data class SaveTodo(
        val id: Long?,
        val title: String,
        val content: String,
    ) : TodoEditEvent()

    data class DeleteTodo(val todoId: Long) : TodoEditEvent()
}

@Parcelize
data class TodoEditScreen(
    val id: Long = -1,
    val mode: TodoMode = TodoMode.CREATE,
) : Screen {
    data class TodoEditScreenState(
        val todo: Todo?,
        val eventSink: (TodoEditEvent) -> Unit,
    ) : CircuitUiState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoEditScreenUI(
    state: TodoEditScreen.TodoEditScreenState,
    modifier: Modifier = Modifier,
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state) {
        title = state.todo?.title.orEmpty()
        content = state.todo?.content.orEmpty()
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        state.eventSink(
                            TodoEditEvent.SaveTodo(
                                id = state.todo?.id,
                                title = title,
                                content = content,
                            )
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIosNew,
                            contentDescription = "go back",
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            state.eventSink(
                                TodoEditEvent.DeleteTodo(
                                    todoId = state.todo?.id ?: -1,
                                )
                            )
                        },
                        enabled = state.todo?.id != null,
                    ) {
                        Icon(
                            Icons.Rounded.Delete,
                            contentDescription = "delete todo",
                        )
                    }
                    IconButton(
                        onClick = {
                            state.eventSink(
                                TodoEditEvent.SaveTodo(
                                    id = state.todo?.id,
                                    title = title,
                                    content = content,
                                )
                            )
                        },
                    ) {
                        Icon(
                            Icons.Rounded.Save,
                            contentDescription = "save todo",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.padding(8.dp),
            ) {
                BasicTextField(
                    value = title,
                    onValueChange = { newText -> title = newText },
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                )
                if (title.isBlank()) {
                    Text(
                        "Title",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier.padding(8.dp),
            ) {
                BasicTextField(
                    value = content,
                    onValueChange = { newText -> content = newText },
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                    modifier = Modifier.fillMaxSize().focusRequester(focusRequester),
                )
                if (content.isBlank()) {
                    Text(
                        "what do you want to do?",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.LightGray,
                        ),
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }

    }
}

class TodoEditPresenter(
    private val screen: TodoEditScreen,
    private val navigator: Navigator,
    private val todoRepository: TodoRepository,
) : Presenter<TodoEditScreen.TodoEditScreenState> {
    @Composable
    override fun present(): TodoEditScreen.TodoEditScreenState {
        val todo by produceState<Todo?>(
            initialValue = null
        ) {
            value = withContext(Dispatchers.IO) {
                when (screen.mode) {
                    TodoMode.CREATE -> null
                    TodoMode.EDIT -> {
                        todoRepository.getTodo(id = screen.id)
                    }
                }
            }
        }

        val eventSink: CoroutineScope.(TodoEditEvent) -> Unit = { event ->
            when (event) {
                is TodoEditEvent.DeleteTodo -> {
                    launchOrThrow(Dispatchers.IO) {
                        todoRepository.deleteTodo(event.todoId)
                    }

                    navigator.pop()
                }

                is TodoEditEvent.SaveTodo -> {
                    launchOrThrow(Dispatchers.IO) {
                        when (event.id) {
                            null -> {
                                if (event.title.isNotBlank() && event.content.isNotBlank()) {
                                    todoRepository.addTodo(
                                        title = event.title,
                                        content = event.content,
                                    )
                                }
                            }

                            else -> {
                                todo?.let { currentTodo ->
                                    todoRepository.updateTodo(
                                        currentTodo.copy(
                                            title = event.title,
                                            content = event.content,
                                            updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                                        )
                                    )
                                }
                            }
                        }
                    }

                    navigator.pop()
                }
            }
        }

        return TodoEditScreen.TodoEditScreenState(
            todo = todo,
            eventSink = wrapEventSink(eventSink),
        )
    }

    class Factory(private val todoRepository: TodoRepository) : Presenter.Factory {
        override fun create(screen: Screen, navigator: Navigator, context: CircuitContext): Presenter<*>? {
            return when (screen) {
                is TodoEditScreen -> TodoEditPresenter(
                    screen = screen,
                    navigator = navigator,
                    todoRepository = todoRepository,
                )

                else -> null
            }
        }
    }
}

fun CoroutineScope.launchOrThrow(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
): Job = launch(context, start, block).also {
    check(!it.isCancelled) {
        "launch failed. Job is already cancelled"
    }
}

@Composable
inline fun <E : CircuitUiEvent> wrapEventSink(
    crossinline eventSink: CoroutineScope.(E) -> Unit,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): (E) -> Unit = { event ->
    if (coroutineScope.isActive) {
        coroutineScope.eventSink(event)
    } else {
        println("Received event, but CoroutineScope is no longer active. See stack trace for caller.")
    }
}