package pt.isel.ps.energysales.services.data.table

import org.jetbrains.exposed.dao.id.IntIdTable

object PriceTable : IntIdTable() {
    val ponta = float("ponta")
    val cheia = float("cheia").nullable()
    val vazio = float("vazio").nullable()
    val superVazio = float("super_vazio").nullable()
    val operadorMercado = float("operador_mercado")
    val gdo = float("gdo")
    val omip = float("omip").nullable()
    val margem = float("margem")
}
