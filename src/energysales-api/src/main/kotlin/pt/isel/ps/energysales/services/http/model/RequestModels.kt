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
data class UpdateServiceRequest(
    val name: String,
    val description: String,
    val cycleName: String,
    val cycleType: String,
    val periodName: String,
    val periodNumPeriods: Int,
    val price: PriceJSON,
)
