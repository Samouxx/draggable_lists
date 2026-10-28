package com.mrmelon54.DraggableLists.duck;

import net.minecraft.server.packs.repository.Pack;

public interface AbstractPackDuckProvider {
    Pack draggable_lists$getPack();

    void draggable_lists$moveTo(AbstractPackDuckProvider target);
}
