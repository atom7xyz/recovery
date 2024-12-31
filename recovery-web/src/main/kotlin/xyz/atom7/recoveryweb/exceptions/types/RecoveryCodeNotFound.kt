package xyz.atom7.recoveryweb.exceptions.types

class RecoveryCodeNotFound(username: String, code: String)
    : Exception("RecoveryCode with `username=$username` and `code=$code` not found")