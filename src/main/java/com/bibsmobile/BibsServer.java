package com.bibsmobile;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * Starts the Bibs Server with an embedded web server
 * do NOT use for production, just for testing and debugging
 */
public class BibsServer {
    private static final int DEFAULT_PORT = 8088;
    private static final String CONTEXT_PATH = "/";

    public static void main(String[] args) throws Exception {
        new BibsServer().startJetty(BibsServer.getPortFromArgs(args));
    }

    private BibsServer() {
        super();
    }

    private static int getPortFromArgs(String[] args) {
        if (args.length > 0) {
            try {
                return Integer.valueOf(args[0]);
            } catch (NumberFormatException ignore) {
            }
        }
        return BibsServer.DEFAULT_PORT;
    }

    private void startJetty(int port) throws Exception {
        Server server = new Server(port);
        server.setHandler(BibsServer.getServletContextHandler());
        server.start();
        server.join();
    }

    private static ServletContextHandler getServletContextHandler() throws IOException {
        WebAppContext contextHandler = new WebAppContext();
        contextHandler.setContextPath(BibsServer.CONTEXT_PATH);
        contextHandler.setWar(new ClassPathResource("webapp").getURI().toString());
        return contextHandler;
    }
}
