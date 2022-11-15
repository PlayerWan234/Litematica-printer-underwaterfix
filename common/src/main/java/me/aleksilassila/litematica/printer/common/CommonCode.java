package me.aleksilassila.litematica.printer.common;

import net.minecraft.client.MinecraftClient;

public class CommonCode {
    public static String getVersion(MinecraftClient client) {
        return client.getGameVersion();
    }

    public static String ping(String string) {
        return string;
    }
}
