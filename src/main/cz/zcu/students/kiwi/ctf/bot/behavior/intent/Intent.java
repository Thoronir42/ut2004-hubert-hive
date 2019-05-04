package cz.zcu.students.kiwi.ctf.bot.behavior.intent;

import cz.zcu.students.kiwi.ctf.bot.CTFBot;
import cz.zcu.students.kiwi.ctf.bot.behavior.BehaviorManager;

public abstract class Intent {
    public abstract boolean actOn(BehaviorManager manager, CTFBot bot);

    public boolean stop() {
        return true;
    }
}
