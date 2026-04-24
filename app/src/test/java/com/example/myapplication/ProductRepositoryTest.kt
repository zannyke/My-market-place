package com.example.myapplication

import com.example.myapplication.data.repository.ProductRepository
import org.junit.Test
import org.junit.Assert.*

class ProductRepositoryTest {

    @Test
    fun `filterProducts returns all products when query and category are empty`() {
        val result = ProductRepository.filterProducts("")
        assertEquals(ProductRepository.products.size, result.size)
    }

    @Test
    fun `filterProducts filters by name correctly`() {
        val result = ProductRepository.filterProducts("Smartphone")
        assertEquals(1, result.size)
        assertEquals("Smartphone X", result[0].name)
    }

    @Test
    fun `filterProducts filters by category correctly`() {
        val result = ProductRepository.filterProducts("", categoryId = "1") // Electronics
        assertTrue(result.all { it.categoryId == "1" })
        assertEquals(2, result.size) // Smartphone X and Wireless Buds
    }

    @Test
    fun `filterProducts returns empty list for non-matching query`() {
        val result = ProductRepository.filterProducts("NonExistent")
        assertTrue(result.isEmpty())
    }
}
