package com.novapay.app.presentation.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novapay.app.domain.model.Transaction
import com.novapay.app.domain.model.TransactionType

@Composable
fun WalletScreen(viewModel: WalletViewModel) {
    val state by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF090D16)
    ) {
        when (val ui = state) {
            is WalletUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF38BDF8))
                }
            }
            is WalletUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = ui.message, color = Color.Red)
                }
            }
            is WalletUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        HeaderSection(accountHolder = ui.account.accountHolder)
                    }
                    item {
                        BalanceCard(
                            balance = ui.account.formattedBalance,
                            maskedNumber = ui.account.maskedAccountNumber
                        )
                    }
                    item {
                        ActionButtonsRow(
                            onSendClicked = {
                                viewModel.sendQuickTransfer("Alex Rivera", 125.0)
                            }
                        )
                    }
                    item {
                        Text(
                            text = "Recent Transactions",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(ui.transactions) { tx ->
                        TransactionRow(transaction = tx)
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderSection(accountHolder: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        alignItems = Alignment.CenterVertically
    ) {
        Column {
            Text("Good day,", color = Color(0xFF94A3B8), fontSize = 13.sp)
            Text(accountHolder, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B)),
            contentAlignment = Alignment.Center
        ) {
            Text("NM", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BalanceCard(balance: String, maskedNumber: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF0284C7), Color(0xFF4F46E5))
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Available Balance", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    Text("NovaPay Business", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Text(
                    text = balance,
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = maskedNumber,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

@Composable
fun ActionButtonsRow(onSendClicked: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onSendClicked,
            modifier = Modifier.weight(1f).height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
        ) {
            Text("Send $125", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
        }
        Button(
            onClick = {},
            modifier = Modifier.weight(1f).height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
        ) {
            Text("Request Wire", color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun TransactionRow(transaction: Transaction) {
    val isPositive = transaction.type == TransactionType.INCOMING_TRANSFER || transaction.type == TransactionType.INVESTMENT_DIVIDEND
    val amountColor = if (isPositive) Color(0xFF10B981) else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E293B))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        alignItems = Alignment.CenterVertically
    ) {
        Column {
            Text(transaction.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(transaction.counterparty, color = Color(0xFF94A3B8), fontSize = 12.sp)
        }
        Text(
            text = transaction.formattedAmount,
            color = amountColor,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}
