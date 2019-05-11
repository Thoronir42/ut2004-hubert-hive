package cz.zcu.students.kiwi.ctf.bot;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.UT2004Skins;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.zcu.students.kiwi.ctf.game.TeamHelper;

import java.util.concurrent.atomic.AtomicInteger;

public class InitializeCommandFactory {

    private int desiredTeam = -1;
    private int botSkill = 6;


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

    public void setDesiredTeam(int desiredTeam) {
        if (desiredTeam < -1 || desiredTeam > 1) {
            throw new IllegalArgumentException("Desired team argument must be -1, 0 or 1. " + desiredTeam + " given");
        }

        this.desiredTeam = desiredTeam;
    }

    public void setBotSkill(int botSkill) {
        this.botSkill = botSkill;
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
                break;
            case AgentInfo.TEAM_BLUE:
                bot.botTeamInstance = BOT_COUNT_BLUE_TEAM.getAndIncrement();
                break;
        }

        return new Initialize()
                .setTeam(targetTeam)
                .setDesiredSkill(this.botSkill)
                .setName(targetName + "-" + TeamHelper.getBotName(targetTeam, bot.botTeamInstance))
                .setSkin(targetTeam == AgentInfo.TEAM_RED ? UT2004Skins.SKINS[0] : UT2004Skins.SKINS[UT2004Skins.SKINS.length - 1]);
    }

    private int decideTeam(int instanceNumber) {
        if (this.desiredTeam != -1) return this.desiredTeam;

        return instanceNumber % 2 == 1 ? AgentInfo.TEAM_BLUE : AgentInfo.TEAM_RED;
    }
}
