package com.mrmelon54.DraggableLists;

import net.minecraft.client.gui.components.ObjectSelectionList;

public interface DragItem<T, E extends ObjectSelectionList.Entry<?>> {
    T draggable_lists$getUnderlyingData();

    E draggable_lists$getUnderlyingEntry();
}
