import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import redis.clients.jedis.Jedis;

/**
 * This class contains logic and data of followed beacon. Each beacon object determines
 * its state according to raw data and defined configurations. Beacon object publishes its
 * state changes to redis topic.
 * @author Kai-Markus Lehtimäki
 * @version 22.11.2017
 */
class Beacon {
    private int absentInterval = 15;
    private final String JEDIS_SERVER = "localhost";

    private final int id;
    private final int homegroup;
    private final String mac;
    private final ArrayList<Long> presenceData;
    private boolean presence = false;

    /**
     * Default constructor for beacon object
     */
    public Beacon(int minorID, int homegroup, String mac, int absentInterval) {
        this.id = minorID;
        this.homegroup = homegroup;
        this.mac = mac;
        this.absentInterval = absentInterval;
        presenceData = new ArrayList<>();
        Timer tm = new Timer();
        class PresenceCheckTask extends TimerTask {
            public PresenceCheckTask(){
            }

            public void run() {
                checkPresence();
            }
        }

        //Check presence every 10 secs
        PresenceCheckTask ct = new PresenceCheckTask();
        //Check interval in seconds
        int checkInterval = 10;
        tm.scheduleAtFixedRate(ct, (checkInterval * 1000), (checkInterval * 1000));
    }

    /**
     * This function publishes message to redis in new thread
     * @param channel Channel to publish to
     * @param message Message to be published
     */
    private void publishRedis(Integer channel, String message){
        new Thread(() -> {
            try {
                Jedis jedis = new Jedis(JEDIS_SERVER);
                String publishChannel = channel + "-parsed";
                jedis.publish(publishChannel, message);
                jedis.quit();
            } catch (Exception e) {
                printWithTime("Error: " + e.getMessage());
            }
        }, "redisPublisherThread").start();

    }

    /**
     * Compare current time and last seen time from raw data. If not seen in X time mark as absent
     * and publish state change to redis topic.
     */
    private void checkPresence() {
        long nowEpoc = Instant.now().getEpochSecond();
        long lastPresence = 0;
        if (presenceData.size() != 0){
            lastPresence = presenceData.get(presenceData.size() - 1);
        }
        // If not seen in absentInterval mark as absent and publish to redis
        if((lastPresence + absentInterval) < nowEpoc) {
            if(presence){
                printWithTime(this.getHomegroup() + "-" + this.getID() + "-inactive");
                publishRedis(this.getHomegroup(), (this.getID() + "-inactive"));
            }
            presence = false;
        }
    }

    public int getID() {
        return this.id;
    }

    public int getHomegroup(){ return this.homegroup; }

    /**
     * This method sets new last seen timestamp to beacon object
     * @param timestamp last seen epoc timestamp
     */
    public void addData(long timestamp) {
        presenceData.add(timestamp);
        if(!presence){
            publishRedis(this.getHomegroup(), (this.getID() + "-active"));
            printWithTime(this.getHomegroup() + "-" + this.getID() + "-active");
        }
        presence = true;
    }

    private static void printWithTime(String message){
        System.out.println(getCurrentTimeStamp() + " | " + message);
    }

    /**
     * @return Current timestamp
     */
    private static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return sdfDate.format(now);
    }

    public String getMac() {
        return mac;
    }
}
