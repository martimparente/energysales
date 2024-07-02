package pt.isel.ps.energysales.services.domain

data class Service(
    val id: Int,
    val name: String,
    val description: String,
    val cycleName: String,
    val cycleType: String,
    val periodName: String,
    val periodNumPeriods: Int,
    val price: Price,
)
