package pt.isel.ps.energysales.services.application.dto

data class CreateServiceInput(
    val name: String,
    val description: String,
    val cycleName: String,
    val cycleType: String,
    val periodName: String,
    val periodNumPeriods: Int,
)

data class UpdateServiceInput(
    val id: Int,
    val name: String?,
    val description: String?,
    val cycleName: String?,
    val cycleType: String?,
    val periodName: String?,
    val periodNumPeriods: Int?,
)
