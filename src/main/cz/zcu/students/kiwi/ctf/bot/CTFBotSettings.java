package cz.zcu.students.kiwi.ctf.bot;

import cz.zcu.students.kiwi.ctf.MapTweaks;

public class CTFBotSettings {
    /**
     * TRUE => attempt to auto-load level geometry on bot startup
     */
    public final boolean LOAD_LEVEL_GEOMETRY = false;
    /**
     * TRUE => draws navmesh and terminates
     */
    public final boolean DRAW_NAVMESH = false;
    /**
     * TRUE => rebinds NAVMESH+NAVIGATION GRAPH; useful when you add new map tweak into {@link MapTweaks}.
     */
    public final boolean UPDATE_NAVMESH = false;
    /**
     * Whether to draw navigation path; works only if you are running 1 bot...
     */
    public final boolean DRAW_NAVIGATION_PATH = false;
}
