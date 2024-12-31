package xyz.atom7.recoveryServer.exceptions

import okhttp3.HttpUrl
import xyz.atom7.recoveryServer.serialization.SerialException

class RecoveryRequestException(httpUrl: HttpUrl, exception: SerialException)
    : Exception("RecoveryRequest failed for `$httpUrl` with exception `$exception`")