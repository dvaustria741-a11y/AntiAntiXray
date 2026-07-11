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
        // Clean up finished jobs
        List<RefreshingJob> nl = new ArrayList<>();
        AntiAntiXray.jobs.forEach(j -> { if (!j.progress.done) nl.add(j); });
        AntiAntiXray.jobs = nl;

        MinecraftClient mc = MinecraftClient.getInstance();

        // Scan keybind
        if (AntiAntiXray.rvn.checkPressed()) {
            if (mc.player != null)
                mc.player.sendMessage(Text.literal("Refreshing blocks..."), true);
            AntiAntiXray.revealNewBlocks(Config.rad, Config.delay);
        }

        // Remove-block keybind
        if (AntiAntiXray.removeBlockBeta.checkPressed()) {
            if (mc.crosshairTarget instanceof BlockHitResult bhr && mc.player != null) {
                BlockPos b2r = bhr.getBlockPos();
                for (int cx = -3; cx <= 3; cx++) {
                    for (int cy = -3; cy <= 3; cy++) {
                        for (int cz = -3; cz <= 3; cz++) {
                            // Use the held item's block, or air
                            Block s = Block.getBlockFromItem(
                                mc.player.getInventory().getMainHandStack().getItem());
                            BlockState b = (s != null) ? s.getDefaultState() : Blocks.AIR.getDefaultState();
                            mc.player.getWorld().setBlockState(b2r.add(cx, cy, cz), b);
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
