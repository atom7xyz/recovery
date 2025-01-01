package xyz.atom7.recoveryVelocity.exceptions

import okhttp3.HttpUrl
import xyz.atom7.recoveryVelocity.serialization.SerialException

class PremiumRequestException(httpUrl: HttpUrl, exception: SerialException)
    : Exception("PremiumRequest failed for `$httpUrl` with exception `$exception`")