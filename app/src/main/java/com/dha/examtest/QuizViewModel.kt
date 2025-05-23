package com.dha.examtest

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class QuizViewModel : ViewModel() {
    // Mode state
    var isCheckMode by mutableStateOf(false)
        private set

    var phoDiem: Triple<Float, List<Float>, Float> = Triple(0.25F, listOf(0.1F,0.25F,0.5F,1F), 0.25F);
    val score = mutableStateOf("")
    val mcQuiz = mutableIntStateOf(0);
    val tfQuiz = mutableIntStateOf(0);
    val saQuiz = mutableIntStateOf(0);
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
    }

    fun isNotEmpty(): Boolean{
        return _mcPracticeAnswers.isNotEmpty() || _tfPracticeAnswers.isNotEmpty() || _saPracticeAnswer.isNotEmpty()
    }

    fun getScore(): Float {
        val mcScore = phoDiem.first;
        val tfScore = phoDiem.second;
        val saScore = phoDiem.third;
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
                Log.d("viewmodel", "$_id $correct")
                if (correct == 0) 0.0 else tfScore[correct-1].toDouble()
            }.toFloat()

        // Tính điểm trả lời ngắn
        val saTotal = _saPracticeAnswer
            .filter { (key, value) ->
                value == _saCheckAnswer[key]
            }.count() * saScore
        score.value = (mcTotal + tfTotal + saTotal).toString();
        Log.d("viewmodel", "$mcTotal $tfTotal $saTotal")
        return mcTotal + tfTotal + saTotal;
    }
}

enum class AnswerState {
    UNANSWERED, // Chưa làm
    TRUE,       // Đúng
    FALSE       // Sai

}