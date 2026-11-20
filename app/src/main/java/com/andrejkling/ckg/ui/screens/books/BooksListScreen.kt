package com.andrejkling.ckg.ui.screens.books

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andrejkling.ckg.R
import com.andrejkling.ckg.model.Book
import com.andrejkling.ckg.ui.theme.Accent
import com.andrejkling.ckg.ui.theme.Unity
import com.andrejkling.ckg.ui.theme.SavedRed

// Books tab, like the iOS BookListView: "Add a book" at the top, a card per book,
// long-press for Edit and Delete. Empty list shows the "My Aspiration" heading.
private val BookCyan = Color(0xFF00F6F8)     // iOS Books card colour
private val BookText = Color(0xFF100031)     // iOS Books card text colour

@Composable
fun BooksListScreen(
    books: List<Book>,
    onAdd: () -> Unit,
    onOpen: (Book) -> Unit,
    onEdit: (Book) -> Unit,
    onDelete: (Book) -> Unit
) {
    var pendingDelete by remember { mutableStateOf<Book?>(null) }

    if (books.isEmpty()) {
        EmptyBooks(onAdd)
    } else {
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
            Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.Center) {
                AddBookButton(onAdd)
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 15.dp, vertical = 21.dp),
                verticalArrangement = Arrangement.spacedBy(27.dp)
            ) {
                items(books, key = { it.id }) { book ->
                    BookCard(
                        book = book,
                        onOpen = { onOpen(book) },
                        onEdit = { onEdit(book) },
                        onDelete = { pendingDelete = book }
                    )
                }
            }
        }
    }

    pendingDelete?.let { book ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete this book?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(book)
                    pendingDelete = null
                }) { Text("Delete", color = SavedRed) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun AddBookButton(onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(50))
            .clickable(onClick = onAdd)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Add, contentDescription = null, tint = Accent, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text("Add a book", color = Accent, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
    }
}

@Composable
private fun GradientWord(word: String, modifier: Modifier = Modifier, size: TextUnit = 28.sp) {
    Text(
        text = word,
        modifier = modifier,
        style = TextStyle(
            brush = Brush.verticalGradient(listOf(Color(0xFF0DFFC7), Color(0xFF39E6FF))),
            fontWeight = FontWeight.Bold,
            fontSize = size,
            letterSpacing = 2.1.sp
        )
    )
}

// Empty Books, like the iOS TrianglesOverLapping: "My" slides in from the left, "Aspiration"
// from the right, and the meditation picture turns inside a wheel of triangles (5 seconds).
@Composable
private fun EmptyBooks(onAdd: () -> Unit) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val slide by animateFloatAsState(
        targetValue = if (started) 0f else 1f,
        animationSpec = tween(5000, easing = LinearEasing),
        label = "slide"
    )
    val spin by animateFloatAsState(
        targetValue = if (started) 18f else 0f,
        animationSpec = tween(5000, easing = EaseOut),
        label = "spin"
    )

    BoxWithConstraints(Modifier.fillMaxSize()) {
        // Everything scales with the screen width and height, so small phones fit as well
        val wheel: Dp = minOf(180f.dp, maxWidth * 0.5f, maxHeight * 0.3f)
        val wordSize = (maxWidth.value * 0.075f).coerceAtMost(28f).sp
        val slideDp = maxWidth.value * 1.05f

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(top = 44.dp, bottom = 110.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AddBookButton(onAdd)
            Spacer(Modifier.height(16.dp))
            GradientWord("My", Modifier.offset(x = (-slideDp * slide).dp), wordSize)
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                MeditationWheel(spin, wheel)
            }
            GradientWord("Aspiration", Modifier.offset(x = (slideDp * slide).dp), wordSize)
        }
    }
}

@Composable
private fun MeditationWheel(degrees: Float, wheel: Dp) {
    Box(Modifier.size(wheel), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            for (i in 0 until 21) {
                rotate(degrees * i, pivot = center) {
                    drawPath(trianglePath(size.width, size.height), WheelColor)
                }
            }
        }
        Image(
            painter = painterResource(R.drawable.meditation),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(wheel * 0.78f).clip(CircleShape)
        )
    }
}

// iOS TriangleFirst: top-left, bottom-right, bottom-left
private fun trianglePath(w: Float, h: Float): Path = Path().apply {
    moveTo(0f, 0f)
    lineTo(w, h)
    lineTo(0f, h)
    close()
}

private val WheelColor = Color(0xFF0DFFC6).copy(alpha = 0.5f)

@Composable
fun BookCard(book: Book, onOpen: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    var menuOpen by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .combinedClickable(onClick = onOpen, onLongClick = { menuOpen = true })
    ) {
        // Cover on the right
        Image(
            painter = painterResource(R.drawable.harmony),
            contentDescription = book.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxWidth(0.5f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(10.dp))
        )
        // Title card on the left, over the cover, like the iOS overlap
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth(0.5f)
                .fillMaxHeight(0.8f)
                .shadow(8.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(BookCyan)
                .padding(21.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(book.title, color = BookText, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, textAlign = TextAlign.Start)
            Text(book.author, color = BookText.copy(alpha = 0.5f), fontSize = 12.sp)
        }

        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
            DropdownMenuItem(text = { Text("Edit") }, onClick = { menuOpen = false; onEdit() })
            DropdownMenuItem(text = { Text("Delete", color = SavedRed) }, onClick = { menuOpen = false; onDelete() })
        }
    }
}
