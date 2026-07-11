package me.constantindev.antiantixray.Mixins;

import me.constantindev.antiantixray.AntiAntiXray;
import me.constantindev.antiantixray.Etc.Config;
import me.constantindev.antiantixray.Etc.Logger;
import me.constantindev.antiantixray.Etc.RefreshingJob;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ClientPlayerEntity.class)
public class TickMixin {

    private BlockPos old;
    private int movedblocks;

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        List<RefreshingJob> nl = new ArrayList<>();
        AntiAntiXray.jobs.forEach(j -> { if (!j.progress.done) nl.add(j); });
        AntiAntiXray.jobs = nl;

        MinecraftClient mc = MinecraftClient.getInstance();

        // Scan keybind (default G) — Mode 1 radius scan
        if (AntiAntiXray.rvn.checkPressed()) {
            if (mc.player != null)
                mc.player.sendMessage(Text.literal("Refreshing blocks..."), true);
            AntiAntiXray.revealNewBlocks(Config.rad, Config.delay);
        }

        // Chunk keybind (default M) — Mode 1 or Mode 2 depending on :mode2 toggle
        if (AntiAntiXray.chunkKey.checkPressed()) {
            if (mc.player != null) {
                String modeLabel = Config.mode2 ? "Mode 2" : "Mode 1";
                mc.player.sendMessage(Text.literal("Scanning current chunk (" + modeLabel + ")..."), true);
            }
            if (Config.mode2) {
                AntiAntiXray.revealChunkMode2(Config.delay);
            } else {
                AntiAntiXray.revealChunk(Config.delay);
            }
        }

        // Remove-block keybind
        if (AntiAntiXray.removeBlockBeta.checkPressed()) {
            if (mc.crosshairTarget instanceof BlockHitResult bhr && mc.player != null && mc.world != null) {
                BlockPos b2r = bhr.getBlockPos();
                for (int cx = -3; cx <= 3; cx++) {
                    for (int cy = -3; cy <= 3; cy++) {
                        for (int cz = -3; cz <= 3; cz++) {
                            Block s = Block.getBlockFromItem(
                                mc.player.getInventory().getSelectedStack().getItem());
                            BlockState b = (s != null) ? s.getDefaultState() : Blocks.AIR.getDefaultState();
                            mc.world.setBlockState(b2r.add(cx, cy, cz), b);
                        }
                    }
                }
            }
        }

        // Auto-scan on movement
        if (Config.auto) {
            try {
                if (mc.player != null) {
                    BlockPos pos = mc.player.getBlockPos();
                    if (!pos.equals(old)) {
                        movedblocks++;
                        if (movedblocks > Config.movethreshhold && AntiAntiXray.jobs.isEmpty()) {
                            AntiAntiXray.revealNewBlocks(Config.rad, Config.delay);
                            Logger.info("Scanning new pos: " + pos.toShortString());
                            movedblocks = 0;
                        }
                    }
                    old = pos;
                }
            } catch (NullPointerException e) {
                Logger.info("Null Error");
            }
        }
    }
}
