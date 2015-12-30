package org.application.net.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

/**
 * Created by marcel on 30-12-15.
 */
public class ScannerStarter {

    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext configurableApplicationContext = new SpringApplicationBuilder(ScannerStarter.class).web(false).run(args);
        configurableApplicationContext.start();
    }
}
