package com.zerocode.todoappciruitwithroom

import com.zerocode.todoappciruitwithroom.storage.DataStoreManager
import com.zerocode.todoappciruitwithroom.storage.Todo
import com.zerocode.todoappciruitwithroom.storage.TodoDatabase
import com.zerocode.todoappciruitwithroom.storage.TodoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TodoRepository(
    private val database: TodoDatabase,
    private val dataStoreManager: DataStoreManager,
) {

    fun isGridViewListingType(): Flow<Boolean> = dataStoreManager.isGridViewListingType

    suspend fun setIsGridViewListingType(value: Boolean) {
        dataStoreManager.setIsGridViewListingType(value)
    }

    private val todoDao = database.getDao()

    suspend fun addTodo(title: String, content: String) {
        todoDao.insert(TodoEntity(title = title, content = content))
    }

    suspend fun updateTodo(todo: Todo) {
        todoDao.update(todo.toEntity())
    }

    suspend fun deleteTodo(id: Long) {
        todoDao.deleteTodo(id)
    }

    suspend fun getTodo(id: Long): Todo? {
        return todoDao.getTodo(id)?.toTodo()
    }

    fun getTodos(): Flow<List<Todo>> {
        return todoDao.getAllAsFlow().map { entities ->
            entities.map { entity -> entity.toTodo() }
        }
    }

    suspend fun getAllTodos(): List<Todo> {
        return todoDao.getAllTodos().map { entities ->
            entities.toTodo()
        }
    }
}