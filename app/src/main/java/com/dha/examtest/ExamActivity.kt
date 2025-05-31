package com.dha.examtest

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dha.examtest.ui.theme.ExamTestTheme
import kotlinx.coroutines.delay
import java.io.FileNotFoundException
import java.util.Locale

class ExamActivity : ComponentActivity() {
    val viewModel = QuizViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExamTestTheme {
                Surface (modifier = Modifier
                    .padding(WindowInsets.ime.asPaddingValues())
                    .fillMaxSize()
                    .statusBarsPadding()) {
                    viewModel.parent = this.filesDir
                    try {
                        viewModel.loadQuiz(
                            intent.getStringExtra("exam") ?: savedInstanceState?.getString("exam")
                            ?: ""
                        )
                    }
                    catch (e: FileNotFoundException) {
                        viewModel.fileName = ""
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
                        ) {
                            Card(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text("Lỗi ${e.message}")
                                    Button(
                                        modifier = Modifier
                                            .padding(horizontal = 5.dp, vertical = 5.dp)
                                            .fillMaxWidth(),
                                        onClick = { finish() }) {
                                        Text("Đóng")
                                    }
                                }
                            }
                        }
                    }

                    LaunchedEffect(true) {
                        while (true) {
                            delay(1000)
                            if (!viewModel.isCheckMode) viewModel.time.longValue++
                        }
                    }
                    if (!viewModel.fileName.isEmpty())
                        ExamScreen(viewModel)
                }
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("exam", viewModel.fileName)
    }
}

@Composable
fun ExamScreen(viewModel: QuizViewModel = QuizViewModel()) {
    val mcQuestions = remember { generateMCQuestions(viewModel.mcQuiz.intValue) }
    val tfQuestions = remember { generateTFQuestions(viewModel.tfQuiz.intValue) }
    val saQuestions = remember { generateSAQuestion(viewModel.saQuiz.intValue) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                // Multiple Choice Section
                item { SectionTitle("Trắc nghiệm") }
                items(mcQuestions) { question ->
                    MCQuestionItem(question, viewModel)
                }

                // True/False Section
                item { SectionTitle("Đúng/Sai") }
                items(tfQuestions) { question ->
                    TFQuestionItem(question, viewModel)
                }

                // Short Answer Section
                item { SectionTitle("Trả lời ngắn") }
                items(saQuestions) { question ->
                    SAQuestionItem(question, viewModel)
                }
            }

            ModeSwitch(viewModel)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(16.dp))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ModeSwitch(viewModel: QuizViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            modifier = Modifier
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
        ) {
            Box(
                modifier = Modifier
                    .clickable(onClick = { viewModel.toggleMode(false) })
                    .background(if (!viewModel.isCheckMode) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Làm bài",
                    color = if (!viewModel.isCheckMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (!viewModel.isCheckMode) FontWeight.Bold else FontWeight.Normal
                )
            }
            Box(
                modifier = Modifier
                    .clickable(onClick = { viewModel.toggleMode(true) })
                    .background(if (viewModel.isCheckMode) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Kiểm tra",
                    color = if (viewModel.isCheckMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (viewModel.isCheckMode) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
//        Text(text = if (viewModel.isCheckMode) "Chế độ kiểm tra" else "Chế độ làm bài")
        if (viewModel.isCheckMode) {
            Text(text = "Điểm: ${viewModel.score.value}")
        }else {
            Text(
                text = "Thời gian: ${
                    String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        viewModel.time.longValue / 60,
                        viewModel.time.longValue % 60
                    )
                }"
            )
        }
//        Switch(
//            checked = viewModel.isCheckMode,
//            onCheckedChange = { viewModel.toggleMode() }
//        )
    }
}

private fun generateMCQuestions(size: Int = 12) = List(size) { id ->
    MultipleChoiceQuestion(
        id = id,
        title = "Câu hỏi trắc nghiệm ${id + 1}",
        options = listOf("A", "B", "C", "D")
    )
}

private fun generateTFQuestions(size: Int = 4) = List(size) { id ->
    TrueFalseQuestion(
        id = id,
        subQuestions = listOf("a", "b", "c", "d").map { subId ->
            TrueFalseQuestion.SubQuestion(
                id = "$id-$subId",
                title = "Câu ${id + 1}-${subId}"
            )
        }
    )
}

private fun generateSAQuestion(size: Int = 4) = List(size) { id ->
    ShortAnswerQuestion(
        id = id,
        title = "Câu hỏi trả lời ngắn ${id + 1}"
    )
}