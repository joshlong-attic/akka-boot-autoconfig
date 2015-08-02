package akka;

import akka.actor.ActorSystem;
import akka.actor.Extension;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AkkaProperties.class)
public class AkkaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    ActorSystem actorSystem(AkkaProperties akkaProperties, Config config) {
        return ActorSystem.create(akkaProperties.getSystem().getName(), config);
    }

    @Bean
    @ConditionalOnMissingBean
    Config akkaConfiguration() {
        return ConfigFactory.load();
    }

    @Bean
    SpringExtension springExtension() {
        return new SpringExtension();
    }
}

class SpringExtension implements Extension, ApplicationContextAware {

    private ApplicationContext applicationContext;

    public Props springPropertiesForActor(String actorBeanName) {
        return Props.create(SpringActorProducer.class, this.applicationContext,
                actorBeanName);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}