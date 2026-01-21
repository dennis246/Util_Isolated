package harp.util;

public  class DebugUtil {

    // guage time elapsed 1
    static long fmainSeekPos = 0;
    static long threshold = 20000;
    static long startedAt = System.currentTimeMillis();
    static long intervalSwitchInd = 0;
    static long intervalSwitchFactor = 0;
    static long timeElapsed = 0;
    static long prevStopAt = System.currentTimeMillis();
    static int thresholdReachedInd = 0;

    public static void setupInitialTSTrace(long fmainSeekPos, long threshold) {
        threshold = 20000;
        startedAt = System.currentTimeMillis();
        intervalSwitchInd = 0;
        intervalSwitchFactor = 0;
        timeElapsed = 0;
        prevStopAt = System.currentTimeMillis();
        thresholdReachedInd = 0;
    }

    public static void validateTSTrace(long fmainSeekPos) {

        // long mainSeekPos, long threshold,
        // long initiatedAt,
        // long tfactInd,
        // long timeElapsed,
        // long prevStopAt,
        // int entryTshMarked

        if (fmainSeekPos > threshold) {

            if (thresholdReachedInd == 0) {
                var thresholdReachedAt = System.currentTimeMillis();
                System.out.println(("entryToTshAt: " + (thresholdReachedAt / 1000)));
                thresholdReachedInd = 1;
            }

            timeElapsed = (System.currentTimeMillis() - prevStopAt) / 1000;
            System.out.println("time elapsed: " + timeElapsed
                    + "      | tfactInd: " + intervalSwitchFactor + "      | mainSeekPos: " + fmainSeekPos);

            if (intervalSwitchFactor == 0) {
                threshold = fmainSeekPos + 10000;
                // tfactInd = 1;
            } else if (intervalSwitchFactor == 1) {
                threshold = fmainSeekPos * 2;
                intervalSwitchFactor = 0;
            }

            if (timeElapsed > 0) {
                var xh = "2";
            }

            prevStopAt = System.currentTimeMillis();
        }
    }

}
