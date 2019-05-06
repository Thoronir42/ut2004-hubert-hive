package cz.zcu.students.kiwi.ctf.bot.behavior.intent;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.CTF;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.zcu.students.kiwi.ctf.bot.CTFBot;
import cz.zcu.students.kiwi.ctf.bot.behavior.Intent;
import cz.zcu.students.kiwi.ctf.bot.behavior.IntentAction;
import cz.zcu.students.kiwi.ctf.bot.behavior.Overwrite;

public class DefendFlagIntent extends Intent {

    @Override
    public boolean isReady(CTFBot bot) {
        return !bot.getCTF().isOurFlagHome();
    }

    @Override
    public boolean run(IntentAction action, CTFBot bot) {
        CTF ctfInfo = bot.getCTF();

        if (ctfInfo.getOurFlag().isVisible()) {
            if (ctfInfo.isOurFlagDropped()) {
                action.navigate(ctfInfo.getOurFlag().getLocation(), this, Overwrite.Force);
                state = "FL-GET";

                return true;
            } else if (ctfInfo.isOurFlagHeld()) {
                Player bearer = bot.getPlayers().getPlayer(ctfInfo.getOurFlag().getHolder());
                action.shoot(bearer, this, Overwrite.Force);
                state = "FB-SHT";

                return true;
            }
        } else {
            action.stopShooting(this);
        }

        if (ctfInfo.isBotCarryingEnemyFlag()) {
            action.navigate(ctfInfo.getOurBase(), this);
            state = "FL-RET";
            return true;
        } else {
            state = null;
//            action.navigate(this.recoverFlag(bot), this);
            return false;
        }
    }


    private ILocated recoverFlag(CTFBot bot) {
        NavPoint enemyBase = bot.getCTF().getEnemyBase();
        if (!bot.getVisibility().isVisible(enemyBase)) {
            return enemyBase;
        }

        return bot.getNavPoints().getRandomVisibleNavPoint();
    }

    @Override
    public String getShortCode() {
        return "DEF-" + this.state;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + this.state;
    }
}
