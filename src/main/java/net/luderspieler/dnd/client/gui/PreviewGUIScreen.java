package net.luderspieler.dnd.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;

import net.luderspieler.dnd.world.inventory.PreviewGUIMenu;
import net.luderspieler.dnd.init.DndModScreens;

public class PreviewGUIScreen extends AbstractContainerScreen<PreviewGUIMenu> implements DndModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("dnd:textures/screens/preview_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("dnd:textures/screens/classiconplaceholder.png");

	public PreviewGUIScreen(PreviewGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 400;
		this.imageHeight = 212;
	}

	@Override
	public void updateMenuState(int elementType, String name, Object elementState) {
		menuStateUpdateActive = true;
		menuStateUpdateActive = false;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, IMAGE_0, this.leftPos + 10, this.topPos + 12, 0, 0, 16, 16, 16, 16);
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		return super.keyPressed(key, b, c);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_classname"), 172, 12, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_fahigkeit"), 199, 30, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_fahigkeit1"), 199, 48, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_fahigkeit2"), 199, 66, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_fahigkeit3"), 199, 84, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_fahigkeit4"), 199, 102, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_fahigkeit5"), 199, 120, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_fahigkeit6"), 199, 138, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_fahigkeit7"), 199, 156, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_fahigkeit8"), 199, 174, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_fahigkeit9"), 199, 192, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_attribute"), 10, 39, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_attribute1"), 10, 57, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_attribute2"), 10, 75, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_attribute3"), 10, 93, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_attribute4"), 10, 111, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_attribute5"), 73, 39, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_attribute6"), 73, 57, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_attribute7"), 73, 75, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_attribute8"), 73, 93, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_attribute9"), 73, 111, -1, false);
	}

	@Override
	public void init() {
		super.init();
	}
}