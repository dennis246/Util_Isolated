package harp.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import harp.util.StringUtil.Direction;

/**
 * @note experimental utility methods
 * @author Dennis Thomas
 */

public class MapUtil {

    public enum SeekPosExclusionCriteria {
        excludeFirst, excludeCurrent, excludeLast, excludeNone
    }

    public enum MatchCriteria {
        First, Last, All, Any
    }

    public enum FilterCriteria {
        Include, Exclude
    }

    public enum UtilOptions {
        TrimSpaces,
    }

    public enum Orientation {
        Top, Right, Bottom, Left
    }

    public enum PseudoExpressionEquator {
        Equate, Negate, Greater, Lesser, GreaterThanOrEquals, LesserThanOrEquals
    }

    public enum LoadCriteria {
        First, Last, Exclude
    }

    public static LinkedHashMap<String, Object> utilInfo = new LinkedHashMap<>();
    private static int mainSeekPos = 0;
    private static int endSeekPos = 0;

    private static Integer[] mainSeekPosHistory = new Integer[0];
    private static Integer[] mainSeekPosStops = new Integer[0];

    private static int endOfContent = 1;

    private static String currentSeqType = null;

    private static String currentSeq = "";

    private static Class<?> currentValueType;

    private static int steppedInLayer = 0;

    private static int stepOutInd = 0;

    private static LinkedHashMap<String, Object> contentMap = new LinkedHashMap<>();

    private static String jsonString = "";

    private static int removeQuotesInd = 0;

    private static int mainIndentLen = 0;

    private static String[] tagLayers = new String[0];
    private static char[][] charArr2DAgg_SWP = new char[0][];
    private static char[][][] charArr3DAgg_SWP = new char[0][][];
    private static String[] strArrAgg_SWP = new String[0];
    private static int cpUpto_SWP = 0;

    private static String[] strArr_SWP = new String[0];

    public static LinkedHashMap<String, Object> removeAllQuotes(final LinkedHashMap<String, Object> currentMap) {

        LinkedHashMap<String, Object> finalMap;
        jsonString = "";
        finalMap = unravelNodeToUnquotedMap(currentMap);

        resetCounters();
        return finalMap;

    }

    public static LinkedHashMap<String, Object> stringToXMLMap(String contentparam, final int xquo) {

        contentparam = linearizeXMLContent(contentparam);
        final char[] contentArr = contentparam.toCharArray();

        mainSeekPosHistory = new Integer[0];
        mainSeekPos = 0;
        endOfContent = 0;
        removeQuotesInd = xquo;

        contentMap = new LinkedHashMap<>();
        contentMap = addToXMLMap(contentArr, contentMap);

        final LinkedHashMap<String, Object> opMap = new LinkedHashMap<>();
        opMap.putAll(contentMap);

        // reset
        contentparam = null;
        contentMap = null;
        resetCounters();

        return opMap;

    }

