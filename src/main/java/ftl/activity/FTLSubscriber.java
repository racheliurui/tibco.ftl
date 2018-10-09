package ftl.activity;

import com.tibco.ftl.*;


import java.util.List;

public class FTLSubscriber implements SubscriberListener {

    private boolean messageReceived = false;

    protected Realm realm;
    protected Subscriber sub;
    protected EventQueue queue;


    public FTLSubscriber(String realmServerUrl, String ftlAppName, String ftlEndPointName) {
        try {
            realm = FTL.connectToRealmServer(realmServerUrl, ftlAppName, null);

            sub = realm.createSubscriber(ftlEndPointName, null, null);

            queue = realm.createEventQueue();
            queue.addSubscriber(sub, this);
        } catch (FTLException e) {
            e.printStackTrace();
        }
    }


    public void close() {
        try {
            queue.removeSubscriber(sub);
            queue.destroy();
            sub.close();
            realm.close();
        } catch (FTLException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage() {
        System.out.printf("waiting for message(s)\n");
        while (messageReceived == false) {
            try {
                queue.dispatch();
            } catch (FTLException e) {
                e.printStackTrace();
            }
        }
        messageReceived = false;
    }

    public void messagesReceived(List<Message> messages, EventQueue eventQueue) {
        for (int i = 0; i < messages.size(); i++) {
            System.out.println(" " + messages.get(i));
            messageReceived = true;
        }
    }
}
