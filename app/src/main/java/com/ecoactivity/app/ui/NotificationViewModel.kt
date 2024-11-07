package com.ecoactivity.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "Ativar Notificações" // Mensagem padrão
    }
    val text: LiveData<String> = _text // Expondo o LiveData
}
