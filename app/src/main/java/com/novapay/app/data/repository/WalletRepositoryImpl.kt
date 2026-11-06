package com.novapay.app.data.repository

import com.novapay.app.domain.model.Transaction
import com.novapay.app.domain.model.TransactionType
import com.novapay.app.domain.model.WalletAccount
import com.novapay.app.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class WalletRepositoryImpl : WalletRepository {

    private val _accountFlow = MutableStateFlow(
        WalletAccount(
            accountId = "acc_us_99214",
            accountHolder = "Nirali Motka",
            availableBalanceCents = 1845250L, // $18,452.50
            currency = "USD"
        )
    )

    private val _transactionsFlow = MutableStateFlow(
        listOf(
            Transaction(
                id = "tx_01",
                title = "Stripe Client Payout",
                counterparty = "Acme Corp US",
                amountCents = 650000L,
                type = TransactionType.INCOMING_TRANSFER,
                timestampEpochMs = System.currentTimeMillis() - 3600000,
                category = "Freelance Consulting"
            ),
            Transaction(
                id = "tx_02",
                title = "AWS Cloud Infrastructure",
                counterparty = "Amazon Web Services",
                amountCents = 14250L,
                type = TransactionType.CARD_PURCHASE,
                timestampEpochMs = System.currentTimeMillis() - 86400000,
                category = "Cloud DevOps"
            ),
            Transaction(
                id = "tx_03",
                title = "Upwork Escrow Milestone",
                counterparty = "Venture Labs Berlin",
                amountCents = 420000L,
                type = TransactionType.INCOMING_TRANSFER,
                timestampEpochMs = System.currentTimeMillis() - 172800000,
                category = "Mobile Architecture"
            ),
            Transaction(
                id = "tx_04",
                title = "GitHub Enterprise Seat",
                counterparty = "GitHub, Inc.",
                amountCents = 2100L,
                type = TransactionType.CARD_PURCHASE,
                timestampEpochMs = System.currentTimeMillis() - 259200000,
                category = "Engineering Tools"
            )
        )
    )

    override fun getAccountStream(): Flow<WalletAccount> = _accountFlow.asStateFlow()

    override fun getRecentTransactions(): Flow<List<Transaction>> = _transactionsFlow.asStateFlow()

    override suspend fun sendPayment(
        recipient: String,
        amountCents: Long,
        note: String
    ): Result<Transaction> {
        val currentAccount = _accountFlow.value
        if (currentAccount.availableBalanceCents < amountCents) {
            return Result.failure(IllegalStateException("Insufficient funds available"))
        }

        val newTx = Transaction(
            id = "tx_${UUID.randomUUID().toString().take(8)}",
            title = "Transfer to $recipient",
            counterparty = recipient,
            amountCents = amountCents,
            type = TransactionType.OUTGOING_PAYMENT,
            timestampEpochMs = System.currentTimeMillis(),
            category = note.ifEmpty { "Direct Wire" }
        )

        _accountFlow.value = currentAccount.copy(
            availableBalanceCents = currentAccount.availableBalanceCents - amountCents
        )
        _transactionsFlow.value = listOf(newTx) + _transactionsFlow.value

        return Result.success(newTx)
    }

    override suspend fun refreshWallet(): Result<Unit> {
        return Result.success(Unit)
    }
}
