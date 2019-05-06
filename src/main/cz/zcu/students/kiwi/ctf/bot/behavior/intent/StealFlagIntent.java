package cz.zcu.students.kiwi.ctf.bot.behavior.intent;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.CTF;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.FlagInfo;
import cz.zcu.students.kiwi.ctf.bot.CTFBot;
import cz.zcu.students.kiwi.ctf.bot.behavior.Intent;
import cz.zcu.students.kiwi.ctf.bot.behavior.IntentAction;

public class StealFlagIntent extends Intent {

    @Override
    public boolean isReady(CTFBot bot) {
        return !bot.getCTF().isOurTeamCarryingEnemyFlag();
    }

    @Override
    public boolean run(IntentAction action, CTFBot bot) {
        if (!action.canNavigate(this)) {
            return false;
        }

        CTF ctfInfo = bot.getCTF();

        if (ctfInfo.isEnemyFlagHome()) {
            action.navigate(ctfInfo.getEnemyBase(), this);
            return true;
        }

        FlagInfo enemyFlag = ctfInfo.getEnemyFlag();
        if (enemyFlag.isVisible()) {
            if (ctfInfo.isEnemyFlagDropped()) {
                action.navigate(enemyFlag.getLocation(), this);
                return true;
            }
        }

        return false;
    }

    @Override
    public String getShortCode() {
        return "F-STL";
    }
}
