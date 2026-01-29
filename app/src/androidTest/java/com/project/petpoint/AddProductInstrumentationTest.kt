package com.project.petpoint


import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.petpoint.view.AddProductActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddProductInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<AddProductActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    // Test 1: Screen loads correctly
    @Test
    fun testAddProductScreenDisplayed() {
        composeRule.onNodeWithTag("AddProductTitle").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        composeRule.onNodeWithText("Product Name").assertIsDisplayed()
        composeRule.onNodeWithText("Category").assertIsDisplayed()
    }

    // Test 2: Product name input works
    @Test
    fun testProductNameInput() {
        composeRule.onNodeWithTag("ProductNameInput")
            .performTextInput("Dog Food Premium")
        composeRule.onNodeWithText("Dog Food Premium").assertExists()
    }

    // Test 3: Category dropdown opens and selection works
    @Test
    fun testCategoryDropdownSelection() {
        composeRule.onNodeWithTag("CategoryDropdown").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Food").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Food").assertIsDisplayed()
    }

    // Test 4: All category options are available
    @Test
    fun testAllCategoryOptions() {
        composeRule.onNodeWithTag("CategoryDropdown").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Food").assertExists()
        composeRule.onNodeWithText("Toys").assertExists()
        composeRule.onNodeWithText("Accessories").assertExists()
    }

    // Test 5: Submit with empty fields shows validation
    @Test
    fun testSubmitWithEmptyFields() {
        composeRule.onNodeWithTag("AddProductButton").performClick()
        composeRule.waitForIdle()
        Thread.sleep(500)

        composeRule.onNodeWithTag("AddProductTitle").assertIsDisplayed()
    }

    // Test 6: Submit with only product name fails
    @Test
    fun testSubmitWithOnlyProductName() {
        composeRule.onNodeWithTag("ProductNameInput")
            .performTextInput("Test Product")

        composeRule.onNodeWithTag("AddProductButton").performClick()
        composeRule.waitForIdle()
        Thread.sleep(500)

        composeRule.onNodeWithTag("AddProductTitle").assertIsDisplayed()
    }

    // Test 7: Complete form filling workflow
    @Test
    fun testCompleteFormFilling() {
        // Fill product name
        composeRule.onNodeWithTag("ProductNameInput")
            .performTextInput("Premium Dog Food")

        // Fill other fields if they exist
        val inputFields = composeRule.onAllNodes(hasSetTextAction())
        if (inputFields.fetchSemanticsNodes().size > 1) {
            inputFields[1].performTextInput("299")
            inputFields[2].performTextInput("50")
        }

        // Select category
        composeRule.onNodeWithTag("CategoryDropdown").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Food").performClick()
        composeRule.waitForIdle()
    }

    // Test 8: Back button navigation
    @Test
    fun testBackButtonClick() {
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.waitForIdle()
        Thread.sleep(500)
    }

    // Test 9: Image upload section is visible
    @Test
    fun testImageUploadVisible() {
        composeRule.onNodeWithText("Tap to upload image").assertIsDisplayed()
    }

    // Test 10: Add button is visible and clickable
    @Test
    fun testAddButtonVisibleAndClickable() {
        composeRule.onNodeWithTag("AddProductButton").assertIsDisplayed()
        composeRule.onNodeWithTag("AddProductButton").performClick()
        composeRule.waitForIdle()
    }
}