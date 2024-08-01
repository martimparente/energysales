package pt.isel.ps.energysales.services.application.dto

import pt.isel.ps.energysales.services.http.model.PriceJSON

data class CreateServiceInput(
    val name: String,
    val description: String,
    val cycleName: String,
    val cycleType: String,
    val periodName: String,
    val periodNumPeriods: Int,
    val price: PriceJSON,
)

data class UpdateServiceInput(
    val id: String,
    val name: String?,
    val description: String?,
    val cycleName: String?,
    val cycleType: String?,
    val periodName: String?,
    val periodNumPeriods: Int?,
    val price: PriceJSON?,
)
