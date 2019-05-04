package cz.zcu.students.kiwi.ctf.bot.behavior.intent;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.zcu.students.kiwi.ctf.bot.CTFBot;
import cz.zcu.students.kiwi.ctf.bot.behavior.BehaviorManager;
import cz.zcu.students.kiwi.ctf.bot.behavior.BehaviorResource;

public class SecureFlagIntent extends Intent {

    private ILocated target;

    @Override
    public boolean actOn(BehaviorManager manager, CTFBot bot) {
        if (!manager.blockResource(BehaviorResource.Movement, this)) {
            return false;
        }

        if (bot.getCTF().isEnemyFlagHome()) {
            manager.setNavigationTarget(bot.getCTF().getEnemyBase());
            return true;
        } else if (!bot.getCTF().isOurFlagHome()) {
            /* todo: find our flag */
            manager.releaseResource(BehaviorResource.Movement, this);
            return false;
        } else if (bot.getCTF().isBotCarryingEnemyFlag()) {
            manager.setNavigationTarget(bot.getCTF().getOurBase());
        } else {
            manager.releaseResource(BehaviorResource.Movement, this);
            return false;
        }

        return true;
    }
}
