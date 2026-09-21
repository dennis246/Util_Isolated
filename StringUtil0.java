package harp.util;

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @note experimental utility methods
 * @author Dennis Thomas
 */

public class StringUtil {
    // public static void main(String[] args) {

    /*
     * String content =
     * "A program consist of set of instructions which are either variables or functions.\nBy default, external variables and functions have the property that all references to the same concept."
     * ;
     * String searchTerm = "consist of set of instructions";
     * 
     * System.out.println("seq" + (findSequence(content, searchTerm, null) ? "" :
     * "\snot") + "\sfound"
     * + "for statement \n\n" + content);
     * 
     * String replaceTerm = "can be seen as a sequence of steps";
     * String replacedContent = replaceSequence(content, searchTerm, replaceTerm);
     * System.out.println("replaced with: \n\n" + replacedContent);
     * 
     * replaceTerm = "";
     * replacedContent = replaceSequence(content,
     * "By default, external variables and functions have the property that all references to the same concept."
     * , replaceTerm);
     * System.out.println("replaced with: \n\n" + replacedContent);
     */

    /*
     * String replaceTerm = "20, 200, 23440, 21, 45, 506";
     * String replacedContent = replace("functional programming", "al program",
     * "_of_");
     * System.out.println("replaced with: \n\n" + replacedContent);
     * 
     * }
     */

    static byte[] opArr = null;
    static byte[] currArrFrag = null;

    public enum FilterCriteria {
        Include, Exclude
    }

    public enum MatchCriteria {
        First, Last, All, Any
    }

    public enum SeekPosExclusionCriteria {
        excludeFirst, excludeCurrent, excludeLast, excludeNone
    }

    public enum CharacterType {
        Numeric, Alphabetic, AlphaNumeric, Symbols, ASCII
    }

    public enum Orientation {
        Top, Right, Bottom, Left
    }

    public enum TrimDirection {
        Leading, Trailing
    }

    public enum Direction {
        Leading, Trailing
    }

    public enum SeekPosFrom {
        Start, Middle, End
    }

    private static LinkedHashMap<String, Object> utilInfo = new LinkedHashMap<>();

    private static char[] charAggArr = new char[0];
    private static char[][] char2DAggArr = new char[0][];
    private static int cpUpto = 0;

    public static void clearConsoleLine(int len) {
        for (int i = 0; i != len; ++i) {
            System.out.print("\b");
        }
    }

    public static String clearLine(String line, int len) {
        char[] lineArr = line.toCharArray();

        for (int i = 0, clf = lineArr.length - 1; i < len; i++, clf--) {
            lineArr[clf] = '\0';
        }
        return new String(lineArr);
    }

