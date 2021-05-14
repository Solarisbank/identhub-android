package de.solarisbank.identhub.example

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import de.solarisbank.identhub.example.databinding.ActivityIdentitySelectionBinding
import de.solarisbank.identhub.session.IdentHub.SESSION_URL_KEY
import timber.log.Timber


class IdentitySelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIdentitySelectionBinding
    private lateinit var urlPreferences: SharedPreferences

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
        Timber.d("initView()")
        urlPreferences = getSharedPreferences(URL_PREFERENCES, Context.MODE_PRIVATE)
        binding.sessionInputField.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Timber.d("beforeTextChanged")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Timber.d("onTextChanged")
                if (s.isNullOrEmpty()) {
                    Timber.d("onTextChanged 2")
                    binding.fourthlineFlowButton.isEnabled = false
                    binding.identHubFlowButton.isEnabled = false
                } else {
                    Timber.d("onTextChanged 3")
                    urlPreferences.edit().putString(URL_VALUE, s.toString()).apply()
                    binding.fourthlineFlowButton.isEnabled = true
                    binding.identHubFlowButton.isEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable?) {
                Timber.d("afterTextChanged")
            }
        })

        urlPreferences.getString(URL_VALUE, null)?.let {
            binding.sessionInputField.setText(it)
        }.run {
            binding.sessionInputField.setText(BuildConfig.SESSION_URL)
        }
        binding.identHubFlowButton.setOnClickListener {
            startActivity(Intent(this, IdentHubInteractionActivity::class.java)
                    .apply {
                        putExtra(SESSION_URL_KEY, urlPreferences.getString(URL_VALUE,""))
                    })
        }

        binding.fourthlineFlowButton.setOnClickListener {
            startActivity(Intent().apply {
                    action = FOURTHLINE_FLOW_ACTIVITY_ACTION
                    putExtra(SESSION_URL_KEY, urlPreferences.getString(URL_VALUE,""))
                })
        }

    }

    companion object {
        private const val FOURTHLINE_FLOW_ACTIVITY_ACTION = "de.solarisbank.identhub.FOURTHLINE_FLOW"
        private const val URL_PREFERENCES = "URL_PREFERENCES"
        private const val URL_VALUE = "URL_VALUE"
    }

}