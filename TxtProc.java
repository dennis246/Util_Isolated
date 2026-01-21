package harp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TxtProc {
/* 
    private static void main(String ag[]) { */

        /*
         * char[] charArr = {'3','4','7','5','.','2','3','5'};
         * var res = extractValFromIntegerSeq(charArr, 0);
         * System.out.println(res);
         */

        
        /*
         * char[] ip = { ']' };
         * var res = checkClosingSeq(ip);
         * System.out.println(res);
         */

        // String contentLocation = "D:\\WS-P\\WS\\Rweather.json";
      /*   String contentLocation = "D:\\WS-P\\WS\\Rweather.json";
        String content = FileUtil.fileToString(contentLocation);
        try {
            initTxtProc(content);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    } */

    static Map<String, Object> utilInfo = new LinkedHashMap<>();

    static int mainSeekPos = 0;
    static Integer[] mainSeekPosHistory = new Integer[0];

    static Integer[] mainSeekPosStops = new Integer[0];
    // static int mainSeekPosPrev = 0;
    static int endOfContent = 1;
    static String currentSeqType = null;
    static String currentSeq = "";
    static Class<?> currentValueType;
    static int steppedInLayer = 0;
    static int stepOutInd = 0;
    // static char[] stepSequence = new char[5];

    static LinkedHashMap<String, Object> contentMap = new LinkedHashMap<>();
    static LinkedHashMap<String, Object> innerMap = new LinkedHashMap<>();

    private static String jsonString = "";


    public static LinkedHashMap<String,Object> stringToMap(String content){
         // remove comments from content
         content = removeComments(content);
         content = setBoundsToJSONContent(content);
         content = linearizeJSONContent(content);
 
         // content = indentJSONContent(content);
         char[] contentArr = content.toCharArray();
 
         mainSeekPos = 0;
         endOfContent = 0;
         contentMap = addToMap(contentArr, contentMap);

         return contentMap;
    }

    private static void initTxtProc(String content) {

        // remove comments from content
        content = removeComments(content);
        content = setBoundsToJSONContent(content);
        content = linearizeJSONContent(content);

        // content = indentJSONContent(content);
        char[] contentArr = content.toCharArray();

        mainSeekPos = 0;
        endOfContent = 0;
        contentMap = addToMap(contentArr, contentMap);
        //System.out.println("end of txt proc: " + contentMap);
        //System.out.println("info: " + utilInfo.toString());
        //System.out.println("info: " + mainSeekPosStops);

        jsonString = "";
        unravelNode(contentMap);
        System.out.println("jsonstring:\n\n " + jsonString);

        FileUtil.stringToFile(jsonString, "D:\\WS-P\\WS\\UVLOutput.txt");

        // System.out.print("end of txt proc: " + contentMap.toString());

        // last map entry
        // Map<String, Object> horizontalMap = flatten(contentMap);
        // System.out.print("HorizontalMap: " + horizontalMap.toString());

    }

    /*
     * private static String parse(LinkedHashMap<String,Object> currentMap) {
     * 
     * unravelNode(currentMap);
     * 
     * }
     */

    private static void unravelNode(LinkedHashMap<String, Object> currentMap) {
        jsonString += "{";
        Set<String> cset = currentMap.keySet();
        int runningIndex = 0;
        for (var crow : cset) {

            String currentID = crow;
            Object currentValue = currentMap.get(crow);

            if (currentValue instanceof LinkedHashMap) {
                jsonString += currentID + ":";
                LinkedHashMap<String, Object> cmap = (LinkedHashMap<String, Object>) currentValue;
                unravelNode(cmap);
                // jsonString += "}";

            } else if (currentValue instanceof Array) {

                String cvStr = currentValue.toString();
                jsonString += "{" + currentID + ":" + cvStr + "}";

            } else if (currentValue instanceof Object[]) {
                
                Object[] cvArr = (Object[]) currentValue;
                jsonString += currentID + ":" + "[";
                
                for (var arrow : cvArr) { 
                    // Object cvInner = arrow;

                    if (arrow instanceof Map) {
                        LinkedHashMap<String, Object> cvInnerMap = (LinkedHashMap<String, Object>) arrow;
                        unravelNode(cvInnerMap);
                    }

                }

                jsonString += "]";

            } else {

                String cvStr = currentValue.toString();
                jsonString += currentID + ":" + cvStr;
            }

            if (runningIndex < cset.size() - 1) {
                jsonString += ",";
            }

            ++runningIndex;
        }

        jsonString += "}";

    }

    private static String setBoundsToJSONContent(String content) {

        String finalContent = "";
        int runningIndex = 0;
        try {

            // todo remove single multi line comments and trim id and data and linearize
            char[] contentArr = content.toCharArray();
            char[] contentArrBound = new char[0];

            if (contentArr[0] != '{') {
                // default Bound
                /*
                 * String id = StringUtil.randomIDGenerator(5, true, false, false);
                 * 
                 * char[] openBoundArr = new char[0];
                 * openBoundArr = addToCharArray(openBoundArr, '{');
                 * openBoundArr = addAllToCharArray(openBoundArr, id.toCharArray());
                 * openBoundArr = addToCharArray(openBoundArr, ':');
                 * 
                 * contentArrBound = addAllToCharArray(contentArrBound, openBoundArr);
                 * 
                 * contentArrBound = addAllToCharArray(contentArrBound, contentArr);
                 * 
                 * char[] closeBoundArr = new char[0];
                 * closeBoundArr = addToCharArray(closeBoundArr, '}');
                 * 
                 * contentArrBound = addAllToCharArray(contentArrBound, closeBoundArr);
                 * finalContent = new String(contentArrBound);
                 */

                // or
                String id = StringUtil.randomIDGenerator(5, true, true, false);
                finalContent = "{" + "\"" + id + "\"" + ":" + content + "}";
            } else {
                finalContent = content;
            }

        } catch (Exception e) {
            e.getStackTrace();
        }

        return finalContent;

    }

    private static Map<String, Object> flatten(LinkedHashMap<String, Object> verticalMap) {

        Map<String, Object> fmap = new LinkedHashMap<>();
        for (var id : verticalMap.keySet()) {

        }

        return fmap;

    }

    private static Map<String, Object> mirror(LinkedHashMap<String, Object> verticalMap) {

        Map<String, Object> fmap = new LinkedHashMap<>();
        for (var id : verticalMap.keySet()) {
            var val = verticalMap.get(id);
            if (val instanceof Map) {
                LinkedHashMap<String, Object> innerMap = (LinkedHashMap<String, Object>) val;
                fmap.put(id, mirror(innerMap));
            } else {
                fmap.put(id, fmap);
            }
        }

        return fmap;

    }

    private static String linearizeJSONContent(String content) {

        String finalContent = "";
        int runningIndex = 0;
        try {

            // todo remove single multi line comments and trim id and data and linearize
            char[] contentArr = content.toCharArray();
            char[] contentArrTrm = new char[0];
            // char[] resArr = new char[0];
            // int prevIndex = 0;
            for (int i = 0; i < contentArr.length; i++, runningIndex++) {

                int cp = contentArr[i];

                /*
                 * if (i > 249) {
                 * System.out.println("ecwbounds" + i);
                 * }
                 */

                if (cp < 32 || contentArr[i] == '\0' || contentArr[i] == '\s' || contentArr[i] == ' '
                        || contentArr[i] == '\n') {
                    continue;
                }

                if (contentArr[i] == '"') {
                    // char[] chwqArr = extractContentWithinBounds(contentArr, i, '"', '"');
                    char[] chwqArr = extractId(contentArr, i).toCharArray();
                    i = (Integer) utilInfo.get("mainLoopEndedAt");

                    char[] resArr = new char[0];
                    resArr = addAllToCharArray(resArr, chwqArr);
                    contentArrTrm = addAllToCharArray(contentArrTrm, resArr);
                } else {
                    contentArrTrm = addToCharArray(contentArrTrm, contentArr[i]);
                }

                if (utilInfo.get("endOfSeek") != null && (Integer) utilInfo.get("endOfSeek") == 1) {
                    break;
                }

            }

            finalContent = new String(contentArrTrm);
            FileUtil.stringToFile(finalContent, "D:\\WS-P\\WS\\LRZOutput.txt");

        } catch (Exception e) {
            System.out.println("Error at Running Index @ " + runningIndex);
            System.out.println(e.toString());
        }
        return finalContent;
    }

    static char[] extractContentWithinBounds(char[] contentArr, int from, char openingBound, char closingBound) {

        if (openingBound == '"' && closingBound == '"') {
            // char[] res = extractId(contentArr, from).toCharArray();
            // return res;
        } else {
            System.out.println("newbounds");
        }

        char[] chwqArr = new char[0];
        int boundCount = 1;
        int runningIndex = 0;
        int seqOpened = 0;

        if (contentArr[from] == '"') {
            boundCount = 1;
        } /*
           * else if (contentArr[from] == ':') {
           * boundCount = 2;
           * }
           */

        seqOpened = boundCount == 1 ? 1 : 0;

        Character[] breakList = { '"', ':', ',', '}', ']' };
        int res = has(breakList, ':');

        try {

            for (int i = from; i < contentArr.length; i++, runningIndex++) {

                if (i > 248) {
                    System.out.println("ecwbounds" + i);
                }

                if ((boundCount > 1 && has(breakList, contentArr[i]) == 1)) {
                    utilInfo.put("mainLoopEndedAt", i - 1);
                    if (i == contentArr.length - 1) {
                        utilInfo.put("endOfSeek", 1);
                    } else {
                        utilInfo.put("endOfSeek", 0);
                    }

                    // include if not added
                    if (chwqArr[0] == '\0') {
                        chwqArr = addToCharArray(chwqArr, contentArr[i]);
                    }

                    break;
                }

                if (contentArr[i] == '"' || contentArr[i] == ':') {

                    if (chwqArr.length > 0) {
                        int cpPrev = contentArr[i - 1];
                        if (chwqArr.length > 0 && cpPrev >= 32 && cpPrev <= 127) {
                            ++boundCount;
                        }
                    }
                }

                int cp = contentArr[i];
                if (cp >= 32 && cp <= 127) {
                    chwqArr = addToCharArray(chwqArr, contentArr[i]);
                }

            }

            var seekX = (from - 1) + runningIndex;
            if (seekX >= contentArr.length - 1) {
                utilInfo.put("endOfSeek", 1);
            } else {
                utilInfo.put("endOfSeek", 0);
            }

            if (chwqArr.length > 0 && chwqArr[0] == ':') {
                if (isAlphaNumericSeq(chwqArr) == 0) {
                    chwqArr = removeSpaces(chwqArr);
                }
            }

            if (chwqArr.length == 0) {
                throw new Exception("extractContentWithinBounds is empty");
            }

        } catch (Exception e) {
            System.out.println("Error @ " + from + "running ind: " + runningIndex);
            System.out.println(e.toString());
        }

        return chwqArr;

    }

    private static int isAlphaNumericSeq(char[] currentArr) {

        int alphaNumericCount = 0;
        int symbolCount = 0;
        for (int i = 0; i < currentArr.length; i++) {
            int cp = currentArr[i];

            if ((cp >= 48 && cp <= 57) || (cp >= 65 && cp >= 90) || (cp >= 97 && cp <= 122)) {
                ++alphaNumericCount;
            } else {
                ++symbolCount;
            }
        }

        if (symbolCount > 0) {
            return 0;
        }
        return 0;

    }

    private static char[] removeSpaces(char[] currentArr) {

        char[] trimArr = new char[0];
        for (int i = 0; i < currentArr.length; i++) {

            int cp = currentArr[i];
            if (cp == 32) {
                continue;
            } else {
                trimArr = addToCharArray(trimArr, currentArr[i]);
            }
        }

        return trimArr;

    }

    private static String removeComments(String content) {

        String finalLines = "";
        String[] lines = content.split("\n");
        List<String> validLines = new ArrayList<>();
        for (String line : lines) {
            if (line.trim().startsWith("*") || line.trim().startsWith("/*") || line.trim().startsWith("//")) {
                continue;
            } else {
                validLines.add(line);
            }
        }

        finalLines = String.join("\n", validLines); // validLines.toString();
        return finalLines;

    }

    private static LinkedHashMap<String, Object> addToMap(char[] currentArr, LinkedHashMap<String, Object> currentMap) {

        try {

            String currentID = null;
            Object currentValue = null;

            do {

                /* if (mainSeekPos >= 225) {
                    System.out.println("e_stop @ " + mainSeekPos);
                } */

                String cresult = validateSeqType(currentArr, mainSeekPos);
                if (cresult == null || !cresult.equals("id") /* && mainSeekPos < currentArr.length - 1 */) {
                    if (stepOutInd == 1) {
                        stepOutInd = 0;
                    }

                    return currentMap;
                }

                currentSeq += cresult;
                if (steppedInLayer > 0) {

                    if (stepOutInd > 0) {
                        stepOutInd = 0;
                        --steppedInLayer;
                        currentSeq = "";
                        return currentMap;
                    }

                    if (cresult == "stepOutInd" && mainSeekPos != currentArr.length - 1) {
                        currentSeq = "";
                        continue;
                    }

                }

                if (cresult == "id" || cresult == "id_inObj") {
                    currentID = extractId(currentArr, mainSeekPos);
                }

                cresult = validateSeqType(currentArr, mainSeekPos);
                currentSeq += cresult;
                if (cresult == "value") {
                    currentValue = extractValue(currentArr, mainSeekPos);
                    if (currentValue != null && currentValue.toString().length() > 40) {
                        // System.out.println("d: " + currentValue.toString());
                    }
                }

                if (currentSeq.equals("idvalue") || currentSeq.equals("id_inObjvalue")
                        || (currentID != null && currentValue != null)) {
                    currentMap.put(currentID, currentValue);

                } else if (currentSeq.equals("idid")) {
                    ++steppedInLayer;
                    currentSeq = "";
                    LinkedHashMap<String, Object> c2Map = new LinkedHashMap<>();
                    currentMap.put(currentID, addToMap(currentArr, c2Map));
                } else if (currentSeq.equals("idstepOutInd")) {
                    currentMap.put(currentID, "");
                }

                // common
                currentID = null;
                currentValue = null;
                currentSeq = "";

                if (stepOutInd > 0) {
                    stepOutInd = 0;
                    --steppedInLayer;
                    currentSeq = "";
                    return currentMap;
                }

            } while (isEndOfContent(currentArr) != 1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return currentMap;
    }

    private static void adjustMainSeekPos(char[] currentArr, int rindex) {

        try {

            int isBoundOrBreakChar = 0;
            Character[] breakList = { '"', ':', ',', '{', '[', '}', ']' };
            int dir = 0;

            do {
                char x = currentArr[mainSeekPos];
                // System.out.println("x@" + x);
                if (has(breakList, x) == 0) {
                    mainSeekPos -= 1;
                    dir = -1;
                    isBoundOrBreakChar = 0;
                } else {
                    --mainSeekPos;
                    isBoundOrBreakChar = 1;
                    var nextC = currentArr[mainSeekPos + 1];

                    if (has(breakList, nextC) == 1) {
                        // mainSeekPos -= 1;
                        isBoundOrBreakChar = -1;
                        break;
                    } else {
                        // determine next value part
                        mainSeekPos -= 1;
                        dir = -1;
                    }

                }
            } while (isBoundOrBreakChar != 1);

        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    private static int isAlphaNumeric(char x) {

        if (x > 32 && x < 177) {
            return 1;
        } else {
            return 0;
        }

    }

    private static String validateSeqType(char[] currentArr, int from) {

        currentSeqType = null;
        currentValueType = null;

       /*  if (mainSeekPos >= 258) {
            System.out.println("e_stop @ " + mainSeekPos);
        } */

        mainSeekPosStops = addToIntegerArray(mainSeekPosStops, from);

        try {

            int i = from;

            // validate: only for debugging
            if (i + 1 > currentArr.length - 1) {
                stepOutInd = steppedInLayer > 1 ? 1 : 0;
                // mainSeekPos += 1;
                currentSeqType = "stepOutInd";
                return currentSeqType;
            }

            if ((currentArr[i] == '{' || currentArr[i] == ',') && currentArr[i + 1] == '"') {
                currentSeqType = "id";
                mainSeekPos += 1;
                if (currentArr[i] == '{') {
                    ++steppedInLayer;
                }
            } else if (currentArr[i] == ',' && currentArr[i + 1] == '{') {
                currentSeqType = "id";
                ++steppedInLayer;
                // mainSeekPos += 1;
                mainSeekPos += 1;
            } else if (currentArr[i] == '[' && currentArr[i + 1] == '{') {
                // obj array start
                ++steppedInLayer;
                currentSeqType = "id";
                mainSeekPos += 1;
            } else if (steppedInLayer > 0 && currentArr[i] == '"') {
                // currentSeqType = "id_inObj";
                // currentSeqType = "id";
                // mainSeekPos += 1; // single curly brace
            } else if (currentArr[i] == '"' && currentArr[i + 1] == ',') {
                mainSeekPos += 1;
            }

            else if (currentArr[i] == ':' || (currentArr[i] == '"' && currentArr[i + 1] == ':')) {

                currentSeqType = "value";

                // consider arr
                if (currentArr[i + 1] == '[') {
                    currentValueType = Array.class;
                    mainSeekPos += 2;
                } else if (currentArr[i + 1] == '{') {
                    currentSeqType = "id";
                    currentValueType = String.class;
                    mainSeekPos += 1;
                    steppedInLayer += 1;
                } else if (currentArr[i + 1] == '"') {
                    currentSeqType = "value";
                    currentValueType = String.class;
                    mainSeekPos += 1;
                } else if (checkIfBooleanSeq(currentArr, i + 1) == 1) {
                    currentValueType = Boolean.class;
                    mainSeekPos += 1;
                } else if (checkIfIntegerSeq(currentArr, i + 1) == 1) {
                    currentValueType = Integer.class;
                    mainSeekPos += 1;
                } else if (checkIfBigDecimalSeq(currentArr, i + 1) == 1) {
                    currentValueType = BigDecimal.class;
                    mainSeekPos += 1;
                } else if (checkIfDoubleSeq(currentArr, i + 1) == 1) {
                    currentValueType = Double.class;
                    mainSeekPos += 1;
                } else {
                    currentValueType = String.class;
                    mainSeekPos += 1;
                }

                // break;
            } else if (currentArr[i] == ']' || currentArr[i] == '}') {
                var x = mainSeekPos;
                if (currentArr[i] == ']' && currentArr[i + 1] == ',') {
                    currentSeqType = "id";
                }

                stepOutInd = steppedInLayer > 1 ? 1 : 0;
                mainSeekPos += 1;
                currentSeqType = "stepOutInd";
            }
        } catch (Exception e) {
            /*
             * if (mainSeekPosHistory.length > 0) {
             * System.out.println("Error near: mspp " +
             * mainSeekPosHistory[mainSeekPosHistory.length - 1]);
             * }
             * System.out.println("Error near: msp " + mainSeekPos);
             * System.out.println(e.toString());
             */
            System.out.println("Error near: msp " + mainSeekPos);
            e.printStackTrace();
        }

        return currentSeqType;

    }

    private static int checkIfDoubleSeq(char[] blockArr, int from) {

        int matchCount = 0;
        int seekLen = 0;

        try {

            for (int i = from; i < blockArr.length; i++, seekLen++) {

                if (blockArr[i] == ',' || blockArr[i] == '}') {
                    break;
                }

                int cp = blockArr[i];

                if ((cp >= 48 && cp <= 57) || cp == '.') {
                    matchCount += 1;
                } else {
                    // return 0;
                }

            }

            if (matchCount < seekLen) {
                return 0;
            } else if (matchCount == seekLen) {
                return 1;
            }

        } catch (Exception e) {
            System.err.println(e.toString());
            return 0;
        }

        return 0;

    }

    private static int checkIfBigDecimalSeq(char[] blockArr, int from) {

        int matchCount = 0;
        int seekLen = 0;
        int crossedDecPoint = 0;
        int decPlaceCount = 0;
        try {

            for (int i = from; i < blockArr.length; i++, seekLen++) {

                if (blockArr[i] == ',' || blockArr[i] == '}') {
                    break;
                }

                if (blockArr[i] == '.') {
                    crossedDecPoint = 1;
                }

                int cp = blockArr[i];

                if ((cp >= 48 && cp <= 57) || cp == '.') {
                    matchCount += 1;
                    if (crossedDecPoint == 1) {
                        ++decPlaceCount;
                    }
                } else {
                    // return 0;
                }

            }

            if (matchCount < seekLen) {
                return 0;
            } else if (matchCount == seekLen && decPlaceCount >= 2) {
                return 1;
            }

        } catch (Exception e) {
            System.err.println(e.toString());
            return 0;
        }

        return 0;

    }

    private static int checkIfIntegerSeq(char[] blockArr, int from) {

        // char[] numArr = new char[0];
        // int[] baseNums = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        int matchCount = 0;
        // int nonIntCharCount = 0;
        int seekLen = 0;

        try {

            for (int i = from; i < blockArr.length; i++, seekLen++) {

                if (blockArr[i] == ',' || blockArr[i] == '}') {
                    break;
                }

                int cp = blockArr[i];

                if (cp >= 48 && cp <= 57) {
                    matchCount += 1;
                } else {
                    // return 0;
                }

            }

            if (matchCount < seekLen) {
                return 0;
            } else if (matchCount == seekLen) {
                return 1;
            }

        } catch (Exception e) {
            System.err.println(e.toString());
            return 0;
        }

        return 0;

    }

    private static int checkIfBooleanSeq(char[] blockArr, int from) {

        char[] wordArr = new char[0];
        for (int i = from; i < from + 5; i++) {
            if (blockArr[i] == ',' || blockArr[i] == '}') {
                break;
            }
            wordArr = addToCharArray(wordArr, blockArr[i]);
        }

        String word = new String(wordArr);

        if (word.equals("true") || word.equals("false")) {
            return 1;
        } else {
            return 0;
        }

    }

    private static String errorAt2(char[] blockArr, int stopAt) {

        return String.valueOf(blockArr).substring(0, stopAt);
    }

    private static String errorAt(char[] blockArr, int stopAt) {

        char[] ecArr = new char[0];
        for (int i = 0; i < blockArr.length; i++) {

            if (i == stopAt) {
                break;
            } else {
                ecArr = addToCharArray(ecArr, blockArr[i]);
            }
            // System.out.println(blockArr[i]);
        }

        return new String(ecArr);
    }

    private static String extractId(char[] currentArr, int from) {

        String extractID = "";
        char[] extractIDArr = new char[0];
        int boundSeq = 0;
        int runningIndex = 0;
        int breakFree = 0;
        for (int i = from, pi = 0; i < currentArr.length; i++) {

            if (breakFree == 1) {
                break;
            }

            if (currentArr[i] == '"') {
                ++boundSeq;
                if (boundSeq > 0 && i != from) {
                    if (i < currentArr.length - 1) {
                        endOfContent = 0;
                        mainSeekPos += pi + 1; // as per quote
                    }

                    runningIndex += i;
                    breakFree = 1;
                    utilInfo.put("mainLoopEndedAt", runningIndex);
                }
            }

            // extractIDArr[pi] = blockArr[i];
            extractIDArr = addToCharArray(extractIDArr, currentArr[i]);
            pi++;

        }

        // mainSeekPos += runningIndex + 1;
        extractID = new String(extractIDArr).trim();
        return extractID;

    }

    private static Object extractValue(char[] currentArr, int from) {

        Object extractVal = null;

        try {

            if (currentValueType == String.class || currentValueType == Boolean.class) {
                extractVal = extractValFromStringSeq(currentArr, from);
            } else if (currentValueType == Integer.class) {
                extractVal = extractValFromIntegerSeq(currentArr, from);
            } else if (currentValueType == BigDecimal.class) {
                extractVal = extractValFromBigDecimalSeq(currentArr, from);
            } else if (currentValueType == Double.class) {
                extractVal = extractValFromDoubleSeq(currentArr, from);
            } else if (currentValueType == Array.class) {
                extractVal = extractValueFromArraySeq(currentArr, from);
            } else {
                extractVal = extractValFromObjectSeq(currentArr, from);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return extractVal;
    }

    private static int sumOf(int[] numArr) {
        int sum = 0;
        for (int i = 0; i < numArr.length; i++) {
            sum += numArr[i];
        }

        return sum;
    }

    private static Object extractValueFromArraySeq(char[] currentArr, int from) {

        try {

            /*
             * if (from > 39922) {
             * var x = 22;
             * }
             */

            char[] jsonArr = trimArrayByOpeningSeq(currentArr, --from, currentArr[from]);
            currentSeq = "idvalue";
            inspectArray(jsonArr, 0);
            int isJSONObjArray = utilInfo.get("isJSONObjectArray") != null
                    && (Integer) utilInfo.get("isJSONObjectArray") == 1 ? 1 : 0;

            /*
             * if (utilInfo.get("isJSONObjectArray") != null
             * && (Integer) utilInfo.get("isJSONObjectArray") == 1) {
             */

            if (isJSONObjArray == 1) {
                ++steppedInLayer;
                mainSeekPosHistory = addToIntegerArray(mainSeekPosHistory, mainSeekPos);
                mainSeekPos = 0;
                currentSeq = "";
                LinkedHashMap<String, Object> innerMap = new LinkedHashMap<>();
                Object[] msa = new Object[0];

                do {

                    /*
                     * if (mainSeekPos >= 34647) {
                     * System.out.println(mainSeekPos);
                     * }
                     */
                    if(jsonArr[1] =='{'){
                        ++mainSeekPos;
                    }

                    innerMap = new LinkedHashMap<>();
                    currentSeq = "";
                    var cmap = addToMap(jsonArr, innerMap);
                    if (cmap != null && !cmap.isEmpty()) {
                        // System.out.println(cmap);
                        msa = add(msa, cmap);
                    } else {

                        if (mainSeekPos < jsonArr.length - 1) {
                            ++mainSeekPos;
                        }

                    }
                    // ++jsonArrSeekPos;
                } while (mainSeekPos < jsonArr.length - 1);

                /*
                 * if (mainSeekPos == 219) {
                 * var x = 2;
                 * }
                 */

                int innerSeekPos = mainSeekPos;
                var prev = mainSeekPosHistory[mainSeekPosHistory.length - 1];
                mainSeekPos = prev + innerSeekPos;
                // mainSeekPos += last;
                mainSeekPosHistory = removeFromIntegerArray(mainSeekPosHistory, prev); // sumFrom(mainSeekPosHistory);
                return msa; // addToMap(jsonArr, innerMap);
            } else {

                // consider as plain string array
                // jsonArr = trimArrayByOpeningSeq(currentArr, from, currentArr[from - 1]);
                currentSeq = "";
                Set<String> strSet = new LinkedHashSet<String>();
                char[] strSeq = new char[0];
                int openingBound = 0;
                for (int i = 1, pi = 0; i < jsonArr.length; i++) {

                    if (jsonArr[i] == '[') {
                        continue;
                    }

                    if (jsonArr[i] == ',' || jsonArr[i] == ']') {
                        // strSeq = addToCharArray(strSeq, jsonArr[i]);
                        strSet.add(new String(strSeq));
                        strSeq = new char[0];
                        openingBound = 0;

                        if (jsonArr[i] == ']') {
                            break;
                        } else {
                            ++i;
                        }
                    }

                    strSeq = addToCharArray(strSeq, jsonArr[i]);

                    /*
                     * applies if array elements were like strings
                     * if (jsonArr[i] == '"') {
                     * ++openingBound;
                     * }
                     * 
                     * if (jsonArr[i] == '"' && openingBound == 2) {
                     * strSeq = addToCharArray(strSeq, jsonArr[i]);
                     * strSet.add(new String(strSeq));
                     * strSeq = new char[0];
                     * openingBound = 0;
                     * ++i;
                     * } else {
                     * strSeq = addToCharArray(strSeq, jsonArr[i]);
                     * }
                     */
                }

                /*
                 * int innerSeekPos = mainSeekPos; mainSeekPos = mainSeekPosPrev + innerSeekPos;
                 * mainSeekPosPrev = mainSeekPos;
                 */
                mainSeekPos += jsonArr.length - 1;
                return strSet;

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("exValFromArraySeq: stopped @" + from);
        }

        return null;

    }

    private static int inspectArrayL1(char[] fragment, int i) {

        char[][] sequences = {
                { '"', ':', '"' },
                { '"', ',', '"' },
                { '"', '}', ',' },
                { '"', '}', '}' },
                { '"', '}', ']' },
                { '"', ']', '}' },
                { '"', ']', ']' },
                { ']', ']', ']' },
                { '}', '}', '}' },
                { '"', ':' },
                { ':', '"' },
                { '{', '"' },
                { '[', '"' },
                { '"', '}' },
                { '"', ']' }
        };

        int exactMatch = 0;
        int matchCount = 0;

        try {

            for (int g = 0; g < sequences.length; g++) {

                matchCount = 0;
                exactMatch = 0;
                char[] cseq = sequences[g];

                if (cseq.length != fragment.length) {
                    continue;
                }

                int sp = 0;
                for (int h = 0; h < cseq.length; h++) {
                    if (fragment[sp] == cseq[h]) {
                        ++matchCount;
                        ++sp;
                    } else {
                        break;
                    }
                }

                if (matchCount == fragment.length) {
                    exactMatch = 1;

                    utilInfo.put("clseq_exactMatch", 1);
                    utilInfo.put("clseq_matchScore", matchCount);

                    return 1;
                }

            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return 0;

    }

    private static Object extractValFromObjectSeq(char[] currentArr, int from) {
        Object[] objArr = new Object[0];

        char[] extractValArr = new char[0];
        for (int i = from, pi = 0, objPi = 0; i < currentArr.length; i++) {

            if (currentArr[i] == ']') {

                if (i < currentArr.length - 1) {
                    endOfContent = 0;
                    // mainSeekPos += i;
                }

                Object cvalue = new String(extractValArr).trim();
                objArr = add(objArr, cvalue);

                mainSeekPos += (i - from) + 1;
                break;

            } else if (currentArr[i] == ',') {

                Object cvalue = new String(extractValArr).trim();
                // objArr[objPi] = cvalue;
                objArr = add(objArr, cvalue);
                objPi++;
                extractValArr = new char[0];

            } else {
                // add(extractValArr, blockArr[i]);
                // extractValArr[pi] = blockArr[i];
                extractValArr = addToCharArray(extractValArr, currentArr[i]); //
                pi++;
            }

        }

        return objArr;
    }

    private static Object extractValFromIntegerSeq(char[] currentArr, int from) {
        Integer[] extractValArr = new Integer[0];
        for (int i = from, pi = 0; i < currentArr.length; i++) {

            int current = currentArr[i] - 48;

            if (currentArr[i] == ',' || currentArr[i] == '}'
                    || (currentValueType == Boolean.class && currentArr[i] == '}')) {
                mainSeekPos += pi;
                isEndOfContent(currentArr);
                break;
            } else {
                // extractValArr[pi] = blockArr[i];
                extractValArr = addToIntegerArray(extractValArr, current);
                pi++;
            }

        }

        // Double maxint = 999999999.9999999999;
        // int maxint = 999999999;
        Object finalNum = extractValArr.length > 9 ? arrayToBigInteger(extractValArr)
                : arrayToInteger(extractValArr);

        return finalNum;
    }

    private static Object extractValFromBigDecimalSeq(char[] currentArr, int from) {

        char[] extractValArr = new char[0];
        for (int i = from, pi = 0; i < currentArr.length; i++) {

            int current = currentArr[i] - 48;

            if (currentArr[i] == ',' || currentArr[i] == '}' || currentArr[i] == ']'
                    || (currentValueType == Boolean.class && currentArr[i] == '}')) {
                mainSeekPos += pi;
                isEndOfContent(currentArr);
                break;
            } else {
                // extractValArr[pi] = blockArr[i];
                extractValArr = addToCharArray(extractValArr, currentArr[i]);
                pi++;
            }

        }

        Object finalNum = (BigDecimal) arrayToBigDecimal(extractValArr);
        return finalNum;
    }

    private static Object extractValFromDoubleSeq(char[] currentArr, int from) {

        char[] extractValArr = new char[0];
        for (int i = from, pi = 0; i < currentArr.length; i++) {

            int current = currentArr[i] - 48;

            if (currentArr[i] == ',' || currentArr[i] == '}' || currentArr[i] == ']'
                    || (currentValueType == Boolean.class && currentArr[i] == '}')) {
                mainSeekPos += pi;
                isEndOfContent(currentArr);
                break;
            } else {
                // extractValArr[pi] = blockArr[i];
                extractValArr = addToCharArray(extractValArr, currentArr[i]);
                pi++;
            }

        }

        Object finalNum = (Double) arrayToDouble(extractValArr);
        return finalNum;

    }

    private static String extractValFromStringSeq(char[] currentArr, int from) {

        char[] extractValArr = new char[0];
        int i = 0, pi = 0;
        int breakFree = 0;
        String extractVal = "";
        char[][] sequences = {
                { '"', ':', '"' },
                { '"', ',', '"' },
                { '"', '}', ',' },
                { '"', '}', '}' },
                { '"', '}', ']' },
                { '"', ']', '}' },
                { '"', ']', ']' },
                { ']', ']', ']' },
                { '}', '}', '}' },
                { ',' },
                { '}' },
                { ']' }
                // { ',', '"' },
                // { '}', ',' }
        };

        try {

            for (i = from, pi = 0; i < currentArr.length; i++) {

                if (i == currentArr.length - 1 || breakFree == 1) {
                    break;
                }

                int cp = currentArr[i];
                int cpNext = currentArr[i + 1];
                if (extractValArr.length > 2 && i + 1 <= currentArr.length - 1 && cpNext >= 32) {

                    char[] ts;
                    if (currentValueType == Boolean.class) {
                        char[] smp = { currentArr[i] };
                        ts = smp;
                    } else {
                        char[] smp = { extractValArr[extractValArr.length - 1], currentArr[i], currentArr[i + 1] };
                        ts = smp;
                    }

                    int score = checkForExactMatchFromSeqSet(ts, sequences);
                    if (score == 1) {
                        breakFree = 1;

                        if (currentValueType == Boolean.class) {
                            mainSeekPos += pi;
                        } else {
                            mainSeekPos += pi;
                        }

                    } else {
                        breakFree = 0;
                    }
                }

                if (cp >= 32 && breakFree == 0) {
                    extractValArr = addToCharArray(extractValArr, currentArr[i]);
                    pi++;
                }

            }

            extractVal = new String(extractValArr).trim();

            // only for debugging
            // char[] seq = { '{', '}' };

            if (checkForExactMatchFromSeqSet(extractValArr, sequences) == 1) {
                System.out.println("Error in extractFromStringSeq @ " + mainSeekPos);
                System.out.println(extractVal);
                System.out.println("invalid value seq");

                var tr = mainSeekPosStops;
            }

        } catch (Exception e) {
            System.out.println(e.toString());
            var tr = mainSeekPosStops;
        }

        return extractVal;
    }

    private static int checkForExactMatchFromSeqSet(char[] fragment, char[][] sequences) {

        /*
         * char[][] sequences = {
         * { '"', ':', '"' },
         * { '"', ',', '"' },
         * { '"', '}', ',' },
         * { '"', '}', '}' },
         * { '"', '}', ']' },
         * { '"', ']', '}' },
         * { '"', ']', ']' },
         * { ']', ']', ']' },
         * { '}', '}', '}' },
         * { ',' },
         * { '}' },
         * { ']' }
         * // { ',', '"' },
         * // { '}', ',' }
         * };
         */

        int exactMatch = 0;
        int matchCount = 0;

        try {

            for (int g = 0; g < sequences.length; g++) {

                matchCount = 0;
                exactMatch = 0;
                char[] cseq = sequences[g];

                if (cseq.length != fragment.length) {
                    continue;
                }

                int sp = 0;
                for (int h = 0; h < cseq.length; h++) {
                    if (fragment[sp] == cseq[h]) {
                        ++matchCount;
                        ++sp;
                    } else {
                        break;
                    }
                }

                if (matchCount == fragment.length) {
                    exactMatch = 1;

                    utilInfo.put("clseq_exactMatch", 1);
                    utilInfo.put("clseq_matchScore", matchCount);

                    return 1;
                }

            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return 0;

    }

    private static int checkClosingSeq2(char[] fragment) {

        int cscount = 0;
        Character[] breakList = { '"', ':', ',', '}', ']' };

        for (int i = 0; i < fragment.length; i++) {
            int cp = fragment[i];
            if (cp >= 32 && has(breakList, fragment[i]) == 1) {
                ++cscount;
            }
        }

        return cscount;

        /*
         * if(cscount >= 2){
         * return 1;
         * }
         * 
         * return 0;
         */
    }

    private static Object extractFromStringSeq2(char[] currentArr, int from) {
        // String extractVal = "";
        int runningIndex = 0;
        char[] extractValArr = new char[0];
        for (int i = from, pi = 0; i < currentArr.length; i++) {

            if ((currentArr[i] == '"' && (currentArr[i + 1] == ',' || currentArr[i + 1] == '}'))
                    || (currentValueType == Boolean.class && (currentArr[i] == '}' || currentArr[i] == ','))) {

                if (i < currentArr.length - 1) {
                    endOfContent = 0;
                    // mainSeekPos += i;
                }

                if (currentValueType == Boolean.class) {
                    mainSeekPos += pi;
                } else {
                    mainSeekPos += pi + 1;
                }

                runningIndex += i;
                break;

            } else {
                // extractValArr[pi] = blockArr[i];
                extractValArr = addToCharArray(extractValArr, currentArr[i]);
                pi++;
            }

        }

        // mainSeekPos += runningIndex;
        // extractVal = new String(extractValArr).trim();
        return new String(extractValArr).trim();
    }

    private static char[] trimArrayByOpeningSeq(char[] currentArr, int from, char openingSeq) {

        char[] trArr = new char[0];

        try {

            char closingSeq = '\0';
            if (openingSeq == '{') {
                closingSeq = '}';
            } else if (openingSeq == '[') {
                closingSeq = ']';
            }

            int openingCount = 0;
            int closingCount = 0;
            for (int i = from; i < currentArr.length; i++) {

                if (currentArr[i] == openingSeq) {
                    ++openingCount;
                }

                if (currentArr[i] == closingSeq) {
                    ++closingCount;
                }

                trArr = addToCharArray(trArr, currentArr[i]);

                if (currentArr[i] == closingSeq && openingCount == closingCount) {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return trArr;

    }

    private static Map inspectArray(char[] currentArr, int from) {

        // char[] jsonSeq = {'"',',','"',''};
        int jsonSeqInd = 0;
        int objOpenBoundCount = 0;
        int objCloseBoundCount = 0;
        int arrayOpenBoundCount = 1;
        int arrayCloseBoundCount = 0;
        int innerArrayPresence = 0;

        try {

            for (int i = from; i < currentArr.length; i++) {
                try {

                    /*
                     * int nxt1FromCur = i + 1;
                     * int nxt2FromCur = i + 2;
                     * 
                     * if (nxt1FromCur < currentArr.length && nxt2FromCur < currentArr.length) {
                     * 
                     * if ((currentArr[i] == '{' && currentArr[i + 1] == '"') ||
                     * (currentArr[i] == '"' && currentArr[i + 1] == ':') ||
                     * (currentArr[i] ==
                     * '"' && currentArr[i + 1] == ':' && currentArr[i + 2] == '"')
                     * || (currentArr[i] ==
                     * '"' && currentArr[i + 1] == ',' && currentArr[i + 2] == '"')
                     * || (currentArr[i] == '"' && currentArr[i + 1] == ':' && currentArr[i + 2] ==
                     * '{')
                     * || (currentArr[i] == '}' && currentArr[i + 1] == ',' && currentArr[i + 2] ==
                     * '{')
                     * || (currentArr[i] == '"' && currentArr[i + 1] == ':' && currentArr[i + 2] ==
                     * '[')
                     * || (currentArr[i] == ']' && currentArr[i + 1] == '}' && currentArr[i + 2] ==
                     * ',')) {
                     * jsonSeqInd += 1;
                     * }
                     * 
                     * } else {
                     * break;
                     * }
                     */

                    if ((currentArr[i] == '{' && currentArr[i + 1] == '"') ||
                            (currentArr[i] == '"' && currentArr[i + 1] == ':')) {
                        jsonSeqInd += 1;
                    }

                    if (currentArr[i] == ']') {
                        ++arrayCloseBoundCount;
                    }

                    if (currentArr[i] == '[') {
                        ++arrayOpenBoundCount;
                    }

                    if (currentArr[i] == '{') {
                        ++objOpenBoundCount;
                    }

                    if (currentArr[i] == '}') {
                        ++objCloseBoundCount;
                    }

                    if (i > from && (currentArr[i] == ':' && currentArr[i + 1] == '[')) {
                        ++innerArrayPresence;
                    }

                } catch (Exception e) {
                    System.out.println("insp arr error at: " + i);
                    System.out.println(e.toString());
                }

            }

            if (jsonSeqInd > 0) {
                utilInfo.put("isJSONArray", 1);
            }

            if (objOpenBoundCount != 0 && objCloseBoundCount != 0 && objOpenBoundCount == objCloseBoundCount) {
                utilInfo.put("hasValidObjectBounds", 1);
            } else {
                utilInfo.put("hasValidObjectBounds", 0);
            }

            if ((objOpenBoundCount > 0 && objCloseBoundCount > 0) && jsonSeqInd > 0) {
                utilInfo.put("isJSONObjectArray", 1);
            } else {
                utilInfo.put("isJSONObjectArray", 0);
            }

            if (innerArrayPresence > 0) {
                utilInfo.put("hasInnerArray", 1);
            } else {
                utilInfo.put("hasInnerArray", 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return utilInfo;
    }

    private static int isEndOfContent(char[] currentArr) {
        if (mainSeekPos >= currentArr.length - 1) {
            endOfContent = 1;
        } else {
            endOfContent = 0;
        }

        return endOfContent;
    }

    private static char[][] addToCharArray2D(char[][] target, char[] item) {
        char[][] cArray = (char[][]) target;
        char[][] cArrayInc = new char[cArray.length + 1][cArray.length + 1];
        for (int i = 0; i < cArray.length; i++) {
            cArrayInc[i] = cArray[i];
        }
        // cArrayInc = cArray;
        cArrayInc[cArrayInc.length - 1] = item;

        return cArrayInc;
    }

    private static char[] addToCharArray(char[] target, char item) {
        char[] cArray = (char[]) target;
        char[] cArrayInc = new char[cArray.length + 1];
        for (int i = 0; i < cArray.length; i++) {
            cArrayInc[i] = cArray[i];
        }
        // cArrayInc = cArray;
        cArrayInc[cArrayInc.length - 1] = item;

        return cArrayInc;

    }

    private static char[] addAllToCharArray(char[] mainArr, char[] subArr) {

        for (int i = 0; i < subArr.length; i++) {
            mainArr = addToCharArray(mainArr, subArr[i]);
        }

        return mainArr;

    }

    private static Integer[] addToIntegerArray(Integer[] target, Integer item) {
        Integer[] cArray = (Integer[]) target;
        Integer[] cArrayInc = new Integer[cArray.length + 1];
        for (int i = 0; i < cArray.length; i++) {
            cArrayInc[i] = cArray[i];
        }
        // cArrayInc = cArray;
        cArrayInc[cArrayInc.length - 1] = item;

        return cArrayInc;

    }

    private static Integer[] removeFromIntegerArray(Integer[] numArr, Integer target) {

        // Integer[] cArray = (Integer[]) numArr;
        Integer[] numArrProc = new Integer[0];
        for (int i = 0; i < numArr.length; i++) {

            if (numArr[i] == target) {
                continue;
            } else {
                numArrProc = addToIntegerArray(numArrProc, numArr[i]);
            }
        }

        return numArrProc;
    }

    static <E> E[] add(E[] target, E item) {

        E[] cArray = (E[]) target;
        Object[] cArrayInc = new Object[cArray.length + 1];
        for (int i = 0; i < cArray.length; i++) {
            cArrayInc[i] = cArray[i];
        }
        // cArrayInc = cArray;
        cArrayInc[cArrayInc.length - 1] = item;

        return (E[]) cArrayInc;

    }

    static <E> int has(E[] arr, E item) {

        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] == item) {
                return 1;
            }
        }
        return 0;
    }

    static <E> int hasElements(E[] arr, E[] items) {

        // for objs what is obj id?
        // if no id cant be compared
        return 0;
    }

    static int hasChar(char[] arr, char item) {

        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] == item) {
                return 1;
            }
        }
        return 0;
    }

    static int hasChars(char[] mainArr, char[] items, int criteria) {
        // criteria : 1 = 'and' while 0 = 'or'
        int[] scoreArr = new int[items.length];

        int hasCount = 0;
        for (int i = 0; i < mainArr.length - 1; i++) {

            for (int j = 0; j < items.length - 1; j++) {

                if (mainArr[i] == items[j]) {
                    hasCount += 1;
                    scoreArr[j] += 1;
                }
            }
        }

        int scoreSum = sumOf(scoreArr);
        if (hasCount > 0 && scoreSum >= scoreArr.length) {
            return 1;
        }

        return 0;
    }

    private static int arrayToInteger(Integer[] numArr) {
        int num = 0;

        for (int i = 0, pf = numArr.length - 1; i < numArr.length; i++, pf--) {
            num += numArr[i] * pow(pf, 10);
        }

        return num;
    }

    private static BigInteger arrayToBigInteger(Integer[] numArr) {
        BigInteger num = BigInteger.ZERO;

        for (int i = 0, pf = numArr.length - 1; i < numArr.length; i++, pf--) {
            // num += numArr[i] * pow(pf,10);
            BigInteger p1 = new BigInteger(String.valueOf(numArr[i]));
            BigInteger p2 = powBig(pf, 10);
            BigInteger current = p1.multiply(p2);
            num = num.add(current);
        }

        return num;
    }

    private static Double arrayToDouble(char[] numArr) {

        return Double.valueOf(numArr.toString());
    }

    private static BigDecimal arrayToBigDecimal(char[] numArr) {

        return new BigDecimal(numArr);

    }

    static BigInteger powBig(int factor, int base) {

        if (factor == 0) {
            return BigInteger.ONE;
        }

        BigInteger baseBig = new BigInteger(String.valueOf(base));
        BigInteger res = baseBig;
        for (int i = 1; i < factor; i++) {
            // res *= base;
            res = res.multiply(baseBig);
        }

        return res;
    }

    static int pow(int factor, int base) {

        if (factor == 0) {
            return 1;
        }

        int res = base;
        for (int i = 1; i < factor; i++) {
            res *= base;
        }

        return res;
    }

}