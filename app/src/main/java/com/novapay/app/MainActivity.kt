package com.novapay.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.novapay.app.data.repository.WalletRepositoryImpl
import com.novapay.app.presentation.wallet.WalletScreen
import com.novapay.app.presentation.wallet.WalletViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = WalletRepositoryImpl()
        val viewModel = WalletViewModel(repository)

        setContent {
            WalletScreen(viewModel = viewModel)
        }
    }
}
