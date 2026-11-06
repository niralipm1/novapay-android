package com.novapay.app.domain.repository

import com.novapay.app.domain.model.Transaction
import com.novapay.app.domain.model.WalletAccount
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun getAccountStream(): Flow<WalletAccount>
    fun getRecentTransactions(): Flow<List<Transaction>>
    suspend fun sendPayment(recipient: String, amountCents: Long, note: String): Result<Transaction>
    suspend fun refreshWallet(): Result<Unit>
}
