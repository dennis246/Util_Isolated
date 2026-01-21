package harp.util;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public class Accummalater implements Serializable {

    private static Accummalater accummalater;
    private static ConcurrentHashMap<String, Object> primarySection;
    private static ConcurrentHashMap<String, Object> secondarySection;
    private static int lastSeekPos = -1;
    private static String currentID;

    public Accummalater() {

        try {
            currentID = StringUtil.randomIDGenerator(10, true, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Accummalater getAccummalater() {
        return accummalater;
    }

    public static void setAccummalater(Accummalater accummalater) {
        Accummalater.accummalater = accummalater;
    }

    public static String getCurrentID() {
        return currentID;
    }

    public static void setCurrentID(String currentID) {
        Accummalater.currentID = currentID;
    }

    public int accummalate(Object currenObject) {
        return 0;
    }

    public ConcurrentHashMap<String, Object> getPrimarySection() {
        return primarySection;
    }

    public void setPrimarySection(ConcurrentHashMap<String, Object> primarySection) {
        this.primarySection = primarySection;
    }

    public ConcurrentHashMap<String, Object> getSecondarySection() {
        return secondarySection;
    }

    public void setSecondarySection(ConcurrentHashMap<String, Object> secondarySection) {
        this.secondarySection = secondarySection;
    }

    public static int getLastSeekPos() {
        return lastSeekPos;
    }

    public static void setLastSeekPos(int lastSeekPos) {
        Accummalater.lastSeekPos = lastSeekPos;
    }

    public static Accummalater addToPrimarySection(String id, Object data) {

        if (accummalater.getPrimarySection() == null) {
            accummalater.setPrimarySection(new ConcurrentHashMap<>());
        }

        accummalater.getPrimarySection().put(id, data);
        return accummalater;

    }

}
