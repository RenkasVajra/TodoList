package com.example.myapplication

import android.app.DatePickerDialog
import android.graphics.Color
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.util.Calendar
import java.util.UUID

@Composable
fun EditTaskScreen(todoItem: TodoItem?, navController: NavHostController, saveChanges: (TodoItem) -> Unit) {

    var textState by rememberSaveable(key = todoItem?.uid) {
        mutableStateOf(todoItem?.text ?: "")
    }
    var deadlineState by rememberSaveable(key = todoItem?.uid) {
        mutableStateOf(todoItem?.deadline ?: "")
    }
    val (selectedColor, setSelectedColor) = rememberSaveable(key = todoItem?.uid) {
        mutableStateOf(todoItem?.color ?: Color.RED)
    }
    val (selectedImportance, setSelectedImportance) = rememberSaveable(key = todoItem?.uid) {
        mutableStateOf(todoItem?.importance ?: Importance.Обычная)
    }

    Column(modifier = Modifier.padding(16.dp)) {

        OutlinedTextField(
            value = textState,
            onValueChange = { textState = it },
            label = { Text("Описание") },
            modifier = Modifier.fillMaxWidth()
        )

        RadioButtonsForImportance(selectedImportance, setSelectedImportance)

        DeadlinePicker(deadlineState) { deadlineState = it }

        ColorPicker(selectedColor) { setSelectedColor(it) }

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            if (textState.isNotBlank() && deadlineState.isNotBlank()) {
                val updatedTodoItem = TodoItem(
                    uid = (todoItem?.uid ?: UUID.randomUUID()).toString(),
                    text = textState,
                    deadline = deadlineState,
                    color = selectedColor,
                    importance = selectedImportance
                )
                saveChanges(updatedTodoItem)
                navController.popBackStack()
            }
        }, enabled = textState.isNotBlank() && deadlineState.isNotBlank())
        {
            Text("Сохранить")
        }
    }
}

@Composable
fun DeadlinePicker(deadline: String, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                onDateSelected("$dayOfMonth-${month + 1}-$year")
            },
            year,
            month,
            day
        )
    }

    OutlinedTextField(
        value = deadline,
        onValueChange = {},
        readOnly = true,
        label = { Text("Дедлайн") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                datePickerDialog.show()
            }
    )
}

@Composable
fun ColorPicker(selectedColor: Int, onColorSelected: (Int) -> Unit) {
    val colors = remember {
        mapOf(
            "Красный" to Color.RED,
            "Зеленый" to Color.GREEN,
            "Синий" to Color.BLUE,
            "Желтый" to Color.YELLOW,
            "Бирюзовый" to Color.CYAN,
            "Серый" to Color.GRAY,
            "Пурпурный" to Color.MAGENTA
        )
    }

    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(containerColor = ComposeColor(selectedColor))
        ) {
            Text(colors.entries.firstOrNull { it.value == selectedColor }?.key ?: "Выберите цвет")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            colors.forEach { (name, color) ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(ComposeColor(color))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = name)
                        }
                    },
                    onClick = {
                        onColorSelected(color)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
private fun RadioButtonsForImportance(currentSelection: Importance, updateSelection: (Importance) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text("Важность", style = MaterialTheme.typography.bodyMedium)
        Importance.values().forEach { value ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = value == currentSelection,
                    onClick = { updateSelection(value) })
                Text(text = value.name)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditTaskScreen() {
    val context = LocalContext.current
    val fakeNavController = remember { NavHostController(context) }
    val fakeSaveChanges: (TodoItem) -> Unit = {}

    EditTaskScreen(
        todoItem = TodoItem(text = "Preview", deadline = "19-05-2026"),
        navController = fakeNavController,
        saveChanges = fakeSaveChanges
    )
}
