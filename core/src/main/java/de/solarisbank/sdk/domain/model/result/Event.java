package de.solarisbank.sdk.domain.model.result;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public final class Event<T> {

    private final T content;

    private boolean hasBeenHandled;

    public Event(@NotNull T content) {
        this.content = content;
    }

    @Nullable
    public T getContent() {
        if (hasBeenHandled) {
            return null;
        }
        hasBeenHandled = true;
        return content;
    }

    public boolean hasBeenHandled() {
        return hasBeenHandled;
    }
}
