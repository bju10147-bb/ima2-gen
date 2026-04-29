package com.ima2gen.app.ui.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ima2gen.app.data.local.db.HistoryDao
import com.ima2gen.app.data.local.db.HistoryEntity
import com.ima2gen.app.data.local.db.SessionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val historyDao: HistoryDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val projectId: String = checkNotNull(savedStateHandle["projectId"])

    val sessions: StateFlow<List<SessionEntity>> = historyDao.getSessionsForProject(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedSessionId = MutableStateFlow<String?>(null)
    val selectedSessionId: StateFlow<String?> = _selectedSessionId.asStateFlow()

    val historyItems: StateFlow<List<HistoryEntity>> = _selectedSessionId
        .flatMapLatest { sessionId ->
            if (sessionId == null) flowOf(emptyList())
            else historyDao.getHistoryForSession(sessionId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            sessions.collect { list ->
                if (_selectedSessionId.value == null && list.isNotEmpty()) {
                    _selectedSessionId.value = list.first().id
                }
            }
        }
    }

    fun selectSession(sessionId: String) {
        _selectedSessionId.value = sessionId
    }

    fun createSession(name: String) {
        viewModelScope.launch {
            val newSession = SessionEntity(projectId = projectId, name = name)
            historyDao.insertSession(newSession)
            _selectedSessionId.value = newSession.id
        }
    }

    fun deleteSession(id: String) {
        viewModelScope.launch {
            historyDao.deleteSession(id)
            if (_selectedSessionId.value == id) {
                _selectedSessionId.value = null
            }
        }
    }

    fun deleteHistoryItem(id: String) {
        viewModelScope.launch {
            historyDao.deleteHistory(id)
        }
    }
}