    public static char[] pad(char[] mainArr, char padChar, int times, Direction dir) {
        // pos -1(prefix) 0(?) 1(postfix)
        try {

            for (int i = 0; i < times; i++) {

                if (dir.equals(Direction.Leading)) {
                    mainArr = addAt(mainArr, 0, padChar);
                } else if (dir.equals(Direction.Trailing)) {
                    mainArr = addToCharArray(mainArr, padChar);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mainArr;

    }

    public static char[] padAllSides(char[] mainArr, char padChar, int times) {

        char[] mainArrLPadded = pad(mainArr, padChar, times, Direction.Leading);
        char[] mainArrTPadded = pad(mainArrLPadded, padChar, times, Direction.Trailing);
        // .. top bottom? na here

        return mainArrTPadded;

    }

    static char[] removeChars(char[] src, char[] items) {

        char[] filteredArr = new char[0];
        try {
            for (int i = 0; i < src.length; i++) {

                int itemsFound = 0;
                for (int j = 0; j < items.length; j++) {
                    if (src[i] == items[j]) {
                        ++itemsFound;
                    }
                }

                if (itemsFound == 0) {
                    filteredArr = addToCharArray(filteredArr, src[i]);
                }

            }
        } catch (Exception e) {
            System.out.print(e.toString());
        }
        return filteredArr;

    }

    public static char[] removeFrom(char[] mainArr, int ti) {

        var finalArr = (char[]) Array.newInstance(mainArr.getClass().getComponentType(), 0);
        for (int i = 0; i < mainArr.length; i++) {

            if (i == ti) {
                continue;
            } else {
                finalArr = addToCharArray(finalArr, mainArr[i]);
            }
        }

        return (char[]) finalArr;
    }

    public static char[] trimZeroes(char[] mainArr, StringUtil.Direction dir) {
        try {

            if (dir.equals(Direction.Leading)) {

                for (int i = 0; i < mainArr.length; i++) {

                    if (mainArr[i] == '0') {
                        mainArr = removeFrom(mainArr, i);
                        --i;
                    }

                    if (i + 1 < mainArr.length - 1 && mainArr[i + 1] != '0') {
                        break;
                    }

                }
            } else if (dir.equals(Direction.Leading)) {

                for (int i = mainArr.length; i >= 0; i--) {

                    if (mainArr[i] == '0') {
                        mainArr = removeFrom(mainArr, i);
                    }

                    if (i - 1 > 0 && mainArr[i - 1] != '0') {
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mainArr;
    }

    @SuppressWarnings("unchecked")
    public static char[] addAt(char[] mainArr, int targetiIndex, char item) throws Exception {

        char[] mainArrInc = (char[]) Array.newInstance(mainArr.getClass().getComponentType(), mainArr.length + 1);

        if (targetiIndex < 0) {
            throw new Exception("Invalid index for method addAt");
        }

        if (targetiIndex > mainArr.length - 1) {
            targetiIndex = mainArrInc.length - 1;
        }

        int itemAddedInd = 0;
        for (int i = 0, pi = 0; i < mainArr.length; i++, pi++) {
            if (i == targetiIndex) {
                mainArrInc[pi] = item;
                ++pi;
                ++itemAddedInd;
            }
            mainArrInc[pi] = mainArr[i];
        }

        if (itemAddedInd == 0) {
            mainArrInc[mainArrInc.length - 1] = item;
        }

        return (char[]) mainArrInc;

    }

    private static String replaceWith(String content, String searchTerm, String replaceTerm) {
        String finalContent = null;

        char[] contentArr = content.toCharArray();
        char[] searchTermArr = searchTerm.toCharArray();
        char[] identifiedSeqArr = new char[searchTermArr.length];

        // p1 + m + p2
        int p1EndsAt = contentArr.length;
        int p2StartsFrom = contentArr.length;

        int termIdIndex = 0;
        for (int i = 0; i < contentArr.length; i++) {

            if (identifiedSeqArr.length == 0) {
                termIdIndex = 0;
            }

            for (int j = termIdIndex; j < searchTermArr.length; j++) {
                if (contentArr[i] == searchTermArr[j]) {

                    // index pos
                    if (identifiedSeqArr[0] == 0) {
                        p1EndsAt = i + 1;
                    }

                    identifiedSeqArr[j] = searchTermArr[j];
                    termIdIndex += j;
                    i++;

                    if (identifiedSeqArr[identifiedSeqArr.length - 1] != 0
                            && identifiedSeqArr.length == searchTermArr.length) {
                        // return true;

                        p2StartsFrom = p1EndsAt + searchTermArr.length - 1;

                        // p1 + m + p2
                        char[] p1 = new char[p1EndsAt];
                        System.arraycopy(contentArr, 0, p1, 0, p1.length - 1);
                        char[] p2 = new char[contentArr.length - p2StartsFrom];
                        System.arraycopy(contentArr, p2StartsFrom, p2, 0, p2.length);

                        char[] m = replaceTerm.toCharArray();

                        char[] fcArr = new char[p1.length + m.length + p2.length];

                        System.arraycopy(p1, 0, fcArr, 0, p1.length);
                        System.arraycopy(m, 0, fcArr, p1.length - 1, m.length);
                        System.arraycopy(p2, 0, fcArr, p1.length + m.length, p2.length);

                        finalContent = new String(fcArr);
                        return finalContent;

                    }

                } else {
                    // reset
                    termIdIndex = 0;
                    identifiedSeqArr = new char[searchTermArr.length];
                    break;
                }

            }
        }

        return finalContent;

    }

    private static boolean find(String content, String searchTerm, String criteria) {

        char[] contentArr = content.toLowerCase().toCharArray();
        char[] searchTermArr = searchTerm.toLowerCase().toCharArray();
        char[] identifiedSeqArr = new char[searchTermArr.length];
        int termIdIndex = 0;
        for (int i = 0; i < contentArr.length; i++) {

            if (identifiedSeqArr.length == 0) {
                termIdIndex = 0;
            }

            for (int j = termIdIndex; j < searchTermArr.length; j++) {
                if (contentArr[i] == searchTermArr[j]) {
                    identifiedSeqArr[j] = searchTermArr[j];
                    termIdIndex += j;
                    i++;

                    if (identifiedSeqArr[identifiedSeqArr.length - 1] != 0
                            && identifiedSeqArr.length == searchTermArr.length) {
                        return true;
                    }

                } else {
                    // reset
                    termIdIndex = 0;
                    identifiedSeqArr = new char[searchTermArr.length];
                    break;
                }

            }
        }

        return false;

    }

    private static void matchCase() {
        int start = 0, end = 127;
        for (int i = start; i < end; i++) {
            var current = (char) i;
            System.err.print(current);

            int factor = 0;
            // for nums
            if (i > 32 && i < 65) {
                factor = 16;
            }
            // for alphabets
            if (i > 65 && i < 65 + 32) {
                factor = 32;
            }

            char convCase = (char) (current / 1 + factor);
            System.err.print(convCase);

            if (i != end) {
                System.err.print("\s");
            }

        }

        List<String> aList = new ArrayList<>();
        // DList<String> aList2 = new DList<>();
        // aList2.add("");
    }

    public static char[] generateRandomSequenceWith(int outputLength, char[] mainArr) {
        char[] finalSeq = new char[0];
        // int[] midxArr = MathUtil.generateNumArrByRange(mainArr.length, 0, 1);
        try {

            for (int i = 0; i < outputLength; i++) {

                int regenInd = 0;
                int ci = -1;
                do {
                    ci = MathUtil.randomNumberGenerator(mainArr.length);
                    char cc = mainArr[ci];
                    if (finalSeq.length < mainArr.length) {

                        if (hasCharAt(finalSeq, cc, null) != -1) {
                            regenInd = 1;
                        } else {
                            regenInd = 0;
                        }

                    } else {
                        regenInd = 0;
                    }

                } while (regenInd == 1);

                finalSeq = addToCharArray(finalSeq, mainArr[ci]);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return finalSeq;

    }

    public static String randomIDGenerator(int idLength, boolean includeChars, boolean includeNumbers,
            boolean includeSymbols)
            throws Exception {

        String generated = null;
        int totalSize = 0;
        // int startFrom = includeSymbols ? 33 : 48;
        int startFrom = 33;

        int seekLimit = startFrom + 1;
        if (includeSymbols) {
            int cap = 15;
            totalSize += cap;
            seekLimit += cap;
        }

        if (includeNumbers) {

            if (!includeSymbols) {
                startFrom = 48;
            }

            int cap = 10;
            totalSize += cap;
            seekLimit += cap + 6;
        }

        if (includeChars) {

            if (!includeSymbols && !includeNumbers) {
                startFrom = 65;
            }

            int cap = 52;
            totalSize += cap;
            seekLimit += cap + 6;
        }

        char[] chars = new char[totalSize];

        for (int id = startFrom, pi = 0; id < 123; id++) {
            if ((includeSymbols && id >= 33 && id <= 47) ||
                    (includeNumbers && id >= 48 && id <= 57) ||
                    (includeChars && ((id >= 65 && id <= 90) || (id >= 97 && id <= 122)))) {

                chars[pi] = (char) id;
                pi++;
            }

            if (chars[chars.length - 1] != '\0') {
                break;
            }
        }

        // System.out.print(chars);

        char[] genArr = new char[idLength];
        for (int i = 0; i < idLength; i++) {
            int cindex = Double.valueOf(Math.random() * chars.length).intValue();
            // System.out.print(cindex + "\s");

            if (cindex < seekLimit) {
                genArr[i] = chars[cindex];
            } else {
                i--;
                throw new Exception("invalid fn result");
            }

        }

        generated = new String(genArr);

        return generated;
    }

    public static String randomIDGeneratorOR(int idLength, boolean includeChars, boolean includeNumbers,
            boolean includeSymbols)
            throws Exception {
        String generated = null;
        int totalSize = 0;
        int startFrom = includeSymbols ? 33 : 48;

        int seekLimit = startFrom + 1;
        if (includeSymbols) {
            int cap = 15;
            totalSize += cap;
            seekLimit += cap;
        }

        if (includeNumbers) {
            int cap = 10;
            totalSize += cap;
            seekLimit += cap + 6;
        }

        if (includeChars) {
            int cap = 52;
            totalSize += cap;
            seekLimit += cap + 6;
        }

        char[] genArr = new char[idLength];
        int outOfRangeResCount = 0;

        for (int i = 0; i < idLength; i++) {

            int id = Double.valueOf(Math.random() * seekLimit).intValue();

            // validate

            if ((includeSymbols && id >= 33 && id <= 47) ||
                    (includeNumbers && id >= 48 && id <= 57) ||
                    (includeChars && ((id >= 65 && id <= 90) || (id >= 97 && id <= 122)))) {

                genArr[i] = (char) id;
            } else {
                i--;
                outOfRangeResCount += 1;
                // throw new Exception("invalid fn result");
            }
        }

        if (outOfRangeResCount > 0) {
            System.out.println("out of range count " + outOfRangeResCount);
        }

        generated = new String(genArr);
        return generated;
    }

    public static String errorAt(char[] blockArr, int stopAt) {

        char[] ecArr = new char[0];
        for (int i = 0; i < blockArr.length; i++) {

            if (i == stopAt) {
                break;
            } else {
                ecArr = ContainerUtil.addToCharArray(ecArr, blockArr[i]);
            }
            // System.out.println(blockArr[i]);
        }

        return new String(ecArr);
    }

    static int hasChar(char[] arr, char item) {

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == item) {
                return 1;
            }
        }
        return 0;
    }

    public static int hasCharAt(final char[] currentArr, final char target, final MatchCriteria optCriteria)
            throws Exception {

        final MatchCriteria matchCriteria = optCriteria == null ? MatchCriteria.First
                : optCriteria;

        if (!matchCriteria.equals(MatchCriteria.First) && !matchCriteria.equals(MatchCriteria.Last)) {
            throw new Exception("Invalid criteria");
        }

        int foundAt = -1;
        for (int i = 0; i < currentArr.length; i++) {

            if (currentArr[i] == target) {
                foundAt = i;
                if (matchCriteria.equals(MatchCriteria.First)) {
                    return foundAt;
                }
            }

        }

        return foundAt;

    }

    static int hasChars(char[] mainArr, char[] items) {

        int hasCount = 0;
        for (int i = 0; i < mainArr.length - 1; i++) {

            for (int j = 0; j < items.length - 1; j++) {

                if (mainArr[i] == items[j]) {
                    hasCount += 1;
                }
            }
        }

        if (hasCount > 0) {
            return 1;
        }

        return 0;
    }

    public static int checkExactSequence(char[] fragment, char[][] sequences) {

        /*
         * char[][] sequences = { { '"', ':', '"' }, { '"', ',', '"' }, {
         * '"', '}', '}' }, { '"', '}', ']' },
         * { '"', ']', '}' }, { '"', ']', ']' }, {',','"'} };
         */

        int exactMatch = 0;
        int matchCount = 0;

        for (int g = 0; g < sequences.length; g++) {

            matchCount = 0;
            exactMatch = 0;
            char[] cseq = sequences[g];

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

                /*
                 * utilInfo.put("clseq_exactMatch", 1);
                 * utilInfo.put("clseq_matchScore", matchCount);
                 */

                return 1;
            }

        }

        return 0;

    }

    public static String indentBy(int factor, char indentchar) {
        String indented = "";
        for (int i = 0; i < factor; i++) {
            indented += indentchar;
        }

        return indented;
    }

    public static String indentAndNewLineBy(int factor, char indentchar) {
        String indented = "";
        for (int i = 0; i < factor; i++) {
            indented += indentchar;
        }

        indented += "\r\n";

        return indented;
    }

    static int occurs(char[] arr, char item) {

        int counter = 0;
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] == item) {
                ++counter;
                ;
            }
        }
        return counter;
    }

    public static String camelCase(String columnName) {

        return columnName.substring(0, 1).toLowerCase() + columnName.substring(1);

    }

    public static String firstCharUpperCase(String columnName) {

        return columnName.substring(0, 1).toUpperCase() + columnName.substring(1).toLowerCase();

    }

    @Deprecated
    public static String nameCase(String columnName) {
        // needs dictionary
        return columnName.substring(0, 1).toUpperCase() + columnName.substring(1);

    }

    public static Date parseDate(String cval) {
        var dateMap = stringToDateMap(cval);

        var ldtIns = LocalDateTime
                .of(Integer.parseInt(dateMap.get("year")),
                        Integer.parseInt(dateMap.get("month")),
                        Integer.parseInt(dateMap.get("day")),
                        Integer.parseInt(dateMap.get("hours")),
                        Integer.parseInt(dateMap.get("minutes")),
                        Integer.parseInt(dateMap.get("seconds")))
                .toInstant(ZoneOffset.UTC);
        var cdate = Date.from(ldtIns);
        return cdate;

    }

    public static char[] addToCharArray(char[] mainArr, char item) {
        // char[] cArray = (char[]) target;
        char[] cArrayInc = new char[mainArr.length + 1];
        for (int i = 0; i < mainArr.length; i++) {
            cArrayInc[i] = mainArr[i];
        }
        // cArrayInc = cArray;
        cArrayInc[cArrayInc.length - 1] = item;
        return cArrayInc;

    }

    public static char[] addAllToCharArray(char[] mainArr, char[] subArr) {

        for (int i = 0; i < subArr.length; i++) {
            mainArr = addToCharArray(mainArr, subArr[i]);
        }

        return mainArr;

    }

     public static String[] addAll(String[] mainArr, final String[] subArr) {

        for (int i = 0; i < subArr.length; i++) {
            mainArr = add(mainArr, subArr[i]);
        }

        return mainArr;

    }

    @SuppressWarnings("unchecked")
    public static <E> E[] add(E[] mainArr, E item) {

        E[] mainArrInc = (E[]) Array.newInstance(mainArr.getClass().getComponentType(), mainArr.length + 1);

        for (int i = 0; i < mainArr.length; i++) {
            mainArrInc[i] = mainArr[i];
        }

        mainArrInc[mainArrInc.length - 1] = item;
        return (E[]) mainArrInc;

    }

    public static <E> E[] add(E[] target, E[] cArrayInc, E item) {

        // var cArrayInc = Arrays.copyOf(target, target.length + 1, target.getClass());
        for (int i = 0; i < target.length; i++) {
            cArrayInc[i] = target[i];
        }

        cArrayInc[cArrayInc.length - 1] = item;
        return (E[]) cArrayInc;

    }

    public static String[] split(String content, String splitter) {
        String[] contentParts = new String[0];
        char[] contentArr = content.toCharArray();

        if (splitter.length() == 1) {

            char splitChar = splitter.charAt(0);
            char[] cseq = new char[0];
            for (int i = 0; i < contentArr.length; i++) {

                if (contentArr[i] != splitChar) {
                    cseq = addToCharArray(cseq, contentArr[i]);
                }

                if (cseq.length > 0 && (contentArr[i] == splitChar || i == contentArr.length - 1)) {
                    contentParts = add(contentParts, new String[contentParts.length + 1], new String(cseq));
                    cseq = new char[0];
                }

            }

        } else if (splitter.length() > 1) {

            int jpos = 0;
            char[] splitterArr = splitter.toCharArray();
            char[] cseq = new char[0];
            char[] splitMatchSeq = new char[0];
            for (int i = 0; i < contentArr.length; i++) {

                int ic = i;
                for (int j = 0; j < splitterArr.length; j++) {

                    if (contentArr[ic] == splitterArr[j] && contentArr.length - i - 1 > splitterArr.length - 1) {
                        splitMatchSeq = addToCharArray(splitMatchSeq, contentArr[ic]);
                        ic++;
                    } else {
                        splitMatchSeq = new char[0];
                        break;
                    }

                }

                if (equate(splitMatchSeq, splitterArr) == 1 || i == contentArr.length - 1) {
                    if (cseq.length > 0) {
                        contentParts = add(contentParts, new String[contentParts.length + 1], new String(cseq));
                    }
                    cseq = new char[0];
                } else {
                    cseq = addToCharArray(cseq, contentArr[i]);
                }

                if (splitMatchSeq.length > 0) {
                    i += splitMatchSeq.length - 1;
                    splitMatchSeq = new char[0];
                }

            }

        }

        return contentParts;

    }

    public static char[][] splitBy(final char[] dataRowArr, final int from, final String splitBy,
            final char[] filterchars) {

        char[][] splitArr = new char[0][0];
        char[] currentArr = new char[0];
        char[] splitByCharSeq = splitBy.toCharArray();
        for (int i = from; i < dataRowArr.length; i++) {

            // if (i == 1171) {
            // var x = i;
            // }

            if (filterchars != null && hasChar(filterchars, dataRowArr[i]) == 1 && i != dataRowArr.length - 1) {
                continue;
            }

            if (i > 0
                    && (checkAroundFor(dataRowArr, splitByCharSeq, dataRowArr[i], i, SeekPosFrom.Start) == 1)
                    || i == dataRowArr.length - 1) {

                if (i == dataRowArr.length - 1
                // && currentArr.length > 0
                ) {
                    currentArr = addToCharArray(currentArr, dataRowArr[i]);
                }

                currentArr = trim(currentArr);
                splitArr = addToCharArray2D(splitArr, currentArr);
                currentArr = new char[0];
                if (i == dataRowArr.length - 1) {

                } else {
                    i += splitByCharSeq.length - 1;
                }

                continue;
            }

            currentArr = addToCharArray(currentArr, dataRowArr[i]);
        }

        return splitArr;
    }

    public static int checkAroundFor(char[] mainArr, char[] searchSeq, char currChar, int seekPos,
            SeekPosFrom seekPosFrom) {
        try {
            // // dnr
            int cCharAt = -1;
            if ((cCharAt = hasCharAt(searchSeq, currChar, MatchCriteria.First)) == -1) {
                return 0;
            }

            int sf = -1; // sf = modified seekpos
            int to = -1;
            if (seekPosFrom.equals(SeekPosFrom.Middle)) {
                sf = seekPos - searchSeq.length / 2 - (searchSeq.length % 2 == 0 ? 1 : 0);
            } else if (seekPosFrom.equals(SeekPosFrom.Start)) {
                sf = seekPos - cCharAt;
            } else if (seekPosFrom.equals(SeekPosFrom.End)) {
                sf = seekPos - (searchSeq.length - cCharAt);
            }

            to = sf + (searchSeq.length - 1);
            if (to > mainArr.length - 1) {
                return 0;
            }

            return equate(searchSeq, sliceByIndexRange(mainArr, sf, to));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;

    }

    public static char[] sliceByIndexRange(char[] mainArr, int from, int to) throws Exception {

        if (from < 0 || from > to || to > mainArr.length) {
            throw new Exception("Invalid range");
        }

        char[] finalArr = new char[0];

        for (int i = from; i <= to; i++) {
            finalArr = addToCharArray(finalArr, mainArr[i]);
        }

        return finalArr;
    }

    public static char[] trim(char[] mainArr) {
        // char[] finalArr = new char[0];
        mainArr = leftTrim(mainArr);
        mainArr = rightTrim(mainArr);
        return mainArr;
    }

    public static char[] leftTrim(final char[] mainArr) {

        char[] finalArr = new char[0];

        int trimInd = 0;
        for (int i = 0; i < mainArr.length; i++) {

            if (trimInd == 0 && hasChar(new char[] { '\s', '\n', '\r' }, mainArr[i]) == 1) {
                continue;
            } else {
                trimInd = 1;
                finalArr = addToCharArray(finalArr, mainArr[i]);
            }

        }

        return finalArr;

    }

    public static char[] rightTrim(char[] mainArr) {

        char[] finalArr = new char[0];
        mainArr = reverse(mainArr);

        int trimInd = 0;
        for (int i = 0; i < mainArr.length; i++) {

            if (trimInd == 0 && hasChar(new char[] { '\s', '\n', '\r' }, mainArr[i]) == 1) {
                continue;
            } else {
                trimInd = 1;
                finalArr = addToCharArray(finalArr, mainArr[i]);
            }

        }

        return reverse(finalArr);

    }

    public static synchronized char[] extractByIndexRange_SWP(final char[] mainArr, final Integer from,
            final Integer to)
            throws Exception {
        // char[] finalArr = new char[0];

        try {

            mainSeekPos = from;
            if (mainArr.length < SplitTasks.splitTasksMaxCount) {
                endSeekPos = mainArr.length - 1;
            } else {
                endSeekPos = SplitTasks.splitTasksMaxCount;
            }

            int parts = 0;
            Double partlen = 0D;
            // int remainingLen = 0;
            if (mainArr.length < SplitTasks.splitTasksMinCount
                    || mainArr.length < SplitTasks.splitTasksMaxCount) {
                parts = 0;
                partlen = Double.valueOf(mainArr.length);
            } else {
                parts = SplitTasks.splitTasksMaxCount;
                partlen = (mainArr.length * 1.000D) / (parts * 1.000D);
                if (mainArr.length % parts > 0) {
                    // remainingLen = mainCharArr.length - partlen.intValue();
                    parts += 1;
                }
            }

            mainSeekPos = 0;
            endSeekPos = partlen.intValue();

            charAggArr = new char[mainArr.length];
            cpUpto = 0;

            final int finalPartLen = partlen.intValue();

            for (int i = 0; i <= parts; i++) {
                final int fparts = parts;
                final int curix = i;
                var ract = new Runnable() {
                    @Override
                    public void run() {

                        try {
                            var currArr = StringUtil.extractByIndexRange(mainArr, mainSeekPos, endSeekPos);
                            // char2DAggArr = addToCharArray2D(char2DAggArr, currArr);
                            // if(cpUpto > 0 && cpUpto > mainSeekPos) {
                            // cpUpto = endSeekPos;
                            // }
                            if (curix == fparts - 1) {
                                var x = 3939;
                            }

                            for (int irix = 0; irix < finalPartLen; irix++, cpUpto++) {
                                try {
                                    charAggArr[cpUpto] = currArr[irix];
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            var x = cpUpto;
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("SWP_ error at ix" + mainSeekPos + "::" + endSeekPos);
                            System.exit(0);
                        }

                    }
                };
                ract.run();

                mainSeekPos = endSeekPos;
                endSeekPos += SplitTasks.splitTasksMaxCount;

                if (endSeekPos >= mainArr.length - 1) {
                    endSeekPos = mainArr.length - 1;
                }

                if (mainSeekPos == mainArr.length - 1) {
                    break;
                }
            }

            // finalArr = new char[parts*partlen.intValue()];

        } catch (Exception e) {
            e.printStackTrace();
        }

        return charAggArr;
    }

    public static char[] extractByIndexRange(final String content, final Integer from, final Integer to)
            throws Exception {

        char[] currentArr = content.toCharArray();
        char[] finalArr = new char[0];

        if (from < 0 || to < 0 || to < from || from > to || from >= currentArr.length || to >= currentArr.length) {
            System.out.println("arr len:" + currentArr.length + "from:" + from + "|to:" + to);
            throw new Exception("invalid range values for extractByIndexRange");
        }

        if (from == to) {
            return new char[0];
        }

        for (int i = 0; i < currentArr.length; i++) {

            if (i >= from && i < to + 1 && from < to) {
                finalArr = addToCharArray(finalArr, currentArr[i]);
            }

            if (i == to) {
                break;
            }
        }

        return finalArr;

    }

    public static char[] extractByIndexRange(final char[] currentArr, final Integer from, final Integer to)
            throws Exception {

        char[] finalArr = new char[0];

        if (from < 0 || to < 0 || to < from || from > to || from >= currentArr.length || to >= currentArr.length) {
            System.out.println("arr len:" + currentArr.length + "from:" + from + "|to:" + to);
            throw new Exception("invalid range values for extractByIndexRange");
        }

        if (from == to) {
            return new char[0];
        }

        for (int i = 0; i < currentArr.length; i++) {

            if (i >= from && i < to + 1 && from < to) {
                finalArr = addToCharArray(finalArr, currentArr[i]);
            }

            if (i == to) {
                break;
            }
        }

        return finalArr;

    }

    public static char[] extractByIndexRange(final char[] currentArr, final Integer from, final Integer to,
            final char[] filterChars)
            throws Exception {

        char[] finalArr = new char[0];

        if (from < 0 || to < 0 || to < from || from > to || from > currentArr.length || to > currentArr.length) {
            throw new Exception("invalid range values");
        }

        for (int i = 0; i < currentArr.length; i++) {

            if (hasChar(filterChars, currentArr[i]) == 1) {
                continue;
            }

            if (i >= from && i < to + 1) {
                finalArr = addToCharArray(finalArr, currentArr[i]);
            }

            if (i == to) {
                break;
            }
        }

        return finalArr;

    }

    public static char[][] extractByIndexRange2D(final char[][] currentArr, final Integer from, final Integer to,
            final char[][] filterChars)
            throws Exception {

        char[][] finalArr = new char[0][];

        if (from < 0 || to < 0 || to < from || from > to || from > currentArr.length || to > currentArr.length) {
            throw new Exception("invalid range values");
        }

        for (int i = 0; i < currentArr.length; i++) {

            if (has(filterChars, currentArr[i]) == 1) {
                continue;
            }

            if (i >= from && i < to + 1) {
                finalArr = addToCharArray2D(finalArr, currentArr[i]);
            }

            if (i == to) {
                break;
            }
        }

        return finalArr;

    }

    public static char[][][] extractByIndexRange3D(final char[][][] currentArr, final Integer from, final Integer to,
            final char[][][] filterChars)
            throws Exception {

        char[][][] finalArr = new char[0][][];

        if (from < 0 || to < 0 || to < from || from > to || from > currentArr.length || to > currentArr.length) {
            throw new Exception("invalid range values");
        }

        for (int i = 0; i < currentArr.length; i++) {

            if (has(filterChars, currentArr[i]) == 1) {
                continue;
            }

            if (i >= from && i < to + 1) {
                finalArr = addToCharArray3D(finalArr, currentArr[i]);
            }

            if (i == to) {
                break;
            }
        }

        return finalArr;

    }

    public static int equate(char[] firstArr, char[] secondArr) {

        if (firstArr == null || secondArr == null || firstArr.length != secondArr.length) {
            return 0;
        }

        int hasCount = 0;
        for (int i = 0; i < firstArr.length; i++) {

            if (firstArr[i] == secondArr[i]) {
                hasCount += 1;
            } else {
                return 0;
            }

        }

        if (hasCount == firstArr.length) {
            return 1;
        }

        return 0;
    }

    public static int checkGroupSeq(char[] contentArr, int from, char[][] groupArr) {

        char[] potSeq = new char[0];

        for (int j = 0; j < groupArr.length; j++) {
            int ic = from;
            char[] grpStrArr = groupArr[j];
            int matchCount = 0;

            for (int k = 0; k < grpStrArr.length; k++) {

                if (contentArr[ic] == grpStrArr[k] && contentArr.length - from - 1 > groupArr.length - 1) {
                    potSeq = addToCharArray(potSeq, contentArr[ic]);
                    ic++;
                    ++matchCount;
                } else {
                    potSeq = new char[0];
                    break;
                }
            }

            if (matchCount == groupArr[j].length) {

                utilInfo = new LinkedHashMap<>();
                utilInfo.put("matchedSequence", new String(potSeq));
                utilInfo.put("mainSeekPos", ic);
                utilInfo.put("runningIndex", ic - from);

                return 1;
            }

        }

        return 0;
    }

    public static char[][] addToCharArray2D(char[][] target, char[] item) {
        char[][] cArray = (char[][]) target;
        char[][] cArrayInc = new char[cArray.length + 1][cArray.length + 1];
        for (int i = 0; i < cArray.length; i++) {
            cArrayInc[i] = cArray[i];
        }
        // cArrayInc = cArray;
        cArrayInc[cArrayInc.length - 1] = item;

        return cArrayInc;
    }

    public static char[][][] addToCharArray3D(char[][][] mainArr, char[][] item) {

        // char[][][] cArray = (char[][][]) target;
        char[][][] cArrayInc = new char[mainArr.length + 1][][];

        try {
            for (int i = 0; i < mainArr.length; i++) {
                cArrayInc[i] = mainArr[i];
            }
            // cArrayInc = cArray;
            cArrayInc[cArrayInc.length - 1] = item;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cArrayInc;
    }

    public static String[] charArray2DToStringArray(final char[][] charArr2D) {

        String[] strArr = new String[0];
        for (int i = 0; i < charArr2D.length; i++) {

            strArr = add(strArr, new String(charArr2D[i]));
        }

        return strArr;

    }

    public static char[][] stringArrayToCharArray2D(String[] mainArr) {

        if (mainArr == null) {
            return null;
        }

        char[][] charArr2D = new char[0][];
        // char[] cword = new char[0];
        for (int i = 0; i < mainArr.length; i++) {
            char[] cword = mainArr[i].toCharArray();
            charArr2D = addToCharArray2D(charArr2D, cword);
        }

        return charArr2D;
    }

    public static String[] objectArrayToStringArray(Object[] objectArr) {

        String[] finalArr = new String[objectArr.length];

        for (int i = 0; i < objectArr.length; i++) {
            var cob = (BigInteger) objectArr[i];

            finalArr = add(finalArr, cob.toString());
        }

        return finalArr;

    }

    public static String slice(String content, String[] openingSeq, String[] closingSeq,
            SeekPosExclusionCriteria openingBoundCriteria, SeekPosExclusionCriteria closingBoundCriteria,
            String[] filterSeq) {
        String slicedContent = "";
        char[] contentArr = content.toCharArray();

        char[][] openingSeqArr = stringArrayToCharArray2D(openingSeq);
        char[][] closingSeqArr = stringArrayToCharArray2D(closingSeq);
        char[][] filterSeqArr = stringArrayToCharArray2D(filterSeq);
        char[] cseq = new char[0];
        char[] potSeq = new char[0];
        char[] cword = new char[0];
        int opBoundFoundOcc = 0;
        int boundInd = -1;

        for (int i = 0; i < contentArr.length; i++) {

            if (checkGroupSeq(contentArr, i, openingSeqArr) == 1) {

                i += Integer.valueOf(utilInfo.get("runningIndex").toString()) - 1;
                String matchedSequence = (String) utilInfo.get("matchedSequence");

                ++opBoundFoundOcc;
                boundInd = 1;
                cword = new char[0];
                utilInfo = null;
                if (openingBoundCriteria.equals(SeekPosExclusionCriteria.excludeFirst)) {

                } else {
                    cword = addAllToCharArray(cword, matchedSequence.toCharArray());
                }

                continue;

            }

            if (checkGroupSeq(contentArr, i, closingSeqArr) == 1) {
                // i += Integer.valueOf(utilInfo.get("runningIndex").toString()) - 1;
                utilInfo = null;
                boundInd = -1;
                if (closingBoundCriteria.equals(SeekPosExclusionCriteria.excludeLast)) {
                    // cword = new char[0];
                    utilInfo = null;
                    break;
                }
            }

            if (checkGroupSeq(contentArr, i, filterSeqArr) == 1) {
                i += Integer.valueOf(utilInfo.get("runningIndex").toString()) - 1;
                utilInfo = null;
            } else {
                cword = addToCharArray(cword, contentArr[i]);
            }

            if (opBoundFoundOcc > 0 && boundInd == -1) {
                break;
            }

        }

        slicedContent = new String(cword);
        return slicedContent;
    }

    public static char[] reverse(char[] currentArr) {
        char[] reverseArr = new char[0];

        for (int i = currentArr.length - 1; i >= 0; i--) {
            reverseArr = addToCharArray(reverseArr, currentArr[i]);
        }

        return reverseArr;
    }

    public static int startsWith(final String mainSequence, final String searchSequence, final int ignoreCase)
            throws Exception {

        return startsWith(mainSequence.toCharArray(), searchSequence.toCharArray(), ignoreCase);

    }

    public static int startsWith(final char[] mainSequence, final char[] searchSequence, final int ignoreCase)
            throws Exception {
        if (mainSequence.length < searchSequence.length) {
            throw new Exception("startsWith : search sequence invalid");
        }

        checkPartialMatch(mainSequence, searchSequence, ignoreCase);
        final var sp = (Integer) utilInfo.get("matchedStartPt");
        final var ep = (Integer) utilInfo.get("matchedEndPt");
        final var ri = (Integer) utilInfo.get("matchedCount");

        if (sp == 0 && ep == searchSequence.length) {
            return 1;
        }

        return 0;

        // return checkPartialMatch(firstArr, secondArr, ignoreCase);
    }

    public static int checkPartialMatch(char[] firstArr, char[] secondArr, int ignoreCase) throws Exception {
        if (ignoreCase != 1 && ignoreCase != 0) {
            ignoreCase = 0;
        }

        char[] interArr = new char[0];
        if (secondArr.length > firstArr.length) {
            interArr = firstArr;
            firstArr = secondArr;
            secondArr = interArr;
        }

        utilInfo = new LinkedHashMap<>();
        int matchedStartPt = -1;
        int matchedEndPt = -1;
        int ri = 0;
        int ip = 0;
        int hasCount = 0;
        int eof = 0;
        if (firstArr.length >= secondArr.length) {
            for (int i = 0; i < secondArr.length; i++, ri++) {

                for (int j = 0; j < firstArr.length; j++) {

                    if ((ignoreCase == 1
                            && Character.toLowerCase(secondArr[i]) == Character.toLowerCase(firstArr[j]))
                            ||
                            (ignoreCase == 0 && secondArr[i] == firstArr[j])) {
                        if (matchedStartPt == -1) {
                            matchedStartPt = j;
                        }

                        hasCount += 1;
                        i++;
                        ip++;

                        if (hasCount == secondArr.length) {
                            matchedEndPt = i;
                            eof = 1;
                            break;
                        }

                        if (i == secondArr.length) {
                            break;
                        }

                    } else {
                        matchedStartPt = -1;
                        matchedEndPt = -1;
                        hasCount = 0;
                        i = i - ip;
                        ip = 0;
                    }

                }

                if (eof == 1) {
                    break;
                }

            }

            utilInfo.put("matchedStartPt", matchedStartPt);
            utilInfo.put("matchedEndPt", matchedEndPt);
            utilInfo.put("matchedCount", hasCount);
            utilInfo.put("runningIndex", ri);

            if (hasCount == secondArr.length) {
                return 1;
            } else {
                return 0;
            }

        } else {
            throw new Exception("unimpl method for haschars");
        }

    }

    public static String parse(final Object object) {
        final String finalString = "";

        return finalString;
    }

    public static int extractCountable(String sequence) {
        char[] numSeqArrP1 = numAccumalator(sequence.toCharArray());

        String numSeqStr = new String(numSeqArrP1);
        int finalNum = Integer.parseInt(numSeqStr);
        return finalNum;
        // return new String(numSeqArrP1);
    }

    public static String extractCountable(String sequence, Direction currentDirection) {

        char[] seqArr = sequence.toCharArray();
        char[] numSeqArrP1 = numAccumalator(seqArr);

        if (currentDirection.equals(Direction.Trailing)) {
            numSeqArrP1 = reverse(numAccumalator(reverse(numSeqArrP1)));
        }

        return new String(numSeqArrP1);
    }

    private static char[] numAccumalator(char[] seqArr) {

        char[] finalSeq = new char[0];
        int nzNumSeqStartInd = 0;
        for (int i = 0; i < seqArr.length; i++) {

            int cp = (int) seqArr[i];
            if (cp >= 48 && cp <= 57) {

                if (seqArr[i] != '0') {
                    ++nzNumSeqStartInd;
                } else {
                    if (nzNumSeqStartInd == 0) {
                        continue;
                    }
                }

                finalSeq = addToCharArray(finalSeq, seqArr[i]);
            } else {

                if (i > 0 && nzNumSeqStartInd > 0) {
                    break;
                }

                continue;

            }

        }

        return finalSeq;
    }

    public static char[] flatten(final char[][] currentArr) {

        char[] flatArr = new char[0];
        for (int i = 0; i < currentArr.length; i++) {
            flatArr = addAllToCharArray(flatArr, currentArr[i]);
        }

        return flatArr;

    }

    static char[] flatArr_SWP;
    static int mainSeekPos = 0;
    static int endSeekPos = 0;

    public static synchronized char[] flatten_SWP(final char[][] mainArr) {
        // applicable only for SWP
        // assumes all inner char arrs are of equal length

        try {
            mainSeekPos = 0;
            var finalsize = mainArr[0].length * mainArr.length;
            flatArr_SWP = new char[finalsize];
            endSeekPos = mainArr[0].length - 1;

            for (int i = 0; i < mainArr.length; i++) {

                var currArr = mainArr[i];

                var ract = new Runnable() {
                    @Override
                    public void run() {
                        // flatArr_SWP = addAllToCharArray(flatArr_SWP, currArr);

                        for (int j = 0; j < currArr.length; j++, mainSeekPos++) {
                            flatArr_SWP[mainSeekPos] = currArr[j];

                            if (j + 1 >= endSeekPos) {
                                var st = "56";
                            }

                        }

                    }
                };
                ract.run();

                mainSeekPos = endSeekPos;
                endSeekPos += mainArr[i].length - 1;

                if (endSeekPos > finalsize) {
                    endSeekPos = mainArr.length - 1;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return flatArr_SWP;

    }

    public static String[] charArrayToStringArray(char[] charArr) {

        String[] strArr = new String[0];
        for (int i = 0; i < charArr.length; i++) {
            strArr = add(strArr, new String(new char[] { charArr[i] }));
        }

        return strArr;

    }

    public static int isAlphaNumericSeq(final char[] currentArr) {

        int alphaNumericCount = 0;
        int symbolCount = 0;
        for (int i = 0; i < currentArr.length; i++) {
            final int cp = currentArr[i];

            if ((cp >= 48 && cp <= 57) || (cp >= 65 && cp >= 90) || (cp >= 97 && cp <= 122)) {
                ++alphaNumericCount;
            } else {
                ++symbolCount;
            }
        }

        if (alphaNumericCount > 0) {
            return 1;
        }
        return 0;

    }

    private static char[] removeSpaces_(final char[] currentArr) {

        char[] trimArr = new char[0];
        for (int i = 0; i < currentArr.length; i++) {

            final int cp = currentArr[i];
            if (cp == 32) {
                continue;
            } else {
                trimArr = addToCharArray(trimArr, currentArr[i]);
            }
        }

        return trimArr;

    }

    public static int isAlphaNumeric(char x) {

        // if (x > 32 && x < 177) {
        if ((x >= 48 && x <= 57) || (x >= 65 && x >= 90)) {
            return 1;
        } else {
            return 0;
        }

    }

    public static char[] alphabet() {
        char[] finalArr = new char[0];
        for (int x = 0; x < 123; x++) {

            if (((x >= 65 && x <= 90) || (x >= 97 && x <= 122))) {
                // System.out.print(((char) x) + "-" + x + "\s");
                finalArr = addToCharArray(finalArr, (char) x);
            }
        }

        return finalArr;
    }

    public static char toLowerCase(char target) {

        var alphabet = alphabet();
        var lcChar = target;
        for (int i = 0; i < alphabet.length; i++) {

            if (alphabet[i] == target) {

                if (i < 26) {
                    lcChar = alphabet[i + 26];
                }

                break;

            }

        }

        return lcChar;

    }

    public static char toUpperCase(char target) {

        var alphabet = alphabet();
        var lcChar = target;
        for (int i = 0; i < alphabet.length; i++) {

            if (alphabet[i] == target) {

                if (i > 25) {
                    lcChar = alphabet[i - 26];
                }

                break;

            }

        }

        return lcChar;

    }

    public static char[] nonAlphaNumericChars() {

        char[] finalArr = new char[0];
        for (int x = 0; x < 256; x++) {
            if ((x >= 48 && x <= 57) ||
                    (((x >= 65 && x <= 90) || (x >= 97 && x <= 122)))) {

            } else {
                finalArr = addToCharArray(finalArr, (char) x);
            }

        }

        return finalArr;

    }

    public static String[] nonAlphaNumericChars_StringArray() {

        return charArrayToStringArray(nonAlphaNumericChars());

    }

    public static String removeNonAlphaNumericChars(char[] contentArr) {

        var charArr2D = stringArrayToCharArray2D(StringUtil.nonAlphaNumericChars_StringArray());
        String res = new String(removeSequences(contentArr, charArr2D));
        return res;

    }

    public static char[] removeNonAlphaNumericCharsFromCharSeq(char[] contentArr) {

        var charArr2D = stringArrayToCharArray2D(StringUtil.nonAlphaNumericChars_StringArray());
        String res = new String(removeSequences(contentArr, charArr2D));
        return res.toCharArray();

    }

    public static String removeNonAlphaNumericChars(String content) {

        var charArr2D = stringArrayToCharArray2D(StringUtil.nonAlphaNumericChars_StringArray());
        String res = new String(removeSequences(content.toCharArray(), charArr2D));
        return res;

    }


    public static String removeNonAlphaNumericChars(String content, char[] excludeChars) {

        var charArr2D = stringArrayToCharArray2D(StringUtil.nonAlphaNumericChars_StringArray());
        String res = new String(removeSequences(content.toCharArray(), charArr2D, excludeChars));
        return res;

    }

    static <E extends Object> int has(final E[] arr, final E item) {

        for (int i = 0; i < arr.length; i++) {

            if (item instanceof Integer || item instanceof Long || item instanceof Double) {
                if (arr[i] == item) {
                    return 1;
                }
            } else if (item instanceof Array) {
                // equate(arr[i], item);
                return 0;
            } else {

                if (arr[i] != null && arr[i].equals(item)) {
                    return 1;
                }

            }
        }
        return 0;
    }

    static <E extends Object> int has(final E[] arr, final E item, final int ignoreCase) {

        try {

            for (int i = 0; i < arr.length; i++) {

                if (item instanceof Integer || item instanceof Long || item instanceof Double) {
                    if (arr[i] == item) {
                        return 1;
                    }
                } else {

                    if (item instanceof String) {
                        if (ignoreCase == 1) {
                            if (arr[i].toString().toLowerCase().equals(item.toString().toLowerCase())) {
                                return 1;
                            }
                        }
                    } else {

                        if (arr[i].equals(item)) {
                            return 1;
                        }

                    }

                }
            }

        } catch (final Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public static <E> E[] filter(final E[] mainArr, final E[] filterArr,
            final FilterCriteria filterCriteria, final int ignoreCase) {
        // TODO filter first arr from values of second arr

        E[] finalArr = (E[]) Array.newInstance(mainArr.getClass().getComponentType(), 0);
        if (mainArr == null || mainArr.length == 0 || filterArr == null || filterArr.length == 0) {
            return finalArr;
        }

        for (int i = 0; i < mainArr.length; i++) {

            if (filterCriteria.equals(FilterCriteria.Include)) {
                if (has(filterArr, mainArr[i], ignoreCase) == 1) {
                    finalArr = add(finalArr, mainArr[i]);
                }
            } else if (filterCriteria.equals(FilterCriteria.Exclude)) {
                if (has(filterArr, mainArr[i], ignoreCase) == 0) {
                    finalArr = add(finalArr, mainArr[i]);
                }
            }

        }

        return finalArr;

    }

    public static char[] filter(final char[] mainArr, final char[] filterArr,
            final FilterCriteria filterCriteria, final int ignoreCase) {
        // TODO filter first arr from values of second arr

        char[] finalArr = new char[0];
        if (mainArr == null || mainArr.length == 0 || filterArr == null || filterArr.length == 0) {
            return finalArr;
        }

        for (int i = 0; i < mainArr.length; i++) {

            if (filterCriteria.equals(FilterCriteria.Include)) {
                if (hasChar(filterArr, mainArr[i]) == 1) {
                    finalArr = addToCharArray(finalArr, mainArr[i]);
                }
            } else if (filterCriteria.equals(FilterCriteria.Exclude)) {
                if (hasChar(filterArr, mainArr[i]) == 0) {
                    finalArr = addToCharArray(finalArr, mainArr[i]);
                }
            }

        }

        return finalArr;

    }

    public static String removeInvalidLangVarSequences(String content) {

        var filtRes = filter(StringUtil.nonAlphaNumericChars_StringArray(), new String[] { "_" },
                FilterCriteria.Exclude, 0);
        // var charArr2D =
        // stringArrayToCharArray2D(StringUtil.nonAlphaNumericChars_StringArray());
        var charArr2D = stringArrayToCharArray2D(filtRes);
        String res = new String(removeSequences(content.toCharArray(), charArr2D));
        return res;

    }

    public static int isBinarySequence(char[] charArr) {

        for (int i = 0; i < charArr.length; i++) {

            int cp = charArr[i];

            if (cp != 48 && cp != 49) {
                return 0;
            }

        }

        return 1;
    }

    public static char[] removeSequences(char[] currentArr, final char[][] charSeqArr) {

        int[] rsIxArrFinal = new int[0];
        for (int i = 0; i < currentArr.length; i++) {

            for (int j = 0; j < charSeqArr.length; j++) {

                if (hasChar(charSeqArr[j], currentArr[i]) == 1) {
                    rsIxArrFinal = MathUtil.addToIntArray(rsIxArrFinal, i);
                }

            }

        }

        currentArr = removeFromIndexes(currentArr, rsIxArrFinal);
        return currentArr;

    }


    public static char[] removeSequences(char[] currentArr, final char[][] charSeqArr, final char[] excludeChars) {

        int[] rsIxArrFinal = new int[0];
        for (int i = 0; i < currentArr.length; i++) {

            for (int j = 0; j < charSeqArr.length; j++) {

                if (hasChar(charSeqArr[j], currentArr[i]) == 1 && hasChar(excludeChars, currentArr[i]) != 1) {
                    rsIxArrFinal = MathUtil.addToIntArray(rsIxArrFinal, i);
                }

            }

        }

        currentArr = removeFromIndexes(currentArr, rsIxArrFinal);
        return currentArr;

    }







    public static char[] removeFromIndexes(final char[] mainArr, final int[] riArr) {

        char[] procArr = new char[0];

        for (int i = 0; i < mainArr.length; i++) {

            if (MathUtil.hasInt(riArr, i) == 1) {
                continue;
            }

            procArr = addToCharArray(procArr, mainArr[i]);

        }

        return procArr;

    }

    public static <E extends Object> E[] shuffle(E[] mainArr) {

        final char[] finalArr = new char[0];
        final int[] shuffIndexArr = new int[0];
        final int totalSize = mainArr.length;

        int cuts = 0;
        while (cuts == 0) {
            cuts = Double.valueOf(Math.random() * totalSize).intValue();
        }

        final int cascadeFactor = totalSize;
        int cascades = 0;
        while (cascades == 0) {
            cascades = Double.valueOf(Math.random() * cascadeFactor).intValue();
        }

        while (cuts > 0) {
            mainArr = doShuffle(mainArr, cascades);
            cuts--;
        }

        return mainArr;

    }

    public static char[] shuffle(char[] mainArr) {

        final char[] finalArr = new char[0];
        final int[] shuffIndexArr = new int[0];
        final int totalSize = mainArr.length;
        // final int pcuts = totalSize < 10 ? totalSize/2;
        int cutfactor = totalSize / 2 > 5 ? totalSize / 2 : 5;
        int cuts = 0;
        while (cuts < 3) {
            cuts = Double.valueOf(Math.random() * cutfactor).intValue();
        }

        final int cascadeFactor = totalSize / 2 + 4;
        final int cascades = Double.valueOf(Math.random() * cascadeFactor).intValue();

        for (; cuts != 0; cuts--) {
            mainArr = doShuffle(mainArr, cascades);
        }

        return mainArr;

    }

    private static char[] doShuffle(char[] currentArr, int cascades) {

        try {

            final int maxIndex = currentArr.length - 1;
            char[] finalCutArr = new char[0];
            int cutAt = Double.valueOf(Math.random() * maxIndex).intValue();

            if (cutAt > maxIndex) {
                cutAt = Double.valueOf(Math.random() * maxIndex / 2).intValue();
            }

            if (cutAt + cascades > maxIndex) {
                cascades = 0;
            }

            if (cascades < maxIndex) {
                // int cutAtCopy = cutAt;
                for (int x = 0; x < cascades; x++) {

                    if (cutAt > maxIndex) {
                        cutAt = maxIndex - cutAt;
                    }

                    finalCutArr = addToCharArray(finalCutArr, currentArr[cutAt]);
                    currentArr = removeFromCharArray(currentArr, cutAt);
                }

            }

            int appendAt = Double.valueOf(Math.random() * maxIndex / 2).intValue();
            while (cutAt == appendAt) {
                appendAt = Double.valueOf(Math.random() * maxIndex / 2).intValue();
            }

            for (int x = 0; x < finalCutArr.length; x++, appendAt++) {
                currentArr = addCharAt(currentArr, appendAt, finalCutArr[x]);
            }

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return currentArr;
    }

    private static <E extends Object> E[] doShuffle(E[] currentArr, int cascades) {

        try {

            final int maxIndex = currentArr.length;
            E[] finalCutArr = (E[]) Array.newInstance(currentArr.getClass().getComponentType(), 0);
            int cutAt = Double.valueOf(Math.random() * maxIndex).intValue();

            if (cutAt > maxIndex) {
                cutAt = Double.valueOf(Math.random() * maxIndex / 2).intValue();
            }

            if (cutAt + cascades > maxIndex) {
                cascades = 1;
            }

            if (cascades < maxIndex) {
                // int cutAtCopy = cutAt;
                for (int x = 0; x < cascades; x++) {

                    if (cutAt > maxIndex) {
                        cutAt = maxIndex - cutAt;
                    }

                    finalCutArr = add(finalCutArr, currentArr[cutAt]);
                    currentArr = removeFrom(currentArr, cutAt);
                }

            }

            int appendAt = Double.valueOf(Math.random() * maxIndex / 2).intValue();
            while (cutAt == appendAt) {
                appendAt = Double.valueOf(Math.random() * maxIndex / 2).intValue();
            }

            for (int x = 0; x < finalCutArr.length; x++, appendAt++) {
                currentArr = addAt(currentArr, appendAt, finalCutArr[x]);
            }

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return currentArr;
    }

    @SuppressWarnings("unchecked")
    public static <E> E[] addAt(final E[] mainArr, int targetiIndex, final E item) throws Exception {

        final E[] mainArrInc = (E[]) Array.newInstance(mainArr.getClass().getComponentType(), mainArr.length + 1);

        if (targetiIndex < 0) {
            throw new Exception("Invalid index for method addAt");
        }

        if (targetiIndex > mainArr.length - 1) {
            targetiIndex = mainArrInc.length - 1;
        }

        int itemAddedInd = 0;
        for (int i = 0, pi = 0; i < mainArr.length; i++, pi++) {
            if (i == targetiIndex) {
                mainArrInc[pi] = item;
                ++pi;
                ++itemAddedInd;
            }
            mainArrInc[pi] = mainArr[i];
        }

        if (itemAddedInd == 0) {
            mainArrInc[mainArrInc.length - 1] = item;
        }

        return (E[]) mainArrInc;

    }

    public static <E> E[] removeFrom(final E[] mainArr, final int ti) {

        var finalArr = (E[]) Array.newInstance(mainArr.getClass().getComponentType(), 0);
        for (int i = 0; i < mainArr.length; i++) {

            if (i == ti) {
                continue;
            } else {
                finalArr = add(finalArr, mainArr[i]);
            }
        }

        return (E[]) finalArr;
    }

    public static char[] removeFromCharArray(final char[] mainArr, final int ti) {

        char[] procArr = new char[0];
        for (int i = 0; i < mainArr.length; i++) {

            if (i == ti) {
                continue;
            } else {
                procArr = addToCharArray(procArr, mainArr[i]);
            }
        }

        return procArr;
    }

    // inbetween
    public static char[] addCharAt(final char[] mainArr, int targetiIndex, final char item) throws Exception {

        final char[] mainArrInc = new char[mainArr.length + 1];

        if (targetiIndex < 0) {
            throw new Exception("Invalid index for method addAt");
        }

        if (targetiIndex > mainArr.length - 1) {
            targetiIndex = mainArrInc.length - 1;
        }

        int itemAddedInd = 0;
        for (int i = 0, pi = 0; i < mainArr.length; i++, pi++) {
            if (i == targetiIndex) {
                mainArrInc[pi] = item;
                ++pi;
                ++itemAddedInd;
            }
            mainArrInc[pi] = mainArr[i];
        }

        if (itemAddedInd == 0) {
            mainArrInc[mainArrInc.length - 1] = item;
        }

        return mainArrInc;

    }

    public static char[][] reorder(final char[][] mainArr, final int[] initSortModeArr, final SortMode order) {

        final char[][] expDRowRO = new char[mainArr.length][];
        int[] expSortModeArr = new int[0];
        if (order != null) {
            expSortModeArr = MathUtil.purgeSort(initSortModeArr,
                    order.equals(SortMode.Asc) ? SortMode.Asc
                            : SortMode.Desc);
        } else {
            expSortModeArr = initSortModeArr;
        }

        for (int i = 0; i < expSortModeArr.length; i++) {
            expDRowRO[i] = mainArr[expSortModeArr[i]];
        }

        return expDRowRO;

    }

    @SuppressWarnings("unchecked")
    public static <E extends Object> E[] reorder(final E[] mainArr, int[] initSortModeArr,
            final SortMode order) {

        if (initSortModeArr == null) {
            initSortModeArr = MathUtil.generateNumArrByRange(mainArr.length, 0, 1);
        }

        E[] expDRowRO = (E[]) Array.newInstance(mainArr.getClass().componentType(), 0);
        int[] expSortModeArr = new int[0];
        if (order != null) {
            expSortModeArr = MathUtil.purgeSort(initSortModeArr,
                    order.equals(SortMode.Asc) ? SortMode.Asc
                            : SortMode.Desc);
        } else {
            expSortModeArr = initSortModeArr;
        }

        for (int i = 0; i < expSortModeArr.length; i++) {
            expDRowRO = add(expDRowRO, mainArr[expSortModeArr[i]]);
        }

        return expDRowRO;
    }

    public static <E extends Object> int hasItemAt(final E[] currentArr, final E item,
            final MatchCriteria... optCriteria)
            throws Exception {

        // note ignores cases
        final MatchCriteria matchCriteria = optCriteria == null || optCriteria.length == 0 ? MatchCriteria.First
                : optCriteria[0];

        if (!matchCriteria.name().equals("First") && !matchCriteria.name().equals("Last")) {
            throw new Exception("Invalid criteria");
        }

        int foundAt = -1;
        for (int i = 0; i < currentArr.length; i++) {

            Object currentRow = currentArr[i];
            Object targetItem = item;
            if (item instanceof String) {
                currentRow = currentRow.toString().toLowerCase();
                targetItem = item.toString().toLowerCase();
            }

            if (currentRow.equals(targetItem)) {
                foundAt = i;
                if (matchCriteria.name().equals("First")) {
                    return foundAt;
                }
            }

        }

        return foundAt;

    }

    public static Map<String, String> stringToDateMap(final String dateStr) {
        final Map<String, String> dateTimeMap = new LinkedHashMap<>();

        try {

            // fills as per standard format
            final int dateSepCount = StringUtil.occurs(dateStr.toCharArray(), '-');
            final int dateSep2Count = StringUtil.occurs(dateStr.toCharArray(), '/');
            final int timeSepCount = StringUtil.occurs(dateStr.toCharArray(), ':');

            if (dateSepCount == 2 || dateSep2Count == 2) {

                String dateSection = dateStr;

                if (timeSepCount >= 2) {
                    dateSection = dateStr.substring(0, dateStr.indexOf("T"));
                }

                final String[] dateParts = dateSection.split("-");

                int passes = 0;
                final int secFilled = 0;
                for (int i = 0; i < 3; i++, passes += i) {

                    if (dateParts[i].length() == 4) {
                        dateTimeMap.put("year", dateParts[i]);
                        ++i;

                        if (dateTimeMap.get("month") == null) {
                            dateTimeMap.put("month", dateParts[i]);
                        }

                        ++i;
                        dateTimeMap.put("day", dateParts[i]);
                        break;

                    } else {

                        // secFilled
                        if (dateTimeMap.get("day") == null) {
                            dateTimeMap.put("day", dateParts[i]);
                        }

                        ++i;
                        if (dateTimeMap.get("month") == null && dateParts[i].length() == 2) {
                            dateTimeMap.put("month", dateParts[i]);
                        } else {

                            dateTimeMap.put("year", dateParts[i]);
                            ++i;
                            dateTimeMap.put("month", dateParts[i]);
                            break;
                        }

                        dateTimeMap.put("year", dateParts[i]);
                        break;

                    }

                }

                if (Integer.parseInt(dateTimeMap.get("month")) > 12) {
                    dateTimeMap.put("day", dateTimeMap.get("month"));
                    dateTimeMap.put("month", dateTimeMap.get("day"));
                }

                if (timeSepCount < 2) {

                    dateTimeMap.put("hours", "00");
                    dateTimeMap.put("minutes", "00");
                    dateTimeMap.put("seconds", "000");

                    return dateTimeMap;
                }
            }

            if (timeSepCount > 0) {

                final String timeSection = dateStr.substring(dateStr.indexOf(":") - 2);
                final String[] timeParts = timeSection.split(":");

                int passes = 0;
                final int secFilled = 0;
                for (int i = 0; i < 3; i++, passes += i) {

                    if (timeParts[i].length() == 2) {
                        dateTimeMap.put("hours", timeParts[i]);
                        ++i;

                        if (dateTimeMap.get("minutes") == null) {
                            dateTimeMap.put("minutes", timeParts[i]);
                        }

                        ++i;
                        dateTimeMap.put("seconds",
                                timeParts[i].contains(".") ? timeParts[i].substring(0, timeParts[i].indexOf("."))
                                        : timeParts[i]);
                        break;

                    } else {

                        // secFilled
                        if (dateTimeMap.get("seconds") == null) {
                            dateTimeMap.put("seconds",
                                    timeParts[i].contains(".") ? timeParts[i].substring(0, timeParts[i].indexOf("."))
                                            : timeParts[i]);
                        }

                        ++i;
                        if (dateTimeMap.get("minutes") == null && timeParts[i].length() == 2) {
                            dateTimeMap.put("minutes", timeParts[i]);
                        } else {

                            dateTimeMap.put("hours", timeParts[i]);
                            ++i;
                            dateTimeMap.put("minutes", timeParts[i]);
                            break;
                        }

                        dateTimeMap.put("hours", timeParts[i]);
                        break;

                    }

                }

            }

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return dateTimeMap;

    }

    // regex attempt1
    public static char[] replaceWith(char[] mainArr, String replaceExpression, String replaceWith) {
        // TODO attempt to group and replace consequcutive recurring chars

        try {

            char[] replaceSeqArr = replaceExpression.toCharArray();
            char[] replaceWithArr = replaceWith.toCharArray();
            if (replaceExpression.endsWith("+")) {
                // assume regex type and all before as the target seq to group
                replaceSeqArr = replaceExpression.substring(0, replaceExpression.indexOf("+")).toCharArray();
            } else {

            }

            // int matchBeganAt = -1;
            // int matchCount = 0;
            // char[] seqMatchedArr = new char[0];
            int[] tixArr = new int[0];
            int eop = 0;
            for (int i = 0; i < mainArr.length; i++) {

                int hasCharAt = -1;
                if ((hasCharAt = hasCharAt(replaceSeqArr, mainArr[i], null)) != -1) {
                    tixArr = MathUtil.addToIntArray(tixArr, i);
                }

                if (hasCharAt == -1 || i == mainArr.length - 1) {

                    if (tixArr.length > 1) {
                        // deliberate: + indicates more than one
                        if (tixArr[tixArr.length - 1] == mainArr.length - 1) {
                            eop = 1;
                        }

                        mainArr = removeFromIndexes(mainArr, tixArr);

                        i = i - (tixArr.length);

                        if (eop == 1) {
                            ++i;
                        }
                        mainArr = addAllToCharArrayFrom(mainArr, i, replaceWithArr);

                        i = i + replaceWithArr.length - 1;

                        tixArr = new int[0];
                    }

                }

                if (eop == 1) {
                    break;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mainArr;

    }

    public static char[] addAllToCharArrayFrom(char[] mainArr, int from, char[] secondArr) {

        try {

            int ixp = from;
            for (int j = 0; j < secondArr.length; j++, ixp++) {
                mainArr = addCharAt(mainArr, ixp, secondArr[j]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mainArr;

    }

    public static char[] defrag(char[] arr, int actsize) {
        // E[] anewArr = (E[]) Array.newInstance(arr.getClass().getComponentType(), 0);

        char[] anewArr = new char[0];
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] != '\0') {
                // vcount += 1;
                anewArr = addToCharArray(arr, arr[i]);
            }
        }

        return anewArr;
    }

    public static void flatPrintMapData(LinkedHashMap<String, char[]> itemMap) {

        String block = "";
        for (var crow : itemMap.keySet()) {
            block += new String(itemMap.get(crow));
        }

        System.out.println(block);
        // System.out.println(new String(item.get("name")) + "-" + new
        // String(item.get("appearance"))
        // + "-" + new String(item.get("atomic_mass")));

    }

    public static String replaceWith(final String content, final String[] seqArr, final String replaceSeq)
            throws Exception {

        if (seqArr == null || seqArr.length == 0) {
            throw new Exception("search sequence is invalid");
        }

        char[] contentArr = content.toCharArray();

        // char[] seqArr = seq.toCharArray();
        char[][] seqArr2D = new char[0][];
        seqArr2D = stringArrayToCharArray2D(seqArr);

        final char[] replaceSeqArr = replaceSeq.toCharArray();

        char[][] seqArr2DFinal = new char[0][];
        for (final var crowArr : seqArr2D) {
            if (equate(crowArr, replaceSeqArr) == 0) {
                seqArr2DFinal = addToCharArray2D(seqArr2DFinal, crowArr);
            }

        }

        seqArr2D = seqArr2DFinal;

        final int from = 0;
        int to = contentArr.length;
        char[] cwordSeq = new char[0];
        for (int i = 0; i < to; i++) {

            if (checkGroupSeq(contentArr, i, seqArr2D) == 1) {
                final int ri = Integer.valueOf(utilInfo.get("runningIndex").toString()) - 1;
                // i += ri;
                final String matchedSequence = (String) utilInfo.get("matchedSequence");

                if (replaceSeqArr.length > 0) {

                    // append
                    char[] part1Arr = extractByIndexRange(contentArr, 0, i - 1);
                    part1Arr = addAllToCharArray(part1Arr, replaceSeqArr);

                    final char[] part2Arr = extractByIndexRange(contentArr, i + ri + 1, to);

                    contentArr = new char[0];
                    contentArr = addAllToCharArray(contentArr, part1Arr);
                    contentArr = addAllToCharArray(contentArr, part2Arr);

                    /*
                     * int remaining = to - ri;
                     * to = replaceSeqArr.length + remaining;
                     */
                    to = contentArr.length;
                    --i;
                    // i += replaceSeqArr.length;
                } else {
                    i += ri;
                }

                continue;
            }

            cwordSeq = addToCharArray(cwordSeq, contentArr[i]);

        }

        final String finalContent = new String(cwordSeq);
        return finalContent;

    }

    public static String[] splitToStrings(String filepath) {
        String desloca = filepath.substring(0, filepath.lastIndexOf(".")) + "_" + LocalDate.now().toString();
        return splitToStrings0(filepath, desloca);
    }

    private static String[] splitToStrings0(String floca, String desloca) {

        var t1 = System.currentTimeMillis();
        String[] cblockArr = new String[0];
        String focaf = floca;

        if (desloca == null) {
            desloca = floca.substring(0, floca.lastIndexOf(".")) + "_" + LocalDate.now().toString();
        }

        final Long maxfsize_mb = 21L;
        final Long maxfsize_b = maxfsize_mb * 1000;

        Long fsize_b = 0L;
        Long fsize_mb = 0L;
        File aFile = new File(focaf);
        // byte[] opArr = null;

        // byte[] opArr = null;
        Long parts_mb = 0L;
        Long parts_b = 0L;
        // int lastFragLen_mb = 0;
        int lastFragLen_b = 0;
        Long partlen_b = 0L;

        if (aFile.exists()) {
            fsize_b = aFile.length();
            opArr = FileUtil.fileToBytes(focaf, fsize_b);

            fsize_mb = Math.round(fsize_b / (1000.0 * 1000.0));

            if ((parts_b = Long.valueOf(fsize_b > maxfsize_b ? fsize_b / maxfsize_b : 0)) > 0) {
                parts_mb = parts_b / 1000;
                partlen_b = fsize_b / parts_mb;

                lastFragLen_b = Long.valueOf(fsize_b - (partlen_b * parts_mb)).intValue();
                parts_mb += 1;

            } else {
                partlen_b = fsize_b;
                parts_mb = 1L;
            }

            mainSeekPos = 0;
            endSeekPos = partlen_b.intValue();
            // int eol = 0;

            var opArrF = opArr;
            for (int i = 1; i <= parts_mb.intValue(); i++) {

                currArrFrag = new byte[partlen_b.intValue()];
                // final var currArrFragF

                var ract = new Runnable() {
                    public void run() {
                        try {

                            // System.err.println("bsize:" + (endSeekPos - mainSeekPos));
                            currArrFrag = ContainerUtil.mergeToMainArr(currArrFrag, new byte[][] { opArrF },
                                    new int[] { mainSeekPos }, new int[] { endSeekPos });
                            // System.err.println("currArrLen:" + currArrFrag.length);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                ract.run();

                // FileUtil.writeBytesToFile(currArrFrag, desloca + "_p" + (i) + ".txt");
                cblockArr = add(cblockArr, new String(currArrFrag));

                if (endSeekPos >= fsize_b) {
                    break;
                }

                mainSeekPos = endSeekPos;
                if (i < parts_mb.intValue() - 1) {
                    endSeekPos += partlen_b.intValue();

                    if (i + 1 == parts_mb.intValue() - 1 && lastFragLen_b < maxfsize_b) {
                        // i++;
                        endSeekPos += lastFragLen_b;
                        partlen_b = Long.valueOf(endSeekPos - mainSeekPos);

                    }
                } else {
                    endSeekPos += lastFragLen_b;
                }

            }
        }

        var t2 = System.currentTimeMillis();
        System.out.println("eom te: " + (t2 - t1) / 1000);
        return cblockArr;
    }

}
