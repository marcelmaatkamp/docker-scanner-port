package org.application.net.scanner.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by marcel on 30-12-15.
 */
public class PortScanner {
    private static final Logger log = LoggerFactory.getLogger(PortScanner.class);


    public static void main(final String... args) throws InterruptedException, ExecutionException {
        System.setProperty("socksProxyHost", "tor");
        System.setProperty("socksProxyPort", "9050");

        PortScanner portScanner = new PortScanner();
        String hostname = "kpynyvym6xqi7wz2.onion";
        List<ScanResult> futures = portScanner.scan(hostname);
        int openPorts = 0;
        for (final ScanResult f : futures) {
            if (f.isOpen()) {
                openPorts++;
                log.info("" + f.getPort());
            }
        }
        log.info("There are " + openPorts + " open ports on host " + hostname);

    }

    public List<ScanResult> scan(String hostname) throws InterruptedException, ExecutionException {
        final ExecutorService es = Executors.newFixedThreadPool(20);


        final int timeout = 7500;
        final List<ScanResult> futures = new ArrayList<>();
        for (int port = 80; port <= 80; port++) {
            // for (int port = 1; port <= 80; port++) {
            Future<ScanResult> scanResult = portIsOpen(es, hostname, port, timeout);
            if(scanResult!=null && scanResult.get() != null) {
                futures.add(scanResult.get());
            }
        }
        es.awaitTermination(-1, TimeUnit.MILLISECONDS);
        log.info("done " + hostname);
        es.shutdown();

        return futures;
    }

    public Future<ScanResult> portIsOpen(final ExecutorService es, final String ip, final int port,
                                                final int timeout) {
        return es.submit(new Callable<ScanResult>() {
            @Override
            public ScanResult call() {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, port), timeout);
                    socket.close();
                    log.info("o; "+ip+","+port);
                    return new ScanResult(ip, port, true);
                } catch (Exception ex) {
                    return null; // new ScanResult(ip, port, false);
                }
            }
        });
    }

    public static class ScanResult {
        private String hostname;
        private int port;
        private boolean isOpen;

        public ScanResult(String hostname, int port, boolean isOpen) {
            super();
            this.hostname = hostname;
            this.port = port;
            this.isOpen = isOpen;
        }

        public String getHostname() {
            return hostname;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public boolean isOpen() {
            return isOpen;
        }

        public void setOpen(boolean isOpen) {
            this.isOpen = isOpen;
        }

    }
}