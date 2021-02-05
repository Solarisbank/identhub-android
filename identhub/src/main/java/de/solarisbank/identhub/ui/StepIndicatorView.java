package de.solarisbank.identhub.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.databinding.LayoutStepIndicatorBinding;

public class StepIndicatorView extends ConstraintLayout {
    public static final int FIRST_STEP = 0;
    public static final int SECOND_STEP = 1;
    public static final int THIRD_STEP = 2;

    private LayoutStepIndicatorBinding binding;

    public StepIndicatorView(@NonNull Context context) {
        super(context);
        initView();
    }

    public StepIndicatorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public StepIndicatorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public StepIndicatorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        binding = LayoutStepIndicatorBinding.inflate(LayoutInflater.from(getContext()), this);
    }

    public void setStep(int step) {
        binding.step1.setSelected(step >= FIRST_STEP);
        binding.step2.setSelected(step >= SECOND_STEP);
        binding.step3.setSelected(step >= THIRD_STEP);

        switch (step) {
            case FIRST_STEP:
                binding.currentStep.setText(R.string.identity_activity_first_step_label);
                binding.nextStep.setText(String.format(getContext().getString(R.string.identity_activity_next_step), getContext().getString(R.string.identity_activity_second_step_label)));
                binding.nextStep.setVisibility(View.VISIBLE);
                break;
            case SECOND_STEP:
                binding.currentStep.setText(R.string.identity_activity_second_step_label);
                binding.nextStep.setText(String.format(getContext().getString(R.string.identity_activity_next_step), getContext().getString(R.string.identity_activity_third_step_label)));
                binding.nextStep.setVisibility(View.VISIBLE);
                break;
            case THIRD_STEP:
                binding.currentStep.setText(R.string.identity_activity_third_step_label);
                binding.nextStep.setVisibility(View.GONE);
                break;
        }
    }
}
