package com.mrmelon54.DraggableLists.mixin.packs;

import com.mrmelon54.DraggableLists.duck.ResourcePackOrganizerDuckProvider;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;

@Mixin(PackSelectionModel.class)
public abstract class PackSelectionModelMixin implements ResourcePackOrganizerDuckProvider {
    @Shadow
    @Final
    Consumer<PackSelectionModel.EntryBase> onListChanged;

    @Override
    public void draggable_lists$updateSelectedList(PackSelectionModel.EntryBase entry) {
        onListChanged.accept(entry);
    }
}
