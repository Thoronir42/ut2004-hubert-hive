package cz.zcu.students.kiwi.ctf.bot.behavior.intent;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.zcu.students.kiwi.ctf.bot.CTFBot;
import cz.zcu.students.kiwi.ctf.bot.behavior.Intent;
import cz.zcu.students.kiwi.ctf.bot.behavior.IntentAction;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public class HoardItemsIntent extends Intent {

    private static final boolean ShowItem = false;

    private Item targetItem;

    @Override
    public boolean isReady(CTFBot bot) {
        return true;
    }

    @Override
    public boolean run(IntentAction action, CTFBot bot) {
        if (targetItem != null && !bot.can.pickNow(targetItem)) {
            targetItem = null;
        }

        if (targetItem == null) {
            Optional<Map.Entry<Item, Double>> min = bot.getItemStatus().getDistances()
                    .filter((entry) -> bot.getItems().isPickupSpawned(entry.getKey()) && bot.getItems().isPickable(entry.getKey()))
                    .min(Comparator.comparingDouble(Map.Entry::getValue));

            targetItem = min.map(Map.Entry::getKey).orElse(null);
        }

        if (targetItem != null) {
            action.navigate(targetItem, this);
            return true;
        } else {
            action.stopNavigating(this);
        }

        return false;
    }

    @Override
    public boolean stop(IntentAction action, CTFBot bot) {
        this.targetItem = null;
        return true;
    }

    @Override
    public String getShortCode() {
        String name = "HRD";
        if (ShowItem) {
            String itemName = this.targetItem == null ? "n/a" : this.targetItem.getType().getName();
            name += '[' + itemName + ']';
        }
        return name;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + this.targetItem;
    }
}
