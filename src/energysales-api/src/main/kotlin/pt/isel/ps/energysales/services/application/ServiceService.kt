package pt.isel.ps.energysales.services.application

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import pt.isel.ps.energysales.services.application.dto.CreateServiceInput
import pt.isel.ps.energysales.services.application.dto.UpdateServiceInput
import pt.isel.ps.energysales.services.data.ServiceRepository
import pt.isel.ps.energysales.services.domain.Service

class ServiceService(
    private val serviceRepository: ServiceRepository,
) {
    // Create
    suspend fun createService(input: CreateServiceInput): ServiceCreationResult =
        either {
            ensure(input.name.length in 2..16) { ServiceCreationError.ServiceNameIsInvalid }
            ensure(!serviceRepository.serviceExistsByName(input.name)) { ServiceCreationError.ServiceAlreadyExists }

            val newService =
                Service(
                    -1,
                    input.name,
                    input.description,
                    input.cycleName,
                    input.cycleType,
                    input.periodName,
                    input.periodNumPeriods,
                )
            serviceRepository.create(newService)
        }

    // Read
    suspend fun getAllServices() = serviceRepository.getAll()

    suspend fun getAllServicesPaging(
        pageSize: Int,
        lastKeySeen: Int?,
    ) = serviceRepository.getAllKeyPaging(pageSize, lastKeySeen)

    // suspend fun getByName(name: String): Service? = serviceRepository.getByName(name)

    suspend fun getById(id: Int): Service? = serviceRepository.getById(id)

    // Update
    suspend fun updateService(input: UpdateServiceInput): ServiceUpdatingResult =
        either {
            val service = serviceRepository.getById(input.id)
            ensureNotNull(service) { ServiceUpdatingError.ServiceNotFound }

            // ONLY Update service if equivalent fields are not null
            val newService =
                service.copy(
                    name = input.name ?: service.name,
                    description = input.description ?: service.description,
                    cycleName = input.cycleName ?: service.cycleName,
                    cycleType = input.cycleType ?: service.cycleType,
                    periodName = input.periodName ?: service.periodName,
                    periodNumPeriods = input.periodNumPeriods ?: service.periodNumPeriods,
                )

            val updatedService = serviceRepository.update(newService)
            ensureNotNull(updatedService) { ServiceUpdatingError.ServiceNotFound }
        }

    suspend fun deleteService(id: Int): ServiceDeletingResult =
        either {
            val service = serviceRepository.getById(id)
            ensureNotNull(service) { ServiceDeletingError.ServiceNotFound }
            serviceRepository.delete(service)
        }
}

typealias ServiceCreationResult = Either<ServiceCreationError, Int>
typealias ServiceReadingResult = Either<ServiceReadingError, Service>
typealias ServiceUpdatingResult = Either<ServiceUpdatingError, Service>
typealias ServiceDeletingResult = Either<ServiceDeletingError, Boolean>

sealed interface ServiceCreationError {
    data object ServiceAlreadyExists : ServiceCreationError

    data object ServiceInfoIsInvalid : ServiceCreationError

    data object ServiceNameIsInvalid : ServiceCreationError

    data object ServiceSurnameIsInvalid : ServiceCreationError

    data object ServiceEmailIsInvalid : ServiceCreationError

//    data object ServiceSurnameIsInvalid : ServiceCreationError
}

sealed interface ServiceReadingError {
    data object ServiceAlreadyExists : ServiceReadingError

    data object ServiceNameIsInvalid : ServiceReadingError
}

sealed interface ServiceUpdatingError {
    data object ServiceNotFound : ServiceUpdatingError

    data object ServiceInfoIsInvalid : ServiceUpdatingError

    data object ServiceNameIsInvalid : ServiceUpdatingError

    data object ServiceSurnameIsInvalid : ServiceUpdatingError

    data object ServiceEmailIsInvalid : ServiceUpdatingError
}

sealed interface ServiceDeletingError {
    data object ServiceNotFound : ServiceDeletingError

    data object ServiceInfoIsInvalid : ServiceDeletingError
}

private fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)\$".toRegex()
    return emailRegex.matches(email)
}
