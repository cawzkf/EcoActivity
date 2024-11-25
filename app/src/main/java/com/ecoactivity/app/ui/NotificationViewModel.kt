package com.ecoactivity.app.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotificationViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _notificationEnabled = MutableLiveData<Boolean>()
    val notificationEnabled: LiveData<Boolean> = _notificationEnabled

    private val _timeSelected = MutableLiveData<Pair<Int, Int>>() // Hora e minuto
    val timeSelected: LiveData<Pair<Int, Int>> = _timeSelected

    private val _frequency = MutableLiveData<String>()
    val frequency: LiveData<String> = _frequency

    private val _customDays = MutableLiveData<Set<String>>()
    val customDays: LiveData<Set<String>> = _customDays

    init {
        loadNotificationSettings()
    }

    fun saveNotificationSettings(
        context: Context,
        isEnabled: Boolean,
        hour: Int,
        minute: Int,
        frequency: String,
        days: Set<String>
    ) {
        val userId = auth.currentUser?.uid ?: return

        val notificationSettings = mapOf(
            "enabled" to isEnabled,
            "hour" to hour,
            "minute" to minute,
            "frequency" to frequency,
            "customDays" to days.toList()
        )

        firestore.collection("users").document(userId)
            .collection("settings").document("notifications")
            .set(notificationSettings)
            .addOnSuccessListener {
                _notificationEnabled.value = isEnabled
                _timeSelected.value = Pair(hour, minute)
                _frequency.value = frequency
                _customDays.value = days
            }
            .addOnFailureListener {
                // Adicione lógica de falha aqui
            }
    }

    fun loadNotificationSettings() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId)
            .collection("settings").document("notifications")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    _notificationEnabled.value = document.getBoolean("enabled") ?: false
                    _timeSelected.value = Pair(
                        document.getLong("hour")?.toInt() ?: 12,
                        document.getLong("minute")?.toInt() ?: 0
                    )
                    _frequency.value = document.getString("frequency") ?: "Diariamente"
                    _customDays.value = (document.get("customDays") as? List<*>)?.mapNotNull { it as? String }?.toSet() ?: emptySet()
                }
            }
            .addOnFailureListener {
                // Adicione lógica de falha aqui
            }
    }
}
