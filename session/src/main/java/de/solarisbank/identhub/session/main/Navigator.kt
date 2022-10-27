package de.solarisbank.identhub.session.main

import android.os.Bundle
import androidx.annotation.IdRes
import de.solarisbank.identhub.session.module.ModuleOutcome

interface Navigator {
    fun navigate(@IdRes navigationId: Int, bundle: Bundle? = null)
    fun onOutcome(outcome: ModuleOutcome?)
}