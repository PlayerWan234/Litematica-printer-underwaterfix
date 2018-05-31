package fi.dy.masa.litematica.gui;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.data.SchematicPlacement;
import fi.dy.masa.litematica.gui.GuiMainMenu.ButtonListenerChangeMenu;
import fi.dy.masa.litematica.gui.base.GuiLitematicaBase;
import fi.dy.masa.litematica.gui.base.GuiTextFieldNumeric;
import fi.dy.masa.litematica.gui.button.ButtonGeneric;
import fi.dy.masa.litematica.gui.button.IButtonActionListener;
import fi.dy.masa.litematica.gui.interfaces.ITextFieldListener;
import fi.dy.masa.litematica.util.PositionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;

public class GuiPlacementConfiguration extends GuiLitematicaBase
{
    private final SchematicPlacement placement;
    private int id;

    public GuiPlacementConfiguration(SchematicPlacement placement)
    {
        this.placement = placement;
        this.title = I18n.format("litematica.gui.title.configure_placement");
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.id = 0;
        int width = 120;
        int x = this.width - width - 10;
        int y = 20;

        this.createButton(x, y, width, ButtonListener.Type.REMOVE_PLACEMENT);
        y += 22;

        this.createButton(x, y, width, ButtonListener.Type.SHOW_HIDE);
        y += 32;

        String label = I18n.format("litematica.gui.label.placement_settings.placement_origin");
        this.addLabel(this.id++, x, y, width, 20, 0xFFFFFFFF, label);
        y += 20;

        this.createCoordinateInput(x, y, 70, CoordinateType.X);
        y += 20;

        this.createCoordinateInput(x, y, 70, CoordinateType.Y);
        y += 20;

        this.createCoordinateInput(x, y, 70, CoordinateType.Z);
        y += 22;

        this.createButton(x, y, width, ButtonListener.Type.MOVE_HERE);
        y += 44;

        this.createButton(x, y, width, ButtonListener.Type.ROTATE);
        y += 22;

        this.createButton(x, y, width, ButtonListener.Type.MIRROR);
        y += 22;

        this.createButton(x, y, width, ButtonListener.Type.RESET_SUB_REGIONS);

        ButtonListenerChangeMenu.ButtonType type = ButtonListenerChangeMenu.ButtonType.MAIN_MENU;
        label = I18n.format(type.getLabelKey());
        int buttonWidth = this.fontRenderer.getStringWidth(label) + 20;
        x = this.width - buttonWidth - 10;
        y = this.height - 36;
        ButtonGeneric button = new ButtonGeneric(this.id++, x, y, buttonWidth, 20, label);
        this.addButton(button, new ButtonListenerChangeMenu(type, this.parent));
    }

    private void createCoordinateInput(int x, int y, int width, CoordinateType type)
    {
        String label = type.name() + ":";
        this.addLabel(this.id++, x, y, width, 20, 0xFFFFFFFF, label);
        int offset = this.mc.fontRenderer.getStringWidth(label) + 4;

        BlockPos pos = this.placement.getOrigin();
        String text = "";

        switch (type)
        {
            case X: text = String.valueOf(pos.getX()); break;
            case Y: text = String.valueOf(pos.getY()); break;
            case Z: text = String.valueOf(pos.getZ()); break;
        }

        GuiTextFieldNumeric textField = new GuiTextFieldNumeric(this.id++, x + offset, y + 1, width, 16, this.mc.fontRenderer);
        textField.setText(text);
        TextFieldListener listener = new TextFieldListener(type, this.placement, this);
        this.addtextField(textField, listener);
    }

