package ftl.activity;

import com.tibco.ftl.FTLException;
import com.tibco.ftl.Message;
import com.tibco.ftl.Publisher;
import com.tibco.ftl.Realm;


/**
 * When sending a message with specific format, publishers can extend this class
 */
public class DefaultFTLPublisher extends FTLPublisher<String> {
    //Publisher pub = null;
    //Realm realm = null;

    public DefaultFTLPublisher(String realmServerUrl, String ftlAppName, String ftlEndPointName) {
        super(realmServerUrl, ftlAppName, ftlEndPointName);
    }

    public void send(String formatName) {
        try {


            Message msg = realm.createMessage(formatName);


            msg.setString("type", "hello");

            System.out.println("sending message: " + msg);
            pub.send(msg);

            msg.destroy();
        } catch (FTLException e) {
            e.printStackTrace();
        }
    }
}
