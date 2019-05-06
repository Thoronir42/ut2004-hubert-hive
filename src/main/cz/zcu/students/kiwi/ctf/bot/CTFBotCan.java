package cz.zcu.students.kiwi.ctf.bot;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;

public class CTFBotCan {

    private CTFBot bot;

    public CTFBotCan(CTFBot bot) {
        this.bot = bot;
    }

    public boolean pickNow(Item item) {

        return bot.getItems().isPickupSpawned(item) && bot.getItems().isPickable(item);
    }
}
