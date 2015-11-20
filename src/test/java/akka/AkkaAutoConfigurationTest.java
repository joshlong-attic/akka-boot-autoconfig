package akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import akka.util.Timeout;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private Log log = LogFactory.getLog(getClass());
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
            this.log.info("got back " + Await.result(result, duration));
        } catch (Exception e) {
            this.log.info("couldn't retrieve result: " + e.getMessage());
            throw e;
        } finally {
            this.actorSystem.shutdown();
            this.actorSystem.awaitTermination();
        }
    }

    @SpringBootApplication
    public static class Main {

        @Bean
        ActorRef client(ActorSystem actorSystem, SpringExtension extension) {
            return actorSystem.actorOf(extension.springPropertiesForActor("counterActor"));
        }
    }
}

@Actor
class CounterActor extends UntypedActor {

    private Log log = LogFactory.getLog(getClass());

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Count) {
            log.info("count!");
        } else if (message instanceof Get) {
            getSender().tell(1, getSelf());
            log.info("get!");
        } else {
            unhandled(message);
        }
    }

    public static class Count {
    }

    public static class Get {
    }
}
