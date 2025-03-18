package com.example.poc_nail_shop_ai.domain.model

import java.lang.Exception

sealed class GenerativeAppointmentResults {
    data class Cancel(val id: Int) : GenerativeAppointmentResults()
    data class Add(val appointment: Appointment) : GenerativeAppointmentResults()
    data class Error(val exception: Exception) : GenerativeAppointmentResults()
    data object Unclear : GenerativeAppointmentResults()
}
