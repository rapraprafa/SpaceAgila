package com.rafa.gokudodge.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.rafa.gokudodge.GokuDodge;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.foregroundFPS = 60;
        config.width = GokuDodge.WIDTH_DESKTOP;
        config.height = GokuDodge.HEIGHT_DESKTOP;
        config.resizable = true;
        new LwjglApplication(new GokuDodge(), config);
    }
}
