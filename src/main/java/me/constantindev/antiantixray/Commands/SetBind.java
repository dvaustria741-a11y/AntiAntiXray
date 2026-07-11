package me.constantindev.antiantixray.Commands;

import me.constantindev.antiantixray.AntiAntiXray;
import me.constantindev.antiantixray.Etc.ConfigHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.io.IOException;

public class SetBind extends Base {
    public SetBind() {
        super("setbind", new String[]{"setbind", "sb", "bind"}, "Sets custom binds for scanning, removing, or chunk scanning. Usage: :setbind <scan|remove|chunk> <key>");
    }

    @Override
    public void run(String[] args) {
        assert MinecraftClient.getInstance().player != null;
        if (args.length < 3) {
            MinecraftClient.getInstance().player.sendMessage(
                Text.of("[AAX] Usage: :setbind <scan|remove|chunk> <key>"), false);
            return;
        }
        int kc = args[2].toUpperCase().charAt(0);
        switch (args[1].toLowerCase()) {
            case "scan":
                AntiAntiXray.rvn.setKeyCode(kc);
                try { ConfigHelper.setScanKBToFile(kc); } catch (IOException e) { e.printStackTrace(); }
                MinecraftClient.getInstance().player.sendMessage(
                    Text.of("[AAX] Set scan keybind to " + ((char) kc)), false);
                break;
            case "remove":
                AntiAntiXray.removeBlockBeta.setKeyCode(kc);
                try { ConfigHelper.setRemoveKBToFile(kc); } catch (IOException e) { e.printStackTrace(); }
                MinecraftClient.getInstance().player.sendMessage(
                    Text.of("[AAX] Set remove keybind to " + ((char) kc)), false);
                break;
            case "chunk":
                AntiAntiXray.chunkKey.setKeyCode(kc);
                try { ConfigHelper.setChunkKBToFile(kc); } catch (IOException e) { e.printStackTrace(); }
                MinecraftClient.getInstance().player.sendMessage(
                    Text.of("[AAX] Set chunk scan keybind to " + ((char) kc)), false);
                break;
            default:
                MinecraftClient.getInstance().player.sendMessage(
                    Text.of("[AAX] Invalid property. Choose: scan, remove, or chunk."), false);
                return;
        }
        super.run(args);
    }
}
