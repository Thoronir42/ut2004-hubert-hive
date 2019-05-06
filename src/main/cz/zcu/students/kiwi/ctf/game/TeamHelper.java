package cz.zcu.students.kiwi.ctf.game;

import java.awt.*;

public class TeamHelper {

    private static TeamInfo[] teams = {
            new TeamInfo("Red", Color.RED, "Raven", "Redrum", "Ritchie", "Rooster", "Rufio"),
            new TeamInfo("Blue", Color.BLUE, "Baldy", "Bedder", "Bingy", "Bob", "Bully"),
            new TeamInfo("Green", Color.GREEN, "Garry", "Gertha", "Gix", "Goku", "Gustaf"),
            new TeamInfo("Yellow", Color.YELLOW, "Yakkuzi", "Yemen", "Yi", "Youre", "Yut"),
    };

    public static Color getTeamColor(int i) {
        return teams[i].color;
    }

    public static String getTeamName(int i) {
        return teams[i].name;
    }

    public static String getBotName(int team, int bot) {
        if (bot >= teams[team].botNames.length) {
            return teams[team].name + "-" + bot;
        }

        return teams[team].botNames[bot];
    }

    private static class TeamInfo {
        String name;
        Color color;
        String[] botNames;

        public TeamInfo(String name, Color color, String... botNames) {
            this.name = name;
            this.color = color;
            this.botNames = botNames;
        }
    }
}


