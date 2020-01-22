package com.rafa.spaceagila.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.rafa.spaceagila.SpaceAgila;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.foregroundFPS = 60;
        config.width = SpaceAgila.WIDTH_DESKTOP;
        config.height = SpaceAgila.HEIGHT_DESKTOP;
        config.resizable = true;
        config.addIcon("desktop_icon32.png", Files.FileType.Internal);
        new LwjglApplication(new SpaceAgila(), config);
    }
}
