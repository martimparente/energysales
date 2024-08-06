package pt.isel.ps.energysales.partners.application

import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import pt.isel.ps.energysales.partners.application.dto.AddPartnerAvatarError
import pt.isel.ps.energysales.partners.application.dto.AddPartnerAvatarResult
import pt.isel.ps.energysales.partners.application.dto.AddPartnerClientError
import pt.isel.ps.energysales.partners.application.dto.AddPartnerClientResult
import pt.isel.ps.energysales.partners.application.dto.AddPartnerPartnerResult
import pt.isel.ps.energysales.partners.application.dto.AddPartnerSellerError
import pt.isel.ps.energysales.partners.application.dto.AddPartnerSellerResult
import pt.isel.ps.energysales.partners.application.dto.AddPartnerServiceError
import pt.isel.ps.energysales.partners.application.dto.CreatePartnerError
import pt.isel.ps.energysales.partners.application.dto.CreatePartnerInput
import pt.isel.ps.energysales.partners.application.dto.CreatePartnerResult
import pt.isel.ps.energysales.partners.application.dto.DeletePartnerError
import pt.isel.ps.energysales.partners.application.dto.DeletePartnerResult
import pt.isel.ps.energysales.partners.application.dto.DeletePartnerSellerResult
import pt.isel.ps.energysales.partners.application.dto.DeletePartnerServiceError
import pt.isel.ps.energysales.partners.application.dto.DeletePartnerServiceResult
import pt.isel.ps.energysales.partners.application.dto.GetPartnerSellersError
import pt.isel.ps.energysales.partners.application.dto.GetPartnerSellersResult
import pt.isel.ps.energysales.partners.application.dto.UpdatePartnerError
import pt.isel.ps.energysales.partners.application.dto.UpdatePartnerInput
import pt.isel.ps.energysales.partners.application.dto.UpdatePartnerResult
import pt.isel.ps.energysales.partners.data.PartnerRepository
import pt.isel.ps.energysales.partners.domain.Location
import pt.isel.ps.energysales.partners.domain.Partner
import pt.isel.ps.energysales.partners.domain.PartnerDetails
import pt.isel.ps.energysales.sellers.data.SellerRepository

class PartnerServiceKtor(
    private val partnerRepository: PartnerRepository,
    private val sellerRepository: SellerRepository,
) : PartnerService {
    // Create
    override suspend fun createPartner(input: CreatePartnerInput): CreatePartnerResult =
        either {
            ensure(input.name.length in 3..50) { CreatePartnerError.PartnerInfoIsInvalid }
            ensure(!partnerRepository.partnerExistsByName(input.name)) { CreatePartnerError.PartnerAlreadyExists }

            val partner = Partner(null, input.name, Location(input.location.district), input.managerId)
            partnerRepository.create(partner)
        }

    // Read
    override suspend fun getAllPartner() = partnerRepository.getAll()

    override suspend fun getAllPartnerPaging(
        limit: Int,
        lastKeySeen: String?,
    ) = partnerRepository.getAllKeyPaging(limit, lastKeySeen?.toIntOrNull())

    override suspend fun getByName(name: String): Partner? = partnerRepository.getByName(name)

    override suspend fun getById(id: String): Partner? = partnerRepository.getById(id)

    override suspend fun getByIdWithDetails(id: String): PartnerDetails? = partnerRepository.getByIdWithDetails(id)

    // Update
    override suspend fun updatePartner(input: UpdatePartnerInput): UpdatePartnerResult =
        either {
            ensure(input.name?.length in 3..50) { UpdatePartnerError.PartnerInfoIsInvalid }
            val partner = partnerRepository.getById(input.id) ?: raise(UpdatePartnerError.PartnerNotFound)
            val patchedPartner =
                partner.copy(
                    name = input.name ?: partner.name,
                    location = input.location ?: partner.location,
                    managerId = input.managerId ?: partner.managerId,
                )

            val updatedPartner = partnerRepository.update(patchedPartner)
            ensureNotNull(updatedPartner) { UpdatePartnerError.PartnerNotFound }
        }

    override suspend fun deletePartner(id: String): DeletePartnerResult =
        either {
            val partner = partnerRepository.getById(id)
            ensureNotNull(partner) { DeletePartnerError.PartnerNotFound }
            partnerRepository.delete(partner)
        }

    override suspend fun getPartnerSellers(id: String): GetPartnerSellersResult =
        either {
            ensure(partnerRepository.partnerExists(id)) { GetPartnerSellersError.PartnerNotFound }
            partnerRepository.getPartnerSellers(id)
        }

    override suspend fun addPartnerSeller(
        id: String,
        sellerId: String,
    ): AddPartnerSellerResult =
        either {
            ensure(partnerRepository.partnerExists(id)) { AddPartnerSellerError.PartnerNotFound }
            ensureNotNull(sellerRepository.sellerExists(sellerId)) { AddPartnerSellerError.SellerNotFound }

            partnerRepository.addSellerToPartner(id, sellerId)
        }

    override suspend fun deletePartnerSeller(sellerId: String): DeletePartnerSellerResult =
        either {
            partnerRepository.deleteSellerFromPartner(sellerId)
        }

    override suspend fun addPartnerService(
        id: String,
        serviceId: String,
    ): AddPartnerPartnerResult =
        either {
            ensure(partnerRepository.partnerExists(id)) { AddPartnerServiceError.PartnerNotFound }
            partnerRepository.addServiceToPartner(id, serviceId)
        }

    override suspend fun deletePartnerService(
        id: String,
        serviceId: String,
    ): DeletePartnerServiceResult =
        either {
            ensure(partnerRepository.partnerExists(id)) { DeletePartnerServiceError.PartnerNotFound }
            partnerRepository.deleteServiceFromPartner(id, serviceId)
        }

    override suspend fun addPartnerClient(
        id: String,
        clientId: String,
    ): AddPartnerClientResult =
        either {
            ensure(partnerRepository.partnerExists(id)) { AddPartnerClientError.PartnerNotFound }
            partnerRepository.addClientToPartner(id, clientId)
        }

    override suspend fun addPartnerAvatar(
        partnerId: String,
        avatarPath: String,
    ): AddPartnerAvatarResult =
        either {
            val partner =
                partnerRepository.getById(partnerId)
                    ?: raise(AddPartnerAvatarError.PartnerNotFound)
            val partnerToUpdate = partner.copy(avatarPath = avatarPath)
            val updatedPartner = partnerRepository.update(partnerToUpdate) ?: raise(AddPartnerAvatarError.PartnerNotFound)
            updatedPartner.avatarPath ?: raise(AddPartnerAvatarError.PartnerNotFound)
        }
}
