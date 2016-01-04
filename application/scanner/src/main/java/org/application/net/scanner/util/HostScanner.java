package org.application.net.scanner.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by marcel on 30-12-15.
 */
public class HostScanner {
    private static final Logger log = LoggerFactory.getLogger(HostScanner.class);

    String[] hosts = {
            "xmh57jrzrnw6insl",
            "3g2upl4pq6kufc4m",
            "3g2upl4pq6kufc4m"
    };

    char[] addressSpace = "abcdefghijklmnopqrstuvwxyz234567".toCharArray();
    int addressSize = 16;

    Random random = new Random();

    PortScanner portScanner = new PortScanner();

    public static void main(final String... args) throws InterruptedException, ExecutionException {
        HostScanner hostScanner = new HostScanner();
    }

    HostScanner() throws InterruptedException, ExecutionException {
        System.setProperty("socksProxyHost", "tor");
        System.setProperty("socksProxyPort", "9050");


        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("Orders-%d")
                .setDaemon(true)
                .build();
        final ExecutorService es = Executors.newFixedThreadPool(10, threadFactory);

        while(true) {
            List<PortScanner.ScanResult> scanResults = new ArrayList<>();

            Future<List<PortScanner.ScanResult>> scanResult = es.submit(new Callable<List<PortScanner.ScanResult>>() {
                @Override
                public List<PortScanner.ScanResult> call() throws ExecutionException, InterruptedException {
                    return scan();
                }
            });

            if(scanResult!=null && scanResult.get() != null) {
                log.info("" + scanResult.get());
            }

            es.awaitTermination(-1, TimeUnit.MILLISECONDS);
            log.info("done..");

            es.shutdown();

        }
    }

    private List<PortScanner.ScanResult> scan() throws InterruptedException, ExecutionException {
        char[] address = new char[addressSize];
        for (int i = 0; i < addressSize; i++) {
            address[i] = addressSpace[random.nextInt(addressSpace.length)];
        }

        String hostname = new String(address) + ".onion";
        return portScanner.scan(hostname);
    }
}