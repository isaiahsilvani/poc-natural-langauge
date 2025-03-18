package com.example.poc_nail_shop_ai.data.local

import androidx.lifecycle.MutableLiveData
import com.example.poc_nail_shop_ai.domain.model.Appointment

object MockDB {
    private val _availableSlots = MutableLiveData<List<Slot>>()
    val availableSlots get() = _availableSlots

    init {
        _availableSlots.value = mutableListOf(
            createSlot("03/14/2025", "03:00 PM", "Jenna", listOf("Pedicure"), 0),
            createSlot("03/14/2025", "01:00 PM", "Keisha", listOf("Manicure"), 1),
            createSlot("03/14/2025", "11:00 AM", "Jenna", listOf("Pedicure"), 2),
            createSlot("03/15/2025", "03:00 PM", "Jenna", listOf("Manicure", "Pedicure"), 3),
            createSlot("03/15/2025", "05:00 PM", "Tom", listOf("Manicure", "Pedicure"), 4),
            createSlot("03/16/2025", "02:00 PM", "Ashley", listOf("Manicure", "Pedicure"), 5),
        )
    }

    fun cancelAppointment(id: Int) {
        val currentSlots = _availableSlots.value?.toMutableList() ?: mutableListOf()
        currentSlots.removeAll { it.id == id }
        _availableSlots.postValue(currentSlots)
    }

    fun addAppointment(appointment: Appointment) {
        val currentSlots = _availableSlots.value?.toMutableList() ?: mutableListOf()
        val slot = Slot(
            id = appointment.id,
            date = appointment.date,
            time = appointment.time,
            provider = appointment.provider,
            services = appointment.services
        )
        currentSlots.add(slot)
        _availableSlots.value = currentSlots
    }

    private fun createSlot(
        date: String,
        time: String,
        provider: String,
        services: List<String>,
        id: Int
    ): Slot {
        return Slot(
            id = id,
            date = date,
            time = time,
            provider = provider,
            services = services
        )
    }
}

data class Slot(
    val id: Int,
    val date: String,
    val time: String,
    val provider: String,
    val services: List<String>
)