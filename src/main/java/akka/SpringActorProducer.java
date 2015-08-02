package akka;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import org.springframework.context.ApplicationContext;


public class SpringActorProducer implements IndirectActorProducer {

    private final ApplicationContext applicationContext;
    private final String actorBeanName;

    public SpringActorProducer(ApplicationContext applicationContext,
                               String actorBeanName) {
        this.applicationContext = applicationContext;
        this.actorBeanName = actorBeanName;
    }

    public Actor produce() {
        return applicationContext.getBean(actorBeanName, Actor.class);
    }

    public Class<? extends Actor> actorClass() {
        return (Class<? extends Actor>)
                applicationContext.getType(actorBeanName);
    }
}