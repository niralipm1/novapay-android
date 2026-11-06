package com.novapay.app.presentation.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novapay.app.domain.model.Transaction
import com.novapay.app.domain.model.WalletAccount
import com.novapay.app.domain.repository.WalletRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface WalletUiState {
    object Loading : WalletUiState
    data class Success(
        val account: WalletAccount,
        val transactions: List<Transaction>,
        val isTransferring: Boolean = false,
        val userNotification: String? = null
    ) : WalletUiState
    data class Error(val message: String) : WalletUiState
}

class WalletViewModel(
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _notification = MutableStateFlow<String?>(null)
    private val _isTransferring = MutableStateFlow(false)

    val uiState: StateFlow<WalletUiState> = combine(
        walletRepository.getAccountStream(),
        walletRepository.getRecentTransactions(),
        _isTransferring,
        _notification
    ) { account, transactions, transferring, notification ->
        WalletUiState.Success(
            account = account,
            transactions = transactions,
            isTransferring = transferring,
            userNotification = notification
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WalletUiState.Loading
    )

    fun sendQuickTransfer(recipient: String, amountDollars: Double) {
        val cents = (amountDollars * 100).toLong()
        viewModelScope.launch {
            _isTransferring.value = true
            val result = walletRepository.sendPayment(recipient, cents, "Instant Payment")
            _isTransferring.value = false

            result.fold(
                onSuccess = {
                    _notification.value = "Sent $%.2f to %s".format(amountDollars, recipient)
                },
                onFailure = { error ->
                    _notification.value = "Error: ${error.message}"
                }
            )
        }
    }

    fun dismissNotification() {
        _notification.value = null
    }
}