    private void createButton(int x, int y, int width, ButtonListener.Type type)
    {
        ButtonListener listener = new ButtonListener(type, this.placement, this);
        String label = "";

        switch (type)
        {
            case ROTATE:
            {
                String value = PositionUtils.getRotationNameShort(this.placement.getRotation());
                label = I18n.format("litematica.gui.button.rotation_value", value);
                break;
            }

            case MIRROR:
            {
                String value = PositionUtils.getMirrorName(this.placement.getMirror());
                label = I18n.format("litematica.gui.button.mirror_value", value);
                break;
            }

            case MOVE_HERE:
                label = I18n.format("litematica.gui.button.move_here");
                break;

            case SHOW_HIDE:
                if (this.placement.isEnabled())
                    label = I18n.format("litematica.gui.button.disable");
                else
                    label = I18n.format("litematica.gui.button.enable");
                break;

            case RESET_SUB_REGIONS:
                if (this.placement.isRegionPlacementModified())
                {
                    label = TXT_ORANGE + I18n.format("litematica.gui.button.reset_sub_region_placements") + TXT_RST;
                }
                else
                {
                    label = I18n.format("litematica.gui.button.reset_sub_region_placements");
                    ButtonGeneric button = new ButtonGeneric(this.id++, x, y, width, 20, label);
                    button.enabled = false;
                    this.addButton(button, listener);
                    return;
                }
                break;

            case REMOVE_PLACEMENT:
                label = TXT_RED + I18n.format("litematica.gui.button.remove_placement") + TXT_RST;
                break;
        }

        this.addButton(new ButtonGeneric(this.id++, x, y, width, 20, label), listener);
    }

    private static class ButtonListener implements IButtonActionListener<ButtonGeneric>
    {
        private final GuiLitematicaBase parent;
        private final SchematicPlacement placement;
        private final Type type;

        public ButtonListener(Type type, SchematicPlacement placement, GuiLitematicaBase parent)
        {
            this.parent = parent;
            this.placement = placement;
            this.type = type;
        }

        @Override
        public void actionPerformed(ButtonGeneric control)
        {
        }

        @Override
        public void actionPerformedWithButton(ButtonGeneric control, int mouseButton)
        {
            Minecraft mc = Minecraft.getMinecraft();

            switch (this.type)
            {
                case ROTATE:
                {
                    boolean reverse = mouseButton == 1;
                    this.placement.setRotation(PositionUtils.cycleRotation(this.placement.getRotation(), reverse));
                    break;
                }

                case MIRROR:
                {
                    boolean reverse = mouseButton == 1;
                    this.placement.setMirror(PositionUtils.cycleMirror(this.placement.getMirror(), reverse));
                    break;
                }

                case MOVE_HERE:
                    BlockPos pos = new BlockPos(mc.player.getPositionVector());
                    this.placement.setOrigin(pos);
                    break;

                case SHOW_HIDE:
                    this.placement.toggleEnabled();
                    break;

                case RESET_SUB_REGIONS:
                    this.placement.resetSubRegionsToSchematicValues();
                    break;

                case REMOVE_PLACEMENT:
                    DataManager.getInstance(mc.world).getSchematicPlacementManager().removeSchematicPlacement(this.placement);
                    mc.displayGuiScreen(null);
                    break;
            }

            this.parent.initGui(); // Re-create buttons/text fields
        }

        public enum Type
        {
            ROTATE,
            MIRROR,
            MOVE_HERE,
            SHOW_HIDE,
            RESET_SUB_REGIONS,
            REMOVE_PLACEMENT;
        }
    }

    private static class TextFieldListener implements ITextFieldListener<GuiTextField>
    {
        private final GuiLitematicaBase parent;
        private final SchematicPlacement placement;
        private final CoordinateType type;

        public TextFieldListener(CoordinateType type, SchematicPlacement placement, GuiLitematicaBase parent)
        {
            this.parent = parent;
            this.placement = placement;
            this.type = type;
        }

        @Override
        public boolean onGuiClosed(GuiTextField textField)
        {
            return this.onTextChange(textField);
        }

        @Override
        public boolean onTextChange(GuiTextField textField)
        {
            try
            {
                int value = Integer.parseInt(textField.getText());
                BlockPos posOld = this.placement.getOrigin();

                switch (this.type)
                {
                    case X: this.placement.setOrigin(new BlockPos(value, posOld.getY(), posOld.getZ())); break;
                    case Y: this.placement.setOrigin(new BlockPos(posOld.getX(), value, posOld.getZ())); break;
                    case Z: this.placement.setOrigin(new BlockPos(posOld.getX(), posOld.getY(), value)); break;
                }
            }
            catch (NumberFormatException e)
            {
                //this.parent.addGuiMessage(InfoType.WARNING, I18n.format("litematica.message.warning.invalid_number", textField.getText()));
            }

            return false;
        }
    }

    public enum CoordinateType
    {
        X,
        Y,
        Z
    }
}
