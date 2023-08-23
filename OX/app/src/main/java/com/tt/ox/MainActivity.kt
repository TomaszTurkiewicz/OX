package com.tt.ox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tt.ox.databinding.ActivityMainBinding

const val NOTHING = 0
const val X = 1
const val O = 2

const val NO_ONE = 0
const val MAIN_PLAYER = 1
const val OPPONENT = 2

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}

//todo add fragment with adding/choosing opponent multiplayer - database