package com.dha.examtest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dha.examtest.ui.theme.ExamTestTheme

class MainActivity : ComponentActivity() {
    val viewModel = QuizViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExamTestTheme {
                val scrollState = rememberScrollState()
                ExamManager.setViewModel(viewModel)
                val mcQuiz = remember { mutableStateOf("0") }
                val tfQuiz = remember { mutableStateOf("0") }
                val saQuiz = remember { mutableStateOf("0") }
                val mcScore = remember { mutableStateOf("0.25") }
                val tfScore1 = remember { mutableStateOf("0.1") }
                val tfScore2 = remember { mutableStateOf("0.25") }
                val tfScore3 = remember { mutableStateOf("0.5") }
                val tfScore4 = remember { mutableStateOf("1") }
                val saScore = remember { mutableStateOf("0.25") }
                val examTemplateList: List<ExamTemplateItem> = listOf(
                    ExamTemplateItem(title="Toán (12-4-6)", form = ExamForm(12,4,6,0.25F, listOf(0.1F,0.25F,0.5F,1F), 0.5F)),
                    ExamTemplateItem(title="Lý, Hóa (18-4-6)", form = ExamForm(18,4,6,0.25F, listOf(0.1F,0.25F,0.5F,1F), 0.25F)),
                    ExamTemplateItem(title="Tiếng Anh (40-0-0)", form = ExamForm(40,0,0,0.25F, listOf(0.1F,0.25F,0.5F,1F), 0.25F)),
                )
                Surface (modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()){
                    Column (Modifier.scrollable(scrollState, Orientation.Vertical)){
                        examTemplateList.forEach { examTemplate ->
                            OutlinedCard(modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    mcQuiz.value = examTemplate.form.mcQuiz.toString()
                                    tfQuiz.value = examTemplate.form.tfQuiz.toString()
                                    saQuiz.value = examTemplate.form.saQuiz.toString()
                                    mcScore.value = examTemplate.form.mcScore.toString()
                                    tfScore1.value = examTemplate.form.tfScore[0].toString()
                                    tfScore2.value = examTemplate.form.tfScore[1].toString()
                                    tfScore3.value = examTemplate.form.tfScore[2].toString()
                                    tfScore4.value = examTemplate.form.tfScore[3].toString()
                                    saScore.value = examTemplate.form.saScore.toString()
                                })
                                .padding(horizontal = 20.dp, vertical = 5.dp)){
                                Text(modifier = Modifier.padding(20.dp),
                                    text = examTemplate.title)
                            }
                        }
                        HorizontalDivider(modifier = Modifier.height(2.dp).padding(vertical = 10.dp),
                            color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(20.dp))
                        NumberTextField(
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 5.dp),
                            value = mcQuiz.value,
                            onValueChange = { mcQuiz.value = it },
                            label = { Text(text = "Nhập số câu trắc nghiệm") }
                        )
                        NumberTextField(
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 5.dp),
                            value = tfQuiz.value,
                            onValueChange = { tfQuiz.value = it },
                            label = { Text(text = "Nhập số câu đúng/sai") }
                        )
                        NumberTextField(
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 5.dp),
                            value = saQuiz.value,
                            onValueChange = { saQuiz.value = it },
                            label = { Text(text = "Nhập số câu trả lời ngắn") }
                        )
                        NumberTextField(
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 5.dp),
                            value = mcScore.value,
                            onValueChange = { mcScore.value = it },
                            label = { Text(text = "Nhập số điểm mỗi câu trắc nghiệm") }
                        )
                        Text(modifier = Modifier.padding(start = 5.dp, top = 5.dp),
                            text = "Số điểm đúng câu đúng sai:",
                            style = MaterialTheme.typography.bodyLarge)
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            NumberTextField(
                                modifier = Modifier.weight(1f).padding(horizontal = 5.dp),
                                value = tfScore1.value,
                                onValueChange = { tfScore1.value = it },
                                label = { Text(text = "1 câu") }
                            )
                            NumberTextField(
                                modifier = Modifier.weight(1f).padding(horizontal = 5.dp),
                                value = tfScore2.value,
                                onValueChange = { tfScore2.value = it },
                                label = { Text(text = "2 câu") }
                            )
                            NumberTextField(
                                modifier = Modifier.weight(1f).padding(horizontal = 5.dp),
                                value = tfScore3.value,
                                onValueChange = { tfScore3.value = it },
                                label = { Text(text = "3 câu") }
                            )
                            NumberTextField(
                                modifier = Modifier.weight(1f).padding(horizontal = 5.dp),
                                value = tfScore4.value,
                                onValueChange = { tfScore4.value = it },
                                label = { Text(text = "4 câu") }
                            )
                        }

                        NumberTextField(
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 5.dp),
                            value = saScore.value,
                            onValueChange = { saScore.value = it },
                            label = { Text(text = "Nhập số điểm mỗi câu trả lời ngắn") }
                        )

                        Button(modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                            onClick = {
                                if (mcQuiz.value.toIntOrNull() != null && tfQuiz.value.toIntOrNull() != null && saQuiz.value.toIntOrNull() != null) {
                                    val intent = Intent(this@MainActivity, ExamActivity::class.java)
                                    viewModel.mcQuiz.intValue = mcQuiz.value.toInt()
                                    viewModel.tfQuiz.intValue = tfQuiz.value.toInt()
                                    viewModel.saQuiz.intValue = saQuiz.value.toInt()
                                    viewModel.phoDiem = Triple(mcScore.value.toFloat(),
                                        listOf(tfScore1.value.toFloat(), tfScore2.value.toFloat(), tfScore3.value.toFloat(), tfScore4.value.toFloat()),
                                        saScore.value.toFloat())
                                    startActivity(intent)
                                }
                            }) {
                            Text(text = if (viewModel.isNotEmpty()) "Tiếp tục" else "Tạo đề thi")
                        }
                    }
                }
            }
        }
    }
}

class ExamTemplateItem (var title: String, var form: ExamForm)
