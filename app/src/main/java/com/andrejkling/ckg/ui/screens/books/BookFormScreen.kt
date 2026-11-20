package com.andrejkling.ckg.ui.screens.books

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andrejkling.ckg.ui.theme.Accent
import com.andrejkling.ckg.ui.theme.Unity

// Add a book, like the iOS UpdateEditFormView: Cancel, Save, Title, Author, Text, Paste copied text.
// Save stays disabled until Title and Author are filled. The Title field gets focus when it opens.
private val FieldFill = Color(0xFFBFFBFF)   // pale cyan, like the iOS form
private val FieldText = Color.Black

@Composable
fun BookFormScreen(
    initialTitle: String = "",
    initialAuthor: String = "Sri Chinmoy",
    initialText: String = "",
    isUpdating: Boolean = false,
    onCancel: () -> Unit,
    onSave: (title: String, author: String, text: String) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var author by remember { mutableStateOf(initialAuthor) }
    var text by remember { mutableStateOf(initialText) }
    val clipboard = LocalClipboardManager.current
    val titleFocus = remember { FocusRequester() }
    val canSave = title.isNotBlank() && author.isNotBlank()
    // Keeps the form scrollable when the keyboard is up, so the focused field stays visible
    val scroll = rememberScrollState()

    LaunchedEffect(Unit) {
        if (!isUpdating) titleFocus.requestFocus()
    }

    // Full screen background; the header stays fixed and the fields scroll under it.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Unity)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scroll)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Cancel and Save, like the iOS top bar
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Cancel",
                    color = Accent,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable(onClick = onCancel).padding(4.dp)
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = if (isUpdating) "Update" else "Save",
                    color = if (canSave) Accent else Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .clickable(enabled = canSave) { onSave(title.trim(), author.trim(), text) }
                        .padding(4.dp)
                )
            }

            FormField(label = "Title", value = title, onValue = { title = it },
                modifier = Modifier.focusRequester(titleFocus), imeAction = ImeAction.Next)
            FormField(label = "Author", value = author, onValue = { author = it }, imeAction = ImeAction.Next)

            // The text copied from the library
            Text("Text", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth().heightIn(min = 160.dp, max = 260.dp),
                textStyle = androidx.compose.ui.text.TextStyle(color = FieldText, fontWeight = FontWeight.SemiBold),
                shape = RoundedCornerShape(4.dp),
                colors = fieldColors()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.White, RoundedCornerShape(12.dp))
                    .clickable {
                        clipboard.getText()?.text?.let { text = it }
                    }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Paste copied text", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    imeAction: ImeAction
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValue,
            singleLine = true,
            modifier = modifier.fillMaxWidth(),
            textStyle = androidx.compose.ui.text.TextStyle(color = FieldText, fontWeight = FontWeight.SemiBold, fontSize = 17.sp),
            shape = RoundedCornerShape(4.dp),
            colors = fieldColors(),
            keyboardOptions = KeyboardOptions(imeAction = imeAction),
            keyboardActions = KeyboardActions.Default
        )
    }
}

@Composable
private fun fieldColors() = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
    focusedContainerColor = FieldFill,
    unfocusedContainerColor = FieldFill,
    focusedBorderColor = Accent,
    unfocusedBorderColor = Accent,
    cursorColor = FieldText
)
