package com.dha.examtest

object ExamManager {
    private var mainViewModel: QuizViewModel? = null

    fun setViewModel(viewModel: QuizViewModel) {
        mainViewModel = viewModel
    }

    fun getViewModel(): QuizViewModel? {
        return mainViewModel
    }

    fun resetViewModel() {
        mainViewModel?.reset()
    }
}