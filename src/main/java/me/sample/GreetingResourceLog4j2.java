package me.sample;

import io.vertx.core.Vertx;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.eclipse.microprofile.context.ManagedExecutor;


import java.util.concurrent.CountDownLatch;

@Path("/hello/log4j2")
public class GreetingResourceLog4j2 {

    @Inject
    Vertx vertx;
    private static final Logger logger = LogManager.getLogger();

    @Inject
    ManagedExecutor executor;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() throws InterruptedException {
        ThreadContext.put("message", "log4j2");
        CountDownLatch latch = new CountDownLatch(1);
        vertx.setTimer(10, (x) -> {
            logger.info("Test (1)");
            vertx.executeBlocking(future -> {
                System.out.println(ThreadContext.get("message"));
                logger.info("Test (2)");
                executor.submit(() -> {
                    logger.info("Test (3)");
                    future.complete(null);
                });
            }).onComplete(y -> latch.countDown());
        });
        latch.await();
        return "Hello from RESTEasy Reactive";
    }
}
