package cz.zcu.students.kiwi.ctf.bot.behavior.intent;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.zcu.students.kiwi.ctf.bot.CTFBot;
import cz.zcu.students.kiwi.ctf.bot.behavior.Intent;
import cz.zcu.students.kiwi.ctf.bot.behavior.IntentAction;

public class ShootVisibleEnemiesIntent extends Intent {

    private Player target;

    @Override
    public boolean isReady(CTFBot bot) {
        return bot.getWeaponry().hasLoadedRangedWeapon();
    }

    @Override
    public boolean run(IntentAction action, CTFBot bot) {
        Player target = this.getTarget(bot);
        if (target == null) {
            action.stopShooting(this);
            action.stopFocus(this);

            return false;
        }

        action.focus(target, this);
        action.shoot(target, this);
        return true;
    }

    private Player getTarget(CTFBot bot) {
        if (this.target != null && this.target.isVisible()) {
            return this.target;
        }

        return this.target = bot.getPlayers().getNearestVisibleEnemy();
    }

    @Override
    public boolean stop(IntentAction action, CTFBot bot) {
        this.target = null;
        action.stopShooting(this);
        return true;
    }

    @Override
    public String getShortCode() {
        return "SVE";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getTargetName();
    }

    private String getTargetName() {
        return this.target == null ? "null" : this.target.getId().getStringId();
    }
}
