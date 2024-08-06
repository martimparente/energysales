package pt.isel.ps.energysales.partners.application.dto

import pt.isel.ps.energysales.partners.domain.Location

data class CreatePartnerInput(
    val name: String,
    val location: Location,
    val managerId: String?,
)

data class UpdatePartnerInput(
    val id: String,
    val name: String?,
    val location: Location?,
    val managerId: String?,
)
