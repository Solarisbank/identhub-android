package de.solarisbank.identhub.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import de.solarisbank.identhub.R;

public class StepIndicatorView extends ConstraintLayout {
    public static final int FIRST_STEP = 1;
    public static final int SECOND_STEP = 2;
    public static final int THIRD_STEP = 3;
    private View step1;
    private View step2;
    private View step3;

    private TextView currentStep;
    private TextView nextStep;

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

    private void initView() {
        View inflatedView = inflate(getContext(), R.layout.layout_step_indicator, this);
        step1 = inflatedView.findViewById(R.id.step1);
        step2 = inflatedView.findViewById(R.id.step2);
        step3 = inflatedView.findViewById(R.id.step3);
        currentStep = inflatedView.findViewById(R.id.currentStep);
        nextStep = inflatedView.findViewById(R.id.nextStep);
    }

    public void setStep(int step) {
        step1.setSelected(step >= FIRST_STEP);
        step2.setSelected(step >= SECOND_STEP);
        step3.setSelected(step >= THIRD_STEP);

        switch (step) {
            case FIRST_STEP:
                currentStep.setText(R.string.identity_activity_first_step_label);
                nextStep.setText(String.format(getContext().getString(R.string.identity_activity_next_step), getContext().getString(R.string.identity_activity_second_step_label)));
                nextStep.setVisibility(View.VISIBLE);
                break;
            case SECOND_STEP:
                currentStep.setText(R.string.identity_activity_second_step_label);
                nextStep.setText(String.format(getContext().getString(R.string.identity_activity_next_step), getContext().getString(R.string.identity_activity_third_step_label)));
                nextStep.setVisibility(View.VISIBLE);
                break;
            case THIRD_STEP:
                currentStep.setText(R.string.identity_activity_third_step_label);
                nextStep.setVisibility(View.GONE);
                break;
        }
    }
}
