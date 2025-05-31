package com.dha.examtest

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MCQuestionItem(
    question: MultipleChoiceQuestion,
    viewModel: QuizViewModel
) {
    val (practiceAnswer, checkAnswer, isCheckMode) = viewModel.getMcState(question.id)

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = question.title, style = MaterialTheme.typography.bodyLarge)

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            question.options.forEachIndexed { index, option ->
                val isSelected = if (isCheckMode) checkAnswer == index else practiceAnswer == index
                val isCorrect = checkAnswer == index
                val isPractice = practiceAnswer == index

                CustomRadioButton(
                    label = option,
                    isSelected = isSelected,
                    isCorrectAnswer = isCorrect,
                    isPracticeAnswer = isPractice,
                    isCheckMode = isCheckMode,
                    onSelect = { viewModel.setMcAnswer(question.id, index) }
                )
            }
        }
    }
}

@Composable
fun TFQuestionItem(
    question: TrueFalseQuestion,
    viewModel: QuizViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Câu ${question.id + 1}", style = MaterialTheme.typography.bodyLarge)

        question.subQuestions.forEach { subQuestion ->
            val (currentState, checkState) = viewModel.getTfAnswer(subQuestion.id)
            val isCheckMode = viewModel.isCheckMode

            val currentAnswer = currentState ?: AnswerState.UNANSWERED
            val checkAnswer = checkState ?: AnswerState.UNANSWERED

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(getTfBackgroundColor(isCheckMode, currentAnswer, checkAnswer))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = subQuestion.title)

                    // Triple State Selector
                    Row(
                        modifier = Modifier
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    ) {
                        AnswerOption(
                            text = "Chưa làm",
                            isSelected = currentAnswer == AnswerState.UNANSWERED,
                            color = Color.Gray,
                            onClick = {
                                viewModel.setTfAnswer(
                                    subQuestion.id,
                                    AnswerState.UNANSWERED
                                )
                            }
                        )

                        AnswerOption(
                            text = "Đúng",
                            isSelected = currentAnswer == AnswerState.TRUE,
                            color = Color.Green,
                            onClick = { viewModel.setTfAnswer(subQuestion.id, AnswerState.TRUE) }
                        )

                        AnswerOption(
                            text = "Sai",
                            isSelected = currentAnswer == AnswerState.FALSE,
                            color = Color.Red,
                            onClick = { viewModel.setTfAnswer(subQuestion.id, AnswerState.FALSE) }
                        )
                    }
                }

                // Hiển thị đáp án đúng khi ở chế độ kiểm tra
                if (isCheckMode) {
                    Text(
                        text = "Đáp án đã chọn: ${if (checkAnswer == AnswerState.TRUE) "Đúng" else if (checkAnswer == AnswerState.FALSE) "Sai" else "Chưa làm"}",
                        color = Color.Blue,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SAQuestionItem(
    question: ShortAnswerQuestion,
    viewModel: QuizViewModel
) {
    val (currentAnswer, compareAnswer) = viewModel.getSaAnswer(question.id)
    val isCheckMode = viewModel.isCheckMode

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(getBackgroundColor(isCheckMode, currentAnswer, compareAnswer))
            .padding(16.dp)
    ) {
        Text(text = question.title)
        NumberTextField(
            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
            value = currentAnswer,
            onValueChange = { viewModel.setSaAnswer(question.id, it) },
            label = { "" }
        )

        // Hiển thị đáp án đúng khi ở chế độ kiểm tra
        if (isCheckMode) {
            Text(
                text = "Đáp án đã chọn: $compareAnswer",
                color = Color.Blue,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        HorizontalDivider(modifier = Modifier.height(2.dp),
            color = MaterialTheme.colorScheme.primary,
            thickness = 2.dp)
    }
}

@Composable
fun AnswerOption(
    text: String,
    isSelected: Boolean,
    color: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) color.copy(alpha = 0.2f) else Color.Transparent
    val textColor = if (isSelected) color else MaterialTheme.colorScheme.onBackground

    Box(
        modifier = Modifier
            .clickable(enabled = enabled, onClick = onClick)
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (enabled) textColor else textColor.copy(alpha = 0.5f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

private fun getTfBackgroundColor(
    isCheckMode: Boolean,
    current: AnswerState,
    check: AnswerState
): Color {
    return if (isCheckMode) {
        when {
            current == check -> Color.Green.copy(alpha = 0.1f)
            check == AnswerState.UNANSWERED -> Color.Yellow.copy(alpha = 0.1f)
            else -> Color.Red.copy(alpha = 0.1f)
        }
    } else Color.Transparent
}

@Composable
private fun CustomRadioButton(
    label: String,
    isSelected: Boolean,
    isCorrectAnswer: Boolean,
    isPracticeAnswer: Boolean,
    isCheckMode: Boolean,
    onSelect: () -> Unit
) {
    val (backgroundColor, borderColor, textColor) = when {
        isCheckMode && isCorrectAnswer -> Triple(
            Color.Green.copy(alpha = 0.3f),
            Color.Green,
            MaterialTheme.colorScheme.onBackground
        )

        isCheckMode && isPracticeAnswer -> Triple(
            Color.Red.copy(alpha = 0.3f),
            Color.Red,
            MaterialTheme.colorScheme.onBackground
        )

        isSelected -> Triple(
            Color.Blue.copy(alpha = 0.3f),
            Color.Blue,
            MaterialTheme.colorScheme.onBackground
        )

        else -> Triple(
            Color.Transparent,
            Color.Gray,
            MaterialTheme.colorScheme.onBackground
        )
    }

    Box(
        modifier = Modifier
            .size(50.dp)
            .clickable(onClick = onSelect)
            .border(2.dp, borderColor, CircleShape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor, CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}


private fun <T> getBackgroundColor(
    isCheckMode: Boolean,
    current: T?,
    compare: T?
): Color {
    return if (isCheckMode) {
        if (current == compare) Color.Green.copy(alpha = 0.2f)
        else Color.Red.copy(alpha = 0.2f)
    } else Color.Transparent
}