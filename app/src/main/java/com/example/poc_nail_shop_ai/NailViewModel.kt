package com.example.poc_nail_shop_ai

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poc_nail_shop_ai.data.local.MockDB
import com.example.poc_nail_shop_ai.domain.model.GenerativeAppointmentResults
import com.example.poc_nail_shop_ai.domain.model.Result
import com.example.poc_nail_shop_ai.util.Secretary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NailViewModel : ViewModel() {
  private val _uiState: MutableStateFlow<UiState> =
    MutableStateFlow(UiState.Initial)
  val uiState: StateFlow<UiState> =
    _uiState.asStateFlow()

  /**
   * Purpose of this prompt is to determine if we either need to add, or cancel an appointment
   */
  fun sendPrompt(
    prompt: String
  ) {
    _uiState.value = UiState.Loading

    viewModelScope.launch(Dispatchers.IO) {
      when (
        val result = Secretary.sendPrompt(prompt)
      ) {
        is Result.Success -> processGenerativeAppointmentsResult(result.data)
        is Result.Error -> result.exception.message?.let { errorMsg ->
          _uiState.value = UiState.Error(errorMsg)
        }
      }
    }
  }

  private fun processGenerativeAppointmentsResult(data: GenerativeAppointmentResults) {
    when (data) {
      is GenerativeAppointmentResults.Add -> Log.e("TEST", "Isaiah - add!")
      is GenerativeAppointmentResults.Cancel -> {
        MockDB.cancelAppointment(data.id)
      }
      is GenerativeAppointmentResults.Error -> Log.e("TEST", "Isaiah - error!")
      GenerativeAppointmentResults.Unclear -> Log.e("TEST", "Isaiah - Unclear!")
    }
  }
}