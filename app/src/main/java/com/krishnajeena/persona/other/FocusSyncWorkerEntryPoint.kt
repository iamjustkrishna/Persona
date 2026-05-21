package com.krishnajeena.persona.other

import com.google.firebase.auth.FirebaseAuth
import com.krishnajeena.persona.data_layer.FocusRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface FocusSyncWorkerEntryPoint {
    fun firebaseAuth(): FirebaseAuth
    fun focusRepository(): FocusRepository
}
