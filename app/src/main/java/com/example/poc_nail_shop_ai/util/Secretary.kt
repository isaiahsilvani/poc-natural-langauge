package com.example.poc_nail_shop_ai.util

import android.util.Log
import com.example.poc_nail_shop_ai.data.local.MockDB
import com.example.poc_nail_shop_ai.domain.model.Appointment
import com.example.poc_nail_shop_ai.domain.model.GeminiResponse
import com.example.poc_nail_shop_ai.domain.model.GenerativeAppointmentResults
import com.example.poc_nail_shop_ai.domain.model.Result
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Acts as a Secretary using AI
 */
object Secretary {
    /** hide this later! **/
    private const val API_KEY = "AIzaSyC7wLUIdgF9izf036uDKWHYLpH82MyC4IQ"

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = API_KEY,
        generationConfig = generationConfig {
            responseMimeType = "application/json"
        }
    )

    suspend fun sendPrompt(
        prompt: String
    ): Result<GenerativeAppointmentResults> {
        return withContext(Dispatchers.IO) {
            try {
                val formattedPrompt = """
                You are an appointment management assistant. Please analyze the following user prompt and generate a JSON response based on the action requested.

                Appointments already set in the system:
                ${MockDB.availableSlots.value?.joinToString("\n")}

                User Prompt: $prompt

                Response Format:
                {
                    "action": "add" or "cancel" or "unclear" or "error",
                    "id": (integer, only if "cancel", you must match the id to the selected appointment to cancel),
                    "appointment": { // if "action" is "cancel", set the 'appointment' field as null
                        "date": "YYYY-MM-DD",
                        "time": "HH:MM:AM/PM",
                        "provider": "...",
                        "services": "[Manicure, Pedicure]"
                    },
                    "errorMessage": "..." (only if "error")
                }
            """.trimIndent()

                val response = generativeModel.generateContent(
                    content {
                        text(formattedPrompt)
                    }
                )
                Log.e("TEST", "Isaiah - response: ${response.text}")
                Result.Success(
                    processGeminiResponse(response)
                )
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    private suspend fun processGeminiResponse(response: GenerateContentResponse): GenerativeAppointmentResults {
        return withContext(Dispatchers.Default) {
            try {
                val jsonString = response.text ?: throw Exception("Response text is null.")
                val geminiResponse = Json.decodeFromString<GeminiResponse>(jsonString)

                when (geminiResponse.action) {
                    "cancel" -> {
                        Log.e("TEST", "Isaiah - CANCEL!!!")
                        val id = geminiResponse.id ?: throw Exception("ID missing for cancel action.")
                        GenerativeAppointmentResults.Cancel(id)
                    }
                    "add" -> {
                        Log.e("TEST", "Isaiah - ADD!!!")
                        val appointmentJson = geminiResponse.appointment ?: throw Exception("Appointment details missing for add action.")
                        val appointment = Appointment(
                            date = appointmentJson.date,
                            time = appointmentJson.time,
                            provider = appointmentJson.provider,
                            services = appointmentJson.services,
                            id = appointmentJson.id
                        )
                        GenerativeAppointmentResults.Add(appointment)
                    }
                    "error" -> {
                        Log.e("TEST", "Isaiah - ERROR!!!")
                        val errorMessage = geminiResponse.errorMessage ?: "Unknown error."
                        GenerativeAppointmentResults.Error(Exception(errorMessage))
                    }
                    "unclear" -> GenerativeAppointmentResults.Unclear
                    else -> GenerativeAppointmentResults.Error(Exception("Invalid action from Gemini: ${geminiResponse.action}"))
                }
            } catch (e: Exception) {
                GenerativeAppointmentResults.Error(e)
            }
        }
    }

}