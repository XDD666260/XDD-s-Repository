package com.example.littlepainter.utils

sealed class LoginRegisterResult {
    class Success:LoginRegisterResult()
    class Failure(val type: WrongType):LoginRegisterResult()
    class None:LoginRegisterResult()
}