package pt.isel.ps.ecoenergy.products.domain.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import pt.isel.ps.ecoenergy.products.data.ProductRepository
import pt.isel.ps.ecoenergy.products.domain.model.Product

class ProductService(
    private val productRepository: ProductRepository,
) {
    // Create
    suspend fun createProduct(
        name: String,
        price: Double,
        description: String,
        image: String,
    ): ProductCreationResult =
        either {
            ensure(name.length in 2..16) { ProductCreationError.ProductNameIsInvalid }
            ensure(!productRepository.productExistsByName(name)) { ProductCreationError.ProductAlreadyExists }

            productRepository.create(
                Product(-1, name, 0.0, "", ""),
            )
        }

    // Read
    suspend fun getAllProducts() = productRepository.getAll()

    suspend fun getAllProductsPaging(
        pageSize: Int,
        lastKeySeen: Int?,
    ) = productRepository.getAllKeyPaging(pageSize, lastKeySeen)

    // suspend fun getByName(name: String): Product? = productRepository.getByName(name)

    suspend fun getById(id: Int): Product? = productRepository.getById(id)

    // Update
    suspend fun updateProduct(product: Product): ProductUpdatingResult =
        either {
            val updatedProduct = productRepository.update(product)
            ensureNotNull(updatedProduct) { ProductUpdatingError.ProductNotFound }
        }

    suspend fun deleteProduct(id: Int): ProductDeletingResult =
        either {
            val product = productRepository.getById(id)
            ensureNotNull(product) { ProductDeletingError.ProductNotFound }
            productRepository.delete(product)
        }
}

typealias ProductCreationResult = Either<ProductCreationError, Int>
typealias ProductReadingResult = Either<ProductReadingError, Product>
typealias ProductUpdatingResult = Either<ProductUpdatingError, Product>
typealias ProductDeletingResult = Either<ProductDeletingError, Boolean>

sealed interface ProductCreationError {
    data object ProductAlreadyExists : ProductCreationError

    data object ProductInfoIsInvalid : ProductCreationError

    data object ProductNameIsInvalid : ProductCreationError

    data object ProductSurnameIsInvalid : ProductCreationError

    data object ProductEmailIsInvalid : ProductCreationError

//    data object ProductSurnameIsInvalid : ProductCreationError
}

sealed interface ProductReadingError {
    data object ProductAlreadyExists : ProductReadingError

    data object ProductNameIsInvalid : ProductReadingError
}

sealed interface ProductUpdatingError {
    data object ProductNotFound : ProductUpdatingError

    data object ProductInfoIsInvalid : ProductUpdatingError

    data object ProductNameIsInvalid : ProductUpdatingError

    data object ProductSurnameIsInvalid : ProductUpdatingError

    data object ProductEmailIsInvalid : ProductUpdatingError
}

sealed interface ProductDeletingError {
    data object ProductNotFound : ProductDeletingError

    data object ProductInfoIsInvalid : ProductDeletingError
}

private fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)\$".toRegex()
    return emailRegex.matches(email)
}
