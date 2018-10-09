package ftl.activity;

import com.tibco.ftl.EventQueue;
import com.tibco.ftl.EventTimer;
import com.tibco.ftl.EventTimerListener;
import com.tibco.ftl.FTL;
import com.tibco.ftl.FTLException;
import com.tibco.ftl.Inbox;
import com.tibco.ftl.InboxSubscriber;
import com.tibco.ftl.Message;
import com.tibco.ftl.Publisher;
import com.tibco.ftl.Realm;
import com.tibco.ftl.SubscriberListener;
import java.util.List;

public class FTLRequestAndReply implements SubscriberListener, EventTimerListener {
    private Publisher pub;
    private Message msg;
    private String realmServer;
    private String appName;
    private String endPoint;
    private boolean replyReceived = false;
    private double pingInterval  = 3.0;
    volatile EventTimer pongTimer = null;
    private int numberMsgToSend = 10;

    public FTLRequestAndReply(String realmServer, String appName, String endPoint) {
        this.realmServer = realmServer;
        this.appName = appName;
        this.endPoint = endPoint;
    }

    public void timerFired(EventTimer eventTimer, EventQueue eventQueue) {
        try {
            pub.send(msg);
        } catch (FTLException e) {
            e.printStackTrace();
            replyReceived = true;
        }
    }

    public void messagesReceived(List<Message> messages, EventQueue eventQueue) {
        try {
            if (pongTimer != null) {
                eventQueue.destroyTimer(pongTimer);
                pongTimer = null;
            }
        } catch (FTLException e) {
            e.printStackTrace();
        }
        replyReceived = true;
        System.out.printf("received reply message:\n");
        System.out.println("  " + messages.get(0));
    }

    public void request() throws FTLException {
        Realm realm = FTL.connectToRealmServer(realmServer, appName, null);
        EventQueue queue = realm.createEventQueue();
        pub = realm.createPublisher(endPoint);

        // send the request messages
        int counter = 0;
        while (counter < numberMsgToSend) {
            InboxSubscriber sub = realm.createInboxSubscriber(endPoint);
            queue.addSubscriber(sub, this);
            Inbox inbox = sub.getInbox();
            msg = realm.createMessage("Format-1");

            // set by name since performance is not demonstrated here
            msg.setString("type", "request" + counter);

            // put our inbox in the request message
            // set by name since performance is not demonstrated here
            msg.setInbox("Inbox", inbox);

            // Should we retry the initial ping until we get a pong?
            if (pingInterval != 0)
                pongTimer = queue.createTimer(pingInterval, this);

            System.out.println("sending request message:\n  "  + msg);
            pub.send(msg);

            while (replyReceived == false) {
                queue.dispatch();
            }
            replyReceived = false;
            counter++;

            queue.removeSubscriber(sub);
            msg.destroy();
            sub.close();
        }


        if (pongTimer != null) {
            queue.destroyTimer(pongTimer);
        }
        queue.destroy();
        pub.close();
        realm.close();
    }

    public static void main(String[] args) throws FTLException {
        String realmServer = "http://localhost:8080";
        String appName = "JavaWithTibco";
        String endPoint = "JWTPublisherEP";
        FTLRequestAndReply requestAndReply = new FTLRequestAndReply(realmServer, appName, endPoint);
        requestAndReply.request();
    }
}
