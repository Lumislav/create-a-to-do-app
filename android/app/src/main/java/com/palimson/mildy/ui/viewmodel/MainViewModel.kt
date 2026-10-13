package com.palimson.mildy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palimson.mildy.data.local.ProjectEntity
import com.palimson.mildy.data.remote.NetworkClient
import com.palimson.mildy.data.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AppSettings(
    val isDarkTheme: Boolean = true,
    val languageCode: String = "ru", // "ru", "kk", "en"
    val aiMode: String = "local", // "local" or "server"
    val backendUrl: String = "http://10.0.2.2:8000",
    val isServerOnline: Boolean = false,
    val serverStatusMessage: String = "On-Device Engine Active"
)

class MainViewModel(private val repository: ProjectRepository) : ViewModel() {

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    val recentProjects: StateFlow<List<ProjectEntity>> = repository.recentProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProjects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        checkBackendHealth()
    }

    fun setTheme(isDark: Boolean) {
        _settings.value = _settings.value.copy(isDarkTheme = isDark)
    }

    fun setLanguage(lang: String) {
        _settings.value = _settings.value.copy(languageCode = lang)
    }

    fun setAiMode(mode: String) {
        _settings.value = _settings.value.copy(aiMode = mode)
    }

    fun setBackendUrl(url: String) {
        NetworkClient.updateBaseUrl(url)
        _settings.value = _settings.value.copy(backendUrl = url)
        checkBackendHealth()
    }

    fun checkBackendHealth() {
        viewModelScope.launch {
            try {
                val resp = NetworkClient.getService().checkHealth()
                if (resp.isSuccessful && resp.body() != null) {
                    val info = resp.body()!!
                    _settings.value = _settings.value.copy(
                        isServerOnline = true,
                        serverStatusMessage = "${info.models} Online"
                    )
                } else {
                    _settings.value = _settings.value.copy(
                        isServerOnline = false,
                        serverStatusMessage = "On-Device Offline Mode"
                    )
                }
            } catch (e: Exception) {
                _settings.value = _settings.value.copy(
                    isServerOnline = false,
                    serverStatusMessage = "On-Device Offline Mode"
                )
            }
        }
    }

    fun createProject(name: String, toolType: String, mediaUri: String? = null) {
        viewModelScope.launch {
            repository.createProject(name, toolType, mediaUri)
        }
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            repository.deleteProject(project)
        }
    }
}
