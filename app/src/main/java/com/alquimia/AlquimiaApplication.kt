package com.alquimia

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AlquimiaApplication : Application() {
    // Você pode adicionar lógica de inicialização global aqui, se necessário.
    // Por enquanto, apenas a anotação @HiltAndroidApp é suficiente para o Hilt.
}
