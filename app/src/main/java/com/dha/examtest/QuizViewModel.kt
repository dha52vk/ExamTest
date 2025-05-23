package com.dha.examtest

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuizViewModel : ViewModel() {
    // Mode state
    var isCheckMode by mutableStateOf(false)
        private set

    var parent: File? = null
    var fileName = ""
    private val examTitle = mutableStateOf("")
    var phoDiem: Triple<Float, List<Float>, Float> = Triple(0.25F, listOf(0.1F,0.25F,0.5F,1F), 0.25F)
    val score = mutableStateOf("0")
    val time = mutableLongStateOf(0L)
    val mcQuiz = mutableIntStateOf(0)
    val tfQuiz = mutableIntStateOf(0)
    val saQuiz = mutableIntStateOf(0)
    // Answers storage
    private val _mcPracticeAnswers = mutableStateMapOf<Int, Int>()
    private val _mcCheckAnswers = mutableStateMapOf<Int, Int>()
    private val _tfPracticeAnswers = mutableStateMapOf<String, AnswerState>()
    private val _tfCheckAnswers = mutableStateMapOf<String, AnswerState>()
    private val _saPracticeAnswer = mutableStateMapOf<Int,String>()
    private val _saCheckAnswer = mutableStateMapOf<Int,String>()

    fun toggleMode() = viewModelScope.launch { isCheckMode = !isCheckMode }

    fun reset(){
        _mcPracticeAnswers.clear()
        _mcCheckAnswers.clear()
        _tfPracticeAnswers.clear()
        _tfCheckAnswers.clear()
        _saPracticeAnswer.clear()
        _saCheckAnswer.clear()
        getScore()
    }

    // Multiple Choice
    fun getMcState(questionId: Int) = Triple(
        _mcPracticeAnswers[questionId],
        _mcCheckAnswers[questionId],
        isCheckMode
    )

    fun getMcAnswer(questionId: Int) = if (isCheckMode)
        _mcCheckAnswers[questionId] to _mcPracticeAnswers[questionId]
    else
        _mcPracticeAnswers[questionId] to null

    fun setMcAnswer(questionId: Int, answer: Int) {
        if (isCheckMode) _mcCheckAnswers[questionId] = answer
        else _mcPracticeAnswers[questionId] = answer
        getScore()
        saveQuiz()
    }

    // True/False
    fun getTfAnswer(subQuestionId: String) = if (isCheckMode)
        _tfCheckAnswers[subQuestionId] to _tfPracticeAnswers[subQuestionId]
    else
        _tfPracticeAnswers[subQuestionId] to null

    fun setTfAnswer(subQuestionId: String, answer: AnswerState) {
        if (isCheckMode) _tfCheckAnswers[subQuestionId] = answer
        else _tfPracticeAnswers[subQuestionId] = answer
        getScore()
        saveQuiz()
    }

    // Short Answer
    fun getSaAnswer(questionId: Int) : Pair<String, String> = if (isCheckMode)
        _saCheckAnswer.getOrDefault(questionId, "") to _saPracticeAnswer.getOrDefault(questionId, "")
    else
        _saPracticeAnswer.getOrDefault(questionId, "") to ""

    fun setSaAnswer(questionId: Int, answer: String) {
        if (isCheckMode) _saCheckAnswer[questionId] = answer
        else _saPracticeAnswer[questionId] = answer
        getScore()
        saveQuiz()
    }

    fun setTitle(title: String){
        examTitle.value = title.trim() // Xoá khoảng trắng đầu/cuối
            .replace(Regex("[\\\\/:*?\"<>|]"), "") // Xoá ký tự không hợp lệ cho tên file
            .replace(Regex("-+"), "-") // Gộp nhiều dấu gạch ngang liên tiếp
            .replace('_', '-') // Thay khoảng trắng và _ thành dấu gạch ngang
    }

    fun getScore(): Float {
        val mcScore = phoDiem.first
        val tfScore = phoDiem.second
        val saScore = phoDiem.third
        // Tính điểm trắc nghiệm
        val mcTotal = _mcPracticeAnswers
            .filter { (key, value) ->
                value == _mcCheckAnswers[key]
            }.count() * mcScore

        // Tính điểm đúng/sai
        val tfGroups = _tfCheckAnswers.keys
            .groupBy { it.split("-")[0] } // Nhóm theo phần trước dấu -
            .toSortedMap(compareBy { it.toInt() })

        val tfTotal = tfGroups.entries
            .sumOf { (_id, subIds) ->
                val correct = subIds.count() { subId ->
                    _tfPracticeAnswers[subId] == _tfCheckAnswers[subId]
                }
                if (correct == 0) 0.0 else tfScore[correct-1].toDouble()
            }.toFloat()

        // Tính điểm trả lời ngắn
        val saTotal = _saPracticeAnswer
            .filter { (key, value) ->
                value == _saCheckAnswer[key]
            }.count() * saScore
        score.value = (mcTotal + tfTotal + saTotal).toString()
        return mcTotal + tfTotal + saTotal
    }

    fun saveQuiz(){
        val quiz = QuizData(
            title = examTitle.value,
            phoDiem = phoDiem,
            time = time.longValue,
            mcQuiz = mcQuiz.intValue,
            tfQuiz = tfQuiz.intValue,
            saQuiz = saQuiz.intValue,
            mcPracticeAnswers = _mcPracticeAnswers,
            mcCheckAnswers = _mcCheckAnswers,
            tfPracticeAnswers = _tfPracticeAnswers,
            tfCheckAnswers = _tfCheckAnswers,
            saPracticeAnswer = _saPracticeAnswer,
            saCheckAnswer = _saCheckAnswer
        )
        val json = Gson().toJson(quiz)

        var file = File(parent, fileName)
        if (!fileName.isEmpty() && file.exists()){
            file.delete()
        }
        fileName = "${examTitle.value}(${score.value}đ)_${SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())}.json"
        file = File(parent, fileName)
        file.writeText(json)
    }

    fun loadQuiz(fileName: String){
        this.fileName = fileName
        val file = File(parent, fileName)
        if (!file.exists()) return
        val json = file.readText()
        val quizData = Gson().fromJson(json, QuizData::class.java)
        examTitle.value = quizData.title
        phoDiem = quizData.phoDiem
        time.longValue = quizData.time
        mcQuiz.intValue = quizData.mcQuiz
        tfQuiz.intValue = quizData.tfQuiz
        saQuiz.intValue = quizData.saQuiz
        _mcPracticeAnswers.clear()
        _mcPracticeAnswers.putAll(quizData.mcPracticeAnswers)
        _mcCheckAnswers.clear()
        _mcCheckAnswers.putAll(quizData.mcCheckAnswers)
        _tfPracticeAnswers.clear()
        _tfPracticeAnswers.putAll(quizData.tfPracticeAnswers)
        _tfCheckAnswers.clear()
        _tfCheckAnswers.putAll(quizData.tfCheckAnswers)
        _saPracticeAnswer.clear()
        _saPracticeAnswer.putAll(quizData.saPracticeAnswer)
        _saCheckAnswer.clear()
        _saCheckAnswer.putAll(quizData.saCheckAnswer)
    }
}

enum class AnswerState {
    UNANSWERED, // Chưa làm
    TRUE,       // Đúng
    FALSE       // Sai
}

data class QuizData(
    val title: String,
    val phoDiem: Triple<Float, List<Float>, Float>,
    val time: Long,
    val mcQuiz: Int,
    val tfQuiz: Int,
    val saQuiz: Int,
    val mcPracticeAnswers: Map<Int, Int>,
    val mcCheckAnswers: Map<Int, Int>,
    val tfPracticeAnswers: Map<String, AnswerState>,
    val tfCheckAnswers: Map<String, AnswerState>,
    val saPracticeAnswer: Map<Int,String>,
    val saCheckAnswer: Map<Int,String>
)