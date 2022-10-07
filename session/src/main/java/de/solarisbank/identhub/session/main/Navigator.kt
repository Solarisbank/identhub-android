package de.solarisbank.identhub.session.main

import android.os.Bundle
import androidx.annotation.IdRes

interface Navigator {
    fun navigate(@IdRes navigationId: Int, bundle: Bundle? = null)
    fun onResult(result: Any?)
}