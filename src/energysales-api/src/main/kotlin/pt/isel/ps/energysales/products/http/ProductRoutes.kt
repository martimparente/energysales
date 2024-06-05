package pt.isel.ps.energysales.products.http

import arrow.core.Either.Left
import arrow.core.Either.Right
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import pt.isel.ps.energysales.Uris
import pt.isel.ps.energysales.auth.http.model.Problem
import pt.isel.ps.energysales.auth.http.model.respondProblem
import pt.isel.ps.energysales.products.domain.model.Product
import pt.isel.ps.energysales.products.domain.service.ProductCreationError
import pt.isel.ps.energysales.products.domain.service.ProductDeletingError
import pt.isel.ps.energysales.products.domain.service.ProductService
import pt.isel.ps.energysales.products.domain.service.ProductUpdatingError
import pt.isel.ps.energysales.products.http.model.CreateProductRequest
import pt.isel.ps.energysales.products.http.model.ProductJSON
import pt.isel.ps.energysales.products.http.model.UpdateProductRequest

@Resource(Uris.PRODUCT)
class ProductResource(
    val lastKeySeen: Int? = null,
) {
    @Resource("{id}")
    class Id(
        val parent: ProductResource = ProductResource(),
        val id: Int,
    )
}

fun Route.productRoutes(productService: ProductService) {
    get<ProductResource> { queryParams ->
        val products = productService.getAllProductsPaging(10, queryParams.lastKeySeen)
        val productsResponse = products.map { product -> ProductJSON.fromProduct(product) }
        call.respond(productsResponse)
    }

    post<ProductResource> {
        val body = call.receive<CreateProductRequest>()

        val res = productService.createProduct(body.name, body.price, body.description, null)
        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.Created)
                call.response.header("Location", "${Uris.PRODUCT}/${res.value}")
            }

            is Left ->
                when (res.value) {
                    ProductCreationError.ProductAlreadyExists ->
                        call.respondProblem(
                            Problem.productEmailAlreadyInUse,
                            HttpStatusCode.Conflict,
                        )

                    ProductCreationError.ProductInfoIsInvalid ->
                        call.respondProblem(
                            Problem.productInfoIsInvalid,
                            HttpStatusCode.BadRequest,
                        )

                    ProductCreationError.ProductEmailIsInvalid -> call.respondProblem(Problem.todo, HttpStatusCode.Continue)
                    ProductCreationError.ProductNameIsInvalid -> call.respondProblem(Problem.todo, HttpStatusCode.Continue)
                    ProductCreationError.ProductSurnameIsInvalid -> call.respondProblem(Problem.todo, HttpStatusCode.Continue)
                }
        }
    }

    get<ProductResource.Id> { pathParams ->
        val product =
            productService.getById(pathParams.id)
                ?: return@get call.respondProblem(Problem.productNotFound, HttpStatusCode.NotFound)
        val productJson = ProductJSON.fromProduct(product)
        call.response.status(HttpStatusCode.OK)
        call.respond(productJson)
    }

    put<ProductResource.Id> { pathParams ->
        val body = call.receive<UpdateProductRequest>()
        val updatedProduct = Product(pathParams.id, body.name, body.price, body.description, null)

        val res = productService.updateProduct(updatedProduct)
        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    ProductUpdatingError.ProductNotFound -> call.respondProblem(Problem.productNotFound, HttpStatusCode.NotFound)
                    ProductUpdatingError.ProductInfoIsInvalid ->
                        call.respondProblem(
                            Problem.productInfoIsInvalid,
                            HttpStatusCode.BadRequest,
                        )

                    ProductUpdatingError.ProductEmailIsInvalid -> TODO()
                    ProductUpdatingError.ProductNameIsInvalid -> TODO()
                    ProductUpdatingError.ProductSurnameIsInvalid -> TODO()
                }
            }
        }
    }

    delete<ProductResource.Id> { pathParams ->
        val res = productService.deleteProduct(pathParams.id)

        when (res) {
            is Right -> call.respond(HttpStatusCode.OK)
            is Left ->
                when (res.value) {
                    ProductDeletingError.ProductNotFound -> call.respondProblem(Problem.productNotFound, HttpStatusCode.NotFound)
                    ProductDeletingError.ProductInfoIsInvalid ->
                        call.respondProblem(
                            Problem.productInfoIsInvalid,
                            HttpStatusCode.BadRequest,
                        )
                }
        }
    }
}
