package autotest.ftl;

import ftl.activity.DefaultFTLPublisher;


public class publisher {


    public static void publish (String realmServerUrl,String pubftlAppName, String pubftlEndPointName, String formatName){

        DefaultFTLPublisher publisher = new DefaultFTLPublisher(realmServerUrl,pubftlAppName,pubftlEndPointName);
        publisher.send(formatName);


        publisher.close();
    }




}
