package de.solarisbank.identhub.verfication.phone;

public class CountDownTimeState {
    private final String currentValue;
    private final boolean isFinish;

    public CountDownTimeState(String currentValue, boolean isFinish) {
        this.currentValue = currentValue;
        this.isFinish = isFinish;
    }

    public CountDownTimeState(String currentValue) {
        this(currentValue, false);
    }

    public CountDownTimeState(boolean isFinish) {
        this("", isFinish);
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public boolean isFinish() {
        return isFinish;
    }
}

