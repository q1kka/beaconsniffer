package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * This program subscribes to Redis topic in which blesniffer-python script publishes data to.
 * Logic is used to determine status of presence and publish presence messages back to redis
 * in the different topic.
 * @author Kai-Markus Lehtimäki
 * @version 16.09.2017
 */
public class ParserMain {
    // Symlinked beacon configurations
    private static String configurationFile = "configurations.conf";
    // Time waited until beacon is marked as absent
    private static int absentInterval = 30;
    // Redis configurations
    private static final String JEDIS_SERVER = "localhost";

    private static ArrayList<Beacon> followedBeacons = new ArrayList<Beacon>();
    private static ArrayList<Integer> followedHomegroups = new ArrayList<Integer>();

    /**
     * Read configurations from conf file, start the program.
     */
    public static void main(String[] args) {
        // Add beacons to be followed from symlinked configurations
        followedBeacons = parseConfigurations(configurationFile);
        followedHomegroups = parseHomegroups(followedBeacons);
        // Start listening for Redis messages
        startSubscriber();
    }

    /**
     * This function parses all followed homegroups from beacon objects supplied.
     * @param followedBeacons List of followed beacons
     * @return List of followed homegroups
     */
    private static ArrayList<Integer> parseHomegroups(ArrayList<Beacon> followedBeacons){
        ArrayList<Integer> homegroups = new ArrayList<>();
        for (Beacon beacon: followedBeacons){
            if(!homegroups.contains(beacon.getHomegroup())){
                homegroups.add(beacon.getHomegroup());
            }
        }
        return homegroups;
    }

    /**
     * This function reads beacon configurations from files and returns list of beacon objects.
     * @param configurationFile
     * @return
     */
    private static ArrayList<Beacon> parseConfigurations(String configurationFile){
        JSONParser parser = new JSONParser();
        ArrayList<Beacon> beaconList = new ArrayList<Beacon>();
        File currentDir = new File(".");
        File configFile = new File(currentDir,configurationFile);;
        try {
            Object obj = parser.parse(new FileReader(configFile));
            JSONObject jsonObject =  (JSONObject) obj;
            JSONObject beacons = (JSONObject) jsonObject.get("beacons");
            for(int i = 1; i < beacons.size() + 1; i++) {
                JSONObject beaconObj = (JSONObject) beacons.get("beacon" + i);
                int id2 = Integer.parseInt(beaconObj.get("id2").toString());
                int id3 = Integer.parseInt(beaconObj.get("id3").toString());
                String mac = beaconObj.get("mac").toString();
                Beacon newBeacon = new Beacon(id3, id2, mac);
                beaconList.add(newBeacon);
                System.out.println("Beacon: " + mac + " - " + id3 + "-" + id2 + " added");
            }
        } catch (Exception e) {
            System.out.println("FATAL: Configuration file with valid syntax not found.");
        }
        return beaconList;
    }

    private static JedisPubSub startSubscriber() {
        final JedisPubSub jedisPubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                parseMessage(channel, message);
            }
        };
        new Thread(new Runnable() {
            public void run() {
                try {
                    printWithTime("Connected to: " + JEDIS_SERVER + " redis server");
                    Jedis jedis = new Jedis(JEDIS_SERVER);
                    for (Integer id: followedHomegroups){
                        jedis.subscribe(jedisPubSub, id.toString());
                    }
                    printWithTime("Subscribe returned, closing down");
                    jedis.quit();
                } catch (Exception e) {
                    printWithTime("Error in redis subscribe: " + e.getMessage());
                    // e.printStackTrace();
                }
            }
        }, "subscriberThread").start();
        return jedisPubSub;
    }

    private static void printWithTime(String message) {
        System.out.println(getCurrentTimeStamp() + " | " + message);
    }

    /**
     * Parse presence values redis publish payload
     *
     * @param message redis payload to parse from
     * @param channel homegroup in which message is sent
     */
    private static void parseMessage(String channel, String message) {
        int homeGroup = Integer.parseInt(channel);
        String[] messageArray = message.split("-");
        int minorID = Integer.parseInt(messageArray[0]);
        long timestamp = Long.parseLong(messageArray[1]);
        // Loop through all beacons and add data to correct beacon
        for (Beacon beac : followedBeacons) {
            if(minorID == beac.getID() && homeGroup == beac.getHomegroup()) {
                beac.addData(timestamp);
            }
        }
    }

    /**
     * @return Current timestamp in format:
     */
    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }
}
