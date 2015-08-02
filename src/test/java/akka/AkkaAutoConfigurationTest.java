package akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = akka.AkkaAutoConfigurationTest.Main.class)
public class AkkaAutoConfigurationTest extends TestCase {


    @SpringBootApplication
    public static class Main {

        @Bean
        ActorRef client(ActorSystem actorSystem, SpringExtension extension) {
            return actorSystem.actorOf(extension.springPropertiesForActor("counterActor"));
        }
    }

    @Autowired
    private ActorSystem actorSystem;

    @Autowired
    @Qualifier("client")
    private ActorRef client;

    @Test
    public void contextLoads() throws Exception {
        this.client.tell(new CounterActor.Count(), null);
        this.client.tell(new CounterActor.Count(), null);
        this.client.tell(new CounterActor.Count(), null);

        FiniteDuration duration = FiniteDuration.create(3, TimeUnit.SECONDS);
        Future<Object> result = Patterns.ask(this.client, new CounterActor.Get(), Timeout.durationToTimeout(duration));
        try {
            System.out.println("Got back " + Await.result(result, duration));
        } catch (Exception e) {
            System.err.println("Failed getting result: " + e.getMessage());
            throw e;
        } finally {
            this.actorSystem.shutdown();
            this.actorSystem.awaitTermination();
        }
    }
}
