package cz.zcu.students.kiwi.ctf.bot.behavior.personality;

import cz.zcu.students.kiwi.ctf.bot.CTFBot;
import cz.zcu.students.kiwi.ctf.bot.behavior.BehaviorResource;
import cz.zcu.students.kiwi.ctf.bot.behavior.Intent;
import cz.zcu.students.kiwi.ctf.bot.behavior.IntentAction;

import java.util.HashSet;
import java.util.Set;

public abstract class Personality {

    protected Set<Intent> intents = new HashSet<>();

    public abstract Set<BehaviorResource> expressSelf(IntentAction action, CTFBot bot);
}
