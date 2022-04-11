package com.example.contingenciaapp.io

import java.util.*

class ObservableObject private constructor() : Observable() {

    fun updateValue(data: Any) {
        synchronized(this) {
            setChanged()
            //Notify that one value changed, but don't specify which one
            notifyObservers(data)
        }
    }

    companion object {
        val instance = ObservableObject()
    }
}