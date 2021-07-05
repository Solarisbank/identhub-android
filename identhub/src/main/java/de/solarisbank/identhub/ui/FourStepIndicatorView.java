package de.solarisbank.identhub.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.solarisbank.sdk.core.R;


public class FourStepIndicatorView extends StepIndicator {

    public static final int FIRST_STEP = 1;
    public static final int SECOND_STEP = 2;
    public static final int THIRD_STEP = 3;
    public static final int FOURTH_STEP = 4;

    private View step1Line;
    private View step2Line;
    private View step3Line;

    private View step1Image;
    private View step2Image;
    private View step3Image;
    private View step4Image;

    private View step1Done;
    private View step2Done;
    private View step3Done;
    private View step4Done;

    private View step1Number;
    private View step2Number;
    private View step3Number;
    private View step4Number;

    public FourStepIndicatorView(@NonNull Context context) {
        super(context);
        initView();
    }

    public FourStepIndicatorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FourStepIndicatorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View inflatedView = inflate(getContext(), R.layout.layout_four_step_indicator, this);

        step1Line = inflatedView.findViewById(R.id.step1Line);
        step2Line = inflatedView.findViewById(R.id.step2Line);
        step3Line = inflatedView.findViewById(R.id.step3Line);

        step1Image = inflatedView.findViewById(R.id.step1Image);
        step2Image = inflatedView.findViewById(R.id.step2Image);
        step3Image = inflatedView.findViewById(R.id.step3Image);
        step4Image = inflatedView.findViewById(R.id.step4Image);

        step1Done = inflatedView.findViewById(R.id.step1Done);
        step2Done = inflatedView.findViewById(R.id.step2Done);
        step3Done = inflatedView.findViewById(R.id.step3Done);
        step4Done = inflatedView.findViewById(R.id.step4Done);

        step1Number = inflatedView.findViewById(R.id.step1Number);
        step2Number = inflatedView.findViewById(R.id.step2Number);
        step3Number = inflatedView.findViewById(R.id.step3Number);
        step4Number = inflatedView.findViewById(R.id.step4Number);

    }

    public void setStep(int step) {
        step1Line.setSelected(step > FIRST_STEP);
        step2Line.setSelected(step > SECOND_STEP);
        step3Line.setSelected(step > THIRD_STEP);

        switch (step) {
            case FIRST_STEP:
                step1Done.setVisibility(View.GONE);
                step1Number.setVisibility(View.VISIBLE);
                step1Image.setEnabled(true);

                step2Image.setEnabled(false);
                step2Number.setEnabled(false);

                step3Image.setEnabled(false);
                step3Number.setEnabled(false);

                step4Image.setEnabled(false);
                step4Number.setEnabled(false);
                break;
            case SECOND_STEP:
                step1Number.setVisibility(View.INVISIBLE);
                step1Image.setVisibility(View.INVISIBLE);
                step1Done.setVisibility(View.VISIBLE);

                step2Done.setVisibility(View.GONE);
                step2Number.setVisibility(View.VISIBLE);
                step2Image.setEnabled(true);

                step3Image.setEnabled(false);
                step3Number.setEnabled(false);

                step4Image.setEnabled(false);
                step4Number.setEnabled(false);
                break;
            case THIRD_STEP:
                step1Number.setVisibility(View.INVISIBLE);
                step1Image.setVisibility(View.INVISIBLE);
                step1Done.setVisibility(View.VISIBLE);

                step2Number.setVisibility(View.INVISIBLE);
                step2Image.setVisibility(View.INVISIBLE);
                step2Done.setVisibility(View.VISIBLE);

                step3Done.setVisibility(View.GONE);
                step3Number.setVisibility(View.VISIBLE);
                step3Image.setEnabled(true);

                step4Image.setEnabled(false);
                step4Number.setEnabled(false);
                break;
            case FOURTH_STEP:
                step1Number.setVisibility(View.INVISIBLE);
                step1Image.setVisibility(View.INVISIBLE);
                step1Done.setVisibility(View.VISIBLE);

                step2Number.setVisibility(View.INVISIBLE);
                step2Image.setVisibility(View.INVISIBLE);
                step2Done.setVisibility(View.VISIBLE);

                step3Number.setVisibility(View.INVISIBLE);
                step3Image.setVisibility(View.INVISIBLE);
                step3Done.setVisibility(View.VISIBLE);

                step4Done.setVisibility(View.GONE);
                step4Number.setVisibility(View.VISIBLE);
                step4Image.setEnabled(true);
                break;
        }
    }

}
