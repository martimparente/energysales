package pt.isel.ps.energysales.email

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import kotlinx.coroutines.future.await
import org.simplejavamail.api.mailer.Mailer
import org.simplejavamail.email.EmailBuilder
import pt.isel.ps.energysales.email.model.EmailData
import pt.isel.ps.energysales.email.model.MailConfig

class SimpleJavaMailService(
    private val mailer: Mailer,
    private val config: MailConfig,
) : MailService {
    override suspend fun sendEmail(data: EmailData): SendEmailResult =
        either {
            println("Sending email to ${data.toEmail} with subject ${data.subject} and message ${data.message}")

            val email =
                EmailBuilder
                    .startingBlank()
                    .from(config.fromUsername, data.fromEmail)
                    .to(data.username, data.toEmail)
                    .withSubject(data.subject)
                    .withPlainText(data.message)
                    .buildEmail()

            try {
                ensure(mailer.validate(email)) { SendEmailError.EmailNotValid }
                mailer.sendMail(email).await()
                println("Email sent successfully")
            } catch (ex: Exception) {
                println("Failed to send email: ${ex.message}")
                raise(SendEmailError.EmailNotSent)
            }
        }

    override suspend fun sendResetPasswordEmail(
        toEmail: String,
        username: String,
    ): SendResetPasswordResult =
        either {
            val subject = "Your Password Reset Link - EnergySales"

            val token =
                java
                    .util
                    .UUID
                    .randomUUID()
                    .toString()
            val resetLink = "${config.resetLinkBaseUrl}?token=$token"
            val message = "Hi $username Click the following link to reset your password: $resetLink"

            println("Sending password reset email to $toEmail with link $resetLink")

            val emailData =
                EmailData(
                    fromEmail = config.fromEmail,
                    toEmail = toEmail,
                    username = config.fromUsername,
                    subject = subject,
                    message = message,
                )
            val res = sendEmail(emailData)
            when (res) {
                is Either.Left -> raise(SendResetPasswordError.EmailNotSent)
                is Either.Right -> Unit
            }
        }

    override suspend fun sendOfferEmail(
        toEmail: String,
        clientName: String,
        offerURL: String,
    ): SendOfferLinkResult =
        either {
            val subject = "Your Energy Offer - EnergySales"

            val token =
                java
                    .util
                    .UUID
                    .randomUUID()
                    .toString()

            val message = "Hi $clientName Here's your Energy Offer $offerURL"

            println("Sending offer to $toEmail with link $offerURL")

            val emailData =
                EmailData(
                    fromEmail = config.fromEmail,
                    toEmail = toEmail,
                    username = config.fromUsername,
                    subject = subject,
                    message = message,
                )
            val res = sendEmail(emailData)
            when (res) {
                is Either.Left -> raise(SendOfferLinkError.EmailNotSent)
                is Either.Right -> Unit
            }
        }
}
