package com.passion.chatapp.Utils

class ErrorMessage {
    companion object {
        var errorMessage: String? = "무엇인가 잘못되었습니다."
    }
}

enum class LoadState {
    SUCCESS, FAILURE, LOADING
}
