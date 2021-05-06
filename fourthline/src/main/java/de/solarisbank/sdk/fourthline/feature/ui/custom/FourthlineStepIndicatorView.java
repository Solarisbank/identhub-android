package de.solarisbank.sdk.fourthline.feature.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import de.solarisbank.sdk.fourthline.R;

public class FourthlineStepIndicatorView extends ConstraintLayout {
    public static final int FIRST_STEP = 1;
    public static final int SECOND_STEP = 2;
    public static final int THIRD_STEP = 3;
    public static final int FOURTH_STEP = 4;
    private View step1;
    private View step2;
    private View step3;
    private View step4;

    private TextView currentStep;
    private TextView nextStep;

    public FourthlineStepIndicatorView(@NonNull Context context) {
        super(context);
        initView();
    }

    public FourthlineStepIndicatorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FourthlineStepIndicatorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View inflatedView = inflate(getContext(), R.layout.layout_fourthline_step_indicator, this);
        step1 = inflatedView.findViewById(R.id.step1);
        step2 = inflatedView.findViewById(R.id.step2);
        step3 = inflatedView.findViewById(R.id.step3);
        step4 = inflatedView.findViewById(R.id.step4);
        currentStep = inflatedView.findViewById(R.id.currentStep);
        nextStep = inflatedView.findViewById(R.id.nextStep);
    }

    public void setStep(int step) {
        step1.setSelected(step >= FIRST_STEP);
        step2.setSelected(step >= SECOND_STEP);
        step3.setSelected(step >= THIRD_STEP);
        step4.setSelected(step >= FOURTH_STEP);

        switch (step) {
            case FIRST_STEP:
//                currentStep.setText("Step1");
//                nextStep.setText("Next step2");
                nextStep.setVisibility(View.VISIBLE);
                break;
            case SECOND_STEP:
//                currentStep.setText("Step2");
//                nextStep.setText("Next step3");
                nextStep.setVisibility(View.VISIBLE);
                break;
            case THIRD_STEP:
//                currentStep.setText("Step3");
//                nextStep.setText("Next step4");
                nextStep.setVisibility(View.VISIBLE);
                break;
            case FOURTH_STEP:
//                currentStep.setText("Step4");
                nextStep.setVisibility(View.GONE);
                break;
        }
    }

    public void setFourthStep(boolean isSuccessful) {
        if (isSuccessful) {
            setStep(4);
        } else {
            step4.setBackgroundColor(getResources().getColor(R.color.ident_hub_color_error_2));
        }
    }
}
