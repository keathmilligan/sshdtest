package net.keathmilligan.sshdtest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.ProcessShellFactory;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LogManager.getLogger();
    public static void main(String args[]) {
        logger.info("sshd example");
        var sshServer = SshServer.setUpDefaultServer();
        sshServer.setHost("127.0.0.1");
        sshServer.setPort(2222);
        sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        sshServer.setPasswordAuthenticator((username, password, session) -> {
            return true;
        });
        sshServer.setShellFactory(new ProcessShellFactory("/bin/sh", "-i", "-l"));
        try {
            logger.info("starting server");
            sshServer.start();
            var scanner = new Scanner(System.in);
            var quit = false;
            while (!quit) {
                System.out.println("\nEnter command (\"quit\" to exit, \"?\" for help):");
                var command = scanner.nextLine();
                switch (command) {
                    case "?":
                    case "help":
                        System.out.println(
                            "q[uit]  Stop server and exit\n" +
                            "?       Print this message\n"
                        );
                        break;
                    case "quit":
                    case "q":
                        logger.info("exiting");
                        sshServer.stop();
                        quit = true;
                        break;
                    default:
                        System.out.println("Invalid command - enter \"?\" for help");
                        break;
                }
            }
            logger.info("server exited");
        } catch (IOException e) {
            logger.fatal("caught IOException " + e.toString());
            System.exit(1);
        }
    }
}
