package com.erbe.protodatastore.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.erbe.protodatastore.UserPreferences
import com.erbe.protodatastore.UserPreferences.SortOrder
import com.erbe.protodatastore.data.Task
import com.erbe.protodatastore.data.TasksRepository
import com.erbe.protodatastore.data.UserPreferencesRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class TasksUiModel(
        val tasks: List<Task>,
        val showCompleted: Boolean,
        val sortOrder: SortOrder
)

class TasksViewModel(
        repository: TasksRepository,
        private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow

    // Every time the sort order, the show completed filter or the list of tasks emit,
    // we should recreate the list of tasks
    private val tasksUiModelFlow = combine(
            repository.tasks,
            userPreferencesFlow
    ) { tasks: List<Task>, userPreferences: UserPreferences ->
        return@combine TasksUiModel(
                tasks = filterSortTasks(
                        tasks,
                        userPreferences.showCompleted,
                        userPreferences.sortOrder
                ),
                showCompleted = userPreferences.showCompleted,
                sortOrder = userPreferences.sortOrder
        )
    }
    val tasksUiModel = tasksUiModelFlow.asLiveData()

    private fun filterSortTasks(
            tasks: List<Task>,
            showCompleted: Boolean,
            sortOrder: SortOrder
    ): List<Task> {
        // filter the tasks
        val filteredTasks = if (showCompleted) {
            tasks
        } else {
            tasks.filter { !it.completed }
        }
        // sort the tasks
        return when (sortOrder) {
            SortOrder.UNSPECIFIED -> filteredTasks
            SortOrder.NONE -> filteredTasks
            SortOrder.BY_DEADLINE -> filteredTasks.sortedByDescending { it.deadline }
            SortOrder.BY_PRIORITY -> filteredTasks.sortedBy { it.priority }
            SortOrder.BY_DEADLINE_AND_PRIORITY -> filteredTasks.sortedWith(
                    compareByDescending<Task> { it.deadline }.thenBy { it.priority }
            )
            // We shouldn't get any other values
            else -> throw UnsupportedOperationException("$sortOrder not supported")
        }
    }

    fun showCompletedTasks(show: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateShowCompleted(show)
        }
    }

    fun enableSortByDeadline(enable: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.enableSortByDeadline(enable)
        }
    }

    fun enableSortByPriority(enable: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.enableSortByPriority(enable)
        }
    }
}

class TasksViewModelFactory(
        private val repository: TasksRepository,
        private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TasksViewModel(repository, userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}