package de.solarisbank.identhub.contract.sign;

import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.FocusFinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.R;
import de.solarisbank.identhub.databinding.FragmentContractSigningBinding;
import de.solarisbank.identhub.di.LibraryComponent;
import de.solarisbank.identhub.BaseFragment;
import de.solarisbank.identhub.identity.IdentityActivityViewModel;
import de.solarisbank.identhub.ui.DefaultTextWatcher;
import de.solarisbank.identhub.verfication.phone.CountDownTimeState;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import timber.log.Timber;

import static de.solarisbank.identhub.verfication.phone.VerificationPhoneViewModel.DEFAULT_STATE;
import static de.solarisbank.identhub.verfication.phone.VerificationPhoneViewModel.DISABLED_STATE;
import static de.solarisbank.identhub.verfication.phone.VerificationPhoneViewModel.ERROR_STATE;
import static de.solarisbank.identhub.verfication.phone.VerificationPhoneViewModel.SUCCESS_STATE;

public class ContractSigningFragment extends BaseFragment {
    private final List<LevelListDrawable> listLevelDrawables = new ArrayList<>();
    protected ViewModelFactory viewModelFactory;
    private Disposable disposable = Disposables.disposed();
    private FragmentContractSigningBinding binding;
    private ContractSigningViewModel viewModel;
    private IdentityActivityViewModel sharedViewModel;
    private View currentFocusedEditText;

    public static Fragment newInstance() {
        return new ContractSigningFragment();
    }

    @Override
    protected void inject(LibraryComponent component) {
        component.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentContractSigningBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        initViews();
        observeInputsState();
        observableInputs();
        observeTimer();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(ContractSigningViewModel.class);
        viewModel.startTimer();

        sharedViewModel = new ViewModelProvider(requireActivity(), viewModelFactory)
                .get(IdentityActivityViewModel.class);
    }

    private void initViews() {
        initFocusListener();
        initTextWatcher();
        initStateOfDigitInputField();

        binding.description.setText(String.format(getString(R.string.verification_phone_description), ""));

        binding.submitButton.setOnClickListener(v -> {
            viewModel.onSubmitButtonClicked();
        });

        binding.sendNewCode.setOnClickListener(v -> {
            viewModel.onSendNewCodeClicked();
        });
    }

    private void initFocusListener() {
        View.OnFocusChangeListener onFocusChangeListener = (view, hasFocus) -> {
            if (hasFocus) {
                currentFocusedEditText = view;
            }
        };

        binding.firstDigit.setOnFocusChangeListener(onFocusChangeListener);
        binding.secondDigit.setOnFocusChangeListener(onFocusChangeListener);
        binding.thirdDigit.setOnFocusChangeListener(onFocusChangeListener);
        binding.fourthDigit.setOnFocusChangeListener(onFocusChangeListener);
        binding.fifthDigit.setOnFocusChangeListener(onFocusChangeListener);
        binding.sixthDigit.setOnFocusChangeListener(onFocusChangeListener);

        binding.firstDigit.requestFocus();
    }

    private void observableInputs() {
        disposable = Observable.combineLatest(
                RxTextView.textChanges(binding.firstDigit),
                RxTextView.textChanges(binding.secondDigit),
                RxTextView.textChanges(binding.thirdDigit),
                RxTextView.textChanges(binding.fourthDigit),
                RxTextView.textChanges(binding.fifthDigit),
                RxTextView.textChanges(binding.sixthDigit),
                this::consumeDigitInputs
        )
                .subscribe(code -> viewModel.onVerificationCodeReady(code), throwable -> {
                    Timber.d("Something went wrong");
                });
    }

    private String consumeDigitInputs(
            CharSequence firstDigit,
            CharSequence secondDigit,
            CharSequence thirdDigit,
            CharSequence fourthDigit,
            CharSequence fifthDigit,
            CharSequence sixthDigit) {

        return String.valueOf(firstDigit) +
                secondDigit +
                thirdDigit +
                fourthDigit +
                fifthDigit +
                sixthDigit;
    }

