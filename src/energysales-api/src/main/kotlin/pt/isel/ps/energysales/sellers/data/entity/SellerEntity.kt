package pt.isel.ps.energysales.sellers.data.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import pt.isel.ps.energysales.partners.data.entity.PartnerEntity
import pt.isel.ps.energysales.sellers.data.table.SellerTable
import pt.isel.ps.energysales.sellers.domain.Seller
import pt.isel.ps.energysales.users.data.entity.UserEntity

class SellerEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<SellerEntity>(SellerTable)

    var user by UserEntity referencedOn SellerTable.id
    var totalSales by SellerTable.totalSales
    var partner by PartnerEntity optionalReferencedOn SellerTable.partner

    fun toSeller() =
        Seller(
            user.toUser(),
            totalSales,
            partner?.id?.value.toString(),
        )
}
