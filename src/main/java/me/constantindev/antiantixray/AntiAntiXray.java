package me.constantindev.antiantixray;

import me.constantindev.antiantixray.Commands.Base;
import me.constantindev.antiantixray.Etc.*;
import me.constantindev.antiantixray.GUI.ProgressBar;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class AntiAntiXray implements ClientModInitializer {
    public static KeyBind rvn             = new KeyBind(Config.kcScan);
    public static KeyBind removeBlockBeta = new KeyBind(Config.kcRemove);
    public static KeyBind chunkKey        = new KeyBind(Config.kcChunk);
    public static List<RefreshingJob> jobs = new ArrayList<>();

    public static void revealNewBlocks(int rad, long delayInMS) {
        ProgressBar pbar = new ProgressBar();
        MinecraftClient.getInstance().getToastManager().add(pbar);
        RefreshingJob rfj = new RefreshingJob(new Runner(rad, delayInMS, pbar), pbar);
        jobs.add(rfj);
    }

    public static void revealChunk(long delayInMS) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;
        double todo = 16.0 * 16.0 * mc.world.getHeight();
        ProgressBar pbar = new ProgressBar(todo);
        mc.getToastManager().add(pbar);
        RefreshingJob rfj = new RefreshingJob(new ChunkRunner(delayInMS, pbar), pbar);
        jobs.add(rfj);
    }

    public static void revealChunkMode2(long delayInMS) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;
        double todo = 16.0 * 16.0 * mc.world.getHeight();
        ProgressBar pbar = new ProgressBar(todo);
        mc.getToastManager().add(pbar);
        RefreshingJob rfj = new RefreshingJob(new Mode2ChunkRunner(delayInMS, pbar), pbar);
        jobs.add(rfj);
    }

    @Override
    public void onInitializeClient() {
        Logger.info("Loading and initializing AAX...");

        ClientSendMessageEvents.ALLOW_CHAT.register(msg -> {
            if (msg.toLowerCase().startsWith(":")) {
                String[] args = msg.substring(1).trim().split(" +");
                String cmd = args[0].toLowerCase();
                Base cmd2r = Config.cmdmanager.getByName(cmd);
                if (cmd2r != null) cmd2r.run(args);
                return false;
            }
            if (msg.toLowerCase().startsWith("@aax")) {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.player != null)
                    mc.player.sendMessage(Text.literal("New prefix is :"), false);
                return false;
            }
            return true;
        });
    }
}
