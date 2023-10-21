package com.tt.ox

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tt.ox.databinding.ActivityMainBinding

const val NOTHING = 0
const val X = 1
const val O = 2

const val NO_ONE = 0
const val MAIN_PLAYER = 1
const val OPPONENT = 2

const val TOP_LEFT = 1
const val TOP_MID = 2
const val TOP_RIGHT = 3
const val MID_LEFT = 4
const val MID_MID = 5
const val MID_RIGHT = 6
const val BOTTOM_LEFT = 7
const val BOTTOM_MID = 8
const val BOTTOM_RIGHT = 9

const val PLAYER_MARK_PRESSED = 1
const val OPPONENT_MARK_PRESSED = 2

const val EASY_GAME = 1
const val NORMAL_GAME = 2
const val HARD_GAME = 3

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}