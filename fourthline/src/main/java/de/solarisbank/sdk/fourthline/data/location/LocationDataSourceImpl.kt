package de.solarisbank.sdk.fourthline.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject
import timber.log.Timber

class LocationDataSourceImpl(private val applicationContext: Context) : LocationDataSource {

    private val subject: SingleSubject<Location> = SingleSubject.create()
    private var count = 0

    @SuppressLint("MissingPermission")
    fun obtainLocation() {
        count++
        LocationServices
            .getFusedLocationProviderClient(applicationContext)
            .getCurrentLocation(
                PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token
            )
            .addOnSuccessListener {
                Timber.d("obtainLocation() 0: it.isFromMockProvider: ${ it.isFromMockProvider }")
                if (it != null && !it.isFromMockProvider) {
                    Timber.d("obtainLocation() 1")
                    count = 0
                    subject.onSuccess(it)
                } else {
                    if (count < FETCHING_LOCATION_AMOUNT) {
                        Timber.d("obtainLocation() 2")
                        obtainLocation()
                    } else {
                        Timber.d("obtainLocation() 3")
                        count = 0
                        subject.onError(NullPointerException("location is null"))
                    }
                }
            }
    }

    override fun getLocation(): Single<Location> {
        Timber.d("getLocation() 0")
        obtainLocation()
        return subject
    }

    companion object {
        private val FETCHING_LOCATION_AMOUNT = 5
    }
}