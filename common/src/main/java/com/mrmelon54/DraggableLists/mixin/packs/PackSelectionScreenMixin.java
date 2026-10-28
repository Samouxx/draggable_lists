package com.mrmelon54.DraggableLists.mixin.packs;

import com.mrmelon54.DraggableLists.DragManager;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PackSelectionScreen.class)
public abstract class PackSelectionScreenMixin {
    @Shadow
    private TransferableSelectionList selectedPackList;

    @Shadow
    private TransferableSelectionList availablePackList;

    @Inject(method = "onClose", at = @At("HEAD"))
    private void releaseDraggedPack(CallbackInfo ci) {
        if (selectedPackList.isDragging()) selectedPackList.mouseReleased(DragManager.RELEASE_OUTSIDE);
        if (availablePackList.isDragging()) availablePackList.mouseReleased(DragManager.RELEASE_OUTSIDE);
    }
}
