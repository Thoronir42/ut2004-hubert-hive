package cz.zcu.students.kiwi.ctf.bot;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.UT2004Skins;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;

import java.util.concurrent.atomic.AtomicInteger;

public class InitializeCommandFactory {
    private CTFBotSettings settings;


    /**
     * How many bots we have started so far;
     * used to split bots into teams.
     */
    private static AtomicInteger BOT_COUNT = new AtomicInteger(0);
    /**
     * How many bots have entered RED team.
     */
    private static AtomicInteger BOT_COUNT_RED_TEAM = new AtomicInteger(0);
    /**
     * How many bots have entered BLUE team.
     */
    private static AtomicInteger BOT_COUNT_BLUE_TEAM = new AtomicInteger(0);

    public InitializeCommandFactory(CTFBotSettings settings) {
        this.settings = settings;
    }

    /**
     * @param bot - Only for setting of debug stuff `botInstance` and `botTeamInstance`
     * @return
     */
    public Initialize create(CTFBotDebug bot) {
        String targetName = "Kiwi";
        bot.botInstance = BOT_COUNT.getAndIncrement();

        int targetTeam = this.decideTeam(bot.botInstance);

        switch (targetTeam) {
            case AgentInfo.TEAM_RED:
                bot.botTeamInstance = BOT_COUNT_RED_TEAM.getAndIncrement();
                targetName += "-RED-" + bot.botTeamInstance;
                break;
            case AgentInfo.TEAM_BLUE:
                bot.botTeamInstance = BOT_COUNT_BLUE_TEAM.getAndIncrement();
                targetName += "-BLUE-" + bot.botTeamInstance;
                break;
        }

        return new Initialize()
                .setTeam(targetTeam)
                .setDesiredSkill(6)
                .setName(targetName)
                .setSkin(targetTeam == AgentInfo.TEAM_RED ? UT2004Skins.SKINS[0] : UT2004Skins.SKINS[UT2004Skins.SKINS.length - 1]);
    }

    private int decideTeam(int instanceNumber) {
        int targetTeam = AgentInfo.TEAM_RED;
        if (!this.settings.START_BOTS_IN_SINGLE_TEAM && instanceNumber % 2 == 1) {
            targetTeam = AgentInfo.TEAM_BLUE;
        }

        return targetTeam;
    }
}
