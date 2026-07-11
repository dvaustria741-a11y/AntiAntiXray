package me.constantindev.antiantixray.Etc;

import me.constantindev.antiantixray.GUI.ProgressBar;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class Runner implements Runnable {
    boolean isRunning = true;
    long delay;
    int rad;
    ProgressBar pbar;

    public Runner(int rad, long delay, ProgressBar pbar) {
        this.rad   = rad;
        this.delay = delay;
        this.pbar  = pbar;
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        ClientPlayNetworkHandler conn = MinecraftClient.getInstance().getNetworkHandler();
        if (conn == null) return;
        if (MinecraftClient.getInstance().player == null) return;

        BlockPos pos        = MinecraftClient.getInstance().player.getBlockPos();
        Block[]  checkblocks = Config.checkblocks;

        for (int cx = -rad; cx <= rad; cx++) {
            for (int cy = -rad; cy <= rad; cy++) {
                for (int cz = -rad; cz <= rad; cz++) {
                    if (!isRunning) return;
                    pbar.progress++;

                    BlockPos currblock = new BlockPos(pos.getX()+cx, pos.getY()+cy, pos.getZ()+cz);
                    if (MinecraftClient.getInstance().player == null) return;

                    Block block = MinecraftClient.getInstance().player
                            .getWorld().getBlockState(currblock).getBlock();

                    boolean good = Config.scanAll;
                    for (Block cb : checkblocks) {
                        if (block.equals(cb)) { good = true; break; }
                    }
                    if (!good) continue;

                    conn.sendPacket(new PlayerActionC2SPacket(
                            PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
                            currblock, Direction.UP));
                    try { Thread.sleep(delay); } catch (InterruptedException ignored) {}
                }
            }
        }
        pbar.done = true;
    }
}
