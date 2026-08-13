package com.harvestpay.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.harvestpay.app.ui.navigation.HarvestPayRoot
import com.harvestpay.app.ui.theme.HarvestPayTheme

class MainActivity : ComponentActivity() {
    private val viewModel: HarvestViewModel by viewModels {
        HarvestViewModel.Factory(application as HarvestPayApplication)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            val auth by viewModel.auth.collectAsStateWithLifecycle()
            HarvestPayTheme(settings.theme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (!auth.resolved) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        HarvestPayRoot(viewModel = viewModel, auth = auth)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.onForeground()
    }

    override fun onStop() {
        viewModel.onBackground()
        super.onStop()
    }
}
