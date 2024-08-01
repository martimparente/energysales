package pt.isel.ps.energysales.services.http.model

import kotlinx.serialization.Serializable
import pt.isel.ps.energysales.services.domain.Price
import pt.isel.ps.energysales.services.domain.Service

@Serializable
data class ServiceJSON(
    val id: String,
    val name: String,
    val description: String,
    val cycleName: String,
    val cycleType: String,
    val periodName: String,
    val periodNumPeriods: Int,
    val price: PriceJSON,
) {
    companion object {
        fun fromService(service: Service) =
            ServiceJSON(
                service.id!!,
                service.name,
                service.description,
                service.cycleName,
                service.cycleType,
                service.periodName,
                service.periodNumPeriods,
                PriceJSON(
                    service.price.ponta,
                    service.price.cheia,
                    service.price.vazio,
                    service.price.superVazio,
                    service.price.operadorMercado,
                    service.price.gdo,
                    service.price.omip,
                    service.price.margem,
                ),
            )
    }
}

@Serializable
data class PriceJSON(
    val ponta: Float,
    val cheia: Float?,
    val vazio: Float?,
    val superVazio: Float?,
    val operadorMercado: Float,
    val gdo: Float,
    val omip: Float?,
    val margem: Float,
) {
    companion object {
        fun fromPrice(price: Price) =
            PriceJSON(
                price.ponta,
                price.cheia,
                price.vazio,
                price.superVazio,
                price.operadorMercado,
                price.gdo,
                price.omip,
                price.margem,
            )

        fun toPrice(price: PriceJSON) =
            Price(
                price.ponta,
                price.cheia,
                price.vazio,
                price.superVazio,
                price.operadorMercado,
                price.gdo,
                price.omip,
                price.margem,
            )
    }
}
