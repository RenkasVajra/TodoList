package com.example.myapplication//.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun EditTaskScreen(todoItem: TodoItem?) {

    val (taskText, setTaskText) = rememberSaveable { mutableStateOf<String>(todoItem?.text.orEmpty()) }
    val (selectedImportance, setSelectedImportance) = rememberSaveable { mutableStateOf<Importance>(todoItem?.importance ?: Importance.Обычная) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = taskText,
            onValueChange = setTaskText,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Описание") },
        )
        RadioButtonsForImportance(selectedImportance, setSelectedImportance)
    }
}

@Composable
private fun RadioButtonsForImportance(currentSelection: Importance, updateSelection: (Importance) -> Unit) {
    MaterialTheme {
        Column {
            Importance.values().forEach { value ->
                Row {
                    RadioButton(
                        selected = value == currentSelection,
                        onClick = { updateSelection(value) }
                    )
                    Text(text = value.name)
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewEditTaskScreen() {
    EditTaskScreen(todoItem = TodoItem(text="biba", deadline = "19.11.2025"))
}