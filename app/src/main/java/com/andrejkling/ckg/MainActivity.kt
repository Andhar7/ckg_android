package com.andrejkling.ckg

import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andrejkling.ckg.model.Aphorism
import com.andrejkling.ckg.model.AphorismStore
import com.andrejkling.ckg.model.Book
import com.andrejkling.ckg.model.BooksStore
import com.andrejkling.ckg.model.FavoritesStore
import com.andrejkling.ckg.ui.components.CKGBackground
import com.andrejkling.ckg.ui.components.FloatingTabBar
import com.andrejkling.ckg.ui.screens.aphorism.AphorismDetailScreen
import com.andrejkling.ckg.ui.screens.aphorism.AphorismHomeScreen
import com.andrejkling.ckg.ui.screens.aphorism.FavouritesScreen
import com.andrejkling.ckg.ui.screens.books.BookDetailScreen
import com.andrejkling.ckg.ui.screens.books.BookFormScreen
import com.andrejkling.ckg.ui.screens.books.LibraryScreen
import com.andrejkling.ckg.ui.screens.books.BooksListScreen
import com.andrejkling.ckg.ui.theme.Accent
import com.andrejkling.ckg.ui.theme.CKGTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The iOS app hides the status bar (statusBarHidden); do the same here
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.statusBars())
        }
        setContent {
            CKGTheme {
                CKGApp()
            }
        }
    }
}

@Composable
fun CKGApp() {
    // Same order as the iOS tab bar: Books first
    var tab by remember { mutableIntStateOf(0) }
    var aphorismCardOpen by remember { mutableStateOf(false) }
    var booksFormOpen by remember { mutableStateOf(false) }

    CKGBackground {
        Box(Modifier.fillMaxSize()) {
            when (tab) {
                0 -> BooksTab(onFullScreenChange = { booksFormOpen = it })
                1 -> ComingSoon("Music")      // next steps
                2 -> AphorismsTab(onFullScreenChange = { aphorismCardOpen = it })
                3 -> ComingSoon("Club")       // next steps
            }
            // Hidden on the Aphorism card and saved list, as the iOS app hides its tab bar there
            if (!aphorismCardOpen && !booksFormOpen) {
                FloatingTabBar(selected = tab, onSelect = { tab = it })
            }
        }
    }
}

@Composable
private fun ComingSoon(name: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("$name is coming in the next step", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
    }
}

@Composable
private fun BooksTab(onFullScreenChange: (Boolean) -> Unit) {
    val context = LocalContext.current
    val store = remember { BooksStore(context) }
    val books = remember { mutableStateListOf<Book>().apply { addAll(store.load().sortedBy { it.title }) } }
    var showingLibrary by remember { mutableStateOf(false) }
    var showingForm by remember { mutableStateOf(false) }
    var openBook by remember { mutableStateOf<Book?>(null) }
    var editing by remember { mutableStateOf<Book?>(null) }

    onFullScreenChange(showingLibrary || showingForm || openBook != null || editing != null)
    // Edit opens the same form, filled with the book, and Update saves the change in place
    editing?.let { book ->
        BookFormScreen(
            initialTitle = book.title,
            initialAuthor = book.author,
            initialText = book.synopsis,
            isUpdating = true,
            onCancel = { editing = null },
            onSave = { title, author, text ->
                val updated = book.copy(title = title, author = author, synopsis = text)
                val index = books.indexOfFirst { it.id == book.id }
                if (index >= 0) books[index] = updated
                books.sortBy { it.title }
                store.save(books.toList())
                editing = null
                openBook = updated
            }
        )
        return
    }
    openBook?.let { current ->
        BookDetailScreen(
            book = current,
            onBack = { openBook = null },
            onEdit = { editing = current },
            onDelete = {
                books.remove(current)
                store.save(books.toList())
                openBook = null
            },
            onSaveText = { newText ->
                val updated = current.copy(synopsis = newText)
                val index = books.indexOfFirst { it.id == current.id }
                if (index >= 0) books[index] = updated
                store.save(books.toList())
                openBook = updated
            }
        )
        return
    }
    if (showingLibrary) {
        // Add a book opens the library first; Create book closes it and opens the form (like iOS)
        LibraryScreen(
            onClose = { showingLibrary = false },
            onCreate = {
                showingLibrary = false
                showingForm = true
            }
        )
        return
    }
    if (showingForm) {
        BookFormScreen(
            onCancel = { showingForm = false },
            onSave = { title, author, text ->
                books.add(Book(title = title, author = author, synopsis = text))
                books.sortBy { it.title }
                store.save(books.toList())
                showingForm = false
            }
        )
        return
    }

    BooksListScreen(
        books = books.toList(),
        onAdd = { showingLibrary = true },
        onOpen = { openBook = it },
        onEdit = {},         // edit comes with the form
        onDelete = { book ->
            books.remove(book)
            store.save(books.toList())
        }
    )
}

@Composable
private fun AphorismsTab(onFullScreenChange: (Boolean) -> Unit) {
    val context = LocalContext.current
    val aphorisms = remember { AphorismStore.load(context) }
    val favoritesStore = remember { FavoritesStore(context) }
    val favorites = remember { mutableStateListOf<Aphorism>().apply { addAll(favoritesStore.load()) } }

    // Index into aphorisms of the card being shown; null shows the home screen
    var selected by remember { mutableStateOf<Int?>(null) }
    var showingSaved by remember { mutableStateOf(false) }

    val index = selected
    onFullScreenChange(showingSaved || index != null)
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
