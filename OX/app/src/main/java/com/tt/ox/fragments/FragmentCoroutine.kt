package com.tt.ox.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tt.ox.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class FragmentCoroutine : Fragment(), CoroutineScope {
    private lateinit var job: Job

    override val coroutineContext: CoroutineContext

        get() = job + Dispatchers.IO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    fun playButtonClick(){
        val activity = activity as MainActivity
        activity.playButtonClickSound()
    }

    fun playWinSound(){
        val activity = activity as MainActivity
        activity.playWinSound()
    }

    fun playDrawSound(){
        val activity = activity as MainActivity
        activity.playDrawSound()
    }

    fun playLoseSound(){
        val activity = activity as MainActivity
        activity.playLoseSound()
    }


}