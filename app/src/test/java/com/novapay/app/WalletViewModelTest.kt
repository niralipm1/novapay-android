package com.novapay.app

import app.cash.turbine.test
import com.novapay.app.data.repository.WalletRepositoryImpl
import com.novapay.app.presentation.wallet.WalletUiState
import com.novapay.app.presentation.wallet.WalletViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WalletViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: WalletRepositoryImpl
    private lateinit var viewModel: WalletViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = WalletRepositoryImpl()
        viewModel = WalletViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial uiState should transition to Success with populated wallet data`() = runTest {
        viewModel.uiState.test {
            // First emit is Loading
            val first = awaitItem()
            assertTrue(first is WalletUiState.Loading || first is WalletUiState.Success)

            testDispatcher.scheduler.advanceUntilIdle()

            val success = if (first is WalletUiState.Success) first else awaitItem()
            assertTrue(success is WalletUiState.Success)
            val state = success as WalletUiState.Success
            assertEquals("Nirali Motka", state.account.accountHolder)
            assertTrue(state.transactions.isNotEmpty())
        }
    }

    @Test
    fun `sendQuickTransfer deducts balance and appends transaction`() = runTest {
        viewModel.sendQuickTransfer("Alex Rivera", 100.0)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val success = awaitItem() as WalletUiState.Success
            assertEquals("tx_01", success.transactions[1].id) // original top is now index 1
            assertEquals("Transfer to Alex Rivera", success.transactions[0].title)
        }
    }
}
