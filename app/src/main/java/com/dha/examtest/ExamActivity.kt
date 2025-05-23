package com.dha.examtest

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dha.examtest.ui.theme.ExamTestTheme

class ExamActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExamTestTheme {
                Surface (modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                    val viewModel = QuizViewModel()
                    viewModel.parent = this.filesDir
                    viewModel.loadQuiz(intent.getStringExtra("exam") ?: "")
                    ExamScreen(viewModel)
                }
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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
        Text(text = if (viewModel.isCheckMode) "Chế độ kiểm tra" else "Chế độ làm bài")
        if (viewModel.isCheckMode) {
            Text(text = "Điểm: ${viewModel.score.value}")
        }
        Text(modifier = Modifier
            .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .combinedClickable(
            onLongClick = { viewModel.reset() },
            onClick = {  }
        ), text = "Reset",
            color = MaterialTheme.colorScheme.onPrimary)
        Switch(
            checked = viewModel.isCheckMode,
            onCheckedChange = { viewModel.toggleMode() }
        )
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