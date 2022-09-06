package de.solarisbank.identhub.session.main

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object MainKoin {
    val module = module {
        viewModel { MainViewModel() }
    }
}