package com.example.ai37c

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.intent.Intents
import com.project.petpoint.view.LoginActivity
import org.junit.Before
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<LoginActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    // Test 1: Verify login Screen UI elements are displayed
    @Test
    fun testLoginScreenDisplayed() {
        composeRule.onNodeWithText("Sign in to continue").assertIsDisplayed()
        composeRule.onNodeWithText("Pet Point").assertIsDisplayed()
        composeRule.onNodeWithText("Log in").assertIsDisplayed()
        composeRule.onNodeWithText("Email").assertIsDisplayed()
        composeRule.onNodeWithText("Password").assertIsDisplayed()
        composeRule.onNodeWithText("Login").assertIsDisplayed()
    }

    // Test 2: email input field accepts text
    @Test
    fun testEmailInput() {
        composeRule.onNode(
            hasSetTextAction() and hasText("Enter your email")
        ).performTextInput("test@example.com")

        composeRule.onNode(
            hasText("test@example.com")
        ).assertExists()
    }

    // Test 3: password input field accepts text
    @Test
    fun testPasswordInput() {
        composeRule.onNode(
            hasSetTextAction() and hasText("Enter your password")
        ).performTextInput("password123")

        composeRule.waitForIdle()
    }

    // Test 4: login button click with empty fields shows validation
    @Test
    fun testLoginWithEmptyFields() {
        composeRule.onNodeWithText("Login").performClick()
        composeRule.waitForIdle()
        Thread.sleep(500)

        // Should remain on login screen (validation failed)
        composeRule.onNodeWithText("Sign in to continue").assertIsDisplayed()
    }

    // Test 5: Test Password visibility toggle
    @Test
    fun testPasswordVisibilityToggle() {
        composeRule.onNode(
            hasSetTextAction() and hasText("Enter your password")
        ).performTextInput("test123")

        // Click show password
        composeRule.onNodeWithContentDescription("Show password").performClick()
        composeRule.waitForIdle()

        // Click hide password
        composeRule.onNodeWithContentDescription("Hide password").performClick()
        composeRule.waitForIdle()
    }

    // Test 6: Test navigation to Sign Up screen
    @Test
    fun testNavigateToSignUp() {
        composeRule.onNodeWithText("Don't have an account? Sign Up")
            .performClick()
        composeRule.waitForIdle()
        Thread.sleep(500)
    }

    // Test 7: Test login with email only (missing password)
    @Test
    fun testLoginWithOnlyEmail() {
        composeRule.onNode(
            hasSetTextAction() and hasText("Enter your email")
        ).performTextInput("test@example.com")

        composeRule.onNodeWithText("Login").performClick()
        composeRule.waitForIdle()
        Thread.sleep(500)

        // Should remain on login screen
        composeRule.onNodeWithText("Sign in to continue").assertIsDisplayed()
    }

    // Test 8: Test both fields can be filled
    @Test
    fun testFillBothFields() {
        composeRule.onNode(
            hasSetTextAction() and hasText("Enter your email")
        ).performTextInput("user@test.com")

        composeRule.onNode(
            hasSetTextAction() and hasText("Enter your password")
        ).performTextInput("pass123")

        composeRule.onNode(hasText("user@test.com")).assertExists()
        composeRule.waitForIdle()
    }
}