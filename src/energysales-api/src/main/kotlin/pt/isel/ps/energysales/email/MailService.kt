package pt.isel.ps.energysales.email

import arrow.core.Either
import pt.isel.ps.energysales.email.model.EmailData

interface MailService {
    suspend fun sendEmail(data: EmailData): SendEmailResult

    suspend fun sendResetPasswordEmail(
        toEmail: String,
        username: String,
    ): SendResetPasswordResult

    suspend fun sendOfferEmail(
        toEmail: String,
        clientName: String,
        offerURL: String,
    ): SendOfferLinkResult
}

typealias SendEmailResult = Either<SendEmailError, Unit>
typealias SendResetPasswordResult = Either<SendResetPasswordError, Unit>
typealias SendOfferLinkResult = Either<SendOfferLinkError, Unit>

sealed interface SendEmailError {
    data object EmailNotSent : SendEmailError

    data object EmailNotValid : SendEmailError
}

sealed interface SendResetPasswordError {
    data object EmailNotSent : SendResetPasswordError

    data object EmailNotValid : SendResetPasswordError
}

sealed interface SendOfferLinkError {
    data object EmailNotSent : SendOfferLinkError

    data object EmailNotValid : SendOfferLinkError
}