    public static LinkedHashMap<String, Object> stringToJSONMap(String contentparam, final int xquo) {

        try {

            resetCounters();
            contentparam = removeComments(contentparam);
            contentparam = setBoundsToJSONContent(contentparam);
            contentparam = linearizeJSONContent(contentparam);
            final char[] contentArr = contentparam.toCharArray();

            mainSeekPosHistory = new Integer[0];
            mainSeekPos = 0;
            endOfContent = 0;
            removeQuotesInd = xquo;

            contentMap = new LinkedHashMap<>();
            utilInfo = new LinkedHashMap<>();
            contentMap = addToJSONMap(contentArr, contentMap);

            final LinkedHashMap<String, Object> opMap = new LinkedHashMap<>();
            opMap.putAll(contentMap);

            // reset
            contentparam = null;
            contentMap = null;
            resetCounters();

            return opMap;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void resetCounters() {
        mainSeekPosHistory = null;
        removeQuotesInd = 0;
        mainSeekPos = 0;
        mainIndentLen = 0;
        steppedInLayer = 0;
        stepOutInd = 0;
        contentMap = null;
        jsonString = null;
        utilInfo = new LinkedHashMap<>();

    }

    public static String addQuotes(final String id) {
        return "\"" + id + "\"";
    }

    public static String removeQuotes(final String id) {
        return id.replace("\"", "");
    }

    public static String formatXML(final String rawString) {

        final var ucm = MapUtil.removeComments(rawString);
        var xmlLinear = MapUtil.linearizeXMLContent(ucm);

        String formatted = "";

        try {
            steppedInLayer = 0;
            xmlLinear = "\s\s" + xmlLinear + "\s\s";
            final char[] xmlArr = xmlLinear.toCharArray();
            endOfContent = 0;
            int clineLen = 0;
            final int vpWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
            final int vpHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
            final int indentWidth = vpWidth;
            String[] tagLayers = new String[0];
            final int xmlPartInd = -1; // closed
            int innerContentInd = 0;
            int innerContentLen = 0;
            final int fontFactor = 14;

            final int maxLineLen = indentWidth / fontFactor;
            mainSeekPos = 2;
            do {

                if (xmlArr[mainSeekPos] == '<') {

                    innerContentInd = 0;
                    int mainSeekPosWas = mainSeekPos;
                    final String currentTag = new String(
                            extractContentWithinBounds(xmlArr, mainSeekPos, '<', '>',
                                    SeekPosExclusionCriteria.excludeNone,
                                    SeekPosExclusionCriteria.excludeNone, null));

                    // tagLayers = ContainerUtil.add(tagLayers, currentTag);
                    int mainSeekPosNow = mainSeekPosWas + mainSeekPos;
                    mainSeekPos = mainSeekPosNow;

                    /*
                     * if (currentTag.startsWith("<") && currentTag.endsWith(">")) {
                     * xmlPartInd *= -1;
                     * }
                     */

                    if (currentTag.startsWith("</") /* || currentTag.endsWith("/>") */) {
                        /*
                         * --steppedInLayer;
                         * stepOutInd = 1;
                         */

                        // check closing with prev opening
                        final String currentTagR1 = new String(StringUtil.removeChars(currentTag.toCharArray(),
                                new char[] { '<', '/', '>' }));
                        final String prevTagR1 = new String(
                                StringUtil.removeChars(tagLayers[tagLayers.length - 1].toCharArray(),
                                        new char[] { '<', '/', '>' }));

                        final String currentTagId = currentTagR1.substring(0,
                                currentTagR1.contains("\s") ? currentTagR1.indexOf("\s") : currentTagR1.length());
                        final String prevTagId = prevTagR1
                                .substring(0,
                                        prevTagR1.contains("\s") ? prevTagR1.indexOf("\s") : prevTagR1.length());

                        if (tagLayers.length > 0 && prevTagId.startsWith(currentTagId)) {
                            tagLayers = ContainerUtil.remove(tagLayers, tagLayers.length - 1);
                            clineLen -= clineLen <= 0 ? 0 : 5;
                        } else {
                            // clineLen -= clineLen <= 0 ? 0 : 5;
                        }

                        formatted += StringUtil.indentBy(clineLen, '\s');
                        formatted += currentTag;
                        formatted += "\r\n";

                    } else {
                        // opening
                        /*
                         * ++steppedInLayer;
                         * stepOutInd = 0;
                         */

                        formatted += StringUtil.indentBy(clineLen, '\s');
                        formatted += currentTag;

                        if (currentTag.endsWith("/>")) {

                        } else {
                            clineLen += 5;
                        }

                        formatted += "\r\n";

                        // if style or script
                        if (currentTag.toLowerCase().startsWith("<style")
                                || currentTag.toLowerCase().startsWith("<script")) {

                            mainSeekPosWas = mainSeekPos;
                            mainSeekPos = 0;
                            // mainseekpos as per pass arr
                            final String codeBlock = new String(
                                    extractContentWithinBounds(xmlArr, mainSeekPosWas, '>', '<',
                                            SeekPosExclusionCriteria.excludeFirst,
                                            SeekPosExclusionCriteria.excludeLast, null));
                            mainSeekPosNow = mainSeekPosWas + mainSeekPos;
                            mainSeekPos = mainSeekPosNow;

                            // uses mainseekpos again so reset as no new chars have been read
                            mainIndentLen = clineLen;
                            final String styleBlockF = formatCodeBlock(codeBlock);
                            formatted += StringUtil.indentBy(clineLen, '\s') + styleBlockF;
                            mainSeekPos = mainSeekPosNow;

                        } else {
                            // for inner content after tag
                            // formatted += StringUtil.indentBy(clineLen, '\s');

                        }

                        if (currentTag.endsWith("/>")) {
                            System.out.println("self closing tag: " + currentTag);
                            // clineLen -= clineLen <= 0 ? 0 : 5;
                        } else {
                            tagLayers = ContainerUtil.add(tagLayers, currentTag);
                        }

                    }

                    // tagLayers = ContainerUtil.add(tagLayers, currentTag);

                } else {
                    //
                    ++innerContentLen;
                    if (innerContentInd == 0) {
                        formatted += StringUtil.indentBy(clineLen, '\s');
                        innerContentInd = 1;
                    }
                    formatted += xmlArr[mainSeekPos];
                }

                // line break
                if (innerContentLen >= maxLineLen - 10) {

                    if (xmlArr[mainSeekPos] == '\s' || xmlArr[mainSeekPos + 1] == '\s') {

                        innerContentLen = 0;
                        formatted += "\r\n";
                        // clineLen -= clineLen <= 0 ? 0 : 5;
                        // indentWidth += clineLen;
                        formatted += StringUtil.indentBy(clineLen, '\s');

                    }
                }

                if (mainSeekPos == xmlArr.length - 1) {
                    endOfContent = 1;
                }

                ++mainSeekPos;

            } while (endOfContent != 1);

            resetCounters();

            // System.out.println("end of form");

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return formatted;

    }

    public static String formatXML_1(String jsonString) {
        steppedInLayer = 0;
        String formatted = "";
        jsonString = "\s\s" + jsonString + "\s\s";
        final char[] jsonArr = jsonString.toCharArray();
        endOfContent = 0;
        int clineLen = 0;
        final int vpWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        final int vpHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
        final int indentWidth = vpWidth;

        int xmlPartInd = -1; // closed

        final int maxLineLen = indentWidth / 14;
        mainSeekPos = 2;
        do {

            if (jsonArr[mainSeekPos] == '<') {

                final int mainSeekPosWas = mainSeekPos;
                final String currentTag = new String(
                        extractContentWithinBounds(jsonArr, mainSeekPos, '<', '>', SeekPosExclusionCriteria.excludeNone,
                                SeekPosExclusionCriteria.excludeNone, null));
                final int mainSeekPosNow = mainSeekPosWas + mainSeekPos;
                mainSeekPos = mainSeekPosNow;

                if (currentTag.startsWith("</")) {
                    --steppedInLayer;
                    stepOutInd = 1;

                    xmlPartInd *= -1;
                    clineLen -= 5;
                    formatted += currentTag;

                } else {
                    ++steppedInLayer;
                    stepOutInd = 0;

                    xmlPartInd *= -1;
                    formatted += currentTag;
                    clineLen += 5;

                }

                formatted += "\r\n";
                formatted += StringUtil.indentBy(clineLen, '\s');

            } else {
                formatted += jsonArr[mainSeekPos];
            }

            // line break
            if (clineLen >= maxLineLen) {
                formatted += "\r\n";
                clineLen -= clineLen >= 5 ? 5 : 0;
                // indentWidth += clineLen;
                formatted += StringUtil.indentBy(clineLen, '\s');
            }

            if (mainSeekPos == jsonArr.length - 1) {
                endOfContent = 1;
            }

            ++mainSeekPos;

        } while (endOfContent != 1);

        resetCounters();

        // System.out.println("end of form");
        return formatted;

    }

    public static String formatJSONFromMap(final LinkedHashMap<String, Object> dataMap) {

        final String finalOp = "";
        // linearizeJSONContent(currentSeq)

        return finalOp;
    }

    public static String formatJSON(String jsonString) {

        jsonString = linearizeJSONContent(jsonString);
        steppedInLayer = 0;
        String formatted = "";
        jsonString = "\s\s" + jsonString + "\s\s";
        final char[] jsonArr = jsonString.toCharArray();
        endOfContent = 0;
        int clineLen = 0;
        final int vpWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        final int vpHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
        final int indentWidth = vpWidth;

        int textPartInd = -1; // closed

        final int maxLineLen = indentWidth / 14;
        mainSeekPos = 2;
        do {

            if (jsonArr[mainSeekPos] == '{' || jsonArr[mainSeekPos] == '[') {

                boolean enterFblock = true;
                if ((mainSeekPos > 0) &&
                        (mainSeekPos + 1 <= jsonArr.length - 1) &&
                        (mainSeekPos + 2 <= jsonArr.length - 1)) {

                    if (textPartInd == 1) {
                        enterFblock = false;
                    }

                    if (enterFblock) {
                        // opening
                        ++steppedInLayer;
                        formatted += jsonArr[mainSeekPos];
                        formatted += "\r\n";
                        clineLen += 5;
                        formatted += StringUtil.indentBy(clineLen, '\s');

                    } else {
                        formatted += jsonArr[mainSeekPos];
                    }

                }

            } else if (jsonArr[mainSeekPos] == ',') {

                if ((mainSeekPos > 0) &&
                        (mainSeekPos + 1 <= jsonArr.length - 1) &&
                        (mainSeekPos + 2 <= jsonArr.length - 1)) {

                    boolean enterFblock = false;

                    if (

                    (checkForExactMatchFromSeqSet(
                            new char[] { jsonArr[mainSeekPos], jsonArr[mainSeekPos + 1] },
                            new char[][] { { ',', '"' } }, 1) == 1) ||

                            (checkForExactMatchFromSeqSet(
                                    new char[] { jsonArr[mainSeekPos - 1], jsonArr[mainSeekPos],
                                            jsonArr[mainSeekPos + 1] },
                                    new char[][] { { '"', ',', '"' }, { '}', ',', '{' } }, 1) == 1)) {
                        enterFblock = true;
                    }

                    if (enterFblock) {
                        formatted += jsonArr[mainSeekPos];
                        formatted += "\r\n";
                        formatted += StringUtil.indentBy(clineLen, '\s');
                    } else {
                        formatted += jsonArr[mainSeekPos];
                    }

                }

            } else if (jsonArr[mainSeekPos] == '}' || jsonArr[mainSeekPos] == ']') {

                // closing
                --steppedInLayer;
                boolean enterFblock = true;
                if (textPartInd == 1) {
                    enterFblock = false;
                }

                if (enterFblock) {
                    formatted += "\r\n";
                    clineLen -= 5;
                    formatted += StringUtil.indentBy(clineLen, '\s');
                    formatted += jsonArr[mainSeekPos];
                }

            } else {
                formatted += jsonArr[mainSeekPos];
            }

            if (jsonArr[mainSeekPos] == '"') {
                textPartInd *= -1;
            }

            // line break
            if (clineLen >= maxLineLen) {
                formatted += "\r\n";
                clineLen -= clineLen >= 5 ? 5 : 0;
                // indentWidth += clineLen;
                formatted += StringUtil.indentBy(clineLen, '\s');
            }

            if (mainSeekPos == jsonArr.length - 1) {
                endOfContent = 1;
            }

            ++mainSeekPos;

        } while (endOfContent != 1);

        resetCounters();

        // System.out.println("end of form");
        return formatted;

    }

    @SuppressWarnings({ "unused", "unchecked" })
    public static List<String> flattenToList(final LinkedHashMap<String, Object> verticalMap) {

        final List<String> opList = new ArrayList<>();
        for (final var crow : verticalMap.entrySet()) {

            if (crow.getValue() instanceof LinkedHashMap) {
                opList.add(crow.getKey());
                final var crowValMap = (LinkedHashMap<String, Object>) crow.getValue();
                opList.addAll(flattenToList(crowValMap));
            } else if (crow.getValue() instanceof String) {
                opList.add((String) crow.getValue());
            }
        }

        return opList;

    }

    public static String linearizeXMLContent(final String content) {

        resetCounters();
        String finalContent = "";
        final int runningIndex = 0;
        endOfContent = 0;
        try {

            final char[] xmlArr = content.toCharArray();
            char[] xmlArrLinear = new char[0];
            final int xmlPartInd = -1;

            // DebugUtil.setupInitialTSTrace(mainSeekPos, 20000);

            do {

                // if (mainSeekPos > 20000) {
                // DebugUtil.validateTSTrace(mainSeekPos);
                // }

                if (xmlArr[mainSeekPos] == '<') {

                    int mainSeekPosWas = mainSeekPos;
                    final char[] currentTagArr = extractContentWithinBounds(xmlArr, mainSeekPos, '<', '>',
                            SeekPosExclusionCriteria.excludeNone,
                            SeekPosExclusionCriteria.excludeNone, new char[] { '\r', '\n' });

                    xmlArrLinear = StringUtil.addAllToCharArray(xmlArrLinear, currentTagArr);

                    int mainSeekPosNow = mainSeekPosWas + mainSeekPos;
                    mainSeekPos = mainSeekPosNow;

                    // find spaces between tags
                    mainSeekPosWas = mainSeekPos;
                    char[] fragments = extractContentWithinBounds(xmlArr, mainSeekPos, '>', '<',
                            SeekPosExclusionCriteria.excludeFirst,
                            SeekPosExclusionCriteria.excludeLast, null);

                    // if (hasChars(fragments, alphaNumericCharSet(), 0) == 1) {
                    if (StringUtil.isAlphaNumericSeq(fragments) == 1) {
                        // System.out.println("Notify: has alpha numeric chars");
                        // reset mainseekpos
                        fragments = StringUtil.replaceWith(fragments, "\s+", "\s");
                        // fragments = new String(fragments).replaceAll("\s+","\s").toCharArray();
                        mainSeekPos = mainSeekPosWas;
                    } else {
                        // System.out.println("fragment after " + currentTagArr);
                        mainSeekPosNow = mainSeekPosWas + mainSeekPos;
                        mainSeekPos = mainSeekPosNow;
                        --mainSeekPos; // deliberate
                    }
                } else {

                    // inner section: skip below spec chars but space char
                    if (hasChar(new char[] { '\r', '\n', '\t', '\0' }, xmlArr[mainSeekPos]) != 1) {
                        xmlArrLinear = StringUtil.addToCharArray(xmlArrLinear, xmlArr[mainSeekPos]);
                    }

                }

                ++mainSeekPos;

                if (mainSeekPos > xmlArr.length - 1) {
                    endOfContent = 1;
                }

            } while (endOfContent != 1);

            // final var xmlLinCheck1 = new String(xmlArrLinear);
            // String opPath1 = "D:\WS-P\WS\LRZOutputP1.txt";
            // FileUtil.stringToFile(xmlLinCheck1, opPath1);

            // xmlArrLinear = removeSequences(xmlArrLinear, new char[][] { { '\s', '\s' }
            // });
            finalContent = new String(xmlArrLinear);
            // String opPath2 = "D:\\WS-P\\junkyard\\LCOutputP2.txt";
            // FileUtil.stringToFile(finalContent, opPath2);

        } catch (final Exception e) {
            System.out.println("Error at Running Index @ " + runningIndex);
            System.out.println(e.toString());
        }

        // resetCounters();
        return finalContent;
    }

    public static char[] removeSequences(char[] currentArr, final char[][] charSeqArr) {

        int[] rsIxArrFinal = new int[0];
        for (int i = 0; i < currentArr.length; i++) {

            for (int j = 0; j < charSeqArr.length; j++) {

                if (hasChar(charSeqArr[j], currentArr[i]) == 1) {
                    rsIxArrFinal = addToIntArray(rsIxArrFinal, i);
                }

            }

        }

        currentArr = removeFromIndexes(currentArr, rsIxArrFinal);
        return currentArr;

    }

    // LinkedHashMap<String, Object> unquotedMap;

    public static char[] removeFromIndexes(final char[] mainArr, final int[] riArr) {

        char[] procArr = new char[0];

        for (int i = 0; i < mainArr.length; i++) {

            if (hasInt(riArr, i) == 1) {
                continue;
            }

            procArr = addToCharArray(procArr, mainArr[i]);

        }

        return procArr;

    }

    public static String removeNonAlphaNumericChars(final char[] contentArr) {

        final var charArr2D = stringArrayToCharArray2D(StringUtil.nonAlphaNumericChars_StringArray());
        final String res = new String(MapUtil.removeSequences(contentArr, charArr2D));
        return res;

    }

    public static String removeNonAlphaNumericChars(final String content) {

        final var charArr2D = stringArrayToCharArray2D(StringUtil.nonAlphaNumericChars_StringArray());
        final String res = new String(MapUtil.removeSequences(content.toCharArray(), charArr2D));
        return res;

    }

    public static String linearizeJSONContent(final String content) {

        String finalContent = "";
        int runningIndex = 0;
        try {

            // todo remove single multi line comments and trim id and data and linearize
            final char[] contentArr = content.toCharArray();
            char[] contentArrTrm = new char[0];

            for (int i = 0; i < contentArr.length; i++, runningIndex++) {

                final int cp = contentArr[i];
                if (cp < 32 || contentArr[i] == '\0' || contentArr[i] == '\s' || contentArr[i] == ' '
                        || contentArr[i] == '\n') {
                    continue;
                }

                if (contentArr[i] == '"') {
                    final char[] chwqArr = extractId(contentArr, i).toCharArray();
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
            // String opPath = "D:\\WS-P\\WS\\LRZOutput.txt";
            // FileUtil.stringToFile(finalContent, opPath);

        } catch (final Exception e) {
            System.out.println("Error at Running Index @ " + runningIndex);
            System.out.println(e.toString());
        }

        resetCounters();
        return finalContent;
    }

    public static String[] extractContentWithinBounds(String contentBlock, final int from, final String separator,
            final String[] openingSeq,
            final String[] closingSeq,
            final SeekPosExclusionCriteria openingBoundCriteria,
            final SeekPosExclusionCriteria closingBoundCriteria,
            final String[] filterSeq,
            final int ignoreCase) {

        // works best when separtor exists

        final char[][] openingSeqArr = stringArrayToCharArray2D(openingSeq);
        final char[][] closingSeqArr = stringArrayToCharArray2D(closingSeq);
        final char[][] filterSeqArr = stringArrayToCharArray2D(filterSeq);
        final char[] separatorArr = separator.toCharArray();
        if (ignoreCase == 1) {
            contentBlock = contentBlock.toLowerCase();
        }

        // asof18052025
        final char[][] columnBlock1 = extractContentWithinBounds(contentBlock.toCharArray(), from, separatorArr,
                openingSeqArr,
                closingSeqArr, openingBoundCriteria,
                closingBoundCriteria,
                filterSeqArr);

        final String[] finalOp = charArray2DToStringArray(columnBlock1);

        return finalOp;

    }

    // added on 19052025
    public static String[] extractContentWithinBounds(String contentBlock, final int from, final String[] separatorSeq,
            final String[] openingSeq,
            final String[] closingSeq,
            final SeekPosExclusionCriteria openingBoundCriteria,
            final SeekPosExclusionCriteria closingBoundCriteria,
            final String[] filterSeq,
            final int ignoreCase) throws Exception {

        final char[][] openingSeqArr = stringArrayToCharArray2D(openingSeq);
        final char[][] closingSeqArr = stringArrayToCharArray2D(closingSeq);
        final char[][] filterSeqArr = stringArrayToCharArray2D(filterSeq);
        // char[] separatorArr = separator.toCharArray();

        // 19052025
        final char[][] separatorSeqArr = stringArrayToCharArray2D(separatorSeq);
        if (ignoreCase == 1) {
            contentBlock = contentBlock.toLowerCase();
        }

        /*
         * if(sep after filter)
         * for (var crow : separatorSeqArr) {
         * if (hasCharSeq(filterSeqArr, crow) == 1) {
         * throw new
         * Exception("Invalid set of params: Filter contains seperator sequences");
         * }
         * }
         */

        final char[][] columnBlockE2 = extractContentWithinBoundsE2(contentBlock.toCharArray(), from, separatorSeqArr,
                openingSeqArr,
                closingSeqArr, openingBoundCriteria,
                closingBoundCriteria,
                filterSeqArr);

        final String[] finalOp = charArray2DToStringArray(columnBlockE2);

        return finalOp;

    }

    public static String[] charArray2DToStringArray(final char[][] charArr2D) {

        String[] strArr = new String[0];
        for (int i = 0; i < charArr2D.length; i++) {

            strArr = add(strArr, new String(charArr2D[i]));
        }

        return strArr;

    }

    public static char[][] stringArrayToCharArray2D(final String[] mainArr) {

        if (mainArr == null) {
            return null;
        }

        char[][] charArr2D = new char[0][];
        // char[] cword = new char[0];
        for (int i = 0; i < mainArr.length; i++) {
            final char[] cword = mainArr[i].toCharArray();
            charArr2D = addToCharArray2D(charArr2D, cword);
        }

        return charArr2D;
    }

    public static String[] extractContentWithinBounds(final char[] currentArr, final int from, final char separator,
            final char[] openingSeq,
            final char[] closingSeq,
            final SeekPosExclusionCriteria openingBoundCriteria, final SeekPosExclusionCriteria closingBoundCriteria,
            final char[] filterChars, final int ignoreCase) {

        // mainSeekPos = 0;
        char[][] finalArr = new char[0][];
        int boundInd = -1;
        int opBoundFoundOcc = 0;
        int ri = 0;
        final int filterInd = 0;

        try {

            char[] cword = new char[0];
            for (int i = from; i < currentArr.length; i++, ri++) {

                if (ri > currentArr.length) {
                    break;
                }

                if (openingSeq != null && hasChar(openingSeq, currentArr[i]) == 1) {
                    boundInd *= opBoundFoundOcc == 0 ? -1 : 1;
                    ++opBoundFoundOcc;
                    cword = new char[0];
                    if (openingBoundCriteria.equals(SeekPosExclusionCriteria.excludeFirst)) {
                        continue;
                    }

                } else if (closingSeq != null && hasChar(closingSeq, currentArr[i]) == 1) {
                    boundInd *= -1;
                    if (closingBoundCriteria != null
                            && closingBoundCriteria.equals(SeekPosExclusionCriteria.excludeLast)) {
                        // filterChars = addToCharArray(filterChars, currentArr[i]);
                    } else {

                        if (cword.length > 0) {
                            finalArr = addToCharArray2D(finalArr, cword);
                        }

                    }
                    // cword = new char[0];
                }

                if (separator == currentArr[i] || i == currentArr.length - 1) {

                    if (boundInd == 1 || i == currentArr.length - 1) {
                        if (cword.length > 0) {
                            finalArr = addToCharArray2D(finalArr, cword);
                        }

                    }
                    cword = new char[0];
                    continue;
                }

                if (filterChars != null && (hasChar(filterChars, currentArr[i]) == 0)) {
                    cword = addToCharArray(cword, currentArr[i]);
                }

                if (opBoundFoundOcc > 0 && boundInd == -1) {
                    break;
                }

            }

        } catch (final Exception e) {
            e.printStackTrace();
            final var x = mainSeekPos;
        }

        return charArray2DToStringArray(finalArr);

    }

    public static char[][] extractContentWithinBounds(final char[] currentArr, final int from,
            final char[] separatorArr,
            final char[][] openingSeq,
            final char[][] closingSeq,
            final SeekPosExclusionCriteria openingBoundCriteria,
            final SeekPosExclusionCriteria closingBoundCriteria,
            final char[][] filterSeq) {

        final char[] filterSeqFlat = flatten(filterSeq);

        // mainSeekPos = 0;
        char[][] finalArr = new char[0][];
        int boundInd = -1;
        int opBoundFoundOcc = 0;
        int ri = 0;
        final int filterInd = 0;

        if (openingSeq == null) {
            // consider opened
            boundInd = 1;
        }

        /*
         * if (openingBoundCriteria.equals(SeekPosExclusionCriteria.excludeFirst)) {
         * // consider opened
         * boundInd = 1;
         * }
         */

        try {

            char[] cword = new char[0];
            for (int i = from; i < currentArr.length; i++, ri++) {

                if (openingSeq != null && hasCharSeq(openingSeq, cword) == 1) {
                    boundInd *= opBoundFoundOcc == 0 ? -1 : 1;
                    ++opBoundFoundOcc;
                    cword = new char[0];
                    i--;
                    if (openingBoundCriteria.equals(SeekPosExclusionCriteria.excludeFirst)) {
                        continue;
                    }

                } else if (closingSeq != null && hasCharSeq(closingSeq, cword) == 1) {
                    boundInd *= -1;
                    if (closingBoundCriteria != null
                            && closingBoundCriteria.equals(SeekPosExclusionCriteria.excludeLast)) {
                    } else {
                        finalArr = addToCharArray2D(finalArr, cword);

                    }
                    cword = new char[0];
                }

                if (hasChar(separatorArr, currentArr[i]) == 1 && cword.length > 0) {

                    if (boundInd == 1) {
                        // trim word
                        // cword = removeSequences(cword, new char[][] { { '\s', '\r', '\n' } });
                        cword = StringUtil.removeNonAlphaNumericCharsFromCharSeq(cword);
                        finalArr = addToCharArray2D(finalArr, cword);
                    }
                    cword = new char[0];
                    continue;
                }

                if (filterSeqFlat != null && (hasChar(filterSeqFlat, currentArr[i]) == 0)) {
                    cword = addToCharArray(cword, currentArr[i]);
                }

                if (opBoundFoundOcc > 0 && boundInd == -1) {
                    break;
                }

            }

        } catch (final Exception e) {
            e.printStackTrace();
            final var x = mainSeekPos;
        }

        return finalArr;
    }

    public static char[][] extractContentWithinBoundsE2(final char[] currentArr, final int from,
            final char[][] separatorSeq,
            final char[][] openingSeq,
            final char[][] closingSeq,
            final SeekPosExclusionCriteria openingBoundCriteria,
            final SeekPosExclusionCriteria closingBoundCriteria,
            final char[][] filterSeq) {

        final char[] filterSeqFlat = flatten(filterSeq);

        final char[] wordFilter = new char[] { '\s', '\n', '\r', '\0' };
        // mainSeekPos = 0;
        char[][] finalArr = new char[0][];
        int boundInd = -1;
        int opBoundFoundOcc = 0;
        int ri = 0;
        final int filterInd = 0;

        if (openingSeq == null) {
            // consider opened
            boundInd = 1;
        }

        try {

            char[] aword = new char[0];
            char[] cword = new char[0];
            for (int i = from; i < currentArr.length; i++, ri++) {

                if (hasChar(wordFilter, currentArr[i]) == 1) {
                    aword = new char[0];
                } else {

                    if ((filterSeqFlat != null && (hasChar(filterSeqFlat, currentArr[i]) == 0))) {
                        aword = addToCharArray(aword, currentArr[i]);
                    }

                    // aword = addToCharArray(aword, currentArr[i]);
                }

                if (opBoundFoundOcc == 0 && openingSeq != null && hasCharSeq(openingSeq, aword) == 1) {

                    boundInd *= opBoundFoundOcc == 0 ? -1 : 1;
                    ++opBoundFoundOcc;
                    aword = new char[0];

                    if (openingBoundCriteria.equals(SeekPosExclusionCriteria.excludeFirst)) {
                        cword = new char[0];
                        continue;
                    } else {

                    }

                }

                if (closingSeq != null && hasCharSeq(closingSeq, aword) == 1) {
                    boundInd *= -1;
                    if (closingBoundCriteria != null
                            && closingBoundCriteria.equals(SeekPosExclusionCriteria.excludeLast)) {
                    } else {
                        finalArr = addToCharArray2D(finalArr, cword);

                    }
                    cword = new char[0];
                    aword = new char[0];
                }

                if (separatorSeq != null && hasCharSeq(separatorSeq, aword) == 1) {
                    if (boundInd == 1) {
                        // trim word
                        cword = trim(cword);
                        finalArr = addToCharArray2D(finalArr, cword);
                    }
                    cword = new char[0];
                    aword = new char[0];
                    continue;
                }

                if (filterSeqFlat != null && (hasChar(filterSeqFlat, currentArr[i]) == 0)) {
                    cword = addToCharArray(cword, currentArr[i]);
                }

                if (opBoundFoundOcc > 0 && boundInd == -1) {
                    break;
                }

            }

        } catch (final Exception e) {
            e.printStackTrace();
            final var x = mainSeekPos;
        }

        return finalArr;
    }

    public static int hasCharSeq(final char[][] currentArr, final char[] item, int ignoreCase) throws Exception {

        if (ignoreCase != 1 && ignoreCase != 0) {
            ignoreCase = 0;
        }

        for (int i = 0; i < currentArr.length; i++) {

            if (ignoreCase == 1) {

                if (currentArr[i] != null && item != null && hasChars(currentArr[i], item, 1) == 1) {
                    return 1;
                }

            } else if (ignoreCase == 0) {
                if (currentArr[i] != null && item != null && equate(currentArr[i], item) == 1) {
                    return 1;
                }

            }

        }

        return 0;

    }

    public static int equate(final char[] firstArr, final char[] secondArr) {

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

    public static String removeComments(final String content) {

        String finalLines = "";
        final String[] lines = content.split("\n");
        final List<String> validLines = new ArrayList<>();
        for (final String line : lines) {
            if (line.trim().startsWith("*") || line.trim().startsWith("/*") || line.trim().startsWith("//")) {
                continue;
            } else {
                validLines.add(line);
            }
        }

        finalLines = String.join("\n", validLines); // validLines.toString();
        return finalLines;

    }

    public static int checkForExactMatchFromSeqSet(final char[] fragment, final char[][] sequences,
            final int appliesTo) {

        // applies for terminating sequences preset in fragment not really for large
        // blocks
        // appliesTo-
        // 1: applicable for param terminating sequences
        // 2: for blocks
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
                final char[] cseq = sequences[g];

                if (appliesTo == 1 && cseq.length != fragment.length) {
                    continue;
                }

                int sp = 0;
                for (int h = 0; h < cseq.length; h++) {
                    if (fragment[sp] == cseq[h]) {
                        ++matchCount;
                        ++sp;
                        if (matchCount == 1) {
                            utilInfo.put("exactseqcheck_startPt_" + g, sp);
                        }

                    } else {
                        break;
                    }
                }

                if (matchCount == fragment.length) {
                    exactMatch = 1;

                    utilInfo.put("exactseqcheck_exactMatch", 1);
                    utilInfo.put("exactseqcheck_matchScore", matchCount);
                    utilInfo.put("exactseqcheck_endPt_" + g, sp);

                    return 1;
                }

            }

        } catch (final Exception e) {
            System.out.println(e.toString());
        }

        return 0;

    }

    public static LinkedHashMap<String, Object> inspectArray_2(final char[] currentArr, final int from) {

        // char[] jsonSeq = {'"',',','"',''};
        final int jsonSeqInd = 0;
        final int objOpenBoundCount = 0;
        final int objCloseBoundCount = 0;
        final int arrayOpenBoundCount = 1;
        final int arrayCloseBoundCount = 0;
        int innerArrayPresence = 0;

        try {

            for (int i = from; i < currentArr.length; i++) {

            }

            // if (hasChars(new char[] { 'd', ']', 'e', 'l', 't', 'a', '[', ']' }, new
            // char[] { ']' }, 1) == 1) {
            if (hasChars(currentArr, new char[] { '[', ']' }, 0) == 1) {
                ++innerArrayPresence;
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

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return utilInfo;

    }

    public static LinkedHashMap<String, Object> inspectArray(final char[] currentArr, final int from) {

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

                    if ((i + 1 <= currentArr.length - 1) && (currentArr[i] == '{' && currentArr[i + 1] == '"') ||
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

                    /*
                     * DNR if (i > from && (currentArr[i] == ':' && currentArr[i + 1] == '[')) {
                     * ++innerArrayPresence;
                     * }
                     */

                    if (i > from && (currentArr[i] == '[' || currentArr[i] == ']')) {
                        ++innerArrayPresence;
                    }

                } catch (final Exception e) {
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

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return utilInfo;
    }

    public static char[][] addToCharArray2D(final char[][] mainArr, final char[] item) {

        final char[][] cArrayInc = new char[mainArr.length + 1][];
        for (int i = 0; i < mainArr.length; i++) {
            cArrayInc[i] = mainArr[i];
        }
        // cArrayInc = cArray;
        cArrayInc[cArrayInc.length - 1] = item;

        return cArrayInc;
    }

    public static char[][][] replaceCharArray3DItemAt(final char[][][] mainArr, final int targetIndex,
            final char[][] replacement)
            throws Exception {

        if (targetIndex < 0 || targetIndex > mainArr.length) {
            throw new Exception("Invalid index for method replaceCharArray3DItemAt");
        }

        char[][][] finalArr = new char[0][][];
        for (int i = 0; i < mainArr.length; i++) {

            if (i == targetIndex) {
                finalArr = addToCharArray3D(finalArr, replacement);
            } else {
                finalArr = addToCharArray3D(finalArr, mainArr[i]);
            }

        }

        return finalArr;
    }

    public static char[][][] addToCharArray3D(final char[][][] mainArr, final char[][] item) {

        final char[][][] cArrayInc = new char[mainArr.length + 1][][];
        for (int i = 0; i < mainArr.length; i++) {
            cArrayInc[i] = mainArr[i];
        }
        // cArrayInc = cArray;
        cArrayInc[cArrayInc.length - 1] = item;

        return cArrayInc;
    }

    public static char[] addToCharArray(final char[] target, final char item) {
        final char[] cArray = (char[]) target;
        final char[] cArrayInc = new char[cArray.length + 1];
        for (int i = 0; i < cArray.length; i++) {
            cArrayInc[i] = cArray[i];
        }
        // cArrayInc = cArray;
        cArrayInc[cArrayInc.length - 1] = item;

        return cArrayInc;

    }

    public static char[][][] addAllToCharArray3D(char[][][] mainArr, final char[][][] subArr) {

        for (int i = 0; i < subArr.length; i++) {
            mainArr = addToCharArray3D(mainArr, subArr[i]);
        }

        return mainArr;

    }

    public static char[][] addAllToCharArray2D(char[][] mainArr, final char[][] subArr) {

        for (int i = 0; i < subArr.length; i++) {
            mainArr = addToCharArray2D(mainArr, subArr[i]);
        }

        return mainArr;

    }

    public static char[] addAllToCharArray(char[] mainArr, final char[] subArr) {

        for (int i = 0; i < subArr.length; i++) {
            mainArr = addToCharArray(mainArr, subArr[i]);
        }

        return mainArr;

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

    public static char[][] filterByIndexes(final char[][] srcArr, final int[] ColumnIndexFilter) throws Exception {
        // var columnfilterIxSum = sumOf(ColumnIndexFilter);
        // var srcArrIxSum = sumOf(generateNumArrByRange(srcArr.length, 0, 1));

        if (ColumnIndexFilter.length > srcArr.length) {
            throw new Exception("invalid filter for method filterByIndexes");
        }

        char[][] desArr = new char[0][];
        for (int i = 0; i < srcArr.length; i++) {

            if (hasInt(ColumnIndexFilter, i) == 1) {
                desArr = addToCharArray2D(desArr, srcArr[i]);
            }

        }

        return desArr;
    }

    public static char[] filterByIndexes(final char[] srcArr, final int[] ColumnIndexFilter) throws Exception {

        final var columnfilterIxSum = sumOf(ColumnIndexFilter);
        final var srcArrIxSum = sumOf(generateNumArrByRange(srcArr.length, 0, 1));

        if (ColumnIndexFilter.length > srcArr.length || columnfilterIxSum < 0 || columnfilterIxSum > srcArrIxSum) {
            throw new Exception("invalid filter for method filterByIndexes");
        }

        char[] desArr = new char[0];
        for (int i = 0; i < srcArr.length; i++) {

            if (hasInt(ColumnIndexFilter, i) == 1) {
                desArr = addToCharArray(desArr, srcArr[i]);
            }

        }

        return desArr;
    }

    public static String[] filterByIndexes(final String[] srcArr, final int[] ColumnIndexFilter) throws Exception {

        final var columnfilterIxSum = sumOf(ColumnIndexFilter);
        final var srcArrIxSum = sumOf(generateNumArrByRange(srcArr.length, 0, 1));

        if (ColumnIndexFilter.length > srcArr.length || columnfilterIxSum < 0 || columnfilterIxSum > srcArrIxSum) {
            throw new Exception("invalid filter for method filterByIndexes");
        }

        String[] desArr = new String[0];
        for (int i = 0; i < srcArr.length; i++) {

            if (hasInt(ColumnIndexFilter, i) == 1) {
                desArr = add(desArr, srcArr[i]);
            }

        }

        return desArr;
    }

    public static char[] distinct(final char[] mainArr) {

        char[] finalArr = new char[0];

        for (int i = 0; i < mainArr.length; i++) {

            if (hasChar(finalArr, mainArr[i]) == 0) {
                finalArr = addToCharArray(finalArr, mainArr[i]);
            }

        }

        return finalArr;

    }

    public static int[] distinct(final int[] mainArr) {

        int[] finalArr = new int[0];

        for (int i = 0; i < mainArr.length; i++) {

            if (hasInt(finalArr, mainArr[i]) == 0) {
                finalArr = addToIntArray(finalArr, mainArr[i]);
            }

        }

        return finalArr;

    }

    @SuppressWarnings("Unchecked")
    public static <E extends Object> E[] distinct(final E[] mainArr) {

        E[] finalArr = (E[]) Array.newInstance(mainArr.getClass().getComponentType(), 0);

        for (int i = 0; i < mainArr.length; i++) {

            if (has(finalArr, mainArr[i]) == 0) {
                finalArr = add(finalArr, mainArr[i]);
            }

        }

        return finalArr;

    }

    public static <E> E[] addAll(E[] mainArr, final E[] subArr) {

        for (int i = 0; i < subArr.length; i++) {
            mainArr = add(mainArr, subArr[i]);
        }

        return mainArr;

    }

    @SuppressWarnings("unchecked")
    public static <E> E[] add(final E[] mainArr, final E item) {

        final E[] mainArrInc = (E[]) Array.newInstance(mainArr.getClass().getComponentType(), mainArr.length + 1);

        for (int i = 0; i < mainArr.length; i++) {
            mainArrInc[i] = mainArr[i];
        }

        mainArrInc[mainArrInc.length - 1] = item;
        return (E[]) mainArrInc;

    }

    @SuppressWarnings("unchecked")
    public static <E extends Object> E[] defrag(final E[] arr) {
        E[] anewArr = (E[]) Array.newInstance(arr.getClass().getComponentType(), 0);

        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] == null || arr[i].toString().isEmpty()) {
                continue;
            } else {
                anewArr = add(anewArr, arr[i]);
            }
        }

        return anewArr;
    }

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

    public static int hasChar(final char[] arr, final char item) {

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == item) {
                return 1;
            }
        }
        return 0;
    }

    public static int hasInt(final int[] arr, final int item) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == item) {
                return 1;
            }
        }
        return 0;
    }

