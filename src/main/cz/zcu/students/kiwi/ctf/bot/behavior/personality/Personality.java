package cz.zcu.students.kiwi.ctf.bot.behavior.personality;

import cz.zcu.students.kiwi.ctf.bot.CTFBot;
import cz.zcu.students.kiwi.ctf.bot.behavior.BehaviorManager;

public abstract class Personality {
    public abstract void expressSelf(BehaviorManager manager, CTFBot bot);
}
