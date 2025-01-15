package com.aryak.tts_voice;

import com.aryak.tts_voice.config.QueueManager;
import com.aryak.tts_voice.consumers.ConverterConsumer;
import com.aryak.tts_voice.consumers.DatabaseConsumer;
import com.aryak.tts_voice.consumers.UploadConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * @author aryak
 * Bean responsible to start the 3 queue consumers by supplying required dependencies
 */
@Component
public class StartupConfigurer {

    private static final Logger log = LoggerFactory.getLogger(StartupConfigurer.class);

    private final QueueManager queueManager;

    private final ConverterConsumer converterConsumer;

    private final DatabaseConsumer databaseConsumer;

    private final UploadConsumer uploadConsumer;

    public StartupConfigurer(QueueManager queueManager, ConverterConsumer converterConsumer, DatabaseConsumer databaseConsumer, UploadConsumer uploadConsumer) {
        this.queueManager = queueManager;
        this.converterConsumer = converterConsumer;
        this.databaseConsumer = databaseConsumer;
        this.uploadConsumer = uploadConsumer;
    }

    public void load() {
        // start the converter consumer
        queueManager.getExecutorService().execute(converterConsumer);
        // start the database consumer
        queueManager.getExecutorService().execute(databaseConsumer);
        // start the upload consumer
        queueManager.getExecutorService().execute(uploadConsumer);
    }

    @PreDestroy
    public void shutDown() {

        uploadConsumer.stop();
        databaseConsumer.stop();
        converterConsumer.stop();

        queueManager.getExecutorService().shutdown();

        try {
            if (!queueManager.getExecutorService().awaitTermination(10, TimeUnit.SECONDS)) {
                log.warn("Executor service did not shut down within the timeout. Forcing shutdown...");
                queueManager.getExecutorService().shutdownNow();

                // Wait again to ensure all threads are terminated.
                if (!queueManager.getExecutorService().awaitTermination(5, TimeUnit.SECONDS)) {
                    log.error("Executor service did not terminate after forced shutdown.");
                }
            }
        } catch (InterruptedException e) {
            log.error("Shutdown interrupted. Forcing executor service shutdown...");
            queueManager.getExecutorService().shutdownNow();
            Thread.currentThread().interrupt(); // Preserve interrupt status.
        }

        if (queueManager.getExecutorService().isShutdown()) {
            log.info("Shutdown completed successfully.");
        } else {
            log.error("Executor service did not shut down gracefully.");
        }
    }


}
