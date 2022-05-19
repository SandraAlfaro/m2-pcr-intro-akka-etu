package m2dl.pcr.akka.eratosthene;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;

public class NumberActor extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private ActorRef nextNumberActorRef;
    private int assignValue;

    public NumberActor() {
        log.info("NumberActor constructor");
    }

    Procedure<Object> assign = new Procedure<Object>() {
        public void apply(Object value) throws Exception {
            if (value instanceof Integer) {
                log.info("Value assign " + value + "!");
                assignValue = (int) value;
                getContext().become(end,true);
            } else {
                unhandled(value);
            }
        }
    };

    Procedure<Object> end = new Procedure<Object>() {
        public void apply(Object value) throws Exception {
            if (value instanceof Integer) {
                if (((int)value % assignValue) != 0) {
                    nextNumberActorRef = getContext().actorOf(Props.create(NumberActor.class), "number"+value+"-actor");
                    nextNumberActorRef.tell(value,getSelf());
                    getContext().become(intermediate,true);
                }
            } else {
                unhandled(value);
            }
        }
    };


    Procedure<Object> intermediate = new Procedure<Object>() {
        public void apply(Object value) throws Exception {
            if (value instanceof Integer) {
                if (((int)value % assignValue) != 0) {
                    nextNumberActorRef.tell(value,getSelf());
                }
            } else {
                unhandled(value);
            }
        }
    };

    @Override
    public void onReceive(Object value) throws Exception {
        assign.apply(value);
    }
}
