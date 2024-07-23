package net.wiredtomato.waygl.config

import com.google.gson.GsonBuilder
import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionFlag
import dev.isxander.yacl3.api.YetAnotherConfigLib
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
import dev.isxander.yacl3.api.controller.StringControllerBuilder
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.SerialEntry
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.text.Text
import net.wiredtomato.waygl.WayGL

class Config {
    companion object {
        val HANDLER = ConfigClassHandler.createBuilder(Config::class.java)
            .id(WayGL.id("waygl"))
            .serializer { config ->
                GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().configDir.resolve("waygl_client.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .setJson5(true)
                    .build()
            }.build()

        val YACL
            get() = generateYacl()

        val useNativeGlfw
            get() = HANDLER.instance().useNativeGlfw

        val nativeGlfwPath
            get() = HANDLER.instance().nativeGlfwPath

        fun generateYacl(): YetAnotherConfigLib {
            val useNativeGlfwOption = Option.createBuilder<Boolean>()
                .name(Text.translatable("yacl3.config.waygl:waygl.useNativeGlfw"))
                .flag(OptionFlag.GAME_RESTART)
                .controller { BooleanControllerBuilder.create(it).yesNoFormatter() }
                .binding(false, { useNativeGlfw }, { HANDLER.instance().useNativeGlfw = it })
                .build()

            val nativeGlfwPathOption = Option.createBuilder<String>()
                .name(Text.translatable("yacl3.config.waygl:waygl.useNativeGlfw"))
                .flag(OptionFlag.GAME_RESTART)
                .controller(StringControllerBuilder::create)
                .binding("/usr/lib/libglfw.so", { nativeGlfwPath }, { HANDLER.instance().nativeGlfwPath = it })
                .build()

            val yacl = YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("yacl3.config.waygl:waygl.title"))
                .save { HANDLER.save() }
                .category(
                    ConfigCategory.createBuilder()
                        .name(Text.translatable("yacl3.config.waygl:waygl.category.glfw"))
                        .option(useNativeGlfwOption)
                        .option(nativeGlfwPathOption)
                        .build()
                )

            return yacl.build()
        }
    }

    @SerialEntry
    var useNativeGlfw = false

    @SerialEntry
    var nativeGlfwPath = "/usr/lib/libglfw.so"
}
