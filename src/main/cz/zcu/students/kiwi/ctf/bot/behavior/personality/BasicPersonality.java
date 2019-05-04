package cz.zcu.students.kiwi.ctf.bot.behavior.personality;

import cz.zcu.students.kiwi.ctf.bot.CTFBot;
import cz.zcu.students.kiwi.ctf.bot.behavior.BehaviorManager;
import cz.zcu.students.kiwi.ctf.bot.behavior.intent.SecureFlagIntent;

public class BasicPersonality extends Personality {

    private SecureFlagIntent secureFlag = new SecureFlagIntent();

    @Override
    public void expressSelf(BehaviorManager manager, CTFBot bot) {
        // todo: expand
        this.secureFlag.actOn(manager, bot);
    }
}
