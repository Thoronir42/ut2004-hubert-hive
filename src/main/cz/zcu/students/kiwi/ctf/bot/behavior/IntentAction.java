package cz.zcu.students.kiwi.ctf.bot.behavior;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.zcu.students.kiwi.ctf.bot.CTFBot;

import java.util.HashSet;
import java.util.Set;

public class IntentAction {
    private final BehaviorManager manager;
    private final CTFBot bot;
    private Set<BehaviorResource> changes = new HashSet<>();

    private ILocated navigationTarget;

    public IntentAction(BehaviorManager manager, CTFBot bot) {
        this.manager = manager;
        this.bot = bot;
    }

    ///
    /// Resource Usage
    ///

    // Navigation
    public boolean canNavigate(Intent intent) {
        return manager.resourceAvailable(BehaviorResource.Navigation, intent);
    }

    public void navigate(ILocated target, Intent intent) {
        this.navigate(target, intent, Overwrite.None);
    }

    public void navigate(ILocated target, Intent intent, Overwrite priority) {
        if (!this.manager.blockResource(BehaviorResource.Navigation, intent, priority)) {
            return;
        }
        bot.getLog().info("Navigating for " + intent.toString());
        this.setNavigationTarget(target);
        this.changes.add(BehaviorResource.Navigation);
    }

    public void stopNavigating(Intent intent) {
        bot.getLog().info("Stopping navigation for " + intent.toString());
        this.manager.releaseResource(BehaviorResource.Navigation, intent);
        this.bot.getNavigation().stopNavigation();
        this.changes.add(BehaviorResource.Navigation);
    }

    // Shooting
    public boolean canShoot(Intent intent) {
        return manager.resourceAvailable(BehaviorResource.Shooting, intent);
    }

    public void shoot(Player target, Intent intent) {
        this.shoot(target, intent, Overwrite.None);
    }

    public void shoot(Player target, Intent intent, Overwrite overwrite) {
        if (!this.manager.blockResource(BehaviorResource.Shooting, intent, overwrite)) {
            return;
        }

        bot.getLog().info("Shooting for " + intent.getClass().getSimpleName());
        this.bot.getShoot().shoot(bot.getWeaponPrefs(), target);
    }

    public void stopShooting(Intent intent) {
        this.manager.releaseResource(BehaviorResource.Shooting, intent);
        this.bot.getShoot().stopShooting();
        this.changes.add(BehaviorResource.Shooting);
    }

    // Focus

    public boolean canFocus(Intent intent) {
        return manager.resourceAvailable(BehaviorResource.Focus, intent);
    }

    public void focus(ILocated target, Intent intent) {
        bot.getNavigation().setFocus(target);
    }

    public void stopFocus(Intent intent) {
        manager.releaseResource(BehaviorResource.Focus, intent);
        bot.getNavigation().setFocus(null);

    }


    public void cleanChanges() {
        this.changes.clear();
    }

    public Set<BehaviorResource> getChanges() {
        return changes;
    }


    public boolean changed(BehaviorResource resource) {
        return this.changes.contains(resource);
    }


    private void setNavigationTarget(ILocated target) {
        if (this.navigationTarget != null && target != null && target.getLocation().getDistance(this.navigationTarget.getLocation()) > 100) {
            bot.getNavigation().stopNavigation();
        }

        this.navigationTarget = target;
        this.bot.getNavigation().navigate(target);
    }

    public void clearIntent(Intent intent) {
        intent.stop(this, this.bot);

    }
}
