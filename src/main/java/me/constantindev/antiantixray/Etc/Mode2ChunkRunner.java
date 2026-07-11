package me.constantindev.antiantixray.Etc;

import me.constantindev.antiantixray.GUI.ProgressBar;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/**
 * Engine Mode 2 bypass.
 *
 * Mode 2 servers send FAKE ores everywhere to confuse clients.
 * We bypass this by sending both START_DESTROY_BLOCK and
 * PlayerInteractBlockC2SPacket for every block in the chunk column,
 * forcing the server to respond with real block data via block update packets.
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
                    BlockPos pos = new BlockPos(x, y, z);

                    // START_DESTROY_BLOCK triggers server-side proximity check
                    conn.sendPacket(new PlayerActionC2SPacket(
                            PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
                            pos, Direction.UP));

                    // PlayerInteractBlock (right-click) forces server to
                    // re-evaluate and send back the real block state
                    conn.sendPacket(new PlayerInteractBlockC2SPacket(
                            Hand.MAIN_HAND,
                            new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false),
                            0));

                    // ABORT so we don't actually break anything
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
