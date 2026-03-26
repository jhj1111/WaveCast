package com.example.wavecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wavecast.core.data.util.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface MainActivityUiState {
    object Loading : MainActivityUiState
    data class Success(val isOnline: Boolean) : MainActivityUiState
}

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    networkMonitor: NetworkMonitor
) : ViewModel() {

    val uiState: StateFlow<MainActivityUiState> = networkMonitor.isOnline
        .map { isOnline ->
            // 여기서 필요한 초기화 로직(예: 테마 로딩, 유저 정보 확인 등)을 수행할 수 있습니다.
            // 데모를 위해 의도적인 지연(splash screen 확인용)을 추가할 수 있습니다.
            MainActivityUiState.Success(isOnline)
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = MainActivityUiState.Loading,
            started = SharingStarted.WhileSubscribed(5_000)
        )
}
