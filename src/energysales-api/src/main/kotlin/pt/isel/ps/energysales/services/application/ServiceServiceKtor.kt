package pt.isel.ps.energysales.services.application

import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import pt.isel.ps.energysales.services.application.dto.CreateServiceError
import pt.isel.ps.energysales.services.application.dto.CreateServiceInput
import pt.isel.ps.energysales.services.application.dto.CreateServiceResult
import pt.isel.ps.energysales.services.application.dto.DeleteServiceError
import pt.isel.ps.energysales.services.application.dto.DeleteServiceResult
import pt.isel.ps.energysales.services.application.dto.UpdateServiceError
import pt.isel.ps.energysales.services.application.dto.UpdateServiceInput
import pt.isel.ps.energysales.services.application.dto.UpdateServiceResult
import pt.isel.ps.energysales.services.data.ServiceRepository
import pt.isel.ps.energysales.services.domain.Service
import pt.isel.ps.energysales.services.http.model.PriceJSON

class ServiceServiceKtor(
    private val serviceRepository: ServiceRepository,
) : ServiceService {
    override suspend fun createService(input: CreateServiceInput): CreateServiceResult =
        either {
            ensure(input.name.length in 2..16) { CreateServiceError.ServiceNameIsInvalid }
            ensure(!serviceRepository.serviceExistsByName(input.name)) { CreateServiceError.ServiceAlreadyExists }

            val newService =
                Service(
                    null,
                    input.name,
                    input.description,
                    input.cycleName,
                    input.cycleType,
                    input.periodName,
                    input.periodNumPeriods,
                    PriceJSON.toPrice(input.price),
                )
            serviceRepository.create(newService)
        }

    // Read
    override suspend fun getAllServices() = serviceRepository.getAll()

    override suspend fun getAllServicesPaging(
        pageSize: Int,
        lastKeySeen: String?,
    ) = serviceRepository.getAllKeyPaging(pageSize, lastKeySeen)

    // override suspend fun getByName(name: String): Service? = serviceRepository.getByName(name)

    override suspend fun getById(id: String): Service? = serviceRepository.getById(id)

    // Update
    override suspend fun updateService(input: UpdateServiceInput): UpdateServiceResult =
        either {
            val service = serviceRepository.getById(input.id)
            ensureNotNull(service) { UpdateServiceError.ServiceNotFound }

            // ONLY Update service if equivalent fields are not null
            val newService =
                service.copy(
                    name = input.name ?: service.name,
                    description = input.description ?: service.description,
                    cycleName = input.cycleName ?: service.cycleName,
                    cycleType = input.cycleType ?: service.cycleType,
                    periodName = input.periodName ?: service.periodName,
                    periodNumPeriods = input.periodNumPeriods ?: service.periodNumPeriods,
                    price = if (input.price != null) PriceJSON.toPrice(input.price) else service.price,
                )

            val updatedService = serviceRepository.update(newService)
            ensureNotNull(updatedService) { UpdateServiceError.ServiceNotFound }
        }

    override suspend fun deleteService(id: String): DeleteServiceResult =
        either {
            val service = serviceRepository.getById(id)
            ensureNotNull(service) { DeleteServiceError.ServiceNotFound }
            serviceRepository.delete(service)
        }
}
