package com.novapay.app.domain.model

data class WalletAccount(
    val accountId: String,
    val accountHolder: String,
    val availableBalanceCents: Long,
    val currency: String = "USD",
    val maskedAccountNumber: String = "•••• 8421"
) {
    val formattedBalance: String
        get() = "$%.2f".format(availableBalanceCents / 100.0)
}
