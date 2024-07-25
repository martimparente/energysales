package pt.isel.ps.energysales.clients.data.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import pt.isel.ps.energysales.sellers.data.table.SellerTable
import pt.isel.ps.energysales.services.data.table.ServiceTable

object OfferTable : IntIdTable() {
    val createdAt = datetime("created_at")
    val createdBy = reference("created_by", SellerTable)
    val client = reference("client", ClientTable)
    val service = reference("service", ServiceTable)
    val link = reference("link", OfferLinkTable, onDelete = ReferenceOption.CASCADE)
}

object OfferLinkTable : UUIDTable() {
    val url = varchar("url", 255)
    val due = datetime("due")
}
