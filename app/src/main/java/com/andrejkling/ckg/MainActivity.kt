package com.andrejkling.ckg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.andrejkling.ckg.model.AphorismStore
import com.andrejkling.ckg.ui.components.CKGBackground
import com.andrejkling.ckg.ui.screens.aphorism.AphorismHomeScreen
import com.andrejkling.ckg.ui.theme.CKGTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CKGTheme {
                CKGApp()
            }
        }
    }
}

@Composable
fun CKGApp() {
    val context = LocalContext.current
    val aphorisms = remember { AphorismStore.load(context) }
    CKGBackground {
        AphorismHomeScreen(
            todays = AphorismStore.today(aphorisms),
            onOpenTodays = {},     // detail screen comes in the next step
            onShowAnother = {},    // detail screen comes in the next step
            onOpenSaved = {}       // Favourites comes in a later step
        )
    }
}
