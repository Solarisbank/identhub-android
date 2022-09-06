package de.solarisbank.identhub.session.main

import androidx.annotation.IdRes

interface Navigator {
    fun navigate(@IdRes navigationId: Int)
}