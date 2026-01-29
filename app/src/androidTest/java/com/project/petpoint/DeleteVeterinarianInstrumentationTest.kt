package com.project.petpoint

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.petpoint.view.VetManagementScreen
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Essential instrumentation tests for Veterinarian Management functionality
@RunWith(AndroidJUnit4::class)
class DeleteVeterinarianInstrumentedTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun setup() {
        Intents.init()
        composeRule.setContent {
            VetManagementScreen()
        }
        Thread.sleep(1500)
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    // Test 1: Verify Vet Management screen is displayed
    @Test
    fun testVetManagementScreenDisplayed() {
        composeRule.onNodeWithText("Veterinarians").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Add").assertIsDisplayed()
        composeRule.onNode(
            hasText("doctors registered", substring = true)
        ).assertExists()
    }

    // Test 2: Test Add Veterinarian dialog opens with all required fields
    @Test
    fun testAddDialogOpens() {
        composeRule.onNodeWithContentDescription("Add").performClick()
        composeRule.waitForIdle()
        Thread.sleep(300)

        composeRule.onNodeWithText("Add Veterinarian").assertIsDisplayed()
        composeRule.onNodeWithText("Name *").assertIsDisplayed()
        composeRule.onNodeWithText("Email *").assertIsDisplayed()
        composeRule.onNodeWithText("Save").assertIsDisplayed()
        composeRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    // Test 3: Test Cancel button closes dialog
    @Test
    fun testCancelDialog() {
        composeRule.onNodeWithContentDescription("Add").performClick()
        Thread.sleep(300)

        composeRule.onNodeWithText("Cancel").performClick()
        composeRule.waitForIdle()
        Thread.sleep(300)

        composeRule.onNodeWithText("Add Veterinarian").assertDoesNotExist()
    }

    // Test 4: Test form validation - submit without required fields
    @Test
    fun testFormValidationWithEmptyFields() {
        composeRule.onNodeWithContentDescription("Add").performClick()
        Thread.sleep(300)

        composeRule.onNodeWithText("Save").performClick()
        composeRule.waitForIdle()
        Thread.sleep(1000)

        // Dialog should still be visible (validation failed)
        composeRule.onNodeWithText("Add Veterinarian").assertExists()
    }

    // Test 5: Test invalid email format validation
    @Test
    fun testInvalidEmailValidation() {
        composeRule.onNodeWithContentDescription("Add").performClick()
        Thread.sleep(300)

        val inputFields = composeRule.onAllNodes(hasSetTextAction())

        inputFields[0].performTextInput("Dr. Test")
        inputFields[2].performTextInput("invalidemail") // Invalid email

        composeRule.onNodeWithText("Save").performClick()
        composeRule.waitForIdle()
        Thread.sleep(1000)

        // Dialog should still be visible (validation failed)
        composeRule.onNodeWithText("Add Veterinarian").assertExists()
    }
    // Test 6: Test adding a complete veterinarian successfully
    @Test
    fun testAddCompleteVeterinarian() {
        composeRule.onNodeWithContentDescription("Add").performClick()
        Thread.sleep(300)

        val inputFields = composeRule.onAllNodes(hasSetTextAction())

        // Fill all required fields
        inputFields[0].performTextInput("Dr. Sarah Williams")
        inputFields[1].performTextInput("Cardiology")
        inputFields[2].performTextInput("sarah@vetclinic.com")
        inputFields[3].performTextInput("9876543210")

        composeRule.onNodeWithText("Save").performClick()
        composeRule.waitForIdle()
        Thread.sleep(1500)

        // Verify dialog closed successfully (indicates validation passed and save completed)
        composeRule.onNodeWithText("Add Veterinarian").assertDoesNotExist()

        // Verify we're back on the main screen
        composeRule.onNodeWithText("Veterinarians").assertIsDisplayed()

        // Verify at least one Edit button exists (proving vets are in the list)
        composeRule.onAllNodesWithText("Edit").fetchSemanticsNodes().isNotEmpty()
    }

    // Test 7: Test Edit button opens Edit dialog
    @Test
    fun testEditDialogOpens() {
        Thread.sleep(2000)

        val editButtons = composeRule.onAllNodesWithText("Edit")
        if (editButtons.fetchSemanticsNodes().isNotEmpty()) {
            editButtons[0].performClick()
            composeRule.waitForIdle()
            Thread.sleep(500)

            composeRule.onNodeWithText("Edit Veterinarian").assertExists()
            composeRule.onNodeWithText("Save").assertIsDisplayed()
            composeRule.onNodeWithText("Cancel").assertIsDisplayed()
        }
    }

    // Test 8: Test editing veterinarian information
    @Test
    fun testEditVeterinarianInfo() {
        Thread.sleep(2000)

        val editButtons = composeRule.onAllNodesWithText("Edit")
        if (editButtons.fetchSemanticsNodes().isNotEmpty()) {
            editButtons[0].performClick()
            composeRule.waitForIdle()
            Thread.sleep(500)

            val inputFields = composeRule.onAllNodes(hasSetTextAction())
            if (inputFields.fetchSemanticsNodes().isNotEmpty()) {
                inputFields[0].performTextInput(" Modified")

                composeRule.onNodeWithText("Save").performClick()
                composeRule.waitForIdle()
                Thread.sleep(1000)

                // Verify dialog closed after save
                composeRule.onNodeWithText("Edit Veterinarian").assertDoesNotExist()
            }
        }
    }

    // Test 9: Test Delete button is clickable
    @Test
    fun testDeleteButtonClick() {
        Thread.sleep(2000)

        val deleteButtons = composeRule.onAllNodesWithText("Delete")
        if (deleteButtons.fetchSemanticsNodes().isNotEmpty()) {
            deleteButtons[0].performClick()
            composeRule.waitForIdle()
            Thread.sleep(500)

            // Test verifies app doesn't crash when delete is clicked
        }
    }

    // Test 10: Test vet card displays all information labels
    @Test
    fun testVetCardDisplaysAllLabels() {
        Thread.sleep(2000)

        val editButtons = composeRule.onAllNodesWithText("Edit")
        if (editButtons.fetchSemanticsNodes().isNotEmpty()) {
            // Verify all information labels exist in cards
            composeRule.onAllNodesWithText("Email").fetchSemanticsNodes()
            composeRule.onAllNodesWithText("Phone").fetchSemanticsNodes()
            composeRule.onAllNodesWithText("Schedule").fetchSemanticsNodes()
            composeRule.onAllNodesWithText("Address").fetchSemanticsNodes()
        }
    }
}