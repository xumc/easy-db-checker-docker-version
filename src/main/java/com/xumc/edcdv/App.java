package com.xumc.edcdv;

import com.github.shyiko.mysql.binlog.BinaryLogClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mxu2 on 5/31/15.
 */
public class App {
    private BinaryLogClient binClient;
    private EventListener eventListener;

    private List<PackagedEvent> globalPackagedEvent = new ArrayList<PackagedEvent>();

    public static void main(String[] args) {
        new App().buildBinlogConnection();
    }

    private void buildBinlogConnection() {
        Config config = Config.getInstance();
        String hostname = config.getHostname();
        int port = config.getPort();
        String user = config.getUser();
        String password = config.getPassword();

        try {
            if (binClient != null) {
                binClient.unregisterEventListener(eventListener);
                binClient.disconnect();
            }
            binClient = new BinaryLogClient(hostname, port, user, password);
            eventListener = new EventListener();
            binClient.registerEventListener(eventListener);
            binClient.connect();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}


