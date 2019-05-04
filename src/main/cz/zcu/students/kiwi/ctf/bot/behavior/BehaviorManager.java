package cz.zcu.students.kiwi.ctf.bot.behavior;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.zcu.students.kiwi.ctf.bot.CTFBot;
import cz.zcu.students.kiwi.ctf.bot.behavior.intent.Intent;
import cz.zcu.students.kiwi.ctf.bot.behavior.personality.BasicPersonality;
import cz.zcu.students.kiwi.ctf.bot.behavior.personality.Personality;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class BehaviorManager {
    private final CTFBot bot;
    private Personality personality;

    private Set<BehaviorResource> changes = new HashSet<>(BehaviorResource.values().length);
    private Map<BehaviorResource, Intent> resourceMap = new EnumMap<>(BehaviorResource.class);

    private ILocated navigationTarget;

    public BehaviorManager(CTFBot bot) {
        this.bot = bot;
        this.personality = new BasicPersonality();
    }

    public Set<BehaviorResource> behave() {
        changes.clear();
        this.personality.expressSelf(this, bot);

        return changes;
    }

    public boolean blockResource(BehaviorResource resource, Intent blocker) {
        return this.resourceMap.putIfAbsent(resource, blocker) == blocker;
    }

    public boolean blockResource(BehaviorResource resource, Intent blocker, boolean force) {
        if (!force) return this.blockResource(resource, blocker);

        Intent current = this.resourceMap.get(resource);
        if (current != null) {
            if (!current.stop()) return false;
        }

        this.resourceMap.put(resource, blocker);
        return true;
    }

    public boolean releaseResource(BehaviorResource resource) {
        if (!this.resourceMap.containsKey(resource)) {
            return false;
        }

        this.resourceMap.remove(resource).stop();
        return true;
    }

    public boolean releaseResource(BehaviorResource resource, Intent holder) {
        return this.resourceMap.remove(resource, holder);
    }

    public void setNavigationTarget(ILocated target) {
        if (this.navigationTarget != null && target != null && target.getLocation().getDistance(this.navigationTarget.getLocation()) > 100) {
            bot.getNavigation().stopNavigation();
        }

        this.navigationTarget = target;
        this.changes.add(BehaviorResource.Movement);
    }
}
