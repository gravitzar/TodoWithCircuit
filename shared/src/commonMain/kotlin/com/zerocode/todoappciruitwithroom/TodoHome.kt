package com.zerocode.todoappciruitwithroom

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.zerocode.todoappciruitwithroom.storage.Todo
import com.zerocode.todoappciruitwithroom.util.Parcelize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

sealed class TodoHomeEvent : CircuitUiEvent {
    data object CreateTodo : TodoHomeEvent()
    data class OnItemClick(val todoId: Long) : TodoHomeEvent()
    data object ToggleListType : TodoHomeEvent()
    data object OnFilterClick : TodoHomeEvent()
}

@Parcelize
data object TodoHomeScreen : Screen {
    data class TodoHomeScreenState(
        val todos: List<Todo> = emptyList(),
        val isGridView: Boolean = false,
        val eventSink: (TodoHomeEvent) -> Unit,
    ) : CircuitUiState
}

@Composable
fun TodoItem(todo: Todo, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        if (todo.title.isNotBlank()) {
            Text(
                todo.title,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                ),
            )
        }
        if (todo.content.isNotBlank()) {
            Text(
                todo.content,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                ),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoHomeScreenUI(
    state: TodoHomeScreen.TodoHomeScreenState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Todos",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                },
                actions = {
                    IconButton(onClick = { state.eventSink(TodoHomeEvent.ToggleListType) }) {
                        Icon(
                            when (state.isGridView) {
                                true -> Icons.AutoMirrored.Rounded.List
                                false -> Icons.Rounded.GridView
                            },
                            contentDescription = "toggle list/grid view"
                        )
                    }
                    IconButton(onClick = {
                        state.eventSink(TodoHomeEvent.OnFilterClick)
                    }) {
                        Icon(
                            Icons.Rounded.FilterList,
                            contentDescription = "filter todos"
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            SmallFloatingActionButton(
                onClick = { state.eventSink(TodoHomeEvent.CreateTodo) },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Filled.Add, "add todo.")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (state.todos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "Nothing but emptiness...",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }
            } else {
                when (state.isGridView) {
                    true -> LazyVerticalStaggeredGrid(
                        modifier = Modifier.fillMaxSize(),
                        verticalItemSpacing = 8.dp,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        columns = StaggeredGridCells.Fixed(2),
                    ) {
                        items(state.todos, key = { todo -> todo.id }) { todo ->
                            TodoItem(
                                todo = todo,
                                onClick = {
                                    state.eventSink(TodoHomeEvent.OnItemClick(todo.id))
                                },
                            )
                        }
                    }

                    false -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.Start,
                        ) {
                            items(state.todos, key = { todo -> todo.id }) { todo ->
                                TodoItem(
                                    todo = todo,
                                    onClick = {
                                        state.eventSink(TodoHomeEvent.OnItemClick(todo.id))
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

class TodoPresenter(
    private val navigator: Navigator,
    private val todoRepository: TodoRepository,
) : Presenter<TodoHomeScreen.TodoHomeScreenState> {
    @Composable
    override fun present(): TodoHomeScreen.TodoHomeScreenState {
        val isGridViewListType by todoRepository.isGridViewListingType().collectAsRetainedState(initial = false)
        val todos by todoRepository.getTodos().collectAsRetainedState(initial = emptyList())

        val eventSink: CoroutineScope.(TodoHomeEvent) -> Unit = { event ->
            when (event) {
                TodoHomeEvent.CreateTodo -> {
                    navigator.goTo(TodoEditScreen())
                }

                is TodoHomeEvent.OnItemClick -> {
                    navigator.goTo(TodoEditScreen(event.todoId, mode = TodoMode.EDIT))
                }

                TodoHomeEvent.ToggleListType -> {
                    launchOrThrow(Dispatchers.IO) {
                        todoRepository.setIsGridViewListingType(!isGridViewListType)
                    }

                }

                TodoHomeEvent.OnFilterClick -> {
                    // not implemented yet
                }
            }
        }

        return TodoHomeScreen.TodoHomeScreenState(
            todos = todos,
            isGridView = isGridViewListType,
            eventSink = wrapEventSink(eventSink),
        )
    }

    class Factory(private val todoRepository: TodoRepository) : Presenter.Factory {
        override fun create(screen: Screen, navigator: Navigator, context: CircuitContext): Presenter<*>? {
            return when (screen) {
                is TodoHomeScreen -> TodoPresenter(navigator = navigator, todoRepository = todoRepository)
                else -> null
            }
        }
    }
}
