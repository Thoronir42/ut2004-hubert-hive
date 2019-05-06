package cz.zcu.students.kiwi.ctf.game;

import java.awt.*;

public class TeamHelper {

    private static TeamInfo[] teams = {
            new TeamInfo("Red", Color.RED),
            new TeamInfo("Blue", Color.BLUE),
            new TeamInfo("Green", Color.GREEN),
            new TeamInfo("Yellow", Color.YELLOW),
    };

    public static Color getTeamColor(int i) {
        return teams[i].color;
    }

    public static String getTeamName(int i) {
        return teams[i].name;
    }
}

class TeamInfo {
    String name;
    Color color;

    public TeamInfo(String name, Color color) {
        this.name = name;
        this.color = color;
    }
}
