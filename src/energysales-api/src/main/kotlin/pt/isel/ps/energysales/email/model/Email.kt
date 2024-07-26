package pt.isel.ps.energysales.email.model

data class EmailData(
    val username: String,
    val fromEmail: String,
    val toEmail: String,
    val subject: String,
    val message: String,
)

data class MailConfig(
    val fromUsername: String,
    val fromEmail: String,
    val resetLinkBaseUrl: String,
)
