package cz.zcu.students.kiwi.ctf.bot.behavior;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.zcu.students.kiwi.ctf.bot.CTFBot;
import cz.zcu.students.kiwi.ctf.bot.behavior.intent.NoIntent;
import cz.zcu.students.kiwi.ctf.bot.behavior.personality.BasicPersonality;
import cz.zcu.students.kiwi.ctf.bot.behavior.personality.Personality;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public final class BehaviorManager {
    private final CTFBot bot;
    private final IntentAction action;

    private Personality personality;

    private Map<BehaviorResource, Intent> resourceMap = new EnumMap<>(BehaviorResource.class);

    public BehaviorManager(CTFBot bot) {
        this.bot = bot;
        this.personality = new BasicPersonality(this);
        this.action = new IntentAction(this, bot);
    }

    public Set<BehaviorResource> behave() {
        IntentAction action = this.getCleanAction();


        this.resourceMap.forEach((resource, intent) -> {
            if (!intent.isReady(this.bot)) {
                intent.stop(action, this.bot);
                this.resourceMap.remove(resource);
            }
        });

        this.personality.expressSelf(action, bot);

        return action.getChanges();
    }

    public boolean resourceAvailable(BehaviorResource resource, Intent taker) {
        return !this.resourceMap.containsKey(resource) || this.resourceMap.get(resource) == taker;
    }

    public boolean blockResource(BehaviorResource resource, Intent blocker) {
        return this.blockResource(resource, blocker, Overwrite.None);
    }

    public boolean blockResource(BehaviorResource resource, Intent blocker, Overwrite overwrite) {
        Intent currentResource = this.resourceMap.get(resource);
        if (currentResource == blocker) return true;
        if (currentResource == null) {
            this.resourceMap.put(resource, blocker);
            return true;
        }

        if (overwrite == Overwrite.None) return false;

        boolean replace = false;
        if (overwrite == Overwrite.Single) {
            if (!this.action.changed(BehaviorResource.Navigation)) replace = true;
            else return false;
        }
        if (overwrite == Overwrite.Force) replace = true;

        if (replace) {
            if (!currentResource.stop(this.action, this.bot)) return false;

            this.resourceMap.put(resource, blocker);
            return true;
        }

        throw new IllegalArgumentException("Invalid overwrite value: " + overwrite.name());
    }

    /*public boolean releaseResource(BehaviorResource resource) {
        if (!this.resourceMap.containsKey(resource)) {
            return false;
        }

        this.resourceMap.remove(resource).stop(this.action, this.bot);
        return true;
    }*/

    public boolean releaseResource(BehaviorResource resource, Intent holder) {
        return this.resourceMap.remove(resource, holder);
    }

    private IntentAction getCleanAction() {
        this.action.cleanChanges();
        return action;
    }

    public String getTag() {
        return "N: " + this.resourceMap.getOrDefault(BehaviorResource.Navigation, NoIntent.instance).getShortCode() +
                ", S: " + this.resourceMap.getOrDefault(BehaviorResource.Shooting, NoIntent.instance).getShortCode();
    }

    public ILocated getNavigationTarget() {
        return bot.getNavigation().getCurrentTarget();
    }

    public void navigationReached() {
        Intent intent = this.resourceMap.remove(BehaviorResource.Navigation);
        intent.stop(this.action, this.bot);
    }

    public void freeIntentsResources(Intent intent) {
        for (BehaviorResource resource : BehaviorResource.values()) {
            this.resourceMap.remove(resource, intent);
        }
    }

    public void reInit() {
        this.resourceMap.forEach((resource, intent) -> {
            intent.stop(action, bot);
            this.resourceMap.remove(resource);
        });
    }
}
