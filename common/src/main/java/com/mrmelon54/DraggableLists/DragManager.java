package com.mrmelon54.DraggableLists;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

import java.util.List;

public class DragManager<T, E extends ObjectSelectionList.Entry<?>> {
    // A release at x = 0 is outside every list: it ends the drag without moving anything
    public static final MouseButtonEvent RELEASE_OUTSIDE = new MouseButtonEvent(0, 0, new MouseButtonInfo(0, 0));

    private final DragList<T, E> dragList;
    private DragItem<T, E> selectedItem;
    private double draggingOffsetY = 0;
    private double softScrollingTimer = 0;
    private double softScrollingOrigin = 0;

    public DragManager(DragList<T, E> dragList) {
        this.dragList = dragList;
    }

    public interface ItemRenderer<L> {
        void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickDelta, L entry);
    }

    public boolean isDragging() {
        return selectedItem != null;
    }

    public boolean mouseReleased(double mouseX, double mouseY) {
        dragList.draggable_lists$setDragging(false);
        if (selectedItem == null) return false;

        DragItem<T, E> selectedItem1 = selectedItem;
        selectedItem = null;
        softScrollingTimer = 0;

        double y = capYCoordinate((int) mouseY, true);
        DragItem<T, E> hoveredEntry = dragList.draggable_lists$getEntryAtPosition(mouseX, y);
        if (hoveredEntry == null) return false;

        T draggingData = selectedItem1.draggable_lists$getUnderlyingData();
        T hoveredData = hoveredEntry.draggable_lists$getUnderlyingData();

        // if the items are null or identical then stop here
        if (draggingData == null || hoveredData == null || draggingData == hoveredData) return false;
        dragList.draggable_lists$moveEntry(selectedItem1, getIndexFromMouseY(y));
        return true;
    }

    public boolean mouseDragged(double mouseX, double mouseY) {
        if (selectedItem != null || !isCapMouseY((int) mouseY)) return selectedItem != null;

        DragItem<T, E> item = dragList.draggable_lists$getEntryAtPosition(mouseX, mouseY);
        if (item == null) return false;

        E entry = item.draggable_lists$getUnderlyingEntry();
        // Don't grab inside the icon: its buttons keep working
        if (mouseX - entry.getX() < 32) return false;

        selectedItem = item;
        draggingOffsetY = entry.getY() - mouseY;
        dragList.draggable_lists$setDragging(true);
        softScrollingTimer = 0;
        return true;
    }

    public <L extends ObjectSelectionList.Entry<?>> void renderListItems(GuiGraphicsExtractor guiGraphics, List<L> entries, int mouseX, int mouseY, float tickDelta, ItemRenderer<L> renderer) {
        List<DragItem<T, E>> items = dragList.draggable_lists$getDragItems();
        int draggingIndex = selectedItem == null ? -1 : items.indexOf(selectedItem);
        int draggedPosition = draggingIndex < 0 ? -1 : getIndexFromMouseY(mouseY);

        for (L entry : entries) {
            int i = draggingIndex < 0 ? -1 : items.indexOf(entry);
            if (i >= 0 && i == draggingIndex) continue; // drawn under the mouse by renderWidget

            // Shift the other items to show where the dragged one will land
            int shift = i < 0 ? 0 : items.get(otherItemIndexShift(i, draggingIndex, draggedPosition)).draggable_lists$getUnderlyingEntry().getY() - entry.getY();
            int top = entry.getY() + shift;
            if (top + entry.getHeight() < dragList.draggable_lists$getY() || top > dragList.draggable_lists$getBottom()) continue;

            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(0, shift);
            renderer.render(guiGraphics, mouseX, mouseY - shift, tickDelta, entry);
            guiGraphics.pose().popMatrix();
        }
    }

    private int otherItemIndexShift(int i, int draggingIndex, int draggedPosition) {
        if (i == draggingIndex) return draggedPosition;
        if (draggingIndex < draggedPosition) return i >= draggingIndex && i <= draggedPosition ? i - 1 : i;
        return i >= draggedPosition && i <= draggingIndex ? i + 1 : i;
    }

    public void renderWidget(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        if (selectedItem == null) return;

        E entry = selectedItem.draggable_lists$getUnderlyingEntry();
        int z = Mth.floor(mouseY + draggingOffsetY);
        int y = capYCoordinate(z);
        int shift = y - entry.getY();

        guiGraphics.requestCursor(CursorTypes.RESIZE_NS);
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(0, shift);
        float shaderColorValue = 191f / 255f;
        guiGraphics.fill(entry.getContentX() - 1, entry.getContentY() - 1, entry.getContentRight() + 1, entry.getContentBottom() + 1, ARGB.colorFromFloat(0.5f, shaderColorValue, shaderColorValue, shaderColorValue));
        entry.extractContent(guiGraphics, mouseX, mouseY - shift, false, delta);
        guiGraphics.pose().popMatrix();

        if (y < z) {
            if (softScrollingTimer == 0) {
                softScrollingTimer = Util.getMillis();
                softScrollingOrigin = dragList.draggable_lists$getScrollAmount();
            }
            float f = (float) (Util.getMillis() - softScrollingTimer) / 5f;
            dragList.draggable_lists$setScrollAmount(softScrollingOrigin + f);
        } else if (y > z) {
            if (softScrollingTimer == 0) {
                softScrollingTimer = Util.getMillis();
                softScrollingOrigin = dragList.draggable_lists$getScrollAmount();
            }
            float f = (float) (Util.getMillis() - softScrollingTimer) / 5f;
            dragList.draggable_lists$setScrollAmount(softScrollingOrigin - f);
        } else {
            softScrollingTimer = 0;
        }
    }

    private int capYCoordinate(int y, boolean useScreenSpace) {
        int itemHeight = useScreenSpace || selectedItem == null ? 0 : selectedItem.draggable_lists$getUnderlyingEntry().getHeight();
        int scrollableTop = dragList.draggable_lists$getY() + 2;
        int scrollableHeight = dragList.draggable_lists$getBottom() - dragList.draggable_lists$getY() - (useScreenSpace ? 2 : itemHeight + 2);
        if (y < scrollableTop) y = scrollableTop;
        if (y > scrollableTop + scrollableHeight) y = scrollableTop + scrollableHeight;
        return y;
    }

    private int capYCoordinate(int y) {
        return capYCoordinate(y, false);
    }

    private boolean isCapMouseY(int y) {
        return capYCoordinate(y, true) == y;
    }

    private int getIndexFromMouseY(double mouseY) {
        List<DragItem<T, E>> items = dragList.draggable_lists$getDragItems();
        if (items.isEmpty()) return 0;
        E first = items.getFirst().draggable_lists$getUnderlyingEntry();
        return Mth.clamp(Mth.floor((mouseY - first.getY()) / first.getHeight()), 0, items.size() - 1);
    }
}
