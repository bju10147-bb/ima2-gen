package com.ima2gen.app.ui.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ima2gen.app.data.local.db.HistoryDao
import com.ima2gen.app.data.local.db.HistoryEntity
import com.ima2gen.app.data.local.db.SessionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class GalleryFilterMode {
    ALL, SESSION
}

data class SessionGroup(
    val session: SessionEntity,
    val items: List<HistoryEntity>
)

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

    private val _filterMode = MutableStateFlow(GalleryFilterMode.SESSION)
    val filterMode: StateFlow<GalleryFilterMode> = _filterMode.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val sessionGroups: StateFlow<List<SessionGroup>> = combine(sessions, _filterMode, _selectedSessionId) { sessions, mode, sessionId ->
        Triple(sessions, mode, sessionId)
    }.flatMapLatest { (sessions, mode, sessionId) ->
        if (mode == GalleryFilterMode.ALL) {
            // Fetch all history and group by session
            historyDao.getAllHistoryForProject(projectId).map { allHistory ->
                sessions.map { session ->
                    SessionGroup(
                        session = session,
                        items = allHistory.filter { it.sessionId == session.id }
                    )
                }.filter { it.items.isNotEmpty() }
            }
        } else if (sessionId != null) {
            // Only one group for the selected session
            historyDao.getHistoryForSession(sessionId).map { items ->
                val session = sessions.find { it.id == sessionId }
                if (session != null) listOf(SessionGroup(session, items)) else emptyList()
            }
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            sessions.collect { list ->
                if (_selectedSessionId.value == null && list.isNotEmpty()) {
                    _selectedSessionId.value = list.first().id
                }
            }
        }
    }

    fun setFilterMode(mode: GalleryFilterMode) {
        _filterMode.value = mode
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

    fun deleteHistory(id: String) {
        viewModelScope.launch {
            historyDao.deleteHistory(id)
        }
    }
}
