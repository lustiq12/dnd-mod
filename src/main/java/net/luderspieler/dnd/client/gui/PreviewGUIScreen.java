package net.luderspieler.dnd.client.gui;

import net.luderspieler.dnd.init.DndModScreens;
import net.luderspieler.dnd.world.inventory.PreviewGUIMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PreviewGUIScreen extends AbstractContainerScreen<PreviewGUIMenu> implements DndModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private Button button_subclass1;
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
		this.imageHeight = 230;
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
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, IMAGE_0, this.leftPos + 10, this.topPos + 21, 0, 0, 16, 16, 16, 16);
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
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_classname"), 172, 21, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_fahigkeit"), 154, 39, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_fahigkeit1"), 154, 57, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_fahigkeit2"), 154, 75, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_fahigkeit3"), 154, 93, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_fahigkeit4"), 154, 111, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_fahigkeit5"), 154, 129, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_fahigkeit6"), 154, 147, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_fahigkeit7"), 154, 165, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_fahigkeit8"), 154, 183, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_fahigkeit9"), 154, 201, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_attribute"), 10, 48, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_attribute1"), 10, 66, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_attribute2"), 10, 84, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_attribute3"), 10, 102, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_attribute4"), 10, 120, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_attribute5"), 73, 48, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_attribute6"), 73, 66, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_attribute7"), 73, 84, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_attribute8"), 73, 102, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.preview_gui.label_attribute9"), 73, 120, -1, false);
	}

	@Override
	public void init() {
		super.init();
		button_subclass1 = Button.builder(Component.translatable("gui.dnd.preview_gui.button_subclass1"), e -> {
		}).bounds(this.leftPos + 289, this.topPos + 39, 70, 20).build();
		this.addRenderableWidget(button_subclass1);
	}
}