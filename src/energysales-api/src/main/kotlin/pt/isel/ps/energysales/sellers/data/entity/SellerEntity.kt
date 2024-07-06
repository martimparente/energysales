import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import pt.isel.ps.energysales.sellers.data.table.SellerTable
import pt.isel.ps.energysales.sellers.domain.Seller
import pt.isel.ps.energysales.teams.data.entity.TeamEntity
import pt.isel.ps.energysales.users.data.entity.UserEntity

class SellerEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<SellerEntity>(SellerTable)

    var user by UserEntity referencedOn SellerTable.id
    var totalSales by SellerTable.totalSales
    var team by TeamEntity optionalReferencedOn SellerTable.team

    fun toSeller() =
        Seller(
            user.toUser(),
            totalSales,
            team?.id?.value,
        )
}
