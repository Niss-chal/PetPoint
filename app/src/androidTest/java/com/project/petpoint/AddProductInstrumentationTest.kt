package com.project.petpoint

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.petpoint.view.AddProductActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddProductInstrumentationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<AddProductActivity>()

    // Test 1: Verify Add Product screen UI elements are displayed
    @Test
    fun testAddProductScreenDisplayed() {
        composeTestRule.onNodeWithTag("AddProductTitle").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        composeTestRule.onNodeWithText("Product Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Price").assertIsDisplayed()
        composeTestRule.onNodeWithText("Category").assertIsDisplayed()
        composeTestRule.onNodeWithText("Stock Quantity").assertIsDisplayed()
    }

    // Test 2: Verify product name input works
    @Test
    fun testProductNameInput() {
        composeTestRule.onNodeWithTag("ProductNameInput")
            .performTextInput("Dog Food")
        composeTestRule.onNodeWithText("Dog Food").assertExists()
    }

    // Test 3: Verify category dropdown selection
    @Test
    fun testCategoryDropdownSelection() {
        composeTestRule.onNodeWithTag("CategoryDropdown").performClick()
        composeTestRule.onNodeWithText("Food").performClick()
        composeTestRule.onNodeWithText("Food").assertIsDisplayed()
    }

    // Test 4: Verify image placeholder is visible
    @Test
    fun testImagePlaceholder() {
        composeTestRule.onNodeWithText("Tap to upload image").assertIsDisplayed()
    }

    // Test 5: Verify form validation when fields are empty
    @Test
    fun testSubmitWithEmptyFields() {
        composeTestRule.onNodeWithTag("AddProductButton").performClick()
        composeTestRule.onNodeWithTag("AddProductTitle").assertIsDisplayed()
    }
}
