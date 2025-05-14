package com.dha.examtest

data class ExamForm(
    val mcQuiz: Int = 0,
    val tfQuiz: Int = 0,
    val saQuiz: Int = 0,
    val mcScore: Float = 0F,
    val tfScore: List<Float> = emptyList(),
    val saScore: Float = 0F
)

// Trắc nghiệm
data class MultipleChoiceQuestion(
    val id: Int,
    val title: String,
    val options: List<String>
)

// Đúng/Sai
data class TrueFalseQuestion(
    val id: Int,
    val subQuestions: List<SubQuestion>
) {
    data class SubQuestion(
        val id: String, // Format: "questionId-subId"
        val title: String
    )
}

// Trả lời ngắn
data class ShortAnswerQuestion(
    val id: Int,
    val title: String
)
