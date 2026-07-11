package me.constantindev.antiantixray.Etc;

import me.constantindev.antiantixray.GUI.ProgressBar;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * Mode 2 chunk scanner — same ABORT_DESTROY_BLOCK packet that works on this server,
 * but queries EVERY block in the chunk column (not just known ores) so fake ores
 * get replaced by the real block the server sends back.
 */
public class Mode2ChunkRunner extends Runner {

    public Mode2ChunkRunner(long delay, ProgressBar pbar) {
        super(0, delay, pbar);
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayNetworkHandler conn = mc.getNetworkHandler();
        if (conn == null || mc.player == null || mc.world == null) return;

        BlockPos playerPos = mc.player.getBlockPos();
        int chunkX = (playerPos.getX() >> 4) << 4;
        int chunkZ = (playerPos.getZ() >> 4) << 4;
        int minY   = mc.world.getBottomY();
        int maxY   = mc.world.getBottomY() + mc.world.getHeight();

        for (int x = chunkX; x < chunkX + 16; x++) {
            for (int z = chunkZ; z < chunkZ + 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    if (!isRunning) return;
                    pbar.progress++;
                    if (mc.player == null || mc.world == null) return;

                    conn.sendPacket(new PlayerActionC2SPacket(
                            PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
                            new BlockPos(x, y, z), Direction.UP));
                    try { Thread.sleep(1L); } catch (InterruptedException ignored) {}
                }
            }
        }
        pbar.done = true;
    }
}
