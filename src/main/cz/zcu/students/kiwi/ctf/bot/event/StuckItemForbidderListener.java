package cz.zcu.students.kiwi.ctf.bot.event;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.NavigationState;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.utils.flag.FlagListener;
import cz.zcu.students.kiwi.ctf.bot.CTFBot;

public class StuckItemForbidderListener implements FlagListener<NavigationState> {
    private final CTFBot bot;

    private final LogCategory log;

    private int targetItemStuck;


    public StuckItemForbidderListener(CTFBot bot) {
        this.bot = bot;
        this.log = bot.getBot().getLogger().getCategory("Stuck listener");
        this.targetItemStuck = 0;
    }

    @Override
    public void flagChanged(NavigationState changedValue) {
        ILocated target = bot.getBehavior().getNavigationTarget();
        Item targetItem = null;
        if (targetItem instanceof Item) {
            targetItem = (Item) target;
        } else {
            return;
        }

        switch (changedValue) {
            case STUCK:
                log.info("Stuck!");
                if (++targetItemStuck >= 3) {
                    this.bot.getItemStatus().forbid(targetItem);
                    log.info("Taboo[stuck] added: " + targetItem.getType());
                }

                break;
            case PATH_COMPUTATION_FAILED:
                log.info("Path computation failed!");
                if (targetItem != null) {
                    bot.getItemStatus().forbid(targetItem, 10);
                    log.info("Taboo[unreachable] added: " + targetItem.getId());
                }

                break;
            case TARGET_REACHED:
                log.info("Target reached!");
                targetItemStuck = 0;
                this.bot.getBehavior().navigationReached();

                break;
        }
    }
}
