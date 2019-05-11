package cz.zcu.students.kiwi.ctf;

import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.zcu.students.kiwi.ctf.bot.CTFBot;
import cz.zcu.students.kiwi.ctf.bot.InitializeCommandFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;

public class Main {

    private InitializeCommandFactory initializeCommandFactory;

    // arguments
    private int year;
    private int botsToStart = 4;
    private String host = "localhost";


    public static void main(String[] args) throws PogamutException {
        Main m = new Main();
        m.parseArguments(args);
        m.startAgents();
    }

    private Main() {
        this.initializeCommandFactory = new InitializeCommandFactory();
    }

    private void parseArguments(String[] args) {
        if (args.length > 0) {
            this.year = Integer.parseInt(args[0]);
        }
        if (args.length > 1) {
            this.initializeCommandFactory.setDesiredTeam(Integer.parseInt(args[1]));
        }
        if (args.length > 2) {
            this.initializeCommandFactory.setBotSkill(Integer.parseInt(args[2]));
        }
        if (args.length > 3) {
            this.botsToStart = Integer.parseInt(args[3]);
        }
        if (args.length > 4) {
            this.host = args[4];
            try {
                InetAddress.getByName(this.host);
            } catch (UnknownHostException e) {
                throw new IllegalArgumentException("Invalid address provided: " + this.host, e);
            }
        }

    }

    void startAgents() {
        CTFBot.INITIALIZE_FACTORY = this.initializeCommandFactory;

        // Starts N agents of the same type at once
        // WHEN YOU WILL BE SUBMITTING YOUR CODE, MAKE SURE THAT YOU RESET NUMBER OF STARTED AGENTS TO '1' !!!
        new UT2004BotRunner(CTFBot.class, "CTFBot")
                .setHost(this.host)
                .setMain(true)
                .setLogLevel(Level.INFO)
                .startAgents(this.botsToStart);
    }
}
