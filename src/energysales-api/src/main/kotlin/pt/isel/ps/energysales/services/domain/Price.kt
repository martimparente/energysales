package pt.isel.ps.energysales.services.domain

data class Price(
    val ponta: Float,
    val cheia: Float?,
    val vazio: Float?,
    val superVazio: Float?,
    val operadorMercado: Float,
    val gdo: Float,
    val omip: Float?,
    val margem: Float,
)
