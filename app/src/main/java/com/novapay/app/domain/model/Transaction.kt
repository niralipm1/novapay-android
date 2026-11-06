package com.novapay.app.domain.model

enum class TransactionType {
    INCOMING_TRANSFER,
    OUTGOING_PAYMENT,
    CARD_PURCHASE,
    INVESTMENT_DIVIDEND
}

data class Transaction(
    val id: String,
    val title: String,
    val counterparty: String,
    val amountCents: Long,
    val type: TransactionType,
    val timestampEpochMs: Long,
    val category: String
) {
    val formattedAmount: String
        get() {
            val dollars = amountCents / 100.0
            val prefix = if (type == TransactionType.INCOMING_TRANSFER || type == TransactionType.INVESTMENT_DIVIDEND) "+" else "-"
            return "$prefix$%.2f".format(kotlin.math.abs(dollars))
        }
}
