package me.constantindev.antiantixray.Etc;

import me.constantindev.antiantixray.GUI.ProgressBar;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * Scans the entire chunk column the player is currently standing in.
 * Sends ABORT_DESTROY_BLOCK packets for every matching block, forcing
 * the server to reveal blocks hidden by anti-xray.
 */
public class ChunkRunner extends Runner {

    public ChunkRunner(long delay, ProgressBar pbar) {
        super(0, delay, pbar); // rad is unused; we handle the loop ourselves
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayNetworkHandler conn = mc.getNetworkHandler();
        if (conn == null || mc.player == null || mc.world == null) return;

        BlockPos playerPos = mc.player.getBlockPos();
        // Align to chunk origin (multiple of 16)
        int chunkX = (playerPos.getX() >> 4) << 4;
        int chunkZ = (playerPos.getZ() >> 4) << 4;
        int minY   = mc.world.getBottomY();
        int maxY   = mc.world.getBottomY() + mc.world.getHeight(); // exclusive upper bound

        Block[] checkblocks = Config.checkblocks;

        for (int x = chunkX; x < chunkX + 16; x++) {
            for (int z = chunkZ; z < chunkZ + 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    if (!isRunning) return;
                    pbar.progress++;

                    if (mc.player == null || mc.world == null) return;
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = mc.world.getBlockState(pos).getBlock();

                    boolean good = Config.scanAll;
                    for (Block cb : checkblocks) {
                        if (block.equals(cb)) { good = true; break; }
                    }
                    if (!good) continue;

                    conn.sendPacket(new PlayerActionC2SPacket(
                            PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
                            pos, Direction.UP));
                    try { Thread.sleep(delay); } catch (InterruptedException ignored) {}
                }
            }
        }
        pbar.done = true;
    }
}
