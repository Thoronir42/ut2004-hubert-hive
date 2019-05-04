package cz.zcu.students.kiwi.ctfbot;

import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.zcu.students.kiwi.ctfbot.initialize.InitializeCommandFactory;

import java.util.logging.Level;

public class Main {

    public static void main(String[] args) throws PogamutException {

        CTFBot.INITIALIZE_FACTORY = new InitializeCommandFactory(CTFBot.SETTINGS);

        // Starts N agents of the same type at once
        // WHEN YOU WILL BE SUBMITTING YOUR CODE, MAKE SURE THAT YOU RESET NUMBER OF STARTED AGENTS TO '1' !!!
        new UT2004BotRunner(CTFBot.class, "CTFBot")
                .setMain(true)
                .setLogLevel(Level.INFO)
                .startAgents(CTFBot.SETTINGS.BOTS_TO_START);
    }
}
