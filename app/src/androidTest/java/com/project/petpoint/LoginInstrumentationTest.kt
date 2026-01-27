package com.project.petpoint

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.petpoint.view.LoginActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LoginInstrumentationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<LoginActivity>()


//  Test 1:  Verify login Screen UI elements are displayed
    @Test
    fun testLoginScreenDisplayed() {
        composeTestRule.onNodeWithText("Sign in to continue").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pet Point").assertIsDisplayed()
        composeTestRule.onNodeWithText("Log in").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
    }


//    Test 2: email input field accepts text
    @Test
    fun testEmailInput() {
        composeTestRule.onNode(
            hasSetTextAction() and hasText("Enter your email")
        ).performTextInput("test@example.com")

        composeTestRule.onNode(
            hasText("test@example.com")
        ).assertExists()
    }


//  Test 3: password input field accepts text
    @Test
    fun testPasswordInput() {
        composeTestRule.onNode(
            hasSetTextAction() and hasText("Enter your password")
        ).performTextInput("password123")

        composeTestRule.waitForIdle()
    }


// Test 4: login button click with empty fields shows validation
    @Test
    fun testLoginWithEmptyFields() {
        composeTestRule.onNodeWithText("Login").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        // Should remain on login screen (validation failed)
        composeTestRule.onNodeWithText("Sign in to continue").assertIsDisplayed()
    }


//    Test 5: Test Password visibility toggle
    @Test
    fun testPasswordVisibilityToggle() {
        composeTestRule.onNode(
            hasSetTextAction() and hasText("Enter your password")
        ).performTextInput("test123")

        // Click show password
        composeTestRule.onNodeWithContentDescription("Show password").performClick()
        composeTestRule.waitForIdle()

        // Click hide password
        composeTestRule.onNodeWithContentDescription("Hide password").performClick()
        composeTestRule.waitForIdle()
    }

//    Test 6: Test navigation to Sign Up screen
    @Test
    fun testNavigateToSignUp() {
        composeTestRule.onNodeWithText("Don't have an account? Sign Up")
            .performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)
    }

//    Test 7: Test login with email only (missing password)
    @Test
    fun testLoginWithOnlyEmail() {
        composeTestRule.onNode(
            hasSetTextAction() and hasText("Enter your email")
        ).performTextInput("test@example.com")

        composeTestRule.onNodeWithText("Login").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        // Should remain on login screen
        composeTestRule.onNodeWithText("Sign in to continue").assertIsDisplayed()
    }

//    Test 8: Test both fields can be filled
    @Test
    fun testFillBothFields() {
        composeTestRule.onNode(
            hasSetTextAction() and hasText("Enter your email")
        ).performTextInput("user@test.com")

        composeTestRule.onNode(
            hasSetTextAction() and hasText("Enter your password")
        ).performTextInput("pass123")

        composeTestRule.onNode(hasText("user@test.com")).assertExists()
        composeTestRule.waitForIdle()
    }
}