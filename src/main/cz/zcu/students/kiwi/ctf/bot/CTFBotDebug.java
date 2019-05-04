package cz.zcu.students.kiwi.ctf.bot;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing.UT2004Draw;

import java.awt.*;
import java.util.List;

public class CTFBotDebug {
    private static boolean navMeshDrawn = false;

    private final UT2004Draw draw;

    /**
     * 0-based; note that during the tournament all your bots will have botInstance == 0!
     */
    int botInstance = 0;

    /**
     * 0-based; note that during the tournament all your bots will have botTeamInstance == 0!
     */
    int botTeamInstance = 0;

    CTFBotDebug(UT2004Draw draw) {
        this.draw = draw;
    }

    /**
     * Synchronized spin-lock like test for static value
     *
     * @return lock-capture status
     */
    boolean tryDrawNavMesh() {
        if (botInstance != 0 || !CTFBot.SETTINGS.DRAW_NAVMESH) {
            return false;
        }

        synchronized (CTFBotDebug.class) {
            if (!CTFBotDebug.navMeshDrawn) {
                CTFBotDebug.navMeshDrawn = true;
                return true;
            }
        }

        return false;
    }

    public void drawNavigationPath(List<ILocated> path) {
        for (int i = 1; i < path.size(); ++i) {
            draw.drawLine(path.get(i - 1), path.get(i));
        }
    }

    public void drawPath(IPathFuture<? extends ILocated> pathFuture, boolean clearAll) {
        if (clearAll) {
            draw.clearAll();
        }
        List<? extends ILocated> path = pathFuture.get();
        for (int i = 1; i < path.size(); ++i) {
            draw.drawLine(path.get(i - 1), path.get(i));
        }
    }

    public void drawPath(IPathFuture<? extends ILocated> pathFuture, Color color, boolean clearAll) {
        if (clearAll) {
            draw.clearAll();
        }
        if (color == null) color = Color.WHITE;
        List<? extends ILocated> path = pathFuture.get();
        for (int i = 1; i < path.size(); ++i) {
            draw.drawLine(color, path.get(i - 1), path.get(i));
        }
    }
}
