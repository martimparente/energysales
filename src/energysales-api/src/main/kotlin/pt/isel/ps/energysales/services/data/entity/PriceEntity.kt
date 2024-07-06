package pt.isel.ps.energysales.services.data.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import pt.isel.ps.energysales.services.data.table.PriceTable
import pt.isel.ps.energysales.services.domain.Price

class PriceEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<PriceEntity>(PriceTable)

    fun toPrice() =
        Price(
            ponta,
            cheia,
            vazio,
            superVazio,
            operadorMercado,
            gdo,
            omip,
            margem,
        )

    var ponta by PriceTable.ponta
    var cheia by PriceTable.cheia
    var vazio by PriceTable.vazio
    var superVazio by PriceTable.superVazio
    var operadorMercado by PriceTable.operadorMercado
    var gdo by PriceTable.gdo
    var omip by PriceTable.omip
    var margem by PriceTable.margem
}