    public static int endsWith(char[] firstArr, char[] secondArr, final int ignoreCase) throws Exception {
        if (firstArr.length < secondArr.length) {
            throw new Exception("hasChars : search sequence invalid");
        }

        firstArr = reverse(firstArr);
        secondArr = reverse(secondArr);

        checkPartialMatch(firstArr, secondArr, ignoreCase);
        final var sp = (Integer) utilInfo.get("matchedStartPt");
        final var ep = (Integer) utilInfo.get("matchedEndPt");
        final var ri = (Integer) utilInfo.get("matchedCount");

        if (sp == 0 && ep == secondArr.length) {
            return 1;
        }

        return 0;

        // return checkPartialMatch(firstArr, secondArr, ignoreCase);
    }

    public static char[] reverse(final char[] currentArr) {
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

    public static int startsWith(final char[] firstArr, final char[] secondArr, final int ignoreCase) throws Exception {
        if (firstArr.length < secondArr.length) {
            throw new Exception("hasChars : search sequence invalid");
        }

        checkPartialMatch(firstArr, secondArr, ignoreCase);
        final var sp = (Integer) utilInfo.get("matchedStartPt");
        final var ep = (Integer) utilInfo.get("matchedEndPt");
        final var ri = (Integer) utilInfo.get("matchedCount");

        if (sp == 0 && ep == secondArr.length) {
            return 1;
        }

        return 0;

        // return checkPartialMatch(firstArr, secondArr, ignoreCase);
    }

    public static int hasChars(final char[] firstArr, final char[] secondArr, final int ignoreCase) throws Exception {

        if (firstArr.length < secondArr.length || secondArr.length < 2) {
            throw new Exception("hasChars : search sequence invalid");
        }

        return checkPartialMatch(firstArr, secondArr, ignoreCase);

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

    public static String[] divideXMLContent(final String content) {
        String[] parts = new String[0];
        final int[] divIndexes = new int[0];

        resetCounters();

        final int runningIndex = 0;
        endOfContent = 0;

        char[] innerContArr = new char[0];
        try {

            // todo remove single multi line comments and trim id and data and linearize
            final char[] xmlArr = content.toCharArray();

            do {

                if (xmlArr[mainSeekPos] == '<') {

                    if (innerContArr.length > 0) {
                        final String partx = new String(innerContArr);

                        if (partx.trim().length() > 0) {
                            parts = ContainerUtil.add(parts, new String(innerContArr));
                        }

                    }

                    innerContArr = new char[0];

                    final int mainSeekPosWas = mainSeekPos;
                    final char[] currentTagArr = extractContentWithinBounds(xmlArr, mainSeekPos, '<', '>',
                            SeekPosExclusionCriteria.excludeNone,
                            SeekPosExclusionCriteria.excludeNone, null);

                    final int mainSeekPosNow = mainSeekPosWas + mainSeekPos;
                    mainSeekPos = mainSeekPosNow;

                    parts = ContainerUtil.add(parts, new String(currentTagArr));

                } else {
                    innerContArr = ContainerUtil.addToCharArray(innerContArr, xmlArr[mainSeekPos]);
                }

                ++mainSeekPos;

                if (mainSeekPos >= xmlArr.length - 1) {
                    endOfContent = 1;
                }

            } while (endOfContent != 1);

        } catch (final Exception e) {
            System.out.println("Error at Running Index @ " + runningIndex);
            System.out.println(e.toString());
        }

        resetCounters();
        return parts;
    }

    public static LinkedHashMap<String, String> splitToStringMap(final String[] sections, final String splitBy) {

        final LinkedHashMap<String, String> opMap = new LinkedHashMap<>();

        for (final var section : sections) {

            if (!section.contains(splitBy)) {
                continue;
            }
            final String[] parts = section.split(splitBy);
            opMap.put(parts[0].trim(), parts[1].trim());
        }

        return opMap;

    }

    public static <E> LinkedHashMap<String, Object> objectToMap(final E currentObject) {
        final var objMap = new LinkedHashMap<String, Object>();
        try {

            final Field[] fields = currentObject.getClass().getDeclaredFields();
            for (final var field : fields) {
                field.setAccessible(true);

                if (field.get(currentObject) != null) {
                    objMap.put(field.getName(), field.get(currentObject));
                }

                field.setAccessible(false);

            }

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return objMap;
    }

    public static <E> E linkedHashMapToModelObject(final LinkedHashMap<String, String> compMap,
            final Class<?> objClass) {

        try {

            final Object tarObjInst = objClass.getDeclaredConstructor().newInstance();

            for (final var ck : compMap.keySet()) {

                String cval = compMap.get(ck).toString();

                if (cval.toLowerCase().contains("null")) {
                    cval = null;
                    continue;
                }

                for (final var cField : tarObjInst.getClass().getDeclaredFields()) {

                    if (cField.getName().toLowerCase().equals(ck.toLowerCase())) {
                        // Field cField = tarObjInst.getClass().getField(ck);
                        cField.setAccessible(true);

                        if (cField.getType().equals(Date.class)) {

                            final var dateMap = stringToDateMap(cval);

                            final var ldtIns = LocalDateTime
                                    .of(Integer.parseInt(dateMap.get("year")),
                                            Integer.parseInt(dateMap.get("month")),
                                            Integer.parseInt(dateMap.get("day")),
                                            Integer.parseInt(dateMap.get("hours")),
                                            Integer.parseInt(dateMap.get("minutes")),
                                            Integer.parseInt(dateMap.get("seconds")))
                                    .toInstant(ZoneOffset.UTC);

                            final var cdate = Date.from(ldtIns);
                            cField.set(tarObjInst, cdate);

                        }

                        else if (cField.getType().equals(BigDecimal.class)) {
                            cField.set(tarObjInst, cval == null ? null : new BigDecimal(cval));
                        } else if (cField.getType().equals(Integer.class)) {
                            cField.set(tarObjInst, cval == null ? null : Integer.parseInt(cval));
                        } else {
                            cField.set(tarObjInst, cval);
                        }

                        cField.setAccessible(false);
                        break;
                    }

                }

            }

            return (E) tarObjInst;

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public static String modelObjectListToJSON(final List<?> currentObjList) {
        // String jsonStr = "{ \r\n";
        String jsonStr = "";

        try {

            // Object currentObj = currentObjList.get(0).getClass().getDeclaredFields()

            final Field[] fields = currentObjList.get(0).getClass().getDeclaredFields();
            int clineLen = 5;

            for (int i = 0; i < currentObjList.size(); i++) {

                Object currentObj = currentObjList.get(i);

                if (currentObj instanceof Date) {
                    currentObj = ((Date) currentObj).toInstant().toString();
                }

                jsonStr += StringUtil.indentBy(clineLen, '\s') + "\""
                        + currentObj.getClass().getSimpleName() + "\"" + " : "
                        + "{ \r\n";
                clineLen += 5;

                int fi = 0;
                int nnf = 0;
                for (final Field field : fields) {

                    field.setAccessible(true);
                    if (field.get(currentObj) == null) {
                        continue;
                    } else {
                        // add comma here
                        jsonStr += fi > 0 ? "," : "";
                        ++nnf;
                    }

                    jsonStr += StringUtil.indentAndNewLineBy(clineLen, '\s');

                    final String fieldValue = (String) field.get(currentObj);

                    jsonStr += """
                            "#{fieldName}" : "#{fieldValue}"
                            """
                            .replace("#{fieldName}", field.getName())
                            .replace("#{fieldValue}", fieldValue);
                    // .replace("#{separator}", fi < fields.length - 1 && fi == nnf - 1 ? "," : ""

                    field.setAccessible(false);
                    fi++;

                }

                clineLen -= 5;
                jsonStr += StringUtil.indentBy(clineLen, '\s') + "}";

                jsonStr = "{" + StringUtil.indentAndNewLineBy(5, '\s') + jsonStr +
                        StringUtil.indentAndNewLineBy(0, '\s') + "}";

            }

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return jsonStr;
    }

    public static String modelObjectToJSON(final Object currentObj) {
        // String jsonStr = "{ \r\n";
        String jsonStr = "";

        try {

            final Field[] fields = currentObj.getClass().getDeclaredFields();
            int clineLen = 5;

            jsonStr += StringUtil.indentBy(clineLen, '\s') + "\"" + currentObj.getClass().getSimpleName() + "\"" + " : "
                    + "{ \r\n";
            clineLen += 5;

            int fi = 0;
            int nnf = 0;
            for (final Field field : fields) {

                field.setAccessible(true);
                if (field.get(currentObj) == null) {
                    continue;
                } else {
                    // add comma here
                    jsonStr += fi > 0 ? "," : "";
                    ++nnf;
                }

                jsonStr += StringUtil.indentAndNewLineBy(clineLen, '\s');
                final String fieldValue = (String) field.get(currentObj);

                jsonStr += """
                        "#{fieldName}" : "#{fieldValue}"
                        """
                        .replace("#{fieldName}", field.getName())
                        .replace("#{fieldValue}", fieldValue);
                // .replace("#{separator}", fi < fields.length - 1 && fi == nnf - 1 ? "," : ""

                field.setAccessible(false);
                fi++;

            }

            clineLen -= 5;
            jsonStr += StringUtil.indentBy(clineLen, '\s') + "}";

            jsonStr = "{" + StringUtil.indentAndNewLineBy(5, '\s') + jsonStr +
                    StringUtil.indentAndNewLineBy(0, '\s') + "}";

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return jsonStr;
    }

    public static LinkedHashMap<String, String> linkedHashMapToStringMap(final LinkedHashMap<String, Object> currentMap,
            final String targetId) {

        final LinkedHashMap<String, String> nmap = new LinkedHashMap<>();

        try {

            final LinkedHashMap<String, Object> targetMap = findMapFromLayers(currentMap, targetId);

            for (final var crow : targetMap.keySet()) {

                final var currentObj = targetMap.get(crow);
                if (targetMap.get(crow) instanceof LinkedHashMap) {
                    linkedHashMapToStringMap((LinkedHashMap<String, Object>) currentObj, targetId);
                } else {
                    final String nv = (String) currentObj;
                    nmap.put(crow, nv);
                }

            }

            return nmap;

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    protected static LinkedHashMap<String, String> linkedHashMapToStringMapL(
            final LinkedHashMap<String, Object> currentMap) {
        final LinkedHashMap<String, String> strMap = new LinkedHashMap<>();
        // depth of 1 only
        try {

            for (var crow : currentMap.entrySet()) {

                String cname = crow.getKey();
                String cvalue = objectToString(crow.getValue());
                cname = StringUtil.removeInvalidLangVarSequences(cname);
                strMap.put(cname, cvalue);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return strMap;
    }

    private static <E> String objectToString(E value) {

        return value.toString();

        // if(value instanceof String){
        // return value;
        // } else if(value instanceof int){
        // return (int) value.toString();
        // }

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

    public static <E> int isEmpty(final E[] externalColumns) {

        int hasCount = 0;
        for (final var crow : externalColumns) {

            if (crow.getClass().equals(String.class)) {

                final var crowStr = (String) crow;
                if (crowStr != null && crowStr.length() > 0) {
                    ++hasCount;
                }

            }

        }

        if (hasCount > 0) {
            return 1;
        } else {
            return 0;
        }

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

    public static Date parseDate(final String cval) {
        final var dateMap = stringToDateMap(cval);

        final var ldtIns = LocalDateTime
                .of(Integer.parseInt(dateMap.get("year")),
                        Integer.parseInt(dateMap.get("month")),
                        Integer.parseInt(dateMap.get("day")),
                        Integer.parseInt(dateMap.get("hours")),
                        Integer.parseInt(dateMap.get("minutes")),
                        Integer.parseInt(dateMap.get("seconds")))
                .toInstant(ZoneOffset.UTC);
        final var cdate = Date.from(ldtIns);
        return cdate;

    }

    public static String dateToString(final Date date) {

        return date.toString();
    }

    public static char[] extractByIndexRange(final char[] currentArr, final Integer from, final Integer to)
            throws Exception {

        char[] finalArr = new char[0];

        if (from < 0 || to < 0 || to < from || from > to || from > currentArr.length || to > currentArr.length) {
            throw new Exception("invalid range values");
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

    public static char[][] splitBy(final char[] dataRowArr, final int from, final char splitBy,
            final char[] filterchars) {

        char[][] splitArr = new char[0][0];
        char[] currentArr = new char[0];
        for (int i = from; i < dataRowArr.length; i++) {

            if (filterchars != null && hasChar(filterchars, dataRowArr[i]) == 1 && i != dataRowArr.length - 1) {
                continue;
            }

            if (i > 0 && (dataRowArr[i] == splitBy || i == dataRowArr.length - 1)) {

                if (i == dataRowArr.length - 1 && currentArr.length > 0) {
                    currentArr = addToCharArray(currentArr, dataRowArr[i]);
                }
                currentArr = trim(currentArr);
                splitArr = addToCharArray2D(splitArr, currentArr);
                currentArr = new char[0];
                continue;
            }

            currentArr = addToCharArray(currentArr, dataRowArr[i]);
        }

        return splitArr;
    }

    public static String[][] splitBy(final String[] dataRowArr, final int from,
            final String splitBy, final String[] filterItems) {
        String[][] splitArr = new String[0][0];

        for (int i = from; i < dataRowArr.length; i++) {

            if (filterItems != null && has(filterItems, dataRowArr[i]) == 1) {
                continue;
            }

            final String[] parts = dataRowArr[i].split(splitBy);
            splitArr = addToArray2D(splitArr, new String[splitArr.length + 1][], parts);

        }

        return splitArr;
    }

    public static Object splitBy_SWP(final char[] mainArr, final int from,
            final String splitBy, final char[] filterItems) {

        try {

            // undecided final output type either string[] or charp[][]
            resetCounters_SWP();

            int parts = SplitTasks.splitTasksMaxCount;
            // int contentlen = content.length();
            Double partlen = (mainArr.length * 1.000D) / (parts * 1.000D);
            mainSeekPos = 0;
            int endSeekPos = partlen.intValue();
            // charArr2DAgg = new char[partlen.intValue() * parts][];

            charArr2DAgg_SWP = new char[0][];
            cpUpto_SWP = 0;
            int ixx = 0;
            for (int i = 0; i <= parts; i++) {

                var currArr = StringUtil.extractByIndexRange(mainArr, mainSeekPos, endSeekPos);

                var ract = new Runnable() {
                    @Override
                    public void run() {

                        var csChar2DArr = splitBy(currArr, mainSeekPos, splitBy, filterItems);

                        // var csChar2DArr = sliceToParts(currArr, openingSeq, closingSeq,
                        // openingBoundCriteria, closingBoundCriteria, filterSeq, replaceSeq,
                        // MatchCriteria.Last);

                        for (int i = 0; i < csChar2DArr.length; i++) {
                            strArr_SWP = StringUtil.add(strArr_SWP, new String(csChar2DArr[i]));
                            // if (i == cpUpto) {
                            // break;
                            // }
                        }

                    }
                };
                ract.run();

                mainSeekPos = endSeekPos;
                endSeekPos += SplitTasks.splitTasksMaxCount;

                if (endSeekPos > mainArr.length - 1) {
                    endSeekPos = mainArr.length - 1;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return strArr_SWP;

    }

    public static char[][] splitByRange_SWP_Enc(final char[] mainArr, final int from, int to,
            final String splitBy, final char[] filterItems) throws Exception {

        if (from < 0 || to < 0 || to < from || from > to || from >= mainArr.length || to >= mainArr.length) {
            System.out.println("arr len:" + mainArr.length + "from:" + from + "|to:" + to);
            throw new Exception("invalid range values for splitByRange_SWP_Unit");
        }

        char[][] split2DArr = new char[0][];
        try {

            char[] lineCharArr = new char[0];
            for (int i = from; i <= to; i++) {

                if (filterItems != null && hasChar(filterItems, mainArr[i]) == 1) {
                    continue;
                }

                if (checkGroupSeq(mainArr, i, new char[][] { splitBy.toCharArray() }, MatchCriteria.First) == 1) {
                    split2DArr = addToCharArray2D(split2DArr, lineCharArr);
                }

                lineCharArr = addToCharArray(lineCharArr, mainArr[i]);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return split2DArr;
    }

    public static char[][] splitBy(final char[] mainArr, final int from,
            final String splitBy, final char[] filterItems) {
        char[][] split2DArr = new char[0][];
        try {

            char[] lineCharArr = new char[0];
            for (int i = from; i < mainArr.length; i++) {

                if (filterItems != null && hasChar(filterItems, mainArr[i]) == 1) {
                    continue;
                }

                if (checkGroupSeq(mainArr, i, new char[][] { splitBy.toCharArray() }, MatchCriteria.First) == 1) {
                    split2DArr = addToCharArray2D(split2DArr, lineCharArr);
                    continue;
                }

                lineCharArr = addToCharArray(lineCharArr, mainArr[i]);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return split2DArr;
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

    public static LinkedHashMap<String, Object> checkForExactMatchFromBlock(final char[] currentArr,
            final char[] keywordArr,
            final MatchCriteria matchCriteria) {

        resetCounters();
        utilInfo = new LinkedHashMap<>();

        int matchedCount = 0;
        int matchCount = 0;

        int[] startPts = new int[0];
        int[] endPts = new int[0];
        String[] matchPts = new String[0];
        utilInfo.put("matchfound", 0);

        for (int i = 0, kwix = 0; i < currentArr.length; i++) {

            if ((currentArr.length - 1) - i < (keywordArr.length - 1) - matchCount) {
                break;
            }

            if (currentArr[i] == keywordArr[kwix]) {
                ++matchCount;

                if (kwix == 0) {
                    utilInfo.put("ExactMatchFromBlock_StartPt", i);
                }

                if (kwix == keywordArr.length - 1) {
                    ++matchedCount;

                    utilInfo.put("ExactMatchFromBlock_EndPt", i);

                    if (matchCriteria.equals(MatchCriteria.First)) {
                        break;
                    } else if (matchCriteria.equals(MatchCriteria.All)) {

                        final String currentKey = "ExactMatchFromBlock_" + matchedCount + "_";
                        utilInfo.put(currentKey + "StartPt", utilInfo.get("ExactMatchFromBlock_StartPt"));
                        utilInfo.put(currentKey + "EndPt", utilInfo.get("ExactMatchFromBlock_EndPt"));

                        matchPts = add(matchPts, utilInfo.get("ExactMatchFromBlock_StartPt") + "-"
                                + utilInfo.get("ExactMatchFromBlock_EndPt"));
                        startPts = addToIntArray(startPts,
                                Integer.parseInt(utilInfo.get("ExactMatchFromBlock_StartPt").toString()));
                        endPts = addToIntArray(endPts,
                                Integer.parseInt(utilInfo.get("ExactMatchFromBlock_EndPt").toString()));

                        matchCount = 0;
                        kwix = 0;
                    }

                } else {
                    kwix++;
                }

            } else {
                matchCount = 0;
                kwix = 0;
            }

        }

        if (matchCriteria.equals(MatchCriteria.Last) || matchCriteria.equals(MatchCriteria.All)) {

            utilInfo.remove("ExactMatchFromBlock_StartPt");
            utilInfo.remove("ExactMatchFromBlock_EndPt");

            if (matchedCount > 0) {
                // return 1;
                utilInfo.put("matchfound", 1);
            }

        } else {

            if (matchCount == keywordArr.length) {
                // return 1;
                utilInfo.put("matchfound", 1);
            } else {
                utilInfo.put("ExactMatchFromBlock_StartPt", -1);
                utilInfo.put("ExactMatchFromBlock_EndPt", -1);
            }

        }

        utilInfo.put("matchPoints", matchPts);
        utilInfo.put("startPoints", startPts);
        utilInfo.put("endPoints", endPts);

        return utilInfo;

    }

    public static String replaceWith(final String content, final String seq, final String replaceSeq) throws Exception {

        if (seq == null || seq == "") {
            throw new Exception("search sequence is invalid");
        }

        char[] contentArr = content.toCharArray();
        final char[] seqArr = seq.toCharArray();
        char[][] seqArr2D = new char[0][];
        seqArr2D = addToCharArray2D(seqArr2D, seqArr);

        final char[] replaceSeqArr = replaceSeq.toCharArray();

        if (equate(seqArr, replaceSeqArr) == 1) {
            return content;
        }

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

    public static <E extends Object> E[] reorder(final E[] mainArr, final E[] expDRowRO, final int[] initSortModeArr,
            final SortMode order) {

        // E[] expDRowRO = new char[mainArr.length][];
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

    public static Map<String, Object> splitAndMap(final String currentBlock, String[] splitterInLevels,
            final String[] filterSeq) {

        final Map<String, Object> currentMap = new LinkedHashMap<>();
        final String cSplitter = splitterInLevels[0];
        splitterInLevels = removeFrom(splitterInLevels, 0);

        final String[] cfragments = split(currentBlock, cSplitter);

        // char[] currentArr = currentBlock.toCharArray();
        for (int i = 0; i < cfragments.length; i++) {
            Map<String, Object> crowMap = new LinkedHashMap<>();
            if (splitterInLevels.length > 0) {
                crowMap = splitAndMap(cfragments[i], splitterInLevels, filterSeq);
                // currentMap.put(crowMap.keySet().toArray()[0].toString(), crowMap);
            } else {
                /*
                 * String[] parts = split(cfragments[i], cSplitter);
                 * crowMap.put(parts[0], parts.length > 1 ? parts[1] : null);
                 */
                // apply filter for both

                if (filterSeq != null) {
                    final var keyPart = removeSequences(cfragments[0].toCharArray(),
                            stringArrayToCharArray2D(filterSeq));
                    final var valuePart = removeSequences(cfragments[1].toCharArray(),
                            stringArrayToCharArray2D(filterSeq));
                    crowMap.put(new String(keyPart), new String(valuePart));
                } else {
                    crowMap.put(cfragments[0], cfragments[1]);
                }

                return crowMap;
            }

            currentMap.putAll(crowMap);

        }

        return currentMap;

    }

    public static int checkGroupSeq(final char[] contentArr, final int from, final char[][] groupArr,
            MatchCriteria matchCriteria) throws Exception {

        if (from >= contentArr.length) {
            throw new Exception("Out of range index pos for checkGroupSeq");
        }

        // if (innerArrMaxLen(groupArr) >= contentArr.length) {
        // var x = 29;
        // }

        char[] potSeq = new char[0];
        utilInfo = new LinkedHashMap<>();

        try {

            for (int j = 0; j < groupArr.length; j++) {
                int ic = from;
                final char[] currCharArr = groupArr[j];
                int matchCount = 0;

                for (int k = 0; k < currCharArr.length; k++) {

                    try {

                        if (contentArr[ic] == currCharArr[k]
                                && (contentArr.length - (from == 0 ? from : from - 1)) > groupArr.length - 1) {
                            potSeq = addToCharArray(potSeq, contentArr[ic]);
                            if (ic < contentArr.length - 1) {
                                ic++;
                            } else {

                                // if(potSeq.length < currCharArr.length){
                                // }

                                break;
                            }

                            // ic++;
                            ++matchCount;
                        } else {
                            potSeq = new char[0];
                            break;
                        }

                    } catch (Exception e) {
                        var x = utilInfo.size();
                        var c = potSeq.length;
                        e.printStackTrace();
                    }
                }

                // takes last match as well
                if (matchCount > 0) {
                    utilInfo.put("matchedSequence", new String(potSeq));
                    utilInfo.put("mainSeekPos", ic);
                    utilInfo.put("runningIndex", ic - from);

                    if (matchCriteria.equals(MatchCriteria.First)) {
                        return 1;
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            var x = utilInfo.firstEntry();
            var c = potSeq.length;
        }

        return 0;
    }

    public static int checkGroupSeq(final char[] contentArr, final int from, final char[][] groupArr) throws Exception {

        if (from >= contentArr.length) {
            throw new Exception("Out of range index pos for checkGroupSeq");
        }

        // if (innerArrMaxLen(groupArr) >= contentArr.length) {
        // var x = 29;
        // }

        char[] potSeq = new char[0];
        utilInfo = new LinkedHashMap<>();

        try {

            for (int j = 0; j < groupArr.length; j++) {
                int ic = from;
                final char[] currCharArr = groupArr[j];
                int matchCount = 0;

                for (int k = 0; k < currCharArr.length; k++) {

                    try {

                        if (ic == 140) {
                            var dk = ic;
                        }
                        if (contentArr[ic] == currCharArr[k]
                                && (contentArr.length - (from == 0 ? from : from - 1)) > groupArr.length - 1) {
                            potSeq = addToCharArray(potSeq, contentArr[ic]);
                            if (ic <= contentArr.length - 1) {
                                ic++;
                            } else {

                                // if(potSeq.length < currCharArr.length){
                                // }

                                break;
                            }

                            // ic++;
                            ++matchCount;
                        } else {
                            potSeq = new char[0];
                            break;
                        }

                    } catch (Exception e) {
                        // var ic_exptn = ic;
                        // var x = utilInfo.size();
                        // var c = potSeq.length;

                        System.out.println("checkGroupSeq_ic: " + ic);
                        System.out.println("checkGroupSeq_potSeq: " + potSeq);
                        System.out.println("checkGroupSeq_utilInfo: " + utilInfo.toString());
                        e.printStackTrace();
                        throw e;
                    }
                }

                // prob: exactly one and first match
                if (matchCount == groupArr[j].length) {

                    utilInfo = new LinkedHashMap<>();
                    utilInfo.put("matchedSequence", new String(potSeq));
                    utilInfo.put("mainSeekPos", ic);
                    utilInfo.put("runningIndex", ic - from);

                    return 1;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            var x = utilInfo.firstEntry();
            var c = potSeq.length;
            throw e;
            // System.out.println(e.toString());
            // throw new Exception("error at checkGroupSeq");
        }

        return 0;
    }

    private static int innerArrMaxLen(char[][] mainArr) {

        if (mainArr.length == 0) {
            return 0;
        }

        int maxlen = mainArr[0].length;
        for (int i = 1; i < mainArr.length; i++) {
            maxlen = mainArr[i].length > maxlen ? mainArr[i].length : maxlen;
        }

        return maxlen;

    }

    private static int innerArrMaxLen(Object[][] mainArr) {

        int maxlen = mainArr[0].length;
        for (int i = 0; i < mainArr.length; i++) {
            maxlen = mainArr[i].length > maxlen ? mainArr[i].length : maxlen;
        }

        return maxlen;

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

    // 19052025
    public static String[] sliceToParts(final String content, final String[] openingSeq, final String[] closingSeq,
            final SeekPosExclusionCriteria openingBoundCriteria, final SeekPosExclusionCriteria closingBoundCriteria,
            final String[] filterSeq,
            final String[] replaceSeq) throws Exception {

        final String slicedContent = "";
        try {

            resetCounters();
            final char[] contentArr = content.toCharArray();

            final char[][] openingSeqArr = stringArrayToCharArray2D(openingSeq);
            final char[][] closingSeqArr = stringArrayToCharArray2D(closingSeq);
            final char[][] filterSeqArr = stringArrayToCharArray2D(filterSeq);

            final char[] cseq = new char[0];
            final char[] potSeq = new char[0];
            char[] cword = new char[0];
            int opBoundFoundOcc = 0;
            int boundInd = -1;

            char[][] slicedParts = new char[0][];
            /*
             * if (openingSeqArr.length == 0 && closingSeqArr.length == 0) {
             * // consider opened
             * boundInd = 1;
             * }
             */

            for (int i = 0; i < contentArr.length; i++) {
                utilInfo = null;

                if (checkGroupSeq(contentArr, i, openingSeqArr) == 1) {

                    if (boundInd == -1) {
                        i += Integer.valueOf(utilInfo.get("runningIndex").toString()) - 1;

                        if (i >= contentArr.length - 1) {
                            break;
                        }

                        final String matchedSequence = (String) utilInfo.get("matchedSequence");

                        ++opBoundFoundOcc;
                        boundInd = 1;
                        cword = new char[0];

                        // utilInfo = null;
                        if (!openingBoundCriteria.equals(SeekPosExclusionCriteria.excludeFirst)) {
                            cword = addAllToCharArray(cword, matchedSequence.toCharArray());
                        }
                        continue;
                    }

                }

                if (checkGroupSeq(contentArr, i, closingSeqArr) == 1 && cword.length > 0) {

                    if (opBoundFoundOcc > 0) {
                        i--;
                        // backup ri
                        // i -= Integer.valueOf(utilInfo.get("runningIndex").toString()) - 1;
                    } else {
                        i += Integer.valueOf(utilInfo.get("runningIndex").toString()) - 1;
                    }

                    if (i >= contentArr.length - 1) {
                        break;
                    }

                    final String matchedSequence = (String) utilInfo.get("matchedSequence");

                    if (!closingBoundCriteria.equals(SeekPosExclusionCriteria.excludeLast)) {
                        cword = addAllToCharArray(cword, matchedSequence.toCharArray());
                    }

                    cword = trim(cword);
                    slicedParts = addToCharArray2D(slicedParts, cword);
                    cword = new char[0];
                    utilInfo = null;
                    boundInd = -1;
                    opBoundFoundOcc = 0;

                    continue;
                }

                if (checkGroupSeq(contentArr, i, filterSeqArr) == 1) {

                    if (replaceSeq != null) {
                        final String matchedSequence = (String) utilInfo.get("matchedSequence");
                        final var filterSeqFoundAt = hasItemAt(filterSeq, matchedSequence, MatchCriteria.First);
                        if (replaceSeq.length > 0 && !replaceSeq[0].isEmpty()) {
                            cword = addAllToCharArray(cword, replaceSeq[filterSeqFoundAt].toCharArray());
                        }

                    }

                    i += Integer.valueOf(utilInfo.get("runningIndex").toString()) - 1;
                    // utilInfo = null;
                } else {

                    if (boundInd == 1) {
                        cword = addToCharArray(cword, contentArr[i]);
                    }

                }

                // if (opBoundFoundOcc > 0 && boundInd == -1)

            }

            // var sp = slicedParts.length;
            // slicedContent = new String(cword);
            return charArray2DToStringArray(slicedParts);
        } catch (final Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    public static void resetCounters_SWP() {
        charArr2DAgg_SWP = null;
        strArrAgg_SWP = null;
    }

    public static synchronized Object sliceToParts_SWP(final char[] mainArr, final String[] openingSeq,
            final String[] closingSeq,
            final SeekPosExclusionCriteria openingBoundCriteria, final SeekPosExclusionCriteria closingBoundCriteria,
            final String[] filterSeq,
            final String[] replaceSeq) {

        try {

            resetCounters_SWP();

            int parts = SplitTasks.splitTasksMaxCount;
            // int contentlen = content.length();
            Double partlen = (mainArr.length * 1.000D) / (parts * 1.000D);
            int mainSeekPos = 0;
            int endSeekPos = partlen.intValue();
            // charArr2DAgg = new char[partlen.intValue() * parts][];

            charArr2DAgg_SWP = new char[0][];
            cpUpto_SWP = 0;
            for (int i = 0; i <= parts; i++) {

                var currArr = StringUtil.extractByIndexRange(mainArr, mainSeekPos, endSeekPos);

                var ract = new Runnable() {
                    @Override
                    public void run() {

                        var csChar2DArr = sliceToParts(currArr, openingSeq, closingSeq,
                                openingBoundCriteria, closingBoundCriteria, filterSeq, replaceSeq, MatchCriteria.Last);

                        for (int i = 0; i < csChar2DArr.length; i++) {
                            strArr_SWP = StringUtil.add(strArr_SWP, new String(csChar2DArr[i]));
                            // if (i == cpUpto) {
                            // break;
                            // }
                        }

                    }
                };
                ract.run();

                mainSeekPos = endSeekPos;
                endSeekPos += SplitTasks.splitTasksMaxCount;

                if (endSeekPos > mainArr.length - 1) {
                    endSeekPos = mainArr.length - 1;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return strArr_SWP;

    }

    public static char[][] sliceToParts(final char[] contentArr, final String[] openingSeq, final String[] closingSeq,
            final SeekPosExclusionCriteria openingBoundCriteria, final SeekPosExclusionCriteria closingBoundCriteria,
            final String[] filterSeq,
            final String[] replaceSeq,
            final MatchCriteria groupMatchCriteria) {

        // final String slicedContent = "";
        try {

            resetCounters();
            // final char[] contentArr = content.toCharArray();

            final char[][] openingSeqArr = stringArrayToCharArray2D(openingSeq);
            final char[][] closingSeqArr = stringArrayToCharArray2D(closingSeq);
            final char[][] filterSeqArr = stringArrayToCharArray2D(filterSeq);

            final char[] cseq = new char[0];
            final char[] potSeq = new char[0];
            char[] cword = new char[0];
            int opBoundFoundOcc = 0;
            int boundInd = -1;

            char[][] slicedParts = new char[0][];
            /*
             * if (openingSeqArr.length == 0 && closingSeqArr.length == 0) {
             * // consider opened
             * boundInd = 1;
             * }
             */

            for (int i = 0; i < contentArr.length; i++) {
                utilInfo = null;

                if (checkGroupSeq(contentArr, i, openingSeqArr, groupMatchCriteria) == 1) {

                    if (boundInd == -1) {
                        i += Integer.valueOf(utilInfo.get("runningIndex").toString()) - 1;

                        if (i >= contentArr.length - 1) {
                            break;
                        }

                        final String matchedSequence = (String) utilInfo.get("matchedSequence");

                        ++opBoundFoundOcc;
                        boundInd = 1;
                        cword = new char[0];

                        // utilInfo = null;
                        if (!openingBoundCriteria.equals(SeekPosExclusionCriteria.excludeFirst)) {
                            cword = addAllToCharArray(cword, matchedSequence.toCharArray());
                        }
                        continue;
                    }

                }

                // if (i >= contentArr.length) {
                // var sf = "f";
                // }

                if (checkGroupSeq(contentArr, i, closingSeqArr) == 1 && cword.length > 0) {

                    if (opBoundFoundOcc > 0) {
                        i--;
                        // backup ri
                        // i -= Integer.valueOf(utilInfo.get("runningIndex").toString()) - 1;
                    } else {
                        i += Integer.valueOf(utilInfo.get("runningIndex").toString()) - 1;
                    }

                    if (i >= contentArr.length - 1) {
                        break;
                    }

                    final String matchedSequence = (String) utilInfo.get("matchedSequence");

                    if (!closingBoundCriteria.equals(SeekPosExclusionCriteria.excludeLast)) {
                        cword = addAllToCharArray(cword, matchedSequence.toCharArray());
                    }

                    cword = trim(cword);
                    slicedParts = addToCharArray2D(slicedParts, cword);
                    cword = new char[0];
                    utilInfo = null;
                    boundInd = -1;
                    opBoundFoundOcc = 0;

                    continue;
                }

                if (checkGroupSeq(contentArr, i, filterSeqArr) == 1) {

                    if (replaceSeq != null) {
                        final String matchedSequence = (String) utilInfo.get("matchedSequence");
                        final var filterSeqFoundAt = hasItemAt(filterSeq, matchedSequence, MatchCriteria.First);
                        cword = addAllToCharArray(cword, replaceSeq[filterSeqFoundAt].toCharArray());
                    }

                    i += Integer.valueOf(utilInfo.get("runningIndex").toString()) - 1;

                    if (i >= contentArr.length - 1) {
                        break;
                    }
                    // utilInfo = null;
                } else {

                    if (boundInd == 1) {
                        cword = addToCharArray(cword, contentArr[i]);
                    }

                }

                // if (opBoundFoundOcc > 0 && boundInd == -1)

            }

            // var sp = slicedParts.length;
            // slicedContent = new String(cword);
            // return charArray2DToStringArray(slicedParts);
            return slicedParts;
        } catch (final Exception e) {
            e.printStackTrace();
            var crl = contentArr.length;
        }

        return null;
    }

    // added 17052025
    public static String slice(final String content, final String[] openingSeq, final String[] closingSeq,
            final SeekPosExclusionCriteria openingBoundCriteria, final SeekPosExclusionCriteria closingBoundCriteria,
            final String[] filterSeq,
            final String[] replaceSeq) {

        String slicedContent = "";
        try {

            final char[] contentArr = content.toCharArray();

            final char[][] openingSeqArr = stringArrayToCharArray2D(openingSeq);
            final char[][] closingSeqArr = stringArrayToCharArray2D(closingSeq);
            final char[][] filterSeqArr = stringArrayToCharArray2D(filterSeq);

            final char[] cseq = new char[0];
            final char[] potSeq = new char[0];
            char[] cword = new char[0];
            int opBoundFoundOcc = 0;
            int boundInd = -1;

            /*
             * if (openingSeqArr.length == 0 && closingSeqArr.length == 0) {
             * // consider opened
             * boundInd = 1;
             * }
             */

            for (int i = 0; i < contentArr.length; i++) {

                if (i == 72) {
                    System.out.println(52);
                }

                if (checkGroupSeq(contentArr, i, openingSeqArr) == 1) {

                    i += Integer.valueOf(utilInfo.get("runningIndex").toString()) - 1;
                    final String matchedSequence = (String) utilInfo.get("matchedSequence");

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
                    final String matchedSequence = (String) utilInfo.get("matchedSequence");
                    utilInfo = null;
                    boundInd = -1;
                    if (closingBoundCriteria.equals(SeekPosExclusionCriteria.excludeLast)) {
                        // cword = new char[0];
                        utilInfo = null;
                        break;
                    } else {
                        cword = addAllToCharArray(cword, matchedSequence.toCharArray());
                    }
                }

                if (checkGroupSeq(contentArr, i, filterSeqArr) == 1) {

                    if (replaceSeq != null) {
                        final String matchedSequence = (String) utilInfo.get("matchedSequence");
                        final var filterSeqFoundAt = hasItemAt(filterSeq, matchedSequence, MatchCriteria.First);
                        cword = addAllToCharArray(cword, replaceSeq[filterSeqFoundAt].toCharArray());
                    }

                    i += Integer.valueOf(utilInfo.get("runningIndex").toString()) - 1;
                    utilInfo = null;
                } else {

                    if (boundInd == 1) {
                        cword = addToCharArray(cword, contentArr[i]);
                    }
                }

                if (opBoundFoundOcc > 0 && boundInd == -1) {
                    break;
                }

            }

            cword = trim(cword);
            slicedContent = new String(cword);

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return slicedContent;
    }

    // added 17052025
    public static String[] split(final String content, final String splitter) {
        String[] contentParts = new String[0];
        final char[] contentArr = content.toCharArray();

        if (splitter.length() == 1) {

            final char splitChar = splitter.charAt(0);
            char[] cseq = new char[0];
            for (int i = 0; i < contentArr.length; i++) {

                if (contentArr[i] != splitChar) {
                    cseq = addToCharArray(cseq, contentArr[i]);
                }

                if (cseq.length > 0 && (contentArr[i] == splitChar || i == contentArr.length - 1)) {

                    // trim option on by default

                    contentParts = add(contentParts, new String(cseq).trim());
                    cseq = new char[0];
                }

            }

        } else if (splitter.length() > 1) {

            final char[] splitterArr = splitter.toCharArray();
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
                        if (i == contentArr.length - 1) {
                            cseq = addToCharArray(cseq, contentArr[i]);
                        }

                        contentParts = add(contentParts, new String(cseq).trim());
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

    public static String formatCodeBlock(String codeBlock) {
        String formatted = "";

        codeBlock = "\s\s" + codeBlock + "\s\s";
        final char[] cbArr = codeBlock.toCharArray();
        int eoc = 0;

        int clineLen = mainIndentLen;
        final int vpWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        final int vpHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
        final int indentWidth = vpWidth;

        final int xmlPartInd = -1; // closed

        final int maxLineLen = indentWidth / 14;
        mainSeekPos = 2;

        do {

            if (cbArr[mainSeekPos] == '{') {
                clineLen += 5;
                formatted += "\s\s" + cbArr[mainSeekPos];
                formatted += "\r\n";
                formatted += StringUtil.indentBy(clineLen, '\s');

            } else if (cbArr[mainSeekPos] == '}') {
                clineLen -= 5;
                formatted += "\r\n";
                formatted += StringUtil.indentBy(clineLen, '\s');
                formatted += cbArr[mainSeekPos];
                formatted += "\r\n";
                formatted += StringUtil.indentBy(clineLen, '\s');

            } else if (cbArr[mainSeekPos] == ';') {
                formatted += cbArr[mainSeekPos];
                formatted += "\r\n";
                formatted += StringUtil.indentBy(clineLen, '\s');
            } else {
                formatted += cbArr[mainSeekPos];
            }

            // line break
            if (clineLen >= maxLineLen) {
                formatted += "\r\n";
                clineLen -= clineLen >= 5 ? 5 : 0;
                // indentWidth += clineLen;
                formatted += StringUtil.indentBy(clineLen, '\s');
            }

            if (mainSeekPos == cbArr.length - 1) {
                endOfContent = 1;
            }

            ++mainSeekPos;

            if (mainSeekPos >= cbArr.length - 1) {
                eoc = 1;
            }

        } while (eoc != 1);

        formatted += "\r\n";

        return formatted;

    }

    public static String formatCodeBlock_Old(String codeBlock) {
        String formatted = "";

        codeBlock = "\s\s" + codeBlock + "\s\s";
        final char[] cbArr = codeBlock.toCharArray();
        int eoc = 0;

        int clineLen = mainIndentLen;
        final int vpWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        final int vpHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
        final int indentWidth = vpWidth;

        final int xmlPartInd = -1; // closed

        final int maxLineLen = indentWidth / 14;
        mainSeekPos = 2;

        do {

            if (cbArr[mainSeekPos] == '{') {
                clineLen += 5;
                formatted += "\s\s" + cbArr[mainSeekPos];
                formatted += "\r\n";
                formatted += StringUtil.indentBy(clineLen, '\s');

            } else if (cbArr[mainSeekPos] == '}') {
                clineLen -= 5;

                formatted += "\r\n";
                formatted += StringUtil.indentBy(clineLen, '\s');
                formatted += cbArr[mainSeekPos];

            } else if (cbArr[mainSeekPos] == ';') {
                formatted += cbArr[mainSeekPos];
                formatted += "\r\n";
                formatted += StringUtil.indentBy(clineLen, '\s');
            } else {
                formatted += cbArr[mainSeekPos];
            }

            // line break
            if (clineLen >= maxLineLen) {
                formatted += "\r\n";
                clineLen -= clineLen >= 5 ? 5 : 0;
                // indentWidth += clineLen;
                formatted += StringUtil.indentBy(clineLen, '\s');
            }

            if (mainSeekPos == cbArr.length - 1) {
                endOfContent = 1;
            }

            ++mainSeekPos;

            if (mainSeekPos >= cbArr.length - 1) {
                eoc = 1;
            }

        } while (eoc != 1);

        formatted += "\r\n";

        return formatted;
    }

    public static String linearizeContent(final String content) {

        final char[] contentArr = content.toCharArray();
        char[] finalArr = new char[0];
        final char[] filterchars = { '\r', '\n' };

        for (int i = 0; i < contentArr.length; i++) {

            if (hasChar(filterchars, contentArr[i]) != 1) {
                finalArr = addToCharArray(finalArr, contentArr[i]);
            }

        }

        return new String(finalArr);

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

        int cuts = 0;
        while (cuts < 3) {
            cuts = Double.valueOf(Math.random() * totalSize / 2).intValue();
        }

        final int cascadeFactor = totalSize / 2 + 4;
        final int cascades = Double.valueOf(Math.random() * cascadeFactor).intValue();

        for (; cuts != 0; cuts--) {
            mainArr = doShuffle(mainArr, cascades);
        }

        return mainArr;

    }

    public static char[] shuffle_old(final char[] mainArr) {

        final char[] finalArr = new char[0];
        int[] shuffIndexArr = new int[0];
        final int totalSize = mainArr.length;

        final int fp = Double.valueOf(Math.random() * totalSize).intValue();
        final int cascadeFactor = totalSize / 2 + 4;
        final int shuffIndex = fp;

        for (int x = 0; x < mainArr.length; x++) {
            mainSeekPos = -1;
            numberGenerator(shuffIndexArr, totalSize);
            shuffIndexArr = addToIntArray(shuffIndexArr, mainSeekPos);
        }

        return finalArr;

    }

    public static void numberGenerator(final int[] numArr, final int maxnum) {

        final var rn = Double.valueOf(Math.random() * maxnum).intValue();
        if (hasInt(numArr, rn) == 1) {
            numberGenerator(numArr, maxnum);
        } else {
            mainSeekPos = rn;
        }

    }

    static char[] extractContentWithinBounds(final char[] currentArr, int from, final char openingBound,
            final char closingBound,
            final SeekPosExclusionCriteria openingBoundCriteria, final SeekPosExclusionCriteria closingBoundCriteria,
            final char[] filterchars) {

        mainSeekPos = 0;
        char[] trmArr = new char[0];
        int boundInd = -1;
        int ri = 0;

        try {

            if (openingBoundCriteria.equals(SeekPosExclusionCriteria.excludeFirst)) {

                // if (currentArr[from] == openingBound) {
                from = from + 1;
                boundInd *= -1;
                // }

            } else if (openingBoundCriteria.equals(SeekPosExclusionCriteria.excludeNone)) {
                boundInd *= -1;
            }

            for (int i = from; i < currentArr.length; i++, ri++) {

                if (filterchars != null && hasChar(filterchars, currentArr[i]) == 1) {
                    continue;
                }

                if (currentArr[i] == openingBound || currentArr[i] == closingBound) {
                    boundInd = -1;
                }

                trmArr = StringUtil.addToCharArray(trmArr, currentArr[i]);

                if (boundInd == -1 && currentArr[i] == closingBound) {
                    break;
                }

            }

            if (trmArr.length > 0 && closingBoundCriteria.name().equals("excludeLast")) {
                trmArr = StringUtil.removeFrom(trmArr, trmArr.length - 1);
                mainSeekPos += trmArr.length;
                ++mainSeekPos;
            } else {
                mainSeekPos += ri;
            }

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return trmArr;
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

    static BigInteger powBig(final int factor, final int base) {

        if (factor == 0) {
            return BigInteger.ONE;
        }

        final BigInteger baseBig = new BigInteger(String.valueOf(base));
        BigInteger res = baseBig;
        for (int i = 1; i < factor; i++) {
            // res *= base;
            res = res.multiply(baseBig);
        }

        return res;
    }

    static int pow(final int factor, final int base) {

        if (factor == 0) {
            return 1;
        }

        int res = base;
        for (int i = 1; i < factor; i++) {
            res *= base;
        }

        return res;
    }

    private static LinkedHashMap<String, Object> addToXMLMap(final char[] xmlArr,
            final LinkedHashMap<String, Object> currentMap) {

        try {
            int tagInd = 0;
            int innerContentInd = 0;
            int innerContentLen = 0;
            final int xmlPartInd = -1;
            int mainSeekPosWas = 0;
            int mainSeekPosNow = 0;
            int skipMapAdd = 0;

            char[] innerContentArr = new char[0];

            do {
                tagInd = 0;
                innerContentInd = 0;
                skipMapAdd = 0;

                String currentTag = null;
                if (xmlArr[mainSeekPos] == '<') {

                    tagInd = 1;
                    innerContentInd = 0;
                    mainSeekPosWas = mainSeekPos;
                    currentTag = new String(
                            extractContentWithinBounds(xmlArr, mainSeekPos, '<', '>',
                                    SeekPosExclusionCriteria.excludeNone,
                                    SeekPosExclusionCriteria.excludeNone, null));
                    mainSeekPosNow = mainSeekPosWas + mainSeekPos;
                    mainSeekPos = mainSeekPosNow;

                    if (currentTag.startsWith("</") || currentTag.endsWith("/>")) {

                        // check closing with prev opening
                        final String currentTagR1 = new String(StringUtil.removeChars(currentTag.toCharArray(),
                                new char[] { '<', '/', '>' }));
                        final String prevTagR1 = new String(
                                StringUtil.removeChars(tagLayers[tagLayers.length - 1].toCharArray(),
                                        new char[] { '<', '/', '>' }));

                        final String currentTagId = currentTagR1.substring(0,
                                currentTagR1.contains("\s") ? currentTagR1.indexOf("\s") : currentTagR1.length());
                        final String prevTagId = prevTagR1
                                .substring(0,
                                        prevTagR1.contains("\s") ? prevTagR1.indexOf("\s") : prevTagR1.length());

                        if (tagLayers.length > 0 && prevTagId.startsWith(currentTagId)) {
                            tagLayers = ContainerUtil.remove(tagLayers, tagLayers.length - 1);
                            ++mainSeekPos;
                            return currentMap;
                        } else {
                            // self closing
                            tagInd = 0;
                            innerContentInd = 0;
                            ++mainSeekPos;

                            if (currentTag.endsWith("/>")) {
                                currentMap.put(currentTag, "");
                            } else {
                                return currentMap;
                            }

                        }

                    } else {

                        if (currentTag.endsWith("/>")) {
                            System.out.println("self closing tag: " + currentTag);
                        } else {
                            tagLayers = ContainerUtil.add(tagLayers, currentTag);
                        }

                    }

                } else {

                    //
                    ++innerContentLen;
                    if (innerContentInd == 0) {
                        innerContentInd = 1;
                    }

                    // find spaces between tags
                    mainSeekPosWas = mainSeekPos;
                    innerContentArr = extractContentWithinBounds(xmlArr, mainSeekPos - 1, '>', '<',
                            SeekPosExclusionCriteria.excludeFirst,
                            SeekPosExclusionCriteria.excludeLast, null);
                    mainSeekPosNow = mainSeekPosWas + mainSeekPos;
                    mainSeekPos = mainSeekPosNow;
                    --mainSeekPos;

                    if (innerContentArr.length == 0 || StringUtil.isAlphaNumericSeq(innerContentArr) == 0) {
                        innerContentInd = 0;
                        skipMapAdd = 1;
                    }

                }

                if (currentTag == null && tagLayers.length > 0 && tagLayers[0] != null) {
                    currentTag = tagLayers[tagLayers.length - 1];
                }

                if (innerContentInd == 1 && innerContentArr.length > 0) {
                    if (skipMapAdd == 0) {
                        currentMap.put(currentTag, new String(innerContentArr));
                        // ++mainSeekPos;
                    }
                } else if (tagInd == 1) {
                    ++mainSeekPos;
                    final LinkedHashMap<String, Object> c2Map = new LinkedHashMap<>();
                    /*
                     * c2Map = addToXMLMap(xmlArr, c2Map);
                     * if (c2Map != null) {
                     * currentMap.put(currentTag, c2Map);
                     * }
                     */
                    currentMap.put(currentTag, addToXMLMap(xmlArr, c2Map));
                }

                if (mainSeekPos >= xmlArr.length - 1) {
                    endOfContent = 1;
                }

            } while (endOfContent != 1);

            // System.out.println("end of form");

        } catch (final Exception e) {
            e.printStackTrace();
            final var xw = mainSeekPos;
        }

        return currentMap;

    }

    private static void initTxtProc(String content) {

        // remove comments from content
        content = removeComments(content);
        content = setBoundsToJSONContent(content);
        content = linearizeJSONContent(content);

        // content = indentJSONContent(content);
        final char[] contentArr = content.toCharArray();

        mainSeekPos = 0;
        endOfContent = 0;
        contentMap = addToJSONMap(contentArr, contentMap);
        // System.out.println("end of txt proc: " + contentMap);
        // System.out.println("info: " + utilInfo.toString());
        // System.out.println("info: " + mainSeekPosStops);

        jsonString = "";
        unravelNodeToUnquotedMap(contentMap);
        System.out.println("jsonstring:\n\n " + jsonString);

        // String opPath = "D:\WS-P\WS\UVLOutput.txt";
        // FileUtil.stringToFile(jsonString, opPath);

        // System.out.print("end of txt proc: " + contentMap.toString());

        // last map entry
        // Map<String, Object> horizontalMap = flatten(contentMap);
        // System.out.print("HorizontalMap: " + horizontalMap.toString());

    }

    @SuppressWarnings("unchecked")
    public static LinkedHashMap<String, Object> unravelMap(LinkedHashMap<String, Object> infoMap) {

        var atLevel = (int) (infoMap.get("AtLevel") != null ? infoMap.get("AtLevel") : 1);
        // var atLevel2 = MapUtil.defaultValue(infoMap.get("AtLevel"), 1);

        var targetLevel = (int) infoMap.get("TargetLevel");
        var currentMap = (LinkedHashMap<String, Object>) infoMap.get("DataMap");

        if (atLevel == targetLevel) {
            return currentMap;
        } else {
            infoMap.put("AtLevel", ++atLevel);
            var dataMap = new LinkedHashMap<String, Object>();
            for (var crow : currentMap.keySet()) {

                final Object currentValue = currentMap.get(crow);

                if (currentValue instanceof LinkedHashMap) {
                    infoMap.put("DataMap", dataMap);
                } else if (currentValue instanceof Object[]) {

                    final Object[] cvArr = (Object[]) currentValue;
                    for (final var arrow : cvArr) {
                        if (arrow instanceof Map) {
                            dataMap.putAll((LinkedHashMap<String, Object>) arrow);
                        }
                    }

                }

                infoMap.put("DataMap", dataMap);
                return unravelMap(infoMap);

            }
        }

        return null;

    }

    public static void unravelNodeToString(final LinkedHashMap<String, Object> currentMap) {
        jsonString += "{";
        final Set<String> cset = currentMap.keySet();
        int runningIndex = 0;
        for (final var crow : cset) {

            final String currentID = crow;
            final Object currentValue = currentMap.get(crow);

            if (currentValue instanceof LinkedHashMap) {
                jsonString += currentID + ":";
                final LinkedHashMap<String, Object> cmap = (LinkedHashMap<String, Object>) currentValue;
                unravelNodeToString(cmap);
                // jsonString += "}";

            } else if (currentValue instanceof Array) {

                final String cvStr = currentValue.toString();
                jsonString += "{" + currentID + ":" + cvStr + "}";

            } else if (currentValue instanceof Object[]) {

                final Object[] cvArr = (Object[]) currentValue;
                jsonString += currentID + ":" + "[";

                for (final var arrow : cvArr) {
                    // Object cvInner = arrow;

                    if (arrow instanceof Map) {
                        final LinkedHashMap<String, Object> cvInnerMap = (LinkedHashMap<String, Object>) arrow;
                        unravelNodeToString(cvInnerMap);
                    }

                }

                jsonString += "]";

            } else {

                final String cvStr = currentValue.toString();
                jsonString += currentID + ":" + cvStr;
            }

            if (runningIndex < cset.size() - 1) {
                jsonString += ",";
            }

            ++runningIndex;
        }

        jsonString += "}";

    }

    private static LinkedHashMap<String, Object> unravelNodeToUnquotedMap(
            final LinkedHashMap<String, Object> currentMap) {
        // unquotedMap += "{";

        final LinkedHashMap<String, Object> unquotedMap = new LinkedHashMap<>();
        final Set<String> cset = currentMap.keySet();
        int runningIndex = 0;
        for (final var crow : cset) {

            final String currentID = crow;
            final Object currentValue = currentMap.get(crow);

            if (currentValue instanceof LinkedHashMap) {
                final LinkedHashMap<String, Object> cmap = (LinkedHashMap<String, Object>) currentValue;
                unquotedMap.put(currentID.replace("\"", ""), unravelNodeToUnquotedMap(cmap));

            } else if (currentValue instanceof Array) {

                final String cvStr = currentValue.toString().replace("\"", "");
                // j2Str += "{" + currentID + ":" + cvStr + "}";
                unquotedMap.put(currentID.replace("\"", ""), cvStr);

            } else if (currentValue instanceof Object[]) {

                final Object[] cvArr = (Object[]) currentValue;
                // j2Str += currentID + ":" + "[";

                for (final var arrow : cvArr) {
                    // Object cvInner = arrow;

                    if (arrow instanceof Map) {
                        final LinkedHashMap<String, Object> cvInnerMap = (LinkedHashMap<String, Object>) arrow;
                        // unravelNodeToUnquotedMap(cvInnerMap);
                        unquotedMap.put(currentID.replace("\"", ""), unravelNodeToUnquotedMap(cvInnerMap));
                    }

                }

                // j2Str += "]";

            } else {

                final String cvStr = currentValue.toString();
                // j2Str += currentID + ":" + cvStr;
                unquotedMap.put(currentID.replace("\"", ""), cvStr.replace("\"", ""));
            }

            if (runningIndex < cset.size() - 1) {
                // j2Str += ",";
            }

            ++runningIndex;
        }

        // j2Str += "}";
        return unquotedMap;

    }

    private static String setBoundsToJSONContent(final String content) {

        String finalContent = "";
        final int runningIndex = 0;
        try {

            // todo remove single multi line comments and trim id and data and linearize
            final char[] contentArr = content.toCharArray();
            final char[] contentArrBound = new char[0];

            if (contentArr[0] != '{') {
                final String id = StringUtil.randomIDGenerator(5, true, true, false);
                finalContent = "{" + "\"" + id + "\"" + ":" + content + "}";
            } else {
                finalContent = content;
            }

        } catch (final Exception e) {
            e.getStackTrace();
        }

        return finalContent;

    }

    private static Map<String, Object> mirror(final LinkedHashMap<String, Object> verticalMap) {

        final Map<String, Object> fmap = new LinkedHashMap<>();
        for (final var id : verticalMap.keySet()) {
            final var val = verticalMap.get(id);
            if (val instanceof Map) {
                final LinkedHashMap<String, Object> innerMap = (LinkedHashMap<String, Object>) val;
                fmap.put(id, mirror(innerMap));
            } else {
                fmap.put(id, fmap);
            }
        }

        return fmap;

    }

    private static char[] alphaNumericCharSet() {

        char[] alnmCharSet = new char[0];
        for (int i = 0; i < 123; i++) {

            if ((i >= 48 && i <= 57) || (i >= 65 && i <= 90) || (i >= 97 && i <= 122)) {
                // ++alphaNumericCount;
                alnmCharSet = addToCharArray(alnmCharSet, (char) i);
            }
        }

        return alnmCharSet;

    }

    private static String[] charArrayToStringArray(final char[] charArr) {

        String[] strArr = new String[0];
        for (int i = 0; i < charArr.length; i++) {
            strArr = add(strArr, new String(new char[] { charArr[i] }));
        }

        return strArr;

    }

    private static char[] flatten(final char[][] currentArr) {

        char[] flatArr = new char[0];
        for (int i = 0; i < currentArr.length; i++) {
            flatArr = addAllToCharArray(flatArr, currentArr[i]);
        }

        return flatArr;

    }

    private static int hasCharSeq(final char[][] currentArr, char[] item) {

        item = trim(item);
        for (int i = 0; i < currentArr.length; i++) {

            if (currentArr[i] != null && item != null && equate(currentArr[i], item) == 1) {
                return 1;
            }

        }

        return 0;

    }

    private static LinkedHashMap<String, Object> addToJSONMap(final char[] currentArr,
            final LinkedHashMap<String, Object> currentMap) {

        try {

            String currentID = null;
            Object currentValue = null;

            do {

                /*
                 * if (mainSeekPos >= 225) {
                 * System.out.println("e_stop @ " + mainSeekPos);
                 * }
                 */

                String cresult = validateJSONSeqType(currentArr, mainSeekPos);
                if (cresult == null || !cresult.equals("id")) {
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
                    if (removeQuotesInd == 1) {
                        currentID = currentID.replace("\"", "");
                    }
                }

                cresult = validateJSONSeqType(currentArr, mainSeekPos);
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
                    final LinkedHashMap<String, Object> c2Map = new LinkedHashMap<>();
                    currentMap.put(currentID, addToJSONMap(currentArr, c2Map));
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

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return currentMap;
    }

    @Deprecated
    private static void adjustMainSeekPos(final char[] currentArr, final int rindex) {

        try {

            int isBoundOrBreakChar = 0;
            final Character[] breakList = { '"', ':', ',', '{', '[', '}', ']' };
            int dir = 0;

            do {
                final char x = currentArr[mainSeekPos];
                // System.out.println("x@" + x);
                if (has(breakList, x) == 0) {
                    mainSeekPos -= 1;
                    dir = -1;
                    isBoundOrBreakChar = 0;
                } else {
                    --mainSeekPos;
                    isBoundOrBreakChar = 1;
                    final var nextC = currentArr[mainSeekPos + 1];

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

        } catch (final Exception e) {
            System.out.println(e.toString());
        }

    }

    private static String validateJSONSeqType(final char[] currentArr, final int from) {

        currentSeqType = null;
        currentValueType = null;

        if (mainSeekPos > 1130) {
            // var x = "1130";
        }

        mainSeekPosStops = addToIntegerArray(mainSeekPosStops, from);

        try {

            final int i = from;

            // final int currentCP = currentArr[i];
            // final int nextCP = currentArr[i + 1];

            // validate: only for debugging
            if (i + 1 > currentArr.length - 1) {
                stepOutInd = steppedInLayer > 1 ? 1 : 0;
                // mainSeekPos += 1;
                currentSeqType = "stepOutInd";
                return currentSeqType;
            }

            if ((currentArr[i] == '{' || currentArr[i] == ',') &&
                    (currentArr[i + 1] == '"' /* || validJSONChar(nextCP) */)) {
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
                final var x = mainSeekPos;
                if (currentArr[i] == ']' && currentArr[i + 1] == ',') {
                    currentSeqType = "id";
                }

                stepOutInd = steppedInLayer > 1 ? 1 : 0;
                mainSeekPos += 1;
                currentSeqType = "stepOutInd";
            }
        } catch (final Exception e) {
            System.out.println("Error near: msp " + mainSeekPos);
            e.printStackTrace();
        }

        return currentSeqType;

    }

    // private static boolean validJSONCharForMap(final int cp) {

    // if ((cp >= 48 && cp <= 57) || (cp >= 65 && cp >= 90)) {
    // return true;
    // }

    // return false;

    // }

    private static int checkIfDoubleSeq(final char[] blockArr, final int from) {

        int matchCount = 0;
        int seekLen = 0;

        try {

            for (int i = from; i < blockArr.length; i++, seekLen++) {

                if (blockArr[i] == ',' || blockArr[i] == '}') {
                    break;
                }

                final int cp = blockArr[i];

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

        } catch (final Exception e) {
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

                if ((cp >= 48 && cp <= 57) || cp == '.' || cp == '-') {
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
            } else if (matchCount == seekLen
            // && decPlaceCount >= 2
            ) {
                return 1;
            }

        } catch (Exception e) {
            System.err.println(e.toString());
            return 0;
        }

        return 0;

    }

    private static int checkIfIntegerSeq(final char[] blockArr, final int from) {

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

                final int cp = blockArr[i];

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

        } catch (final Exception e) {
            System.err.println(e.toString());
            return 0;
        }

        return 0;

    }

    private static int checkIfBooleanSeq(final char[] blockArr, final int from) {

        char[] wordArr = new char[0];
        for (int i = from; i < from + 5; i++) {
            if (blockArr[i] == ',' || blockArr[i] == '}') {
                break;
            }
            wordArr = addToCharArray(wordArr, blockArr[i]);
        }

        final String word = new String(wordArr);

        if (word.equals("true") || word.equals("false")) {
            return 1;
        } else {
            return 0;
        }

    }

    private static String errorAt2(final char[] blockArr, final int stopAt) {

        return String.valueOf(blockArr).substring(0, stopAt);
    }

    private static String errorAt(final char[] blockArr, final int stopAt) {

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

    private static String extractId(final char[] currentArr, final int from) {

        utilInfo = new LinkedHashMap<>();
        String extractID = "";
        char[] extractIDArr = new char[0];
        int boundSeq = 0;
        int runningIndex = 0;
        int breakFree = 0;
        for (int i = from, pi = 0; i < currentArr.length; i++) {

            if (breakFree == 1) {
                break;
            }

            // conditions applicable only for id
            if (currentArr[i] == '"'
                    || (((i + 1) < currentArr.length) && removeQuotesInd == 1 && currentArr[i + 1] == ':')) {
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

    private static Object extractValue(final char[] currentArr, final int from) {

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

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return extractVal;
    }

    private static int sumOf(final int[] numArr) {
        int sum = 0;
        for (int i = 0; i < numArr.length; i++) {
            sum += numArr[i];
        }

        return sum;
    }

    private static Object extractValueFromArraySeq(final char[] currentArr, int from) {

        try {

            final char[] jsonArr = trimArrayByOpeningSeq(currentArr, --from, currentArr[from]);
            currentSeq = "idvalue";
            inspectArray(jsonArr, 0);
            final int isJSONObjArray = utilInfo.get("isJSONObjectArray") != null
                    && (Integer) utilInfo.get("isJSONObjectArray") == 1 ? 1 : 0;

            if (isJSONObjArray == 1) {
                ++steppedInLayer;
                mainSeekPosHistory = addToIntegerArray(mainSeekPosHistory, mainSeekPos);
                mainSeekPos = 0;
                currentSeq = "";
                LinkedHashMap<String, Object> innerMap = new LinkedHashMap<>();
                Object[] msa = new Object[0];

                do {

                    if (jsonArr[1] == '{') {
                        ++mainSeekPos;
                    }

                    innerMap = new LinkedHashMap<>();
                    currentSeq = "";
                    final var cmap = addToJSONMap(jsonArr, innerMap);
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

                final int innerSeekPos = mainSeekPos;
                final var prev = mainSeekPosHistory[mainSeekPosHistory.length - 1];
                mainSeekPos = prev + innerSeekPos;
                // mainSeekPos += last;
                mainSeekPosHistory = removeFromIntegerArray(mainSeekPosHistory, prev); // sumFrom(mainSeekPosHistory);
                return msa; // addToJSONMap(jsonArr, innerMap);
            } else {

                // consider as plain string array
                // jsonArr = trimArrayByOpeningSeq(currentArr, from, currentArr[from - 1]);
                currentSeq = "";
                final Set<String> strSet = new LinkedHashSet<String>();
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

                mainSeekPos += jsonArr.length - 1;
                return strSet;

            }

        } catch (final Exception e) {
            e.printStackTrace();
            System.out.println("exValFromArraySeq: stopped @" + from);
        }

        return null;

    }

    /*
     * private static int inspectArrayL1(char[] fragment, int i) {
     * 
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
     * { '"', ':' },
     * { ':', '"' },
     * { '{', '"' },
     * { '[', '"' },
     * { '"', '}' },
     * { '"', ']' }
     * };
     * 
     * int exactMatch = 0;
     * int matchCount = 0;
     * 
     * try {
     * 
     * for (int g = 0; g < sequences.length; g++) {
     * 
     * matchCount = 0;
     * exactMatch = 0;
     * char[] cseq = sequences[g];
     * 
     * if (cseq.length != fragment.length) {
     * continue;
     * }
     * 
     * int sp = 0;
     * for (int h = 0; h < cseq.length; h++) {
     * if (fragment[sp] == cseq[h]) {
     * ++matchCount;
     * ++sp;
     * } else {
     * break;
     * }
     * }
     * 
     * if (matchCount == fragment.length) {
     * exactMatch = 1;
     * 
     * utilInfo.put("exactseqcheck_exactMatch", 1);
     * utilInfo.put("clseq_matchScore", matchCount);
     * 
     * return 1;
     * }
     * 
     * }
     * 
     * } catch (Exception e) {
     * System.out.println(e.toString());
     * }
     * 
     * return 0;
     * 
     * }
     */
    private static Object extractValFromObjectSeq(final char[] currentArr, final int from) {
        Object[] objArr = new Object[0];

        char[] extractValArr = new char[0];
        for (int i = from, pi = 0, objPi = 0; i < currentArr.length; i++) {

            if (currentArr[i] == ']') {

                if (i < currentArr.length - 1) {
                    endOfContent = 0;
                    // mainSeekPos += i;
                }

                final Object cvalue = new String(extractValArr).trim();
                objArr = add(objArr, cvalue);

                mainSeekPos += (i - from) + 1;
                break;

            } else if (currentArr[i] == ',') {

                final Object cvalue = new String(extractValArr).trim();
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

    private static Object extractValFromIntegerSeq(final char[] currentArr, final int from) {
        Integer[] extractValArr = new Integer[0];
        for (int i = from, pi = 0; i < currentArr.length; i++) {

            final int current = currentArr[i] - 48;

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
        final Object finalNum = extractValArr.length > 9 ? arrayToBigInteger(extractValArr)
                : arrayToInteger(extractValArr);

        return finalNum;
    }

    private static Object extractValFromBigDecimalSeq(final char[] currentArr, final int from) {

        char[] extractValArr = new char[0];
        for (int i = from, pi = 0; i < currentArr.length; i++) {

            final int current = currentArr[i] - 48;

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

        final Object finalNum = (BigDecimal) arrayToBigDecimal(extractValArr);
        return finalNum;
    }

    private static Object extractValFromDoubleSeq(final char[] currentArr, final int from) {

        char[] extractValArr = new char[0];
        for (int i = from, pi = 0; i < currentArr.length; i++) {

            final int current = currentArr[i] - 48;

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

        final Object finalNum = (Double) arrayToDouble(extractValArr);
        return finalNum;

    }

    private static String extractValFromStringSeq(char[] currentArr, int from) {

        char[] extractValArr = new char[0];
        int i = 0, pi = 0;
        int breakFree = 0;
        String extractVal = "";
        char[][] breakSequences = {
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
                { ']' },
                { ',', '"' },
                { '}', ',' },
                // {'n','u','l','l',','}
        };

        try {

            if (from > 240) {
                // System.out.println("Stop");
            }

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

                    int score = checkForExactMatchFromSeqSet(ts, breakSequences, 1);
                    if (score == 1) {
                        breakFree = 1;

                        if (currentValueType == Boolean.class) {
                            mainSeekPos += pi;
                        } else {
                            mainSeekPos += pi;
                        }

                    } else {

                        // int cmInd = -1;
                        if (currentArr[i] == ',' && traceForNullCharSeq(currentArr, mainSeekPos,
                                null) == 1) {
                            mainSeekPos += 4;
                            breakFree = 1;
                        } else {
                            breakFree = 0;
                        }

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

            if (checkForExactMatchFromSeqSet(extractValArr, breakSequences, 1) == 1) {
                System.out.println("Error in extractFromStringSeq @ " + mainSeekPos);
                System.out.println(extractVal);
                System.out.println("invalid value seq");

                var tr = mainSeekPosStops;
            }

        } catch (Exception e) {
            System.out.println(e.toString());
            var tr = mainSeekPosStops;
        }

        if (removeQuotesInd == 1) {
            extractVal = extractVal.replace("\"", "");
            // mainSeekPos -= 2;
        }

        return extractVal;
    }

    private static int traceForNullCharSeq(char[] currentArr, int from, Direction dir) {
        utilInfo = new LinkedHashMap<>();
        // int nullSeqStartsAt = -1;

        char[] nullCharSeq = "null".toCharArray();

        int matchCount = 0;
        if (dir != null && dir.equals(Direction.Leading)) {
            from = from - 4;
        }

        for (int i = 0; i < nullCharSeq.length; i++) {

            char cc = StringUtil.toLowerCase(currentArr[from]);
            if (currentArr[from] == nullCharSeq[i]) {
                ++matchCount;
                ++from;
            } else {
                break;
            }

            if (matchCount == nullCharSeq.length) {
                return 1;
                // nullSeqStartsAt = from;
                // break;
            }
        }

        return 0;
    }

    private static char[] trimArrayByOpeningSeq(final char[] currentArr, final int from, final char openingSeq) {

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

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return trArr;

    }

    private static int isEndOfContent(final char[] currentArr) {
        if (mainSeekPos >= currentArr.length - 1) {
            endOfContent = 1;
        } else {
            endOfContent = 0;
        }

        return endOfContent;
    }

    private static <E> E[][] addToArray2D(final E[][] mainArr, final E[][] destArr, final E[] item) {
        // E[][] cArray = (E[][]) target;
        // E[][] cArrayInc = new E[cArray.length + 1][cArray.length + 1];
        for (int i = 0; i < mainArr.length; i++) {
            destArr[i] = mainArr[i];
        }
        // cArrayInc = cArray;
        destArr[destArr.length - 1] = item;

        return destArr;
    }

    private static char[][] replaceCharArray2DItemAt(final char[][] mainArr, final int targetIndex,
            final char[] replacement)
            throws Exception {

        if (targetIndex < 0 || targetIndex > mainArr.length) {
            throw new Exception("Invalid index for method replaceCharArray3DItemAt");
        }

        char[][] finalArr = new char[0][];
        for (int i = 0; i < mainArr.length; i++) {

            if (i == targetIndex) {
                finalArr = addToCharArray2D(finalArr, replacement);
            } else {
                finalArr = addToCharArray2D(finalArr, mainArr[i]);
            }

        }

        return finalArr;
    }

    private static <E> E[] replaceAt(final E[] mainArr, E[] finalArr, final int targetIndex, final E replacement)
            throws Exception {

        if (targetIndex < 0 || targetIndex > mainArr.length || mainArr.length != finalArr.length) {
            throw new Exception("Invalid index or length for method replaceAt");
        }

        // E[] finalArr = new E[0][];
        for (int i = 0; i < mainArr.length; i++) {

            if (i == targetIndex) {
                finalArr = add(finalArr, replacement);
            } else {
                finalArr = add(finalArr, mainArr[i]);
            }

        }

        return finalArr;
    }

    private static int[] addToIntArray(final int[] target, final int item) {
        final int[] cArray = (int[]) target;
        final int[] cArrayInc = new int[cArray.length + 1];
        for (int i = 0; i < cArray.length; i++) {
            cArrayInc[i] = cArray[i];
        }
        // cArrayInc = cArray;
        cArrayInc[cArrayInc.length - 1] = item;

        return cArrayInc;

    }

    private static Integer[] addToIntegerArray(final Integer[] target, final Integer item) {
        final Integer[] cArray = (Integer[]) target;
        final Integer[] cArrayInc = new Integer[cArray.length + 1];
        for (int i = 0; i < cArray.length; i++) {
            cArrayInc[i] = cArray[i];
        }
        // cArrayInc = cArray;
        cArrayInc[cArrayInc.length - 1] = item;

        return cArrayInc;

    }

    private static Integer[] removeFromIntegerArray(final Integer[] numArr, final Integer item) {

        // Integer[] cArray = (Integer[]) numArr;
        Integer[] numArrProc = new Integer[0];
        for (int i = 0; i < numArr.length; i++) {

            if (numArr[i] == item) {
                continue;
            } else {
                numArrProc = addToIntegerArray(numArrProc, numArr[i]);
            }
        }

        return numArrProc;
    }

    private static int[] generateNumArrByRange(final int size, final int from, final int dir) {

        int[] finalArr = new int[0];
        int inc = from;
        do {

            finalArr = addToIntArray(finalArr, inc);
            if (dir > 0) {
                ++inc;
            } else {
                --inc;
            }

        } while (inc >= size);

        return finalArr;
    }

    private static int arrayToInteger(final Integer[] numArr) {
        int num = 0;

        for (int i = 0, pf = numArr.length - 1; i < numArr.length; i++, pf--) {
            num += numArr[i] * pow(pf, 10);
        }

        return num;
    }

    private static BigInteger arrayToBigInteger(final Integer[] numArr) {
        BigInteger num = BigInteger.ZERO;

        for (int i = 0, pf = numArr.length - 1; i < numArr.length; i++, pf--) {
            // num += numArr[i] * pow(pf,10);
            final BigInteger p1 = new BigInteger(String.valueOf(numArr[i]));
            final BigInteger p2 = powBig(pf, 10);
            final BigInteger current = p1.multiply(p2);
            num = num.add(current);
        }

        return num;
    }

    private static Double arrayToDouble(final char[] numArr) {

        return Double.valueOf(numArr.toString());
    }

    private static BigDecimal arrayToBigDecimal(final char[] numArr) {

        return new BigDecimal(numArr);

    }

    private static LinkedHashMap<String, Object> findMapFromLayers(final LinkedHashMap<String, Object> currentMap,
            final String targetId) {

        for (final var ck : currentMap.keySet()) {

            final var cmap = (LinkedHashMap<String, Object>) currentMap.get(ck);

            if (ck.equals(targetId)) {
                return cmap;
            } else {

                final var resMap = findMapFromLayers(cmap, targetId);
                if (resMap != null) {
                    return resMap;
                }

            }

        }

        return null;

    }

    private static <E> E first(final E[] currentArr) {
        if (currentArr != null && currentArr.length > 0) {
            return currentArr[0];
        }
        return null;
    }

    private static <E> E itemAt(final E[] currentArr, final int targetIndex) throws Exception {

        if (targetIndex < 0 || targetIndex > currentArr.length) {
            throw new Exception("invalid index");
        }

        for (int i = 0; i < currentArr.length; i++) {

            if (i == targetIndex) {
                return currentArr[i];
            }

        }

        return null;

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

    public MapUtil() {
        resetCounters();
    }

    @Deprecated
    public static Object findNonNullFrom(Object[] dataMap,
            String columnName) {
        // demands customization based on specific use cases

        Object targetColumnVal = null;

        for (var crow : dataMap) {

            if (crow instanceof LinkedHashMap) {
                var crowMap = (LinkedHashMap<String, Object>) crow;
                targetColumnVal = crowMap.get(columnName);
                if (targetColumnVal != null) {
                    return targetColumnVal;
                }
            } else if (crow instanceof String) {
                var crowco = (String) crow;
                targetColumnVal = crowco;
                if (targetColumnVal != null) {
                    return targetColumnVal;
                }
            } else if (crow instanceof Integer) {
                var crowco = (Integer) crow;
                targetColumnVal = crowco;
                if (targetColumnVal != null) {
                    return targetColumnVal;
                }
            } else if (crow instanceof BigDecimal) {
                var crowco = (BigDecimal) crow;
                targetColumnVal = crowco;
                if (targetColumnVal != null) {
                    return targetColumnVal;
                }
            }
        }
        return null;
    }

    // public static <E> E defaultValue(E initVal, E defaultVal) {
    // // for lengthy known types
    // return initVal != null ? initVal : defaultVal;

    // }

    // public static <E> E defaultValue(E initVal, E defaultVal) {
    // // for maps: lengthy known types

    // if(!initVal.getClass().equals(defaultVal.getClass()){
    // throw new Exception("Invalid default value");
    // }

    // return initVal != null ? initVal : defaultVal;

    // }

}