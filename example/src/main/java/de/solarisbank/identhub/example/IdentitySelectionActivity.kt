package de.solarisbank.identhub.example

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import de.solarisbank.identhub.example.databinding.ActivityIdentitySelectionBinding
import de.solarisbank.identhub.session.IdentHub.SESSION_URL_KEY
import timber.log.Timber

class IdentitySelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIdentitySelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIsFourthlineAvailable()
        binding = ActivityIdentitySelectionBinding.inflate(layoutInflater)
        val view: ConstraintLayout = binding.root
        setContentView(view)
        initView()
    }

    private fun checkIsFourthlineAvailable() {
        val isFourthlineModuleAvailable =
                !packageManager.queryIntentActivities(
                        Intent(FOURTHLINE_FLOW_ACTIVITY_ACTION, null)
                                .apply { addCategory(Intent.CATEGORY_DEFAULT) }, 0)
                        .isNullOrEmpty()
        if (!isFourthlineModuleAvailable) {
            startActivity(
                    Intent(this, IdentHubInteractionActivity::class.java)
            )
            finish()
        }
    }

    private fun initView() {
        binding.identHubFlowButton.setOnClickListener {
            startActivity(Intent(this, IdentHubInteractionActivity::class.java))
        }
        binding.fourthlineFlowButton.setOnClickListener {
                startActivity(Intent().apply {
                    action = FOURTHLINE_FLOW_ACTIVITY_ACTION
                    Timber.d("BuildConfig.SESSION_URL : ${BuildConfig.SESSION_URL}")
                    putExtra(SESSION_URL_KEY, BuildConfig.SESSION_URL)
                })
        }
    }

    companion object {
        private const val FOURTHLINE_FLOW_ACTIVITY_ACTION = "de.solarisbank.identhub.FOURTHLINE_FLOW"
    }

}