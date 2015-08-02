package akka;

import akka.actor.ActorSystem;
import akka.actor.Extension;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private Log log = LogFactory.getLog(getClass());

    @Bean
    @ConditionalOnMissingBean
    ActorSystem actorSystem(AkkaProperties akkaProperties, Config config) {
        String actorSystemName = akkaProperties.getSystem().getName();
        ActorSystem actorSystem = ActorSystem.create(akkaProperties.getSystem().getName(), config);
        this.log.info("created actorSystem with actorSystemName '" + actorSystemName + "'");
        return actorSystem;
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