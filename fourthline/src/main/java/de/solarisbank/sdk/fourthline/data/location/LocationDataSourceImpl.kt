package de.solarisbank.sdk.fourthline.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject
import timber.log.Timber

class LocationDataSourceImpl(private val applicationContext: Context) : LocationDataSource {

    @SuppressLint("MissingPermission")
    override fun getLocation(): Single<Location> {
        Timber.d("getLocation() 0")
        val subject: SingleSubject<Location> = SingleSubject.create()
        LocationServices
                .getFusedLocationProviderClient(applicationContext)
                .getCurrentLocation(
                        PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token
                )
                .addOnCompleteListener { task: Task<Location> ->
                    Timber.d("getLocation() 00")
                    if (task != null && task.isSuccessful && task.result != null) {
                        Timber.d("getLocation() 1")
                        val result: Location = task.result
                        subject.onSuccess(result)
                    } else if (task == null || task.result == null) {
                        Timber.d("getLocation() 2")
                        subject.onError(NullPointerException("task or task result is null"))
                    } else {
                        val exception =
                            if (task != null && task.exception != null) task.exception
                            else NullPointerException("Location task.exception is null")
                        Timber.e(exception,"getLocation() 3")
                        subject.onError(exception)
                    }
                }
        return subject
    }
}