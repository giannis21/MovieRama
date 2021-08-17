package com.example.movierama.utils

sealed class ApiCallState{
    data class NoInternetErrorMessage(val data:String) : ApiCallState()
    data class Success(val data:String) : ApiCallState()
}