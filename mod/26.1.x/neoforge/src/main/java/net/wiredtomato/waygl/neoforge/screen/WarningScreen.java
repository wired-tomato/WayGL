package net.wiredtomato.waygl.neoforge.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.loading.FMLConfig;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public class WarningScreen extends Screen {
    private static final Component WARNING = Component.translatable("waygl.warning.early_window_active");
    private static final Component WARNING_DESC = Component.translatable("waygl.warning.early_window_active.desc");
    private static final Component CONTINUE = Component.translatable("waygl.warning.early_window_active.continue");
    private static final Component DISABLE = Component.translatable("waygl.warning.early_window_active.disable");

    private final @Nullable Screen parent;

    public WarningScreen(Component title, @Nullable Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    protected void init() {
        var warning = new MultiLineTextWidget(
                width / 2 - font.width(WARNING) / 2,
                height / 2 - 100,
                Component.translatable("waygl.warning.early_window_active"),
                font
        );

        var longestLine = font.split(WARNING_DESC, width / 2)
                .stream().map(font::width)
                .max(Comparator.naturalOrder())
                .orElseThrow();

        var desc = new MultiLineTextWidget(
                width / 2 - longestLine / 2,
                height / 2 - 80,
                WARNING_DESC,
                font
        ).setCentered(true).setMaxWidth(width / 2);

        var continueAnyways = Button.builder(CONTINUE, (it) -> {
            minecraft.setScreen(parent);
        }).pos(width / 2 - 75, height / 2 + 12).size(150, 20).build();

        var disableAndCloseGame = Button.builder(DISABLE, (it) -> {
            FMLConfig.updateConfig(FMLConfig.ConfigValue.EARLY_WINDOW_CONTROL, false);

            minecraft.stop();
        }).pos(width / 2 - 75, height / 2 - 12).size(150, 20).build();

        addRenderableWidget(warning);
        addRenderableWidget(desc);
        addRenderableWidget(continueAnyways);
        addRenderableWidget(disableAndCloseGame);
    }
}
