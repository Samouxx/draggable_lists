package com.mrmelon54.DraggableLists;

import net.minecraft.client.gui.components.ObjectSelectionList;

import java.util.List;

public interface DragList<T, E extends ObjectSelectionList.Entry<?>> {
    DragItem<T, E> draggable_lists$getEntryAtPosition(double mouseX, double mouseY);

    // The entries that can be dragged, in display order (list headers and LAN servers are left out)
    List<DragItem<T, E>> draggable_lists$getDragItems();

    void draggable_lists$setDragging(boolean b);

    int draggable_lists$getY();

    int draggable_lists$getBottom();

    double draggable_lists$getScrollAmount();

    void draggable_lists$setScrollAmount(double v);

    void draggable_lists$moveEntry(DragItem<T, E> item, int n);
}
