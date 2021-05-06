package de.solarisbank.sdk.fourthline.feature.ui.loaction

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import de.solarisbank.sdk.core.BaseActivity
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import timber.log.Timber

class LocationAccessFragment : FourthlineFragment() {

    private var title: TextView? = null
    private var subtitle: TextView? = null
    private var locationImage: ImageView? = null
    private var confirmButton: Button? = null
    private var progressBar: ProgressBar? = null

    private val activityViewModel: FourthlineViewModel by lazy {
        ViewModelProvider(requireActivity(), (requireActivity() as BaseActivity).viewModelFactory)
                .get(FourthlineViewModel::class.java)
    }

    private val kycSharedViewModel: KycSharedViewModel by lazy<KycSharedViewModel> {
        activityViewModels()
    }

    private var cancellationTokenSource = CancellationTokenSource()

    //todo move to location datasource with dependency injection
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext().applicationContext)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_location_access, container, false)
                .also {
                    title = it.findViewById(R.id.title)
                    subtitle = it.findViewById(R.id.subtitle)
                    locationImage = it.findViewById(R.id.locationImage)
                    confirmButton = it.findViewById(R.id.confirmButton)
                    progressBar = it.findViewById(R.id.progressBar)
                    confirmButton!!.setOnClickListener {
                        activityViewModel.navigateToKycUploadFragemnt()
                    }
                }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        obtainCurrentLocation()
    }

    //todo move to location datasource with dependency injection
    //todo move to Kyc upload service
    @SuppressLint("MissingPermission")
    private fun obtainCurrentLocation() {
        val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
                PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
        )

        currentLocationTask.addOnCompleteListener { task: Task<Location> ->
            if (task.isSuccessful) {
                val result: Location = task.result
                Timber.d("Location (success): ${result.latitude}, ${result.longitude}")
                kycSharedViewModel.updateKycLocation(result)
                progressBar!!.visibility = View.INVISIBLE
                confirmButton!!.isEnabled = true
            } else {
                val exception = task.exception
                Timber.d("Location (failure): $exception")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_CODE && (grantResults.isEmpty() || grantResults[0] != PermissionChecker.PERMISSION_GRANTED)) {
            Toast.makeText(requireContext(), "Location permission is required to proceed", Toast.LENGTH_SHORT)
                    .show()
            requestLocationPermission()
        }
    }

    override fun onDestroyView() {
        title = null
        subtitle = null
        locationImage = null
        confirmButton = null
        progressBar = null
        super.onDestroyView()
    }

    override fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

    companion object {
        private const val LOCATION_PERMISSION_CODE = 42
    }
}