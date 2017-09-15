import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import redis.clients.jedis.Jedis;

public class Beacon {
    private int id;
    private int homegroup;
    private String mac;
    private ArrayList<Long> presenceData;
    private boolean presence = false;
    private int absentInterval = 10;
    private int checkInterval = 10;
    private String JEDIS_SERVER = "localhost";

    public Beacon(int minorID, int homegroup, String mac) {
        this.id = minorID;
        this.homegroup = homegroup;
        this.mac = mac;
        presenceData = new ArrayList<Long>();
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
        tm.scheduleAtFixedRate(ct, (checkInterval * 1000), (checkInterval * 1000));
    }

    /**
     * This function publishes message to redis in new thread
     * @param channel Channel to publish to
     * @param message Message to be published
     */
    private void publishRedis(Integer channel, String message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Jedis jedis = new Jedis(JEDIS_SERVER);
                    String publishChannel = channel + "-parsed";
                    jedis.publish(publishChannel, message);
                    jedis.quit();
                } catch (Exception e) {
                    printWithTime("Error: " + e.getMessage());
                }
            }
        }, "redisPublisherThread").start();

    }
    /**
     * Compare current time and last seen time from raw data. If not seen in X time mark as absent.
     * @param absentInterval If not seen in absentInterval mark as absent.
     * @return true if present, false if absent.
     */
    private boolean checkPresence() {
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
        return presence;
    }

    public int getID() {
        return this.id;
    }

    public String getMAC() {
        return this.mac;
    }

    public int getHomegroup(){ return this.homegroup; }

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
     * @return Current timestamp in format:
     */
    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }
}
