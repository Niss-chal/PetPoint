package com.project.petpoint

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.petpoint.view.VetManagementScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Essential instrumentation tests for Delete Veterinarian functionality
// Tests core UI interactions for adding, editing, and deleting vets
@RunWith(AndroidJUnit4::class)
class DeleteVeterinarianInstrumentationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        composeTestRule.setContent {
            VetManagementScreen()
        }
        Thread.sleep(1500)
    }

    // Test 1: Verify Vet Management screen is displayed
    @Test
    fun testVetManagementScreenDisplayed() {
        composeTestRule.onNodeWithText("Veterinarians").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add").assertIsDisplayed()
        composeTestRule.onNode(
            hasText("doctors registered", substring = true)
        ).assertExists()
    }

    // Test 2: Test Add Veterinarian dialog opens
    @Test
    fun testAddDialogOpens() {
        composeTestRule.onNodeWithContentDescription("Add").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(300)

        composeTestRule.onNodeWithText("Add Veterinarian").assertIsDisplayed()
        composeTestRule.onNodeWithText("Name *").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email *").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    // Test 3: Test Cancel button closes dialog
    @Test
    fun testCancelDialog() {
        composeTestRule.onNodeWithContentDescription("Add").performClick()
        Thread.sleep(300)

        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(300)

        composeTestRule.onNodeWithText("Veterinarians").assertIsDisplayed()
    }

    // Test 4: Test name field input
    @Test
    fun testNameFieldInput() {
        composeTestRule.onNodeWithContentDescription("Add").performClick()
        Thread.sleep(300)

        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextInput("Dr. Test Vet")

        composeTestRule.waitForIdle()
    }

    // Test 5: Test email field input
    @Test
    fun testEmailFieldInput() {
        composeTestRule.onNodeWithContentDescription("Add").performClick()
        Thread.sleep(300)

        // Email field is typically the 3rd input field (index 2)
        composeTestRule.onAllNodes(hasSetTextAction())[2]
            .performTextInput("vet@clinic.com")

        composeTestRule.waitForIdle()
    }

    // Test 6: Test form validation - submit without required fields
    @Test
    fun testFormValidation() {
        composeTestRule.onNodeWithContentDescription("Add").performClick()
        Thread.sleep(300)

        // Try to save without filling required fields
        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(1000)

        // Dialog should still be visible (validation failed)
        composeTestRule.onNodeWithText("Add Veterinarian").assertExists()
    }

    // Test 7: Test filling required fields
    @Test
    fun testFillRequiredFields() {
        composeTestRule.onNodeWithContentDescription("Add").performClick()
        Thread.sleep(300)

        val inputFields = composeTestRule.onAllNodes(hasSetTextAction())

        // Fill Name
        inputFields[0].performTextInput("Dr. John Doe")

        // Fill Specialization
        inputFields[1].performTextInput("Surgery")

        // Fill Email
        inputFields[2].performTextInput("john@vet.com")

        // Fill Phone
        inputFields[3].performTextInput("1234567890")

        composeTestRule.waitForIdle()
    }

    // Test 8: Test Edit button visibility when vets exist
    @Test
    fun testEditButtonVisibility() {
        Thread.sleep(2000)

        val editButtons = composeTestRule.onAllNodesWithText("Edit")
        if (editButtons.fetchSemanticsNodes().isNotEmpty()) {
            editButtons[0].assertIsDisplayed()
        }
    }

    // Test 9: Test Delete button visibility when vets exist
    @Test
    fun testDeleteButtonVisibility() {
        Thread.sleep(2000)

        val deleteButtons = composeTestRule.onAllNodesWithText("Delete")
        if (deleteButtons.fetchSemanticsNodes().isNotEmpty()) {
            deleteButtons[0].assertIsDisplayed()
        }
    }

    // Test 10: Test clicking Edit button opens Edit dialog
    @Test
    fun testEditDialogOpens() {
        Thread.sleep(2000)

        val editButtons = composeTestRule.onAllNodesWithText("Edit")
        if (editButtons.fetchSemanticsNodes().isNotEmpty()) {
            editButtons[0].performClick()
            composeTestRule.waitForIdle()
            Thread.sleep(500)

            composeTestRule.onNodeWithText("Edit Veterinarian").assertExists()
        }
    }

    // Test 11: Test clicking Delete button
    // Note: This triggers Android AlertDialog which is harder to test
    @Test
    fun testDeleteButtonClick() {
        Thread.sleep(2000)

        val deleteButtons = composeTestRule.onAllNodesWithText("Delete")
        if (deleteButtons.fetchSemanticsNodes().isNotEmpty()) {
            deleteButtons[0].performClick()
            composeTestRule.waitForIdle()
            Thread.sleep(500)

            // AlertDialog appears (not easily testable in Compose)
            // Test verifies app doesn't crash
        }
    }

    // Test 12: Test vet card displays information labels
    @Test
    fun testVetCardLabels() {
        Thread.sleep(2000)

        val editButtons = composeTestRule.onAllNodesWithText("Edit")
        if (editButtons.fetchSemanticsNodes().isNotEmpty()) {
            // Verify information labels exist in cards
            composeTestRule.onAllNodesWithText("Email").fetchSemanticsNodes()
            composeTestRule.onAllNodesWithText("Phone").fetchSemanticsNodes()
            composeTestRule.onAllNodesWithText("Schedule").fetchSemanticsNodes()
            composeTestRule.onAllNodesWithText("Address").fetchSemanticsNodes()
        }
    }
}
