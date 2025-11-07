<<<<<<< HEAD
package com.example.myapplication

import com.example.myapplication.TodoItem
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

    var textState by rememberSaveable { mutableStateOf(todoItem?.text ?: "") }
    var deadlineState by rememberSaveable { mutableStateOf(todoItem?.deadline ?: "") }
    val initialColor = todoItem?.color ?: Color.WHITE
    val (selectedColor, setSelectedColor) = rememberSaveable { mutableStateOf(initialColor) }
    val (selectedImportance, setSelectedImportance) = rememberSaveable { mutableStateOf(todoItem?.importance ?: Importance.Обычная) }

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
            // Сохраняем изменения
            val updatedTodoItem = TodoItem(
                uid = (todoItem?.uid ?: UUID.randomUUID()).toString(), // Новый ID для нового элемента
                text = textState,
                deadline = deadlineState,
                color = selectedColor,
                importance = selectedImportance
            )

            saveChanges(updatedTodoItem)

            navController.popBackStack()
        }) {
            Text("Сохранить изменения")
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

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

        val colorValues = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)

        colorValues.forEach { color ->
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(ComposeColor(color))
                    .clickable {onColorSelected(color)

                    }
            )
        }

        Box(
            modifier = Modifier
                .size(30.dp)
                .background(ComposeColor(selectedColor))
        )
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
=======
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
>>>>>>> e641694f437f91ab5b478bc8f5f84eee7316c40b
            }
        }
    }
}
<<<<<<< HEAD



@Preview(showBackground = true)
@Composable
fun PreviewEditTaskScreen() {
    val context = LocalContext.current // Безопасно получаем контекст внутри @Composable
    val fakeNavController = remember { NavHostController(context) }
    val fakeSaveChanges: (TodoItem) -> Unit = {}

    EditTaskScreen(
        todoItem = TodoItem(text = "Preview", deadline = "19-05-2026"),
        navController = fakeNavController,
        saveChanges = fakeSaveChanges
    )
}


=======
@Preview(showBackground = true)
@Composable
fun PreviewEditTaskScreen() {
    EditTaskScreen(todoItem = TodoItem(text="biba", deadline = "19.11.2025"))
}
>>>>>>> e641694f437f91ab5b478bc8f5f84eee7316c40b
