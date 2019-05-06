package cz.zcu.students.kiwi.ctf.bot.behavior.intent;

import cz.zcu.students.kiwi.ctf.bot.CTFBot;
import cz.zcu.students.kiwi.ctf.bot.behavior.Intent;
import cz.zcu.students.kiwi.ctf.bot.behavior.IntentAction;

public class NoIntent extends Intent {

    public static final NoIntent instance = new NoIntent();

    private NoIntent() {
    }

    @Override
    public boolean run(IntentAction action, CTFBot bot) {
        return false;
    }

    @Override
    public boolean isReady(CTFBot bot) {
        return false;
    }

    @Override
    public String getShortCode() {
        return "n/a";
    }
}
