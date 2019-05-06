package cz.zcu.students.kiwi.ctf.bot.behavior.personality;

import cz.zcu.students.kiwi.ctf.bot.CTFBot;
import cz.zcu.students.kiwi.ctf.bot.behavior.BehaviorManager;
import cz.zcu.students.kiwi.ctf.bot.behavior.BehaviorResource;
import cz.zcu.students.kiwi.ctf.bot.behavior.Intent;
import cz.zcu.students.kiwi.ctf.bot.behavior.IntentAction;
import cz.zcu.students.kiwi.ctf.bot.behavior.intent.*;

import java.util.Set;

public class BasicPersonality extends Personality {
    private BehaviorManager behavior;

    public BasicPersonality(BehaviorManager behavior) {
        this.behavior = behavior;

        this.intents.add(new DefendFlagIntent());
        this.intents.add(new StealFlagIntent());
        this.intents.add(new SecureFlagIntent());
        this.intents.add(new ShootVisibleEnemiesIntent());
        this.intents.add(new HoardItemsIntent());
    }

    @Override
    public Set<BehaviorResource> expressSelf(IntentAction action, CTFBot bot) {
        for (Intent intent : this.intents) {
            if (!intent.isReady(bot)) continue;

            if (!intent.run(action, bot)) {
                action.clearIntent(intent);
                behavior.freeIntentsResources(intent);
            }
        }

        return action.getChanges();
    }
}
