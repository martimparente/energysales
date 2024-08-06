package pt.isel.ps.energysales.partners.domain

import pt.isel.ps.energysales.sellers.domain.Seller
import pt.isel.ps.energysales.services.domain.Service

data class Partner(
    val id: String? = null,
    val name: String,
    val location: Location,
    val managerId: String?,
    val avatarPath: String? = null,
)

data class PartnerDetails(
    val partner: Partner,
    val members: List<Seller>,
    val services: List<Service>,
)
