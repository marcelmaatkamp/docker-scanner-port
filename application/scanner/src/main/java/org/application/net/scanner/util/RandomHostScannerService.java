package org.application.net.scanner.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by marcel on 03-01-16.
 */
public class RandomHostScannerService implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Handler.class);

    private final ExecutorService pool;

    String[] hosts = {
            "xmh57jrzrnw6insl",
            "3g2upl4pq6kufc4m",
            "3g2upl4pq6kufc4m"
    };

    char[] addressSpace = "abcdefghijklmnopqrstuvwxyz234567".toCharArray();
    int addressSize = 16;
    Random random = new Random();

    public RandomHostScannerService(int poolSize)
            throws IOException {
        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("hostscanner-%d")
                .setDaemon(true)
                .build();
        this.pool = Executors.newFixedThreadPool(poolSize, threadFactory);
    }

    public void run() {
        for (;;) {
            pool.execute(new Handler(getRandomHostname()));
        }
    }

    private String getRandomHostname() {
        char[] address = new char[addressSize];
        for (int i = 0; i < addressSize; i++) {
            address[i] = addressSpace[random.nextInt(addressSpace.length)];
        }
        return  new String(address) + ".onion";
    }

    public static void main(String[] args) throws IOException {
        System.setProperty("socksProxyHost", "tor");
        System.setProperty("socksProxyPort", "9050");

        RandomHostScannerService randomHostScannerService = new RandomHostScannerService(8);
        randomHostScannerService.run();
    }
}

class Handler implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Handler.class);

    private final String hostname;
    Handler(String hostname) { this.hostname = hostname; }

    public void run() {
        try {
            // log.info("scanning " + hostname);
            for(PortScanner.ScanResult scanResult : new PortScanner().scan(hostname)) {
                if(scanResult.isOpen()) {
                    log.info("host: " +hostname + ":" + scanResult.getPort() + " open");
                }
            }
        } catch (InterruptedException e) {
            log.error("Exception: ",e );
        } catch (ExecutionException e) {
            log.error("Exception: ",e );
        }
    }
}