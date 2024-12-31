package xyz.atom7.recoveryweb.exceptions.types

import xyz.atom7.recoveryweb.entities.RecoveryCode

class CodeExistsException(username: String, recoveryCode: RecoveryCode)
    : Exception("`username=$username` already has `recoveryCode=$recoveryCode`")