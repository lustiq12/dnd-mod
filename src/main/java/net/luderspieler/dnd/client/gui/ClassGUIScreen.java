package net.luderspieler.dnd.client.gui;

import net.luderspieler.dnd.init.DndModScreens;
import net.luderspieler.dnd.world.inventory.ClassGUIMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ClassGUIScreen extends AbstractContainerScreen<ClassGUIMenu> implements DndModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private Button button_subclass1;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("dnd:textures/screens/class_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("dnd:textures/screens/classiconplaceholder.png");

	public ClassGUIScreen(ClassGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 425;
		this.imageHeight = 280;
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
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, IMAGE_0, this.leftPos + 23, this.topPos + 46, 0, 0, 16, 16, 16, 16);
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
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.class_gui.label_classname"), 185, 46, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.class_gui.label_fahigkeit"), 167, 64, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.class_gui.label_fahigkeit1"), 167, 82, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.class_gui.label_fahigkeit2"), 167, 100, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.class_gui.label_fahigkeit3"), 167, 118, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.class_gui.label_fahigkeit4"), 167, 136, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.class_gui.label_fahigkeit5"), 167, 154, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.class_gui.label_fahigkeit6"), 167, 172, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.class_gui.label_fahigkeit7"), 167, 190, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.class_gui.label_fahigkeit8"), 167, 208, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.class_gui.label_fahigkeit9"), 167, 226, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.class_gui.label_attribute"), 23, 73, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.class_gui.label_attribute1"), 23, 91, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.class_gui.label_attribute2"), 23, 109, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.class_gui.label_attribute3"), 23, 127, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.class_gui.label_attribute4"), 23, 145, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.class_gui.label_attribute5"), 86, 73, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.class_gui.label_attribute6"), 86, 91, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.class_gui.label_attribute7"), 86, 109, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.class_gui.label_attribute8"), 86, 127, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dnd.class_gui.label_attribute9"), 86, 145, -1, false);
	}

	@Override
	public void init() {
		super.init();
		button_subclass1 = Button.builder(Component.translatable("gui.dnd.class_gui.button_subclass1"), e -> {
		}).bounds(this.leftPos + 302, this.topPos + 64, 70, 20).build();
		this.addRenderableWidget(button_subclass1);
	}
}