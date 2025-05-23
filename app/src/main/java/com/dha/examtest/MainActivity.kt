package com.dha.examtest

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dha.examtest.ui.theme.ExamTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExamTestTheme {
                var selectedIndex by remember { mutableIntStateOf(0) }

                val items = listOf(
                    NavItem("Trang chủ", Icons.Filled.Home),
                    NavItem("Lịch sử", Icons.Filled.DateRange)
                )

                Scaffold(
                    bottomBar = {
                        NavigationBar() {
                            items.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    icon = { Icon(item.icon, contentDescription = item.title) },
                                    label = { Text(item.title) },
                                    selected = selectedIndex == index,
                                    onClick = { selectedIndex = index }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    // Nội dung màn hình theo tab chọn
                    Column(modifier = Modifier.padding(innerPadding)) {
                        when (selectedIndex) {
                            0 -> CreateExamScreen()
                            1 -> HistoryExamScreen()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryExamScreen() {
    val fileNameRemove = remember { mutableStateOf("") }
    val context = LocalContext.current
    val exams = remember {
        mutableStateOf(context.filesDir.listFiles()?.filter { it.name.endsWith(".json") }
            ?.reversed())
    }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            exams.value =
                context.filesDir.listFiles()?.filter { it.name.endsWith(".json") }?.reversed()
        }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            if (!exams.value.isNullOrEmpty()) {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        text = "Lịch sử đề thi:",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.SemiBold
                    )
                    HorizontalDivider(
                        modifier = Modifier.height(2.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                items(exams.value!!.size) { ind ->
                    val exam = exams.value!![ind]
                    Text(
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = {
                                    val intent = Intent(context, ExamActivity::class.java)
                                    intent.putExtra("exam", exam.name)
                                    launcher.launch(intent)
                                },
                                onLongClick = {
                                    fileNameRemove.value = exam.name
                                }
                            )
                            .padding(horizontal = 20.dp, vertical = 5.dp),
                        text = exam.nameWithoutExtension)
                    HorizontalDivider(
                        modifier = Modifier.height(2.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                item {
                    Text(
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 5.dp),
                        text = "Không có đề thi nào"
                    )
                }
            }
        }
        if (fileNameRemove.value.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
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
                        Text("Bạn có muốn xóa đề thi ${fileNameRemove.value}?")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 5.dp),
                                onClick = { fileNameRemove.value = "" }) {
                                Text("Hủy")
                            }
                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 5.dp),
                                onClick = {
                                    context.deleteFile(fileNameRemove.value)
                                    fileNameRemove.value = ""
                                    exams.value = context.filesDir.listFiles()
                                        ?.filter { it.name.endsWith(".json") }?.reversed()
                                }) {
                                Text("Xóa")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateExamScreen() {
    val examTitle = remember { mutableStateOf("") }
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
        ExamTemplateItem(
            title = "Toán (12-4-6)",
            form = ExamForm(12, 4, 6, 0.25F, listOf(0.1F, 0.25F, 0.5F, 1F), 0.5F)
        ),
        ExamTemplateItem(
            title = "Lý, Hóa (18-4-6)",
            form = ExamForm(18, 4, 6, 0.25F, listOf(0.1F, 0.25F, 0.5F, 1F), 0.25F)
        ),
        ExamTemplateItem(
            title = "Tiếng Anh (40-0-0)",
            form = ExamForm(40, 0, 0, 0.25F, listOf(0.1F, 0.25F, 0.5F, 1F), 0.25F)
        ),
    )
    val context = LocalContext.current
    val textFieldModifier = Modifier
        .fillMaxWidth()
        .height(70.dp)
        .padding(horizontal = 20.dp, vertical = 5.dp)
    Column(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            examTemplateList.forEach { examTemplate ->
                OutlinedCard(
                    modifier = Modifier
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
                            examTitle.value = examTemplate.title.substringBefore('(')
                        })
                        .padding(horizontal = 20.dp, vertical = 5.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(20.dp),
                        text = examTemplate.title
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.height(2.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                modifier = textFieldModifier,
                value = examTitle.value,
                onValueChange = { examTitle.value = it },
                label = { Text(text = "Nhập tiêu đề đề thi") })
            NumberTextField(
                modifier = textFieldModifier,
                value = mcQuiz.value,
                onValueChange = { mcQuiz.value = it },
                label = { Text(text = "Nhập số câu trắc nghiệm") }
            )
            NumberTextField(
                modifier = textFieldModifier,
                value = tfQuiz.value,
                onValueChange = { tfQuiz.value = it },
                label = { Text(text = "Nhập số câu đúng/sai") }
            )
            NumberTextField(
                modifier = textFieldModifier,
                value = saQuiz.value,
                onValueChange = { saQuiz.value = it },
                label = { Text(text = "Nhập số câu trả lời ngắn") }
            )
            NumberTextField(
                modifier = textFieldModifier,
                value = mcScore.value,
                onValueChange = { mcScore.value = it },
                label = { Text(text = "Nhập số điểm mỗi câu trắc nghiệm") }
            )
            Text(
                modifier = Modifier.padding(start = 5.dp, top = 5.dp),
                text = "Số điểm đúng câu đúng sai:",
                style = MaterialTheme.typography.bodyLarge
            )
            Row(
                modifier = textFieldModifier,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NumberTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 5.dp),
                    value = tfScore1.value,
                    onValueChange = { tfScore1.value = it },
                    label = { Text(text = "1 câu") }
                )
                NumberTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 5.dp),
                    value = tfScore2.value,
                    onValueChange = { tfScore2.value = it },
                    label = { Text(text = "2 câu") }
                )
                NumberTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 5.dp),
                    value = tfScore3.value,
                    onValueChange = { tfScore3.value = it },
                    label = { Text(text = "3 câu") }
                )
                NumberTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 5.dp),
                    value = tfScore4.value,
                    onValueChange = { tfScore4.value = it },
                    label = { Text(text = "4 câu") }
                )
            }

            NumberTextField(
                modifier = textFieldModifier,
                value = saScore.value,
                onValueChange = { saScore.value = it },
                label = { Text(text = "Nhập số điểm mỗi câu trả lời ngắn") }
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            onClick = {
                if (mcQuiz.value.toIntOrNull() == null || tfQuiz.value.toIntOrNull() == null
                    || saQuiz.value.toIntOrNull() == null || examTitle.value.isEmpty()
                ) {
                    Toast.makeText(context, "Vui lòng nhập chính xác thông tin", Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                }
                val intent = Intent(context, ExamActivity::class.java)
                val viewModel = QuizViewModel()
                viewModel.setTitle(examTitle.value)
                viewModel.mcQuiz.intValue = mcQuiz.value.toInt()
                viewModel.tfQuiz.intValue = tfQuiz.value.toInt()
                viewModel.saQuiz.intValue = saQuiz.value.toInt()
                viewModel.phoDiem = Triple(
                    mcScore.value.toFloat(),
                    listOf(
                        tfScore1.value.toFloat(),
                        tfScore2.value.toFloat(),
                        tfScore3.value.toFloat(),
                        tfScore4.value.toFloat()
                    ),
                    saScore.value.toFloat()
                )
                viewModel.parent = context.filesDir
                viewModel.saveQuiz()
                intent.putExtra("exam", viewModel.fileName)
                context.startActivity(intent)
            }) {
            Text(text = "Tạo đề thi")
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

data class NavItem(val title: String, val icon: ImageVector)

class ExamTemplateItem(var title: String, var form: ExamForm)
