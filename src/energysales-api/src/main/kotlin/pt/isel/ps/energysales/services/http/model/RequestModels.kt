import kotlinx.serialization.Serializable
import pt.isel.ps.energysales.services.http.model.PriceJSON

@Serializable
data class CreateServiceRequest(
    val name: String,
    val description: String,
    val cycleName: String,
    val cycleType: String,
    val periodName: String,
    val periodNumPeriods: Int,
    val price: PriceJSON,
)

@Serializable
data class PatchServiceRequest(
    val name: String? = null,
    val description: String? = null,
    val cycleName: String? = null,
    val cycleType: String? = null,
    val periodName: String? = null,
    val periodNumPeriods: Int? = null,
    val price: PriceJSON? = null,
)