    private void initTextWatcher() {
        TextWatcher textWatcher = new DefaultTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (after > count && after == 1) {
                    changeFocusToNextInput();
                }
            }
        };

        binding.firstDigit.addTextChangedListener(textWatcher);
        binding.secondDigit.addTextChangedListener(textWatcher);
        binding.thirdDigit.addTextChangedListener(textWatcher);
        binding.fourthDigit.addTextChangedListener(textWatcher);
        binding.fifthDigit.addTextChangedListener(textWatcher);
    }

    private void changeFocusToNextInput() {
        View view = FocusFinder.getInstance().findNextFocus(binding.getRoot(), currentFocusedEditText, View.FOCUS_RIGHT);
        view.requestFocus();
    }

    private void observeInputsState() {
        viewModel.getState().observe(getViewLifecycleOwner(), this::onStateOfDigitInputChanged);
    }

    private void observeTimer() {
        viewModel.getTimer().observe(getViewLifecycleOwner(), this::onDataTimerChanged);
    }

    private void onDataTimerChanged(CountDownTimeState countDownTimeState) {
        if (countDownTimeState != null) {
            binding.newCodeCounter.setVisibility(countDownTimeState.isFinish() ? View.GONE : View.VISIBLE);
            binding.sendNewCode.setVisibility(countDownTimeState.isFinish() ? View.VISIBLE : View.GONE);
            binding.newCodeCounter.setText(String.format(getString(R.string.verification_phone_request_code), countDownTimeState.getCurrentValue()));
        }
    }

    private void initStateOfDigitInputField() {
        listLevelDrawables.add((LevelListDrawable) binding.firstDigit.getBackground());
        listLevelDrawables.add((LevelListDrawable) binding.secondDigit.getBackground());
        listLevelDrawables.add((LevelListDrawable) binding.thirdDigit.getBackground());
        listLevelDrawables.add((LevelListDrawable) binding.fourthDigit.getBackground());
        listLevelDrawables.add((LevelListDrawable) binding.fifthDigit.getBackground());
        listLevelDrawables.add((LevelListDrawable) binding.sixthDigit.getBackground());
    }

    private void onStateOfDigitInputChanged(int state) {
        binding.newCodeCounter.setVisibility(state == DEFAULT_STATE ? View.VISIBLE : View.GONE);
        binding.errorMessage.setVisibility(state == ERROR_STATE ? View.VISIBLE : View.GONE);
        binding.sendNewCode.setVisibility(state == ERROR_STATE || state == DISABLED_STATE ? View.VISIBLE : View.GONE);

        binding.sendNewCode.setEnabled(state != DISABLED_STATE);
        binding.submitButton.setEnabled(state != DISABLED_STATE);
        binding.submitButton.setText(state == DISABLED_STATE ? R.string.verification_phone_status_verifying : R.string.contract_signing_preview_sign_action);

        binding.firstDigit.setEnabled(state != DISABLED_STATE);
        binding.secondDigit.setEnabled(state != DISABLED_STATE);
        binding.thirdDigit.setEnabled(state != DISABLED_STATE);
        binding.fourthDigit.setEnabled(state != DISABLED_STATE);
        binding.fifthDigit.setEnabled(state != DISABLED_STATE);
        binding.sixthDigit.setEnabled(state != DISABLED_STATE);

        for (LevelListDrawable listLevelDrawable : listLevelDrawables) {
            listLevelDrawable.setLevel(state);
        }
        if (state == DEFAULT_STATE) {
            clearInputs();
        }

        if (state == SUCCESS_STATE) {
            sharedViewModel.navigateTo(R.id.action_contractSigningFragment_to_applicationProcessingFragment);
        }
    }

    private void clearInputs() {
        binding.firstDigit.setText(null);
        binding.secondDigit.setText(null);
        binding.thirdDigit.setText(null);
        binding.fourthDigit.setText(null);
        binding.fifthDigit.setText(null);
        binding.sixthDigit.setText(null);
    }

    @Override
    public void onDestroyView() {
        disposable.dispose();
        super.onDestroyView();
    }
}
