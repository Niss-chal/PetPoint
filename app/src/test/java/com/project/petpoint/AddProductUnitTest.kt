package com.project.petpoint.viewmodel

import android.content.Context
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.project.petpoint.model.ProductModel
import com.project.petpoint.repository.ProductRepo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

class AddProductUnitTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repo: ProductRepo
    private lateinit var viewModel: ProductViewModel
    private lateinit var mockContext: Context
    private lateinit var mockUri: Uri

    @Before
    fun setup() {
        repo = mock()
        viewModel = ProductViewModel(repo)
        mockContext = mock()
        mockUri = mock()
    }

    // ============ ADD PRODUCT TESTS ============

    @Test
    fun addProduct_success_test() {
        val product = ProductModel(
            productId = "123",
            name = "Dog Food",
            price = 25.99,
            description = "Premium dog food",
            imageUrl = "https://example.com/image.jpg",
            stock = 50
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Product added successfully")
            null
        }.`when`(repo).addProduct(eq(product), any())

        var successResult = false
        var messageResult = ""

        viewModel.addProduct(product) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Product added successfully", messageResult)
        verify(repo).addProduct(eq(product), any())
    }

    @Test
    fun addProduct_failure_test() {
        val product = ProductModel(
            productId = "",
            name = "Cat Toy",
            price = 15.0,
            description = "Interactive toy",
            imageUrl = "https://example.com/toy.jpg",
            stock = 30
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(false, "Failed to add product")
            null
        }.`when`(repo).addProduct(eq(product), any())

        var successResult = true
        var messageResult = ""

        viewModel.addProduct(product) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertFalse(successResult)
        assertEquals("Failed to add product", messageResult)
        verify(repo).addProduct(eq(product), any())
    }

    // ============ UPDATE PRODUCT TESTS ============

    @Test
    fun updateProduct_success_test() {
        val product = ProductModel(
            productId = "123",
            name = "Updated Dog Food",
            price = 29.99,
            description = "Premium organic dog food",
            imageUrl = "https://example.com/updated.jpg",
            stock = 100
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Product updated successfully")
            null
        }.`when`(repo).updateProduct(eq(product), any())

        var successResult = false
        var messageResult = ""

        viewModel.updateProduct(product) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Product updated successfully", messageResult)
        verify(repo).updateProduct(eq(product), any())
    }

    @Test
    fun updateProduct_failure_test() {
        val product = ProductModel(
            productId = "999",
            name = "Non-existent Product",
            price = 10.0,
            description = "Test",
            imageUrl = "url",
            stock = 5
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(false, "Product not found")
            null
        }.`when`(repo).updateProduct(eq(product), any())

        var successResult = true
        var messageResult = ""

        viewModel.updateProduct(product) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertFalse(successResult)
        assertEquals("Product not found", messageResult)
        verify(repo).updateProduct(eq(product), any())
    }

    // ============ DELETE PRODUCT TESTS ============

    @Test
    fun deleteProduct_success_test() {
        val productId = "123"

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Product deleted successfully")
            null
        }.`when`(repo).deleteProduct(eq(productId), any())

        var successResult = false
        var messageResult = ""

        viewModel.deleteProduct(productId) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Product deleted successfully", messageResult)
        verify(repo).deleteProduct(eq(productId), any())
    }

    @Test
    fun deleteProduct_failure_test() {
        val productId = "999"

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(false, "Failed to delete product")
            null
        }.`when`(repo).deleteProduct(eq(productId), any())

        var successResult = true
        var messageResult = ""

        viewModel.deleteProduct(productId) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertFalse(successResult)
        assertEquals("Failed to delete product", messageResult)
        verify(repo).deleteProduct(eq(productId), any())
    }

    // ============ GET PRODUCT BY ID TESTS ============

    @Test
    fun getProductById_success_test() {
        val productId = "123"
        val expectedProduct = ProductModel(
            productId = "123",
            name = "Dog Food",
            price = 25.99,
            description = "Premium dog food",
            imageUrl = "https://example.com/image.jpg",
            stock = 50
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, ProductModel?) -> Unit>(1)
            callback(true, "Success", expectedProduct)
            null
        }.`when`(repo).getProductById(eq(productId), any())

        viewModel.getProductById(productId)

        assertEquals(false, viewModel.loading.value)
        assertEquals(expectedProduct, viewModel.selectedProduct.value)
        verify(repo).getProductById(eq(productId), any())
    }

    @Test
    fun getProductById_failure_test() {
        val productId = "999"

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, ProductModel?) -> Unit>(1)
            callback(false, "Product not found", null)
            null
        }.`when`(repo).getProductById(eq(productId), any())

        viewModel.getProductById(productId)

        assertEquals(false, viewModel.loading.value)
        assertNull(viewModel.selectedProduct.value)
        verify(repo).getProductById(eq(productId), any())
    }

    // ============ GET ALL PRODUCTS TESTS ============

    @Test
    fun getAllProduct_success_test() {
        val productList = listOf(
            ProductModel(productId = "1", name = "Dog Food", price = 25.99, description = "Premium", imageUrl = "url1", stock = 50),
            ProductModel(productId = "2", name = "Cat Toy", price = 15.0, description = "Fun toy", imageUrl = "url2", stock = 30),
            ProductModel(productId = "3", name = "Pet Bed", price = 45.0, description = "Comfortable", imageUrl = "url3", stock = 20)
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<ProductModel>?) -> Unit>(0)
            callback(true, "Success", productList)
            null
        }.`when`(repo).getAllProduct(any())

        viewModel.getAllProduct()

        assertEquals(false, viewModel.loading.value)
        assertEquals(productList, viewModel.allProducts.value)
        assertEquals(productList, viewModel.filteredProducts.value)
        verify(repo).getAllProduct(any())
    }

    @Test
    fun getAllProduct_failure_test() {
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<ProductModel>?) -> Unit>(0)
            callback(false, "Failed to fetch products", null)
            null
        }.`when`(repo).getAllProduct(any())

        viewModel.getAllProduct()

        assertEquals(false, viewModel.loading.value)
        assertNull(viewModel.allProducts.value)
        assertNull(viewModel.filteredProducts.value)
        assertEquals("Failed to fetch products", viewModel.message.value)
        verify(repo).getAllProduct(any())
    }

    // ============ GET PRODUCT BY CATEGORY TESTS ============

    @Test
    fun getProductByCategory_success_test() {
        val categoryId = "Food"
        val categoryProducts = listOf(
            ProductModel(productId = "1", name = "Dog Food", price = 25.99, description = "Premium", imageUrl = "url1", stock = 50),
            ProductModel(productId = "2", name = "Cat Food", price = 20.0, description = "Nutritious", imageUrl = "url2", stock = 40)
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<ProductModel>?) -> Unit>(1)
            callback(true, "Success", categoryProducts)
            null
        }.`when`(repo).getProductByCategory(eq(categoryId), any())

        viewModel.getProductByCategory(categoryId)

        assertEquals(false, viewModel.loading.value)
        assertEquals(categoryProducts, viewModel.filteredProducts.value)
        verify(repo).getProductByCategory(eq(categoryId), any())
    }

    @Test
    fun getProductByCategory_failure_test() {
        val categoryId = "InvalidCategory"

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<ProductModel>?) -> Unit>(1)
            callback(false, "No products found", null)
            null
        }.`when`(repo).getProductByCategory(eq(categoryId), any())

        viewModel.getProductByCategory(categoryId)

        assertEquals(false, viewModel.loading.value)
        assertNull(viewModel.filteredProducts.value)
        assertEquals("No products found", viewModel.message.value)
        verify(repo).getProductByCategory(eq(categoryId), any())
    }

    // ============ UPLOAD IMAGE TESTS ============

    @Test
    fun uploadImage_success_test() {
        val expectedUrl = "https://example.com/uploaded-image.jpg"

        doAnswer { invocation ->
            val callback = invocation.getArgument<(String?) -> Unit>(2)
            callback(expectedUrl)
            null
        }.`when`(repo).uploadImage(eq(mockContext), eq(mockUri), any())

        var resultUrl: String? = null

        viewModel.uploadImage(mockContext, mockUri) { url ->
            resultUrl = url
        }

        assertEquals(expectedUrl, resultUrl)
        verify(repo).uploadImage(eq(mockContext), eq(mockUri), any())
    }

    @Test
    fun uploadImage_failure_test() {
        doAnswer { invocation ->
            val callback = invocation.getArgument<(String?) -> Unit>(2)
            callback(null)
            null
        }.`when`(repo).uploadImage(eq(mockContext), eq(mockUri), any())

        var resultUrl: String? = "not-null"

        viewModel.uploadImage(mockContext, mockUri) { url ->
            resultUrl = url
        }

        assertNull(resultUrl)
        verify(repo).uploadImage(eq(mockContext), eq(mockUri), any())
    }

    // ============ SEARCH FUNCTIONALITY TESTS ============

    @Test
    fun onSearchQueryChange_updatesSearchQuery_test() {
        val query = "Dog"

        viewModel.onSearchQueryChange(query)

        assertEquals(query, viewModel.searchQuery.value)
    }

    @Test
    fun filterProducts_emptyQuery_returnsAllProducts_test() {
        val allProducts = listOf(
            ProductModel(productId = "1", name = "Dog Food", price = 25.99, description = "Premium", imageUrl = "url1", stock = 50),
            ProductModel(productId = "2", name = "Cat Toy", price = 15.0, description = "Fun toy", imageUrl = "url2", stock = 30)
        )

        // Manually set allProducts
        viewModel.allProducts.postValue(allProducts)

        viewModel.onSearchQueryChange("")

        assertEquals(allProducts, viewModel.filteredProducts.value)
    }

    @Test
    fun filterProducts_byName_test() {
        val allProducts = listOf(
            ProductModel(productId = "1", name = "Dog Food", price = 25.99, description = "Premium", imageUrl = "url1", stock = 50),
            ProductModel(productId = "2", name = "Cat Toy", price = 15.0, description = "Fun toy", imageUrl = "url2", stock = 30),
            ProductModel(productId = "3", name = "Dog Bed", price = 45.0, description = "Comfortable", imageUrl = "url3", stock = 20)
        )

        viewModel.allProducts.postValue(allProducts)
        viewModel.onSearchQueryChange("Dog")

        assertEquals(2, viewModel.filteredProducts.value?.size)
        assertTrue(viewModel.filteredProducts.value?.all { it.name.contains("Dog", true) } == true)
    }

    @Test
    fun filterProducts_byDescription_test() {
        val allProducts = listOf(
            ProductModel(productId = "1", name = "Food", price = 25.99, description = "Premium quality", imageUrl = "url1", stock = 50),
            ProductModel(productId = "2", name = "Toy", price = 15.0, description = "Premium material", imageUrl = "url2", stock = 30),
            ProductModel(productId = "3", name = "Bed", price = 45.0, description = "Comfortable", imageUrl = "url3", stock = 20)
        )

        viewModel.allProducts.postValue(allProducts)
        viewModel.onSearchQueryChange("Premium")

        assertEquals(2, viewModel.filteredProducts.value?.size)
    }

    @Test
    fun filterProducts_caseInsensitive_test() {
        val allProducts = listOf(
            ProductModel(productId = "1", name = "Dog Food", price = 25.99, description = "Premium", imageUrl = "url1", stock = 50),
            ProductModel(productId = "2", name = "dog toy", price = 15.0, description = "Fun", imageUrl = "url2", stock = 30)
        )

        viewModel.allProducts.postValue(allProducts)
        viewModel.onSearchQueryChange("DOG")

        assertEquals(2, viewModel.filteredProducts.value?.size)
    }

    @Test
    fun filterProducts_noMatch_returnsEmpty_test() {
        val allProducts = listOf(
            ProductModel(productId = "1", name = "Dog Food", price = 25.99, description = "Premium", imageUrl = "url1", stock = 50),
            ProductModel(productId = "2", name = "Cat Toy", price = 15.0, description = "Fun toy", imageUrl = "url2", stock = 30)
        )

        viewModel.allProducts.postValue(allProducts)
        viewModel.onSearchQueryChange("Bird")

        assertEquals(0, viewModel.filteredProducts.value?.size)
    }

    // ============ PRODUCT CLICK TESTS ============

    @Test
    fun onProductClick_setsSelectedProduct_test() {
        val product = ProductModel(productId = "1", name = "Dog Food", price = 25.99, description = "Premium", imageUrl = "url1", stock = 50)

        viewModel.onProductClick(product)

        assertEquals(product, viewModel.selectedProduct.value)
        assertEquals("Opening Dog Food", viewModel.message.value)
    }

    // ============ UTILITY FUNCTION TESTS ============

    @Test
    fun refreshProducts_callsGetAllProduct_test() {
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<ProductModel>?) -> Unit>(0)
            callback(true, "Success", emptyList())
            null
        }.`when`(repo).getAllProduct(any())

        viewModel.refreshProducts()

        verify(repo).getAllProduct(any())
    }

    @Test
    fun clearMessage_setsMessageToNull_test() {
        viewModel.message.postValue("Test message")

        viewModel.clearMessage()

        assertNull(viewModel.message.value)
    }

    // ============ LOADING STATE TESTS ============

    @Test
    fun getAllProduct_setsLoadingState_test() {
        doAnswer { invocation ->
            // Verify loading was set to true before callback
            assertEquals(true, viewModel.loading.value)
            val callback = invocation.getArgument<(Boolean, String, List<ProductModel>?) -> Unit>(0)
            callback(true, "Success", emptyList())
            null
        }.`when`(repo).getAllProduct(any())

        viewModel.getAllProduct()

        // After completion, loading should be false
        assertEquals(false, viewModel.loading.value)
    }
}