package cz.zcu.students.kiwi.ctf.bot;

import cz.cuni.amis.pathfinding.alg.astar.AStarResult;
import cz.cuni.amis.pathfinding.map.IPFMapView;
import cz.cuni.amis.pogamut.base.agent.navigation.impl.PrecomputedPathFuture;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.AnnotationListenerRegistrator;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectClassEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry.RayCastResult;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.NavMeshClearanceComputer.ClearanceLimit;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathfollowing.NavMeshNavigation;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.*;
import cz.cuni.amis.pogamut.ut2004.teamcomm.bot.UT2004BotTCController;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.zcu.students.kiwi.ctf.MapTweaks;
import cz.zcu.students.kiwi.ctf.bot.behavior.BehaviorManager;
import cz.zcu.students.kiwi.ctf.bot.behavior.BehaviorResource;
import cz.zcu.students.kiwi.ctf.mapView.StandardMapView;
import cz.zcu.students.kiwi.ctf.tc.CTFCommItems;
import cz.zcu.students.kiwi.ctf.tc.CTFCommObjectUpdates;
import math.geom2d.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * CTF BOT TEMPLATE CLASS
 * Version: 0.0.1
 */
@AgentScoped
public class CTFBot extends UT2004BotTCController<UT2004Bot> {

    public static CTFBotSettings SETTINGS = new CTFBotSettings();
    public static InitializeCommandFactory INITIALIZE_FACTORY;

    private CTFCommItems<CTFBot> commItems;
    private CTFCommObjectUpdates<CTFBot> commObjectUpdates;

    protected CTFBotDebug debug;
    protected CTFBotLifeCycle lifeCycle;
    private BehaviorManager behavior;

    private NavPoint lastAStarTarget = null;


    // ==========================
    // EVENT LISTENERS / HANDLERS
    // ==========================
    private IPFMapView<NavPoint> mapView = new StandardMapView();


    /**
     * Bot's preparation - called before the bot is connected to GB2004 and launched into UT2004.
     */
    @Override
    public void prepareBot(UT2004Bot bot) {
        // DEFINE WEAPON PREFERENCES
        initWeaponPreferences();

        // INITIALIZATION OF COMM MODULES
        commItems = new CTFCommItems<CTFBot>(this);
        commObjectUpdates = new CTFCommObjectUpdates<CTFBot>(this);
        this.behavior = new BehaviorManager(this);
    }

    @Override
    protected void initializeModules(UT2004Bot bot) {
        super.initializeModules(bot);

        this.debug = new CTFBotDebug(this, this.draw);
        this.lifeCycle = new CTFBotLifeCycle();

        levelGeometryModule.setAutoLoad(SETTINGS.LOAD_LEVEL_GEOMETRY);
    }

    /**
     * This is a place where you should use map tweaks, i.e., patch original Navigation Graph that comes from UT2004.
     */
    @Override
    public void mapInfoObtained() {
        // See {@link MapTweaks} for details; add tweaks in there if required.
        MapTweaks.tweak(navBuilder);
        navMeshModule.setReloadNavMesh(SETTINGS.UPDATE_NAVMESH);
    }

    /**
     * Define your weapon preferences here (if you are going to use weaponPrefs).
     * <p>
     * For more info, see slides (page 8): http://diana.ms.mff.cuni.cz/pogamut_files/lectures/2010-2011/Pogamut3_Lecture_03.pdf
     */
    private void initWeaponPreferences() {
        weaponPrefs.addGeneralPref(UT2004ItemType.MINIGUN, false);
        weaponPrefs.addGeneralPref(UT2004ItemType.MINIGUN, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.LINK_GUN, false);
        weaponPrefs.addGeneralPref(UT2004ItemType.LIGHTNING_GUN, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.SHOCK_RIFLE, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.ROCKET_LAUNCHER, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.LINK_GUN, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.ASSAULT_RIFLE, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.FLAK_CANNON, false);
        weaponPrefs.addGeneralPref(UT2004ItemType.FLAK_CANNON, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.BIO_RIFLE, true);
    }

    @Override
    public Initialize getInitializeCommand() {
        // IT IS FORBIDDEN BY COMPETITION RULES TO CHANGE DESIRED SKILL TO DIFFERENT NUMBER THAN 6
        // IT IS FORBIDDEN BY COMPETITION RULES TO ALTER ANYTHING EXCEPT NAME & SKIN VIA INITIALIZE COMMAND

        return INITIALIZE_FACTORY.create(this.debug);
    }

    /**
     * Bot has been initialized inside GameBots2004 (Unreal Tournament 2004) and is about to enter the play
     * (it does not have the body materialized yet).
     *
     * @param gameInfo
     * @param currentConfig
     * @param init
     */
    @Override
    public void botInitialized(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init) {
        // INITIALIZE TABOO SETS, if you have them, HERE

        AnnotationListenerRegistrator listenerRegistrator = new AnnotationListenerRegistrator(this, this.getWorldView(), this.log);
        listenerRegistrator.addListeners();
    }

    /**
     * {@link PlayerDamaged} listener that senses that "some other bot was hurt".
     *
     * @param event
     */
    @EventListener(eventClass = PlayerDamaged.class)
    public void playerDamaged(PlayerDamaged event) {
        UnrealId botHurtId = event.getId();
        if (botHurtId == null) return;

        int damage = event.getDamage();

        log.info("OTHER HURT: " + damage + " DMG to " + botHurtId.getStringId() + " [type=" + event.getDamageType() + ", weapon=" + event.getWeaponName() + "]");
    }

    /**
     * {@link BotDamaged} listener that senses that "I was hurt".
     *
     * @param event
     */
    @EventListener(eventClass = BotDamaged.class)
    public void botDamaged(BotDamaged event) {
        int damage = event.getDamage();

        if (event.getInstigator() == null) {
            log.info("HURT: " + damage + " DMG done to ME [type=" + event.getDamageType() + ", weapon=" + event.getWeaponName() + "] by UNKNOWN");
        } else {
            UnrealId whoCauseDmgId = event.getInstigator();
            Player player = (Player) world.get(whoCauseDmgId); // MAY BE NULL!
            log.info("HURT: " + damage + " DMG done to ME [type=" + event.getDamageType() + ", weapon=" + event.getWeaponName() + "] by " + whoCauseDmgId.getStringId());
        }
    }

    /**
     * {@link PlayerKilled} listener that senses that "some other bot has died".
     *
     * @param event
     */
    @EventListener(eventClass = PlayerKilled.class)
    public void playerKilled(PlayerKilled event) {
        UnrealId botDiedId = event.getId();
        if (botDiedId == null) return;

        Player botDied = (Player) world.get(botDiedId);

        if (event.getKiller() == null) {
            log.info("OTHER DIED: " + botDiedId.getStringId() + ", UNKNOWN killer");
        } else {
            UnrealId killerId = event.getKiller();
            if (killerId.equals(info.getId())) {
                log.info("OTHER KILLED: " + botDiedId.getStringId() + " by ME");
            } else {
                Player killer = (Player) world.get(killerId);
                if (botDiedId.equals(killerId)) {
                    log.info("OTHER WAS KILLED: " + botDiedId.getStringId() + " comitted suicide");
                } else {
                    log.info("OTHER WAS KILLED: " + botDiedId.getStringId() + " by " + killerId.getStringId());
                }
            }
        }
    }

    /**
     * {@link BotKilled} listener that senses that "your bot has died".
     */
    @Override
    public void botKilled(BotKilled event) {
        if (event.getKiller() == null) {
            log.info("DEAD");
        } else {
            UnrealId killerId = event.getKiller();
            Player killer = (Player) world.get(killerId);
            log.info("KILLED by" + killerId.getStringId());
        }
    }

    /**
     * {@link HearNoise} listener that senses that "some noise was heard by the bot".
     *
     * @param event
     */
    @EventListener(eventClass = HearNoise.class)
    public void hearNoise(HearNoise event) {
        double noiseDistance = event.getDistance();   // 100 ~ 1 meter
        Rotation faceRotation = event.getRotation();  // rotate bot to this if you want to face the location of the noise
        log.info("HEAR NOISE: distance = " + noiseDistance);
    }

    /**
     * {@link ItemPickedUp} listener that senses that "your bot has picked up some item".
     * <p>
     * See sources for {@link ItemType} for details about item types / categories / groups.
     *
     * @param event
     */
    @EventListener(eventClass = ItemPickedUp.class)
    public void itemPickedUp(ItemPickedUp event) {
        ItemType itemType = event.getType();
        ItemType.Group itemGroup = itemType.getGroup();
        ItemType.Category itemCategory = itemType.getCategory();
        log.info("PICKED " + itemCategory.name + ": " + itemType.getName() + " [group=" + itemGroup.getName() + "]");
    }

    // ==============
    // MAIN BOT LOGIC
    // ==============

    /**
     * {@link IncomingProjectile} listener that senses that "some projectile has appeared OR moved OR disappeared".
     *
     * @param event
     */
    @ObjectClassEventListener(objectClass = IncomingProjectile.class, eventClass = WorldObjectUpdatedEvent.class)
    public void incomingProjectileUpdated(WorldObjectUpdatedEvent<IncomingProjectile> event) {
        IncomingProjectile projectile = event.getObject();
        log.info("PROJECTILE UPDATED: " + projectile);
    }

    /**
     * {@link Player} listener that senses that "some other bot has appeared OR moved OR disappeared"
     * <p>
     * WARNING: this method will also be called during handshaking GB2004.
     *
     * @param event
     */
    @ObjectClassEventListener(objectClass = Player.class, eventClass = WorldObjectUpdatedEvent.class)
    public void playerUpdated(WorldObjectUpdatedEvent<Player> event) {
        if (info.getLocation() == null) {
            // HANDSHAKING GB2004
            return;
        }
        Player player = event.getObject();
        log.info("PLAYER UPDATED: " + player.getId().getStringId());
    }

    /**
     * {@link Item} listener that senses that "some SPAWNED item has appeared OR moved OR disappeared"
     *
     * @param event
     */
    @ObjectClassEventListener(objectClass = Item.class, eventClass = WorldObjectUpdatedEvent.class)
    public void itemUpdated(WorldObjectUpdatedEvent<Item> event) {
        if (info.getLocation() == null) {
            // HANDSHAKING GB2004
            return;
        }
        Item item = event.getObject();
        log.info("ITEM UPDATED: " + item.getId().getStringId());
    }

    /**
     * {@link FlagInfo} listener that senses changes of CTF game state.
     *
     * @param event
     */
    @ObjectClassEventListener(objectClass = FlagInfo.class, eventClass = WorldObjectUpdatedEvent.class)
    public void flagInfoUpdated(WorldObjectUpdatedEvent<FlagInfo> event) {
        log.info("FLAG INFO UPDATED: " + event.getObject());
    }

    /**
     * {@link TeamScore} listener that senses changes within scoring.
     *
     * @param event
     */
    @ObjectClassEventListener(objectClass = TeamScore.class, eventClass = WorldObjectUpdatedEvent.class)
    public void teamScoreUpdated(WorldObjectUpdatedEvent<TeamScore> event) {
        switch (event.getObject().getTeam()) {
            case AgentInfo.TEAM_RED:
                log.info("RED TEAM SCORE UPDATED: " + event.getObject());
                break;
            case AgentInfo.TEAM_BLUE:
                log.info("BLUE TEAM SCORE UPDATED: " + event.getObject());
                break;
        }
    }

    // ===========
    // MIND LOGGER
    // ===========

    /**
     * {@link Self} object has been updated. This update is received about every 50ms. You can use this update
     * to fine-time some of your behavior like "weapon switching". I.e. SELF is updated every 50ms while LOGIC is invoked every 250ms.
     * <p>
     * Note that during "SELF UPDATE" only information about your bot location/rotation ({@link Self}) is updated. All other visibilities
     * remains the same as during last {@link #logic()}.
     * <p>
     * Note that new {@link NavMeshNavigation} is using SELF UPDATES to fine-control the bot's navigation.
     *
     * @param event
     */
    @ObjectClassEventListener(objectClass = Self.class, eventClass = WorldObjectUpdatedEvent.class)
    public void selfUpdated(WorldObjectUpdatedEvent<Self> event) {
        if (lifeCycle.lastLogicStartMillis == 0) {
            // IGNORE ... logic has not been executed yet...
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (lifeCycle.selfLastUpdateStartMillis == 0) {
            lifeCycle.selfLastUpdateStartMillis = currentTimeMillis;
            return;
        }
        long selfTimeDelta = currentTimeMillis - lifeCycle.selfLastUpdateStartMillis;
        lifeCycle.selfLastUpdateStartMillis = currentTimeMillis;
//        log.info("---[ SELF UPDATE | D: " + (selfTimeDelta) + "ms ]---");

        try {

            // YOUR CODE HERE

        } catch (Exception e) {
            // MAKE SURE THAT YOUR BOT WON'T FAIL!
            log.info(ExceptionToString.process(e));
        } finally {
            //log.info("---[ SELF UPDATE END ]---");
        }

    }

    // ======================================
    // UT2004 DEATH-MATCH INTERESTING GETTERS
    // ======================================

    /**
     * Method that is executed only once before the first {@link CTFBot#logic()}
     */
    @Override
    public void beforeFirstLogic() {
    }

    /**
     * Main method that controls the bot - makes decisions what to do next. It
     * is called iteratively by Pogamut engine every time a synchronous batch
     * from the environment is received. This is usually 4 times per second.
     * <p>
     * This is a typical place from where you start coding your bot. Even though bot
     * can be completely EVENT-DRIVEN, the reactive aproach via "ticking" logic()
     * method is more simple / straight-forward.
     */
    @Override
    public void logic() {
        long logicStartTime = System.currentTimeMillis();
        long timeDelta;
        if (lifeCycle.lastLogicStartMillis == 0) {
            lifeCycle.lastLogicStartMillis = logicStartTime;
            log.info("===[ LOGIC ITERATION ]===");
            timeDelta = 1;
        } else {
            timeDelta = logicStartTime - lifeCycle.lastLogicStartMillis;
            log.info("===[ LOGIC ITERATION | Delta: " + (timeDelta) + "ms | Since last: " + (logicStartTime - lifeCycle.lastLogicEndMillis) + "ms]===");
            lifeCycle.lastLogicStartMillis = logicStartTime;
        }

        if (debug.tryDrawNavMesh()) {
            log.warning("!!! DRAWING NAVMESH !!!");
            navMeshModule.getNavMeshDraw().draw(true, true);
            log.warning("NavMesh drawn, waiting a bit to finish the drawing...");
        }

        try {
            // LOG VARIOUS INTERESTING VALUES
//            logMind();

            // UPDATE TEAM COMM
            /*commItems.update();
            commObjectUpdates.update();*/

            Set<BehaviorResource> changes = this.behavior.behave();

            // RANDOM NAVIGATION
            if (changes.contains(BehaviorResource.Movement)) {
                log.info("RUNNING TO: " + navigation.getCurrentTarget());
                if (SETTINGS.DRAW_NAVIGATION_PATH) {
                    if (this.debug.botInstance == 0) draw.clearAll();

                    debug.drawNavigationPath(navigation.getCurrentPathCopy());
                }
            }

        } catch (Exception e) {
            // MAKE SURE THAT YOUR BOT WON'T FAIL!
            log.info(ExceptionToString.process(e));
        } finally {
            // MAKE SURE THAT YOUR LOGIC DOES NOT TAKE MORE THAN 250 MS (Honestly, we have never seen anybody reaching even 150 ms per logic cycle...)
            // Note that it is perfectly OK, for instance, to count all path-distances between you and all possible pickup-points / items in the game
            // sort it and do some inference based on that.
            lifeCycle.lastLogicEndMillis = System.currentTimeMillis();
            long timeSpentInLogic = lifeCycle.lastLogicEndMillis - logicStartTime;

            log.info("Logic time:         " + timeSpentInLogic + " ms");
            if (timeSpentInLogic >= 245) {
                log.warning("!!! LOGIC TOO DEMANDING !!!");
            }
            log.info("===[ LOGIC END ]===");

        }
    }

    /**
     * It is good in-general to periodically log anything that relates to your's {@link CTFBot#logic()} decision making.
     * <p>
     * You might consider exporting these values to some custom Swing window (you create for yourself) that will be more readable.
     */
    public void logMind() {
        log.info("My health/armor:   " + info.getHealth() + " / " + info.getArmor() + " (low:" + info.getLowArmor() + " / high:" + info.getHighArmor() + ")");
        log.info("My weapon:         " + weaponry.getCurrentWeapon());
        log.info("Have flag:         " + ctf.isBotCarryingEnemyFlag());
        log.info("Our flag state:    " + ctf.getOurFlag().getState());
        log.info("Enemey flag state: " + ctf.getEnemyFlag().getState());
    }

    // ==========
    // RAYCASTING
    // ==========

    /**
     * Returns path-nearest {@link NavPoint} that is covered from 'enemy'. Uses {@link UT2004BotModuleController#getVisibility()}.
     *
     * @param enemy
     * @return
     */
    public NavPoint getNearestCoverPoint(Player enemy) {
        if (!visibility.isInitialized()) {
            log.warning("VISIBILITY NOT INITIALIZED: returning random navpoint");
            return MyCollections.getRandom(navPoints.getNavPoints().values());
        }
        List<NavPoint> coverPoints = new ArrayList<NavPoint>(visibility.getCoverNavPointsFrom(enemy.getLocation()));
        return fwMap.getNearestNavPoint(coverPoints, info.getNearestNavPoint());
    }

    /**
     * Returns whether 'item' is possibly spawned (to your current knowledge).
     *
     * @param item
     * @return
     */
    public boolean isPossiblySpawned(Item item) {
        return items.isPickupSpawned(item);
    }

    /**
     * Returns whether you can actually pick this 'item', based on "isSpawned" and "isPickable" in your current state and knowledge.
     */
    public boolean isCurrentlyPickable(Item item) {
        return isPossiblySpawned(item) && items.isPickable(item);
    }

    /**
     * Performs a client-side raycast agains UT2004 map geometry.
     * <p>
     * It is not sensible to perform more than 1000 raycasts per logic() per bot.
     *
     * @param from
     * @param to
     * @return
     */
    public RayCastResult raycast(ILocated from, ILocated to) {
        if (!levelGeometryModule.isInitialized())
            return null;
        return levelGeometryModule.getLevelGeometry().rayCast(from.getLocation(), to.getLocation());
    }

    /**
     * Performs a client-side raycast against NavMesh in 'direction'. Returns distance of the edge in given 'direction' sending the ray 'from'.
     *
     * @param from
     * @param direction
     * @return
     */
    public double raycastNavMesh(ILocated from, Vector2D direction) {
        if (!navMeshModule.isInitialized())
            return 0;
        ClearanceLimit limit = navMeshModule.getClearanceComputer().findEdge(from.getLocation(), direction);
        if (limit == null)
            return Double.POSITIVE_INFINITY;
        return from.getLocation().getDistance(limit.getLocation());
    }

    // =====
    // AStar
    // =====

    public boolean navigateAStarPath(NavPoint targetNavPoint) {
        if (lastAStarTarget == targetNavPoint) {
            if (navigation.isNavigating()) return true;
        }
        PrecomputedPathFuture<ILocated> path = getAStarPath(targetNavPoint);
        if (path == null) {
            navigation.stopNavigation();
            return false;
        }
        lastAStarTarget = targetNavPoint;
        navigation.navigate(path);
        return true;
    }

    // ===========
    // MAIN METHOD
    // ===========

    private PrecomputedPathFuture<ILocated> getAStarPath(NavPoint targetNavPoint) {
        NavPoint startNavPoint = info.getNearestNavPoint();
        AStarResult<NavPoint> result = aStar.findPath(startNavPoint, targetNavPoint, mapView);
        if (result == null || !result.isSuccess()) return null;
        PrecomputedPathFuture path = new PrecomputedPathFuture(startNavPoint, targetNavPoint, result.getPath());
        return path;
    }

}
