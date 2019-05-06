package cz.zcu.students.kiwi.ctf.bot;

import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Items;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathPlanner.AStar.NavMeshAStarPathPlanner;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class CTFBotItemStatus {

    private final CTFBot bot;
    private final Items items;

    private final Map<Item, Double> itemDistances;
    private final TabooSet<Item> forbiddenItems;


    public CTFBotItemStatus(CTFBot bot, Items items) {
        this.bot = bot;
        this.items = items;
        this.itemDistances = new HashMap<>();
        this.forbiddenItems = new TabooSet<>(bot.getBot());
    }

    public Stream<Map.Entry<Item, Double>> getDistances() {
        if (this.itemDistances.isEmpty()) {
            this.recalculateDistances();
        }

        return this.itemDistances.entrySet().stream();
    }

    public void clear() {
        this.itemDistances.clear();
    }

    private void recalculateDistances() {
        Location location = bot.getInfo().getLocation();
        if(location == null) {
            bot.getLog().severe("I HAVE NO LOCATION");
            return;
        }

        bot.getItems().getAllItems().values().stream()
                .filter((item) -> !this.forbiddenItems.contains(item))
                .forEach((item) -> {
                    double distance = bot.getNavMeshModule().getAStarPathPlanner().getDistance(location, item);
                    this.itemDistances.put(item, distance);
                });
    }


    public void forbid(Item targetItem) {
        this.forbiddenItems.add(targetItem);
    }

    public void forbid(Item item, int duration) {
        this.forbiddenItems.add(item, duration);
    }
}
