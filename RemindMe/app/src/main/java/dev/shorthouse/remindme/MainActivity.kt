package dev.shorthouse.remindme

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.compose.navigation.RemindMeNavHost
import dev.shorthouse.remindme.theme.RemindMeTheme

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            RemindMeTheme {
                RemindMeNavHost()
            }
        }
    }
}
