package com.ima2gen.app.ui.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ima2gen.app.data.local.db.HistoryDao
import com.ima2gen.app.data.local.db.ProjectEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val historyDao: HistoryDao
) : ViewModel() {

    val projects: StateFlow<List<ProjectEntity>> = historyDao.getAllProjects()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addProject(name: String, rootUri: String) {
        viewModelScope.launch {
            historyDao.insertProject(
                ProjectEntity(
                    name = name,
                    rootUri = rootUri
                )
            )
        }
    }

    fun deleteProject(id: String) {
        viewModelScope.launch {
            historyDao.deleteProject(id)
        }
    }
}
