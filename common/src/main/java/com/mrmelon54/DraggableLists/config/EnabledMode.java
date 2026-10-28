package com.mrmelon54.DraggableLists.config;

import net.minecraft.client.Minecraft;

public enum EnabledMode {
    DISABLED,
    REQUIRES_MODIFIER,
    ENABLED;

    public boolean isEnabled() {
        return switch (this) {
            case DISABLED -> false;
            case REQUIRES_MODIFIER -> Minecraft.getInstance().hasShiftDown();
            case ENABLED -> true;
        };
    }
}
