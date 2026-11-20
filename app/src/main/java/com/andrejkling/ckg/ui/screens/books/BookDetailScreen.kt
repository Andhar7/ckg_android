package com.andrejkling.ckg.ui.screens.books

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andrejkling.ckg.R
import com.andrejkling.ckg.model.Book
import com.andrejkling.ckg.ui.theme.Accent
import com.andrejkling.ckg.ui.theme.SavedRed
import com.andrejkling.ckg.ui.theme.Unity

// Book detail, like the iOS DetailBookView: Back and Delete, the cover with title and author,
// Share and Edit pills, and the text of the book in a box that can be saved.
@Composable
fun BookDetailScreen(
    book: Book,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSaveText: (String) -> Unit
) {
    val context = LocalContext.current
    var text by remember(book.id) { mutableStateOf(book.synopsis) }
    var confirmDelete by remember { mutableStateOf(false) }
    val changed = text != book.synopsis

    BackHandler(onBack = onBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Unity)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back and Delete, like the iOS header
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "‹ Back",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                modifier = Modifier.clickable(onClick = onBack).padding(4.dp)
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "Delete",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.clickable { confirmDelete = true }.padding(4.dp)
            )
        }

        // Cover on the left, title and author on the right with the two pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(R.drawable.harmony),
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .height(170.dp)
                    .clip(RoundedCornerShape(topEnd = 21.dp, bottomEnd = 21.dp))
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                Text(book.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 24.sp)
                Text("By ${book.author}", color = Color.White.copy(alpha = 0.7f), fontSize = 15.sp)
                Spacer(Modifier.height(8.dp))
                // Two equal pills that share the row, like the iOS HStack with pillStyle()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Pill("Share", Icons.Filled.Share) {
                        val send = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, text)
                        }
                        context.startActivity(Intent.createChooser(send, null))
                    }
                    Pill("Edit", Icons.Filled.Edit, onClick = onEdit)
                }
            }
        }

        // Text panel, like the iOS BookDetails panel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1B2A6B).copy(alpha = 0.9f))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Text input", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                if (changed) {
                    Text(
                        text = "Save",
                        color = SavedRed,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .clickable { onSaveText(text) }
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                // Fixed height inside the panel, so long text scrolls inside the box
                modifier = Modifier.fillMaxWidth().height(260.dp),
                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = Accent
                )
            )
        }
    }

    if (confirmDelete) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("What do you prefer?") },
            text = { Text("Are you sure that you want to delete book?") },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    confirmDelete = false
                    onDelete()
                }) { Text("Yes", color = SavedRed) }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { confirmDelete = false }) { Text("No") }
            }
        )
    }
}

// Share and Edit share one size, so both pills look the same, like the iOS pillStyle()
@Composable
private fun RowScope.Pill(label: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .weight(1f)
            .height(38.dp)
            .clip(RoundedCornerShape(50))
            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(50))
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Accent, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, color = Accent, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}
