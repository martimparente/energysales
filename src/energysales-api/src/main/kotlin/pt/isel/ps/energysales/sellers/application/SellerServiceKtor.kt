package pt.isel.ps.energysales.sellers.application

import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import pt.isel.ps.energysales.sellers.application.dto.CreateSellerInput
import pt.isel.ps.energysales.sellers.application.dto.CreateSellerResult
import pt.isel.ps.energysales.sellers.application.dto.DeleteSellerError
import pt.isel.ps.energysales.sellers.application.dto.DeleteSellerResult
import pt.isel.ps.energysales.sellers.application.dto.GetAllSellerInput
import pt.isel.ps.energysales.sellers.application.dto.GetAllSellersResult
import pt.isel.ps.energysales.sellers.application.dto.UpdateSellerError
import pt.isel.ps.energysales.sellers.application.dto.UpdateSellerResult
import pt.isel.ps.energysales.sellers.data.SellerRepository
import pt.isel.ps.energysales.sellers.domain.Seller
import pt.isel.ps.energysales.users.domain.Role
import pt.isel.ps.energysales.users.domain.User

class SellerServiceKtor(
    private val sellerRepository: SellerRepository,
) : SellerService {
    // Create
    override suspend fun createSeller(info: CreateSellerInput): CreateSellerResult =
        either {
            val user = User(null, info.name, info.surname, info.email, Role.SELLER)
            val seller = Seller(user, 0.0f, info.team)

            sellerRepository.create(seller)
        }

    // Read
    override suspend fun getAllSellers(input: GetAllSellerInput): GetAllSellersResult =
        either {
            if (input.noTeam) {
                sellerRepository.getSellersWithNoTeam(input.searchQuery)
            } else {
                sellerRepository.getAll()
            }
        }

    override suspend fun getAllSellersPaging(
        pageSize: Int,
        lastKeySeen: String?,
        noTeam: Boolean,
    ) = sellerRepository.getAllKeyPaging(pageSize, lastKeySeen, noTeam)

    // override suspend fun getByName(name: String): Seller? = sellerRepository.getByName(name)

    override suspend fun getById(id: String): Seller? = sellerRepository.getById(id)

    // Update
    override suspend fun updateSeller(seller: Seller): UpdateSellerResult =
        either {
            val updatedSeller = sellerRepository.update(seller)
            ensureNotNull(updatedSeller) { UpdateSellerError.SellerNotFound }
        }

    override suspend fun deleteSeller(id: String): DeleteSellerResult =
        either {
            val seller = sellerRepository.getById(id)
            ensureNotNull(seller) { DeleteSellerError.SellerNotFound }
            sellerRepository.delete(seller)
        }
}
