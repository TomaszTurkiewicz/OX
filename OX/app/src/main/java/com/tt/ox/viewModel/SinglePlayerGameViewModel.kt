package com.tt.ox.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SinglePlayerGameViewModel() : ViewModel() {
}

class SinglePlayerGameViewModelFactory() : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SinglePlayerGameViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return SinglePlayerGameViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}