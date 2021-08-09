package de.solarisbank.sdk.fourthline.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import de.solarisbank.sdk.fourthline.data.dto.LocationDto
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject
import timber.log.Timber

class LocationDataSourceImpl(private val applicationContext: Context) : LocationDataSource {

    private val locationManager =
        applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var locationResultSubject: SingleSubject<LocationDto>? = null
    private var count = 0

    @SuppressLint("MissingPermission")
    private fun dispatchLocationRequest() {
        Timber.d("dispatchLocationRequest() 00")
        count++
        LocationServices
            .getFusedLocationProviderClient(applicationContext).getCurrentLocation(PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener {
//                Timber.d("dispatchLocationRequest() 0: it.isFromMockProvider: ${ it?.isFromMockProvider }")
                if (it != null && !it.isFromMockProvider) {
                    Timber.d("dispatchLocationRequest() 1")
                    count = 0
                    locationResultSubject!!.onSuccess(LocationDto.SUCCESS(it))
                    locationResultSubject!!.onSuccess(LocationDto.SUCCESS(it))
                } else {
                    if (count < FETCHING_LOCATION_AMOUNT) {
                        Timber.d("dispatchLocationRequest() 2")
                        dispatchLocationRequest()
                    } else {
                        Timber.d("dispatchLocationRequest() 3")
                        count = 0
                        locationResultSubject!!.onSuccess(LocationDto.LOCATION_FETCHING_ERROR)
                    }
                }
            }
    }

    @SuppressLint("MissingPermission")
    private fun obtainLocationDto() {

        var isGpsEnabled: Boolean = false
        var isNetworkEnabled: Boolean = false
        Timber.d("obtainLocation() 0")
        try {
            Timber.d("obtainLocation() 1")
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
            Timber.d("obtainLocation() 2")
            locationResultSubject!!.onSuccess(LocationDto.LOCATION_CLIEN_NOT_ENABLED_ERROR)
        }

        try {
            Timber.d("obtainLocation() 3")
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
            Timber.d("obtainLocation() 4")
            locationResultSubject!!.onSuccess(LocationDto.NETWORK_NOT_ENABLED_ERROR)
        }

        if (!isGpsEnabled) {
            Timber.d("obtainLocation() 5")
            locationResultSubject!!.onSuccess(LocationDto.LOCATION_CLIEN_NOT_ENABLED_ERROR)
        } else if (!isNetworkEnabled) {
            Timber.d("obtainLocation() 6")
            locationResultSubject!!.onSuccess(LocationDto.NETWORK_NOT_ENABLED_ERROR)
        } else {
            Timber.d("obtainLocation() 7")
            dispatchLocationRequest()
        }
    }

    @Synchronized
    override fun getLocation(): Single<LocationDto> {
        Timber.d("getLocation() 0")
        locationResultSubject = SingleSubject.create()
        obtainLocationDto()
        return locationResultSubject!!
    }

    companion object {
        private val FETCHING_LOCATION_AMOUNT = 2
    }
}