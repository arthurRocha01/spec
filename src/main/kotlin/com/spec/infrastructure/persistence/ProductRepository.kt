package com.spec.infrastructure.persistence

import com.spec.domain.product.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import kotlin.time.Clock

class ProductRepository {
    fun findByUrl(url: String): Product? {
        val row = ProductTable.selectAll().where { ProductTable.url eq url }
            .singleOrNull() ?: return null

        val id = row[ProductTable.id]
        val specs = ProductSpecTable.selectAll().where { ProductSpecTable.productId eq id }
            .map { Specification(it[ProductSpecTable.key], it[ProductSpecTable.value]) }
        val compatibilities = ProductCompatibilityTable.selectAll().where { ProductCompatibilityTable.productId eq id }
            .map { Compatibility(it[ProductCompatibilityTable.model], it[ProductCompatibilityTable.yearRange]) }
        val images = ProductImageTable.selectAll().where { ProductImageTable.productId eq id }
            .map { ProductImage(it[ProductImageTable.url], it[ProductImageTable.imageOrder]) }

        return Product(
            name = row[ProductTable.name],
            specifications = specs,
            compatibility = compatibilities,
            images = images,
            sourceUrl = row[ProductTable.url]
        )
    }

    fun save(product: Product): Int {
        val id = ProductTable.insertAndGetId {
            it[url] = product.sourceUrl
            it[name] = product.name
            it[query] = null
            it[rawText] = null
            it[llmResponse] = null
            it[createdAt] = Clock.System.now().toString()
            it[accessedAt] = null
        }.value

        product.specifications.forEach { spec ->
            ProductSpecTable.insert {
                it[productId] = id
                it[key] = spec.key
                it[value] = spec.value
            }
        }

        product.compatibility.forEach { comp ->
            ProductCompatibilityTable.insert {
                it[productId] = id
                it[model] = comp.model
                it[yearRange] = comp.yearRange
            }
        }

        product.images.forEach { image ->
            ProductImageTable.insert {
                it[productId] = id
                it[url] = image.url
                it[imageOrder] = image.order
            }
        }

        return id
    }

    fun delete(id: Int) {
        ProductImageTable.deleteWhere { ProductImageTable.productId eq id }
        ProductCompatibilityTable.deleteWhere { ProductCompatibilityTable.productId eq id }
        ProductSpecTable.deleteWhere { ProductSpecTable.productId eq id }
        ProductTable.deleteWhere { ProductTable.id eq id }
    }
}