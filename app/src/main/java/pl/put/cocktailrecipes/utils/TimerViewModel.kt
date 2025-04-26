package pl.put.cocktailrecipes.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {

    private val _timeInSec = MutableStateFlow(0)
    val timeInSec: StateFlow<Int> = _timeInSec

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private var timerJob: Job? = null
    private val maxTime = (59 * 60) + 59

    fun startTimer() {
        if (_isPlaying.value || _timeInSec.value >= maxTime) return
        _isPlaying.value = true
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                if (_timeInSec.value < maxTime) {
                    _timeInSec.value += 1
                } else {
                    _isPlaying.value = false
                    break
                }
            }
        }
    }

    fun pauseTimer() {
        _isPlaying.value = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        _isPlaying.value = false
        timerJob?.cancel()
        _timeInSec.value = 0
    }
}