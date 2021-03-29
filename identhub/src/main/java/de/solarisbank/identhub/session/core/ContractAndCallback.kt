package de.solarisbank.identhub.session.core

import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY)
class ContractAndCallback<I, O>(
        val activityResultContract: ActivityResultContract<I, O>,
        val callback: (O) -> Unit
)