package pt.isel.ps.energysales.services.http.model

import kotlinx.serialization.Serializable
import pt.isel.ps.energysales.services.domain.Service

@Serializable
data class ServiceJSON(
    val id: Int,
    val name: String,
    val description: String,
    val cycleName: String,
    val cycleType: String,
    val periodName: String,
    val periodNumPeriods: Int,
) {
    companion object {
        fun fromService(service: Service) =
            ServiceJSON(
                service.id,
                service.name,
                service.description,
                service.cycleName,
                service.cycleType,
                service.periodName,
                service.periodNumPeriods,
            )
    }
}

@Serializable
data class CreateServiceRequest(
    val name: String,
    val description: String,
    val cycleName: String,
    val cycleType: String,
    val periodName: String,
    val periodNumPeriods: Int,
)

@Serializable
data class UpdateServiceRequest(
    val name: String,
    val description: String,
    val cycleName: String,
    val cycleType: String,
    val periodName: String,
    val periodNumPeriods: Int,
)
