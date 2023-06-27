package me.sample;

import io.vertx.core.Vertx;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.eclipse.microprofile.context.ManagedExecutor;


import java.util.concurrent.CountDownLatch;

@Path("/hello/log4j1")
public class GreetingResourceLog4j1 {

    @Inject
    Vertx vertx;

    @Inject
    ManagedExecutor executor;

    private static final Logger logger = Logger.getLogger("test");

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() throws InterruptedException {
        MDC.put("message", "log4j1");
        CountDownLatch latch = new CountDownLatch(1);
        vertx.setTimer(10, (x) -> {
            logger.info("Test (1)");
            vertx.executeBlocking(future -> {
                System.out.println(MDC.get("message"));
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
