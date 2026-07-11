package me.constantindev.antiantixray.Etc;

import me.constantindev.antiantixray.Commands.Manager;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.io.IOException;

public class Config {
    public static int rad = 5;
    public static long delay = 10;
    public static Manager cmdmanager = new Manager();
    public static boolean scanAll = false;
    public static boolean auto = false;
    public static boolean mode2 = false;
    public static int movethreshhold = 5;
    public static Block[] checkblocks = {Blocks.OBSIDIAN, Blocks.CLAY, Blocks.MOSSY_COBBLESTONE,
            Blocks.DIAMOND_ORE, Blocks.REDSTONE_ORE, Blocks.IRON_ORE, Blocks.COAL_ORE, Blocks.LAPIS_ORE,
            Blocks.GOLD_ORE, Blocks.EMERALD_ORE, Blocks.NETHER_GOLD_ORE, Blocks.NETHER_QUARTZ_ORE,
            Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.DEEPSLATE_GOLD_ORE,
            Blocks.DEEPSLATE_LAPIS_ORE, Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.DEEPSLATE_COAL_ORE,
            Blocks.DEEPSLATE_EMERALD_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.COPPER_ORE,
            Blocks.ANCIENT_DEBRIS};
    public static int kcScan;
    public static int kcRemove;
    public static int kcChunk;

    static {
        try {
            kcScan   = ConfigHelper.getScanKBFromFile();
            kcRemove = ConfigHelper.getRemoveKBFromFile();
            kcChunk  = ConfigHelper.getChunkKBFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
