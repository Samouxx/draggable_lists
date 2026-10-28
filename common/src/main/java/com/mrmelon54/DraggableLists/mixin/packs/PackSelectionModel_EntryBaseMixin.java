package com.mrmelon54.DraggableLists.mixin.packs;

import com.mrmelon54.DraggableLists.duck.AbstractPackDuckProvider;
import com.mrmelon54.DraggableLists.duck.ResourcePackOrganizerDuckProvider;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(PackSelectionModel.EntryBase.class)
public abstract class PackSelectionModel_EntryBaseMixin implements AbstractPackDuckProvider {
    @Shadow
    protected abstract List<Pack> getSelfList();

    @Shadow
    @Final
    private Pack pack;

    @Shadow
    @Dynamic("field_25460 is provided by PackSelectionModel.EntryBase but has no mapping")
    @Final
    private PackSelectionModel field_25460;

    @Override
    public Pack draggable_lists$getPack() {
        return pack;
    }

    @Override
    public void draggable_lists$moveTo(AbstractPackDuckProvider target) {
        if (pack.isFixedPosition()) return;

        List<Pack> list = getSelfList();
        int j = list.indexOf(target.draggable_lists$getPack());
        if (j < 0) return;
        list.remove(pack);
        list.add(j, pack);

        // get all fixed position resource packs, remove them and add them all at the end
        List<Pack> fixedPacks = list.stream().filter(Pack::isFixedPosition).toList();
        list.removeAll(fixedPacks);
        list.addAll(fixedPacks);

        if (field_25460 instanceof ResourcePackOrganizerDuckProvider duckProvider)
            duckProvider.draggable_lists$updateSelectedList((PackSelectionModel.EntryBase) (Object) this);
    }
}
