package de.solarisbank.identhub.navigation;

import android.os.Bundle;

public final class NaviDirection {
    private final int actionId;
    private final Bundle args;

    public NaviDirection(int actionId, Bundle args) {
        this.actionId = actionId;
        this.args = args;
    }

    public NaviDirection(int actionId) {
        this.actionId = actionId;
        this.args = null;
    }

    public int getActionId() {
        return actionId;
    }

    public Bundle getArgs() {
        return args;
    }
}
