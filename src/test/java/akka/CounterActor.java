package akka;

import akka.actor.UntypedActor;

@Actor
public class CounterActor
        extends UntypedActor {

    public static class Count {
    }

    public static class Get {
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Count) {
            System.out.println("count!");
        } else if (message instanceof Get) {
            getSender().tell(1, getSelf());
            System.out.println("get!");
        } else {
            unhandled(message);
        }
    }
}


