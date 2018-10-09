
import ftl.activity.*;
import org.apache.commons.cli.ParseException;

public class Main {
    public static void main(String[] args) throws ParseException {

        String realmServerUrl="http://localhost:8080";
        String pubftlAppName="amqp.tibco.publisher";
        String pubftlEndPointName="amqp.tibco.publisherEP";

        String subftlAppName="amqp.tibco.subscriber";
        String subftlEndPointName="amqp.tibco.subscriberEP";



        FTLSubscriber subscriber = new FTLSubscriber(realmServerUrl,subftlAppName, subftlEndPointName);

        autotest.ftl.publisher.publish(realmServerUrl,pubftlAppName, pubftlEndPointName,"defaultFormat" );


        subscriber.receiveMessage();
        subscriber.close();



        //DefaultFTLPublisher publisher = new DefaultFTLPublisher(commandLineArgs);
        //publisher.send("defaultFormat");
        //publisher.close();
        //FTLSubscriberAndReply subscriberAndReply = new FTLSubscriberAndReply(commandLineArgs);
        //subscriberAndReply.receiveMessage();
        //subscriberAndReply.close();
    }
}
