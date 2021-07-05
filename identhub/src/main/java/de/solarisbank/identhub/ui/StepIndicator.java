package de.solarisbank.identhub.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

public abstract class StepIndicator extends ConstraintLayout {
    public StepIndicator(Context context) {
        super(context);
    }

    public StepIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StepIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    abstract public void setStep(int step);
}
