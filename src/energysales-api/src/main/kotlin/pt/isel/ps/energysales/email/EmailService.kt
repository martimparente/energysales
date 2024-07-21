package pt.isel.ps.energysales.email

import arrow.core.Either
import pt.isel.ps.energysales.email.model.EmailData

interface EmailService {
    suspend fun sendEmail(data: EmailData): SendEmailResult

    suspend fun sendResetPasswordEmail(toEmail: String): SendResetPasswordResult
}

typealias SendEmailResult = Either<SendEmailError, Unit>
typealias SendResetPasswordResult = Either<SendResetPasswordError, Unit>

sealed interface SendEmailError {
    data object EmailNotSent : SendEmailError
}

sealed interface SendResetPasswordError {
    data object EmailNotSent : SendResetPasswordError
}
