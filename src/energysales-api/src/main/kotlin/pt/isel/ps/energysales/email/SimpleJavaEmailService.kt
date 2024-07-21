package pt.isel.ps.energysales.email

import arrow.core.raise.either
import kotlinx.coroutines.future.await
import org.simplejavamail.api.mailer.Mailer
import org.simplejavamail.email.EmailBuilder
import pt.isel.ps.energysales.email.model.EmailConfig
import pt.isel.ps.energysales.email.model.EmailData

class SimpleJavaEmailService(
    private val mailer: Mailer,
    private val config: EmailConfig,
) : EmailService {
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
                mailer.sendMail(email).await()
                println("Email sent successfully")
            } catch (ex: Exception) {
                println("Failed to send email: ${ex.message}")
                raise(SendEmailError.EmailNotSent)
            }
        }

    override suspend fun sendResetPasswordEmail(toEmail: String): SendResetPasswordResult =
        either {
            val subject = "Your Password Reset Link - EnergySales"

            val token =
                java
                    .util
                    .UUID
                    .randomUUID()
                    .toString()
            val resetLink = "${config.resetLinkBaseUrl}?token=$token"
            val message = "Click the following link to reset your password: $resetLink"

            println("Sending password reset email to $toEmail with link $resetLink")

            val emailData =
                EmailData(
                    fromEmail = config.fromEmail,
                    toEmail = toEmail,
                    username = config.fromUsername,
                    subject = subject,
                    message = message,
                )
            sendEmail(emailData)
        }
}
