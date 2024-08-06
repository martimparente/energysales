package pt.isel.ps.energysales.sellers.http.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateSellerRequest(
    val name: String,
    val surname: String,
    val email: String,
    val partner: String? = null,
)

@Serializable
data class PatchSellerRequest(
    val partner: String? = null,
)
