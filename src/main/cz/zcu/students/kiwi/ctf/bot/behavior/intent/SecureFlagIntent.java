package cz.zcu.students.kiwi.ctf.bot.behavior.intent;

import cz.zcu.students.kiwi.ctf.bot.CTFBot;
import cz.zcu.students.kiwi.ctf.bot.behavior.Intent;
import cz.zcu.students.kiwi.ctf.bot.behavior.IntentAction;
import cz.zcu.students.kiwi.ctf.bot.behavior.Overwrite;

public class SecureFlagIntent extends Intent {

    @Override
    public boolean isReady(CTFBot bot) {
        return bot.getCTF().isBotCarryingEnemyFlag();
    }

    @Override
    public boolean run(IntentAction action, CTFBot bot) {
        action.navigate(bot.getCTF().getOurBase(), this, Overwrite.Single);
        return true;
    }

    @Override
    public boolean stop(IntentAction action, CTFBot bot) {
        action.stopNavigating(this);
        return true;
    }

    @Override
    public String getShortCode() {
        return "F-SCR";
    }
}
