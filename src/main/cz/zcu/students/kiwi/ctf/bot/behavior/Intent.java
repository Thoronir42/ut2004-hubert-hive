package cz.zcu.students.kiwi.ctf.bot.behavior;

import cz.zcu.students.kiwi.ctf.bot.CTFBot;

public abstract class Intent {

    protected String state;

    public boolean isReady(CTFBot bot) {
        return true;
    }

    public abstract boolean run(IntentAction action, CTFBot bot);

    public boolean stop(IntentAction action, CTFBot bot) {
        return true;
    }

    public String getShortCode() {
        return getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
