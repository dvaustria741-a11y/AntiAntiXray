package me.constantindev.antiantixray.Etc;

import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ConfigHelper {

    private static File ensureConfigDir() {
        File f = new File(MinecraftClient.getInstance().runDirectory.getAbsolutePath() + "/.aaxconfig");
        if (!f.exists()) f.mkdir();
        if (!f.isDirectory()) { f.delete(); f.mkdir(); }
        return f;
    }

    private static int readKBFile(File dir, String filename, int defaultKc) throws IOException {
        File sf = new File(dir.getAbsolutePath() + "/" + filename);
        if (!sf.exists()) {
            sf.createNewFile();
            FileWriter fw = new FileWriter(sf);
            fw.write(defaultKc + "\n");
            fw.flush();
            fw.close();
        }
        StringBuilder data = new StringBuilder();
        Scanner s = new Scanner(sf);
        while (s.hasNext()) data.append(s.nextLine());
        s.close();
        try {
            return Integer.parseInt(data.toString());
        } catch (Exception exc) {
            sf.delete();
            return defaultKc;
        }
    }

    private static void writeKBFile(File dir, String filename, int kb) throws IOException {
        File sf = new File(dir.getAbsolutePath() + "/" + filename);
        if (sf.exists()) sf.delete();
        sf.createNewFile();
        FileWriter fw = new FileWriter(sf);
        fw.write(kb + "\n");
        fw.flush();
        fw.close();
    }

    public static int getScanKBFromFile()  throws IOException { return readKBFile(ensureConfigDir(), "scankb.bin",  GLFW_KEY_G); }
    public static int getRemoveKBFromFile() throws IOException { return readKBFile(ensureConfigDir(), "rmkb.bin",   GLFW_KEY_V); }
    public static int getChunkKBFromFile()  throws IOException { return readKBFile(ensureConfigDir(), "chunkkb.bin", GLFW_KEY_B); }

    public static void setScanKBToFile(int kb)   throws IOException { writeKBFile(ensureConfigDir(), "scankb.bin",  kb); }
    public static void setRemoveKBToFile(int kb) throws IOException { writeKBFile(ensureConfigDir(), "rmkb.bin",    kb); }
    public static void setChunkKBToFile(int kb)  throws IOException { writeKBFile(ensureConfigDir(), "chunkkb.bin", kb); }
}
