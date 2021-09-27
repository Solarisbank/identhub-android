package de.solarisbank.sdk.fourthline.di

import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.feature.di.internal.Provider
import java.util.*

class FourthlineViemModelMapProvider(

) : Provider<Map<Class<out ViewModel>, Provider<ViewModel>>> {

    override fun get(): Map<Class<out ViewModel>, Provider<ViewModel>> {
        val map: MutableMap<Class<out ViewModel>, Provider<ViewModel>> = LinkedHashMap(10)


        return map
    }
}