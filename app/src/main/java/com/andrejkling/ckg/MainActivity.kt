package com.andrejkling.ckg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.andrejkling.ckg.model.Aphorism
import com.andrejkling.ckg.model.AphorismStore
import com.andrejkling.ckg.model.FavoritesStore
import com.andrejkling.ckg.ui.components.CKGBackground
import com.andrejkling.ckg.ui.screens.aphorism.AphorismDetailScreen
import com.andrejkling.ckg.ui.screens.aphorism.AphorismHomeScreen
import com.andrejkling.ckg.ui.screens.aphorism.FavouritesScreen
import com.andrejkling.ckg.ui.theme.CKGTheme
import java.time.LocalDate

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
    val favoritesStore = remember { FavoritesStore(context) }
    val favorites = remember { mutableStateListOf<Aphorism>().apply { addAll(favoritesStore.load()) } }

    // Index into aphorisms of the card being shown; null shows the home screen
    var selected by remember { mutableStateOf<Int?>(null) }
    var showingSaved by remember { mutableStateOf(false) }

    CKGBackground {
        val index = selected
        if (showingSaved) {
            FavouritesScreen(
                favorites = favorites.toList(),
                onRemove = { a ->
                    favorites.remove(a)
                    favoritesStore.save(favorites.toList())
                },
                onDone = { showingSaved = false }
            )
        } else if (index == null) {
            AphorismHomeScreen(
                todays = AphorismStore.today(aphorisms),
                onOpenTodays = {
                    val today = AphorismStore.today(aphorisms, LocalDate.now())
                    selected = aphorisms.indexOf(today).takeIf { it >= 0 } ?: 0
                },
                onShowAnother = { selected = aphorisms.indices.random() },
                onOpenSaved = { showingSaved = true }
            )
        } else {
            AphorismDetailScreen(
                aphorisms = aphorisms,
                startIndex = index,
                isSaved = { favorites.contains(it) },
                onToggleSave = { a ->
                    if (favorites.contains(a)) favorites.remove(a) else favorites.add(a)
                    favoritesStore.save(favorites.toList())
                },
                onBack = { selected = null }
            )
        }
    }
}
