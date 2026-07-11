package me.constantindev.antiantixray.Commands;

import me.constantindev.antiantixray.Etc.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class Mode2 extends Base {
    public Mode2() {
        super("mode2", new String[]{"mode2", "m2"}, "Toggles Engine Mode 2 bypass (use when server sends fake ores)");
    }

    @Override
    public void run(String[] args) {
        Config.mode2 = !Config.mode2;
        assert MinecraftClient.getInstance().player != null;
        MinecraftClient.getInstance().player.sendMessage(
            Text.of("[AAX] Mode 2 bypass " + (Config.mode2 ? "ENABLED" : "DISABLED") +
                    " — chunk scan key will now use " + (Config.mode2 ? "Mode 2" : "Mode 1") + " packets."),
            false);
        super.run(args);
    }
}
