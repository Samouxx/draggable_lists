package com.mrmelon54.DraggableLists.mixin.packs;

import com.mrmelon54.DraggableLists.DragItem;
import com.mrmelon54.DraggableLists.DragList;
import com.mrmelon54.DraggableLists.DragManager;
import com.mrmelon54.DraggableLists.DraggableLists;
import com.mrmelon54.DraggableLists.duck.AbstractPackDuckProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(TransferableSelectionList.class)
@Environment(EnvType.CLIENT)
public abstract class TransferableSelectionListMixin extends ObjectSelectionList<TransferableSelectionList.Entry> implements DragList<PackSelectionModel.Entry, TransferableSelectionList.PackEntry> {
    @Unique
    private final DragManager<PackSelectionModel.Entry, TransferableSelectionList.PackEntry> draggable_lists$dragManager = new DragManager<>(this);

    public TransferableSelectionListMixin(Minecraft minecraft, int i, int j, int k, int l) {
        super(minecraft, i, j, k, l);
    }

    @Override
    protected void extractListItems(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickDelta) {
        draggable_lists$dragManager.renderListItems(guiGraphics, children(), mouseX, mouseY, tickDelta, this::extractItem);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (draggable_lists$dragManager.mouseReleased(event.x(), event.y())) return true;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (DraggableLists.CONFIG.resourcePackDraggingEnabled.isEnabled() && !isOverScrollbar(event.x(), event.y()) && draggable_lists$dragManager.mouseDragged(event.x(), event.y())) return true;
        return super.mouseDragged(event, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (draggable_lists$dragManager.isDragging()) return true;
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        super.extractWidgetRenderState(guiGraphics, mouseX, mouseY, delta);
        draggable_lists$dragManager.renderWidget(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public DragItem<PackSelectionModel.Entry, TransferableSelectionList.PackEntry> draggable_lists$getEntryAtPosition(double mouseX, double mouseY) {
        return getEntryAtPosition(mouseX, mouseY) instanceof TransferableSelectionList.PackEntry packEntry ? draggable_lists$asDragItem(packEntry) : null;
    }

    @Override
    public List<DragItem<PackSelectionModel.Entry, TransferableSelectionList.PackEntry>> draggable_lists$getDragItems() {
        List<DragItem<PackSelectionModel.Entry, TransferableSelectionList.PackEntry>> items = new ArrayList<>();
        for (TransferableSelectionList.Entry entry : children()) {
            if (entry instanceof TransferableSelectionList.PackEntry packEntry) items.add(draggable_lists$asDragItem(packEntry));
        }
        return items;
    }

    @Unique
    @SuppressWarnings("unchecked")
    private static DragItem<PackSelectionModel.Entry, TransferableSelectionList.PackEntry> draggable_lists$asDragItem(TransferableSelectionList.PackEntry packEntry) {
        return (DragItem<PackSelectionModel.Entry, TransferableSelectionList.PackEntry>) (Object) packEntry;
    }

    @Override
    public void draggable_lists$setDragging(boolean b) {
        setDragging(b);
    }

    @Override
    public int draggable_lists$getY() {
        return getY();
    }

    @Override
    public int draggable_lists$getBottom() {
        return getBottom();
    }

    @Override
    public double draggable_lists$getScrollAmount() {
        return scrollAmount();
    }

    @Override
    public void draggable_lists$setScrollAmount(double v) {
        setScrollAmount(v);
    }

    @Override
    public void draggable_lists$moveEntry(DragItem<PackSelectionModel.Entry, TransferableSelectionList.PackEntry> item, int n) {
        // Move to the pack shown at position n: the model's list may also hold packs hidden by the search box
        PackSelectionModel.Entry target = draggable_lists$getDragItems().get(n).draggable_lists$getUnderlyingData();
        if (item.draggable_lists$getUnderlyingData() instanceof AbstractPackDuckProvider moving && target instanceof AbstractPackDuckProvider targetPack) {
            moving.draggable_lists$moveTo(targetPack);
        }
    }
}
