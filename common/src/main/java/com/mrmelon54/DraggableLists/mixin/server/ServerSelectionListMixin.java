package com.mrmelon54.DraggableLists.mixin.server;

import com.mrmelon54.DraggableLists.DragItem;
import com.mrmelon54.DraggableLists.DragList;
import com.mrmelon54.DraggableLists.DragManager;
import com.mrmelon54.DraggableLists.DraggableLists;
import com.mrmelon54.DraggableLists.duck.ServerListDuckProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(ServerSelectionList.class)
@Environment(EnvType.CLIENT)
public abstract class ServerSelectionListMixin extends ObjectSelectionList<ServerSelectionList.Entry> implements DragList<ServerData, ServerSelectionList.OnlineServerEntry> {
    @Shadow
    @Final
    private JoinMultiplayerScreen screen;

    @Shadow
    public abstract void updateOnlineServers(ServerList serverList);

    @Unique
    private final DragManager<ServerData, ServerSelectionList.OnlineServerEntry> draggable_lists$dragManager = new DragManager<>(this);

    public ServerSelectionListMixin(Minecraft minecraftClient, int i, int j, int k, int l) {
        super(minecraftClient, i, j, k, l);
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
        if (DraggableLists.CONFIG.serverDraggingEnabled.isEnabled() && draggable_lists$dragManager.mouseDragged(event.x(), event.y())) return true;
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
    public DragItem<ServerData, ServerSelectionList.OnlineServerEntry> draggable_lists$getEntryAtPosition(double mouseX, double mouseY) {
        return getEntryAtPosition(mouseX, mouseY) instanceof ServerSelectionList.OnlineServerEntry onlineServerEntry ? draggable_lists$asDragItem(onlineServerEntry) : null;
    }

    @Override
    public List<DragItem<ServerData, ServerSelectionList.OnlineServerEntry>> draggable_lists$getDragItems() {
        List<DragItem<ServerData, ServerSelectionList.OnlineServerEntry>> items = new ArrayList<>();
        for (ServerSelectionList.Entry entry : children()) {
            if (entry instanceof ServerSelectionList.OnlineServerEntry onlineServerEntry) items.add(draggable_lists$asDragItem(onlineServerEntry));
        }
        return items;
    }

    @Unique
    @SuppressWarnings("unchecked")
    private static DragItem<ServerData, ServerSelectionList.OnlineServerEntry> draggable_lists$asDragItem(ServerSelectionList.OnlineServerEntry onlineServerEntry) {
        return (DragItem<ServerData, ServerSelectionList.OnlineServerEntry>) (Object) onlineServerEntry;
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
    public void draggable_lists$moveEntry(DragItem<ServerData, ServerSelectionList.OnlineServerEntry> item, int n) {
        ServerList servers = screen.getServers();
        if (servers instanceof ServerListDuckProvider duckProvider) {
            duckProvider.draggable_lists$moveItem(item, n);
            servers.save();
            updateOnlineServers(servers);
        }
    }
}
