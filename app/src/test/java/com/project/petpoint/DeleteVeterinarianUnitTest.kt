package com.project.petpoint.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.project.petpoint.repository.VetRepo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

class DeleteVeterinarianUnitTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var repo: VetRepo
    private lateinit var viewModel: VetViewModel

    @Before
    fun setup() {
        repo = mock()
        viewModel = VetViewModel(repo)
    }

    @Test
    fun deleteDoctor_success_test() {
        val vetId = "vet123"

        doAnswer {
            val callback = it.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Veterinarian deleted successfully")
            null
        }.`when`(repo).deleteDoctor(eq(vetId), any())

        var resultSuccess = false
        var resultMessage = ""

        viewModel.deleteDoctor(vetId) { success, message ->
            resultSuccess = success
            resultMessage = message
        }

        assertTrue(resultSuccess)
        assertEquals("Veterinarian deleted successfully", resultMessage)
        verify(repo).deleteDoctor(eq(vetId), any())
    }

    @Test
    fun deleteDoctor_failure_test() {
        val vetId = "invalid_id"

        doAnswer {
            val callback = it.getArgument<(Boolean, String) -> Unit>(1)
            callback(false, "Doctor not found")
            null
        }.`when`(repo).deleteDoctor(eq(vetId), any())

        var resultSuccess = true
        var resultMessage = ""

        viewModel.deleteDoctor(vetId) { success, message ->
            resultSuccess = success
            resultMessage = message
        }

        assertFalse(resultSuccess)
        assertEquals("Doctor not found", resultMessage)
        verify(repo).deleteDoctor(eq(vetId), any())
    }
}
