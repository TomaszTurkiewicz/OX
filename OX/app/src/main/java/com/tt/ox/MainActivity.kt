package com.tt.ox

import android.content.ContentValues.TAG
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.tt.ox.databinding.ActivityMainBinding
import com.tt.ox.drawables.BackgroundColorDrawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

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

const val EASY_GAME = 1
const val NORMAL_GAME = 2
const val HARD_GAME = 3

const val MOVES = 10

const val TEST = true

const val DARK_MODE_AUTO = 0
const val DARK_MODE_ON = 1
const val DARK_MODE_OFF = 2

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var consentInformation: ConsentInformation
    // Use an atomic boolean to initialize the Google Mobile Ads SDK and load ads once.
    private var isMobileAdsInitializeCalled = AtomicBoolean(false)

    private var buttonClickSound0: MediaPlayer? = null
    private var winSound: MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainActivityLayout.background = BackgroundColorDrawable(this)

        requestConsentForm()
        buttonClickSound0 = MediaPlayer.create(this, R.raw.button_click)

    }

    fun playButtonClick() {
        GlobalScope.launch(Dispatchers.Default) {
            buttonClickSound0?.start()
        }
    }

    fun playWinSound(){
        winSound?.stop()
        winSound?.release()
        winSound = null
        winSound = MediaPlayer.create(this,R.raw.win_sound)
        GlobalScope.launch(Dispatchers.Default){
            winSound?.start()
        }
    }


    private fun requestConsentForm(){

//        //only for testing
//        val debugSettings = ConsentDebugSettings.Builder(this)
//            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
//            .addTestDeviceHashedId("5FB4342F22793F9BF0E1E40F60712E1C")
//            .build()


        // Set tag for under age of consent. false means users are not under age
        // of consent.
        val params = ConsentRequestParameters
            .Builder()
//            .setConsentDebugSettings(debugSettings)
            .setTagForUnderAgeOfConsent(false)
            .build()


        consentInformation = UserMessagingPlatform.getConsentInformation(this)
//        consentInformation.reset()
        consentInformation.requestConsentInfoUpdate(
            this,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                    this@MainActivity
                ) { loadAndShowError ->
                    // Consent gathering failed.
                    Log.w(
                        TAG, String.format(
                            "%s: %s",
                            loadAndShowError?.errorCode,
                            loadAndShowError?.message
                        )
                    )

                    // Consent has been gathered.
                    if (consentInformation.canRequestAds()) {
                        initializeMobileAdsSdk()
                    }
                }
            },
            {
                    requestConsentError ->
                // Consent gathering failed.
                Log.w(TAG, String.format("%s: %s",
                    requestConsentError.errorCode,
                    requestConsentError.message))
            })

        // Check if you can initialize the Google Mobile Ads SDK in parallel
        // while checking for new consent information. Consent obtained in
        // the previous session can be used to request ads.
        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk()
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.get()) {
            return
        }
        isMobileAdsInitializeCalled.set(true)

        // Initialize the Google Mobile Ads SDK.
        MobileAds.initialize(this)

    }

}
/*todo
*  better buttons and layouts ui
*  buttons not active change ui (online search, reset and send invitation)
*  multiplayer give hint to add user if user list empty
*  settings: (sounds?, clear statistics?, send game, other games, delete user from firebase if logged in and logged in at least once)
*  sounds (button click, win/lose/draw sound, add moves sound) , background music
*  online show progress bar when checking last activity and downloading users from firebase
 */