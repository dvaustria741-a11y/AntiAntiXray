package me.constantindev.antiantixray.GUI;

import me.constantindev.antiantixray.Etc.Config;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;

public class ProgressBar implements Toast {

    public boolean done     = false;
    public int     progress = 1;
    public double  todo     = Math.pow(Config.rad * 2 + 1, 3);

    private static double round(double value) {
        int scale = (int) Math.pow(10, 2);
        return (double) Math.round(value * scale) / scale;
    }

    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
        // Dark background
        context.fill(0, 0, getWidth(), getHeight(), 0xCC1A1A2E);
        context.fill(1, 1, getWidth()-1, getHeight()-1, 0xFF252545);

        String pct  = round((progress / todo) * 100) + "%";
        String line = "AAX Scan: " + pct;

        // Progress bar fill
        int barW = (int)((progress / todo) * (getWidth() - 6));
        context.fill(3, getHeight()-5, 3+barW, getHeight()-2, 0xFF4CAF50);

        // Text
        int tx = getWidth()  / 2 - textRenderer.getWidth(line) / 2;
        int ty = getHeight() / 2 - textRenderer.fontHeight / 2 - 2;
        context.drawText(textRenderer, Text.literal(line), tx, ty, 0xFFFFFF, true);
    }

    @Override
    public Visibility update(ToastManager manager, long startTime) {
        return done ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override public int getWidth()  { return 200; }
    @Override public int getHeight() { return 32;  }
}
