package harp.util;

import static harp.util.StringUtil.log;
import static harp.util.StringUtil.removeFrom;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import harp.util.StringUtil.Direction;

/**
 * @note experimental utility methods
 * @author Dennis Thomas(dennis85468_dennis246)
 */

public class MathUtil {

    public static List<Integer> numStore = new ArrayList<>();
    public static int regPassCount = 0;

    public static final int smallArrSize = 128;
    public static final int mediumArrSize = 1024;
    public static final int largeArrSize = 65535;
    public static final int largeX1ArrSize = 4194303;
    public static final int largeX2ArrSize = 134217727;

    public enum MatchCriteria {
        First, Last, All, Any
    }

    public static enum Distribution {
        Uneven, Even
    }

    public static enum Flux {
        ONE, TWO, THREE, FOUR
    }

    public static enum TrimDirection {
        Leading, Trailing
    }

    public static enum RotationalDirection {
        Clockwise, AntiClockwise
    }

    public static enum PadDirection {
        All, Left_Right, Top_Down, Top, Right, Bottom, Left
    }

    public static enum ElementPosition {
        Top, Right, Bottom, Left, Center
    }

    public BigInteger prevArrSize = BigInteger.ZERO;

    static Thread[] threadsArr = new Thread[0];

    Integer[] mainArrInc;

    public static int[] primeSequence(int from, int to) {

        int[] finalArr = new int[0];
        int[] unitDivs = { 2, 3, 5 };
        for (int i = from; i < to; i++) {

            /*
             * if ( (i / 1 == i && i % 2 != 0 && i % 3 != 0 && i % 5 != 0)) {
             * System.out.print(i + "\s");
             * }
             */
            int passCounts = 0;
            if (i / 1 == i && i / 1 != 1) {
                passCounts += 1;
            }

            for (int d = 0; d < unitDivs.length; d++) {

                if (i == unitDivs[d] || i % unitDivs[d] != 0) {
                    passCounts += 1;
                }
            }

            if (passCounts == 1 + unitDivs.length) {
                // System.out.print(i + "\s");
                finalArr = addToIntArray(finalArr, i);
            }

        }

        return finalArr;
    }

    public static boolean checkIfPrime(int num) {
        int[] unitDivs = { 2, 3, 5 };

        int passCounts = 0;
        if (num / 1 == num && num / 1 != 1) {
            passCounts += 1;
        }

        for (int d = 0; d < unitDivs.length; d++) {

            if (num == unitDivs[d] || num % unitDivs[d] != 0) {
                passCounts += 1;
            }
        }

        if (passCounts == 1 + unitDivs.length) {
            // System.out.print(num + "is prime!");
            return true;
        } else {
            return false;
        }

    }

    // public static void fibo(int from, int count) {

    // int[] pool = { 0, 1 };
    // System.out.print(pool[0] + "\s" + pool[1] + "\s");

    // for (int i = from; i < count + 1; i++) {

    // int res = pool[0] + pool[1];
    // System.out.print(res + "\s");

    // pool[0] = pool[1];
    // pool[1] = res;

    // }
    // }

    public static BigInteger[] fiboSeq(int from, int count) {

        var bfrom = BigInteger.valueOf(from);
        // var bcount = BigInteger.valueOf(count);

        BigInteger[] finalArr = new BigInteger[0];

        BigInteger[] pool = new BigInteger[2];
        pool[0] = BigInteger.ZERO;
        pool[1] = bfrom == BigInteger.ZERO ? BigInteger.ONE : bfrom;

        finalArr = addToBigIntArray(finalArr, pool[0]);
        finalArr = addToBigIntArray(finalArr, pool[1]);

        // System.out.print(pool[0] + "\s" + pool[1] + "\s");

        for (int i = 0; i < count + 1; i++) {

            BigInteger res = pool[0].add(pool[1]);
            // System.out.print(res + "\s");
            finalArr = addToBigIntArray(finalArr, res);

            pool[0] = pool[1];
            pool[1] = res;

        }

        return finalArr;
    }

    public static void primeFactorization(int target) {
        // factorStatment += target + " = ";

        if (checkIfPrime(target)) {
            System.out.print(target + " is prime");
            return;
        }
        unitDivision(target, 2);
        int index = 0;
        for (var item : numStore) {
            index += 1;
            System.out.print(item + (index != numStore.size() ? "x" : ""));
        }
    }

    public static void unitDivision(int target, int div) {
        if (target == 0 || target < div) {
            return;
        }

        int quot = 0;
        if (target % div == 0) {
            quot = target / div;
            numStore.add(div);

            if (quot > 0) {
                unitDivision(quot, div);
            }

        } else {
            // target = quot;
            unitDivision(target, div + 1);
        }

    }

    public static int GCF(int num1, int num2) {
        int gtrNum = 0;
        int lsrNum = 0;

        if (num1 > num2) {
            gtrNum = num1;
            lsrNum = num2;
        } else if (num1 < num2) {
            lsrNum = num1;
            gtrNum = num2;
        }

        if (lsrNum == 0) {
            return gtrNum;
        }

        regReduce(gtrNum, lsrNum);
        // System.out.print("GCF of " + num1 + " and " + num2 + " is " +
        // numStore.get(numStore.size() - 1));
        int res = numStore.get(numStore.size() - 1);
        numStore = new ArrayList<>();
        return res;

    }

    public static void regReduce(int gtrNum, int lsrNum) {

        int res = gtrNum - lsrNum;

        if (res == 0) {
            numStore.add(lsrNum);
            return;
        }

        if (res > lsrNum) {
            numStore.add(lsrNum);
            regReduce(res, lsrNum);
        } else if (res <= lsrNum) {
            regReduce(lsrNum, res);
        }

        // return res;

    }

    private static void findGCF(List<Integer> numList) {

        // if(numList.size() > 1)
        int num1 = numList != null ? numList.get(0) : 0;
        int num2 = numList != null && numList.size() > 1 ? numList.get(1) : 0;
        int res = 0;
        for (int i = 1; i < numList.size(); i++) {

            res = GCF(num1, num2);

            if (i == numList.size() - 1) {
                break;
            }

            num1 = res;
            num2 = numList.get(i + 1);

        }

        System.out.print("GCF of set is " + res);

    }

    public static int LCM(List<Integer> numList) {

        // if(numList.size() > 1)
        int num1 = numList != null ? numList.get(0) : 0;
        int num2 = numList != null && numList.size() > 1 ? numList.get(1) : 0;
        int resGCF = 0;
        int resLCM = 0;
        for (int i = 1; i < numList.size(); i++) {

            resGCF = GCF(num1, num2);

            resLCM = num1 * num2 / resGCF;

            if (i == numList.size() - 1) {
                break;
            }

            num1 = resLCM;
            num2 = numList.get(i + 1);

        }

        // System.out.print("LCM of set is " + resLCM);
        return resLCM;

    }

    private static int arrayToInteger(Integer[] numArr) {
        int num = 0;

        for (int i = 0, pf = numArr.length - 1; i < numArr.length; i++, pf--) {
            num += numArr[i] * pow(pf, 10);
        }

        return num;
    }

    private static BigInteger arrayToBigInteger(int[] numArr) {
        BigInteger num = BigInteger.ZERO;

        for (int i = 0, pf = numArr.length - 1; i < numArr.length; i++, pf--) {
            // num += numArr[i] * pow(pf,10);
            BigInteger p1 = new BigInteger(String.valueOf(numArr[i]));
            BigInteger p2 = powBig(new BigInteger(String.valueOf(pf)), new BigInteger("10"));
            BigInteger current = p1.multiply(p2);
            num = num.add(current);
        }

        return num;
    }

    /**
     * 
     * @param result
     * @param base
     * @return
     * @throws Exception
     */
    public static BigInteger exponent(BigInteger result, BigInteger base) throws Exception {

        if (base.compareTo(result) >= 0 && base != BigInteger.ONE) {
            throw new Exception("invalid numbers");
        }

        if (!result.divideAndRemainder(base)[1].equals(BigInteger.ZERO)) {
            throw new Exception("invalid numbers");
        }

        var expRes = base;
        var ix = BigInteger.ONE;
        while (!expRes.equals(result)) {

            if (expRes.compareTo(result) > 0) {
                throw new Exception("invalid numbers");
            }
            expRes = base.multiply(expRes);
            ix = ix.add(BigInteger.ONE);
        }

        return ix;

    }

    /**
     * 
     * this is experimental and has undetermined usefuleness
     * 
     */
    public static int exponentBinary_(int[] resultArr, int[] baseArr) throws Exception {

        var s1 = (int) sumOf(resultArr);

        if (s1 == 0) {
            // return new int[] { -1 };
            return -1;
        }

        var s2 = (int) sumOf(baseArr);

        if (s2 == 0) {
            // return new int[] { -1 };
            return -1;
        }

        var solve1Map = solveBinary2_(resultArr, baseArr, 0);
        var solveRes1 = (int) solve1Map.get("result");

        if (solveRes1 == -1) {
            throw new Exception("invalid numbers");
        }

        if (solveRes1 == 0) {
            return 1;
        }

        // if (base.compareTo(result) >= 0 && base != BigInteger.ONE) {
        // throw new Exception("invalid numbers");
        // }

        // if (!result.divideAndRemainder(base)[1].equals(BigInteger.ZERO)) {
        // throw new Exception("invalid numbers");
        // }

        // if (baseArr[baseArr.length - 1] == 1) {
        // throw new Exception("invalid numbers");
        // }

        var exponResArr = baseArr;
        // var ixArr = new int[] { 0 };
        var ix = 0;
        while (binaryEquals_(resultArr, exponResArr) != 1) {

            // if (expRes.compareTo(result) > 0) {
            // throw new Exception("invalid numbers");
            // }
            var solve2Map = solveBinary2_(resultArr, exponResArr, 1);
            var solve2Res = (int) solve2Map.get("result");
            if (solve2Res == -1) {
                throw new Exception("invalid numbers expBinRunIx:" + (ix));
            }

            if (solve2Res == 1) {
                resultArr = (int[]) solve2Map.get("d1");
                exponResArr = (int[]) solve2Map.get("d2");
            } else {
                break;
            }

            // expRes = base.multiply(expRes);
            exponResArr = multiplyTwoBinaryNumbers(baseArr, exponResArr, 0);
            // ix = ix.add(BigInteger.ONE);
            // ixArr = incrementBinaryNumberByOne(ixArr);
            ix += 1;
        }

        // ixArr = incrementBinaryNumberByOne(ixArr);
        ix += 1;
        return ix;

    }

    public static Map exponentBinary(int[] resultArr, int[] baseArr) throws Exception {

        var s1 = (int) sumOf(resultArr);

        if (s1 == 0) {
            return Map.of("d", new int[] { 0 }, "result", -1);
            // return -1;
        }

        var s2 = (int) sumOf(baseArr);

        if (s2 == 0) {
            return Map.of("d", new int[] { 0 }, "result", -1);
            // return -1;
        }

        var solve1Map = solveBinary2_(resultArr, baseArr, 0);
        var solveRes1 = (int) solve1Map.get("result");

        if (solveRes1 == -1) {
            throw new Exception("invalid numbers");
        }

        if (solveRes1 == 0) {
            // return new int[] { 0 };
            return Map.of("d", new int[] { 0 }, "result", -1);
        }

        var exponResArr = baseArr;
        var ixArr = new int[] { 0, 0 };
        var ix = 0; // debug
        while (binaryEquals_(resultArr, exponResArr) != 1) {

            var solve2Map = solveBinary2_(resultArr, exponResArr, 1);
            var solve2Res = (int) solve2Map.get("result");
            if (solve2Res == -1) {
                // throw new Exception("invalid numbers expBinRunIx:" +
                // (binaryArrayToString(ixArr)));
                var exponResDec = binaryToDecimal(ixArr);
                System.out.println("invalid numbers expBinRunIx:" + exponResDec);
                return Map.of("d", ixArr, "result", -1, "exponResArr", exponResArr, "exponResDec", exponResDec,
                        "ixArr", ixArr, "ix", ix);
            }

            if (solve2Res == 1) {
                resultArr = (int[]) solve2Map.get("d1");
                exponResArr = (int[]) solve2Map.get("d2");
            } else {
                break;
            }

            // expRes = base.multiply(expRes);
            exponResArr = multiplyTwoBinaryNumbers(baseArr, exponResArr, 0);
            // ix = ix.add(BigInteger.ONE);
            ixArr = incrementBinaryNumberByOne(ixArr);
            ix += 1;
        }

        ixArr = incrementBinaryNumberByOne(ixArr);
        // ix += 1;
        return Map.of("d", ixArr, "result", 1);

    }

    public static BigInteger powBig(BigInteger expfactor, BigInteger base) {

        if (expfactor.equals(BigInteger.ZERO)) {
            return BigInteger.ONE;
        }

        BigInteger baseBig = new BigInteger(String.valueOf(base));
        BigInteger res = baseBig;

        var i = 1;
        while (i != expfactor.intValue()) {
            // res *= base;
            res = res.multiply(baseBig);
            i += 1;
        }

        return res;
    }

    public static int pow(int expfactor, int base) {

        if (expfactor == 0) {
            return 1;
        }

        int res = base;
        var i = 1;
        while (i != expfactor) {
            res *= base;
        }

        return res;
    }

    public static int subtractionOf(int[] numArr) {
        int sum = 0;
        for (int i = 0; i < numArr.length; i++) {
            sum -= numArr[i];
        }

        return sum;
    }

    // public static int sumOf(String numArrAsStr) {

    // var ix = 0;
    // int sum = 0;
    // while (ix != numArrAsStr.length()) {
    // sum += Character.getNumericValue(numArrAsStr.charAt(ix));
    // ix += 1;
    // }

    // return sum;

    // }

    public static int sumOf(Integer[] numArr) {
        int sum = 0;
        for (int i = 0; i < numArr.length; i++) {
            sum += numArr[i];
        }

        return sum;
    }

    public static Number sumOf(int[] numArr) {

        if (numArr.length < largeArrSize) {
            var sum = 0;
            var ix = 0;
            while (ix != numArr.length) {
                sum += numArr[ix];
                ix += 1;
            }
            return sum;
        } else {
            BigInteger sumBig = BigInteger.ZERO;
            var ix = 0;
            while (ix != numArr.length) {
                sumBig = sumBig.add(new BigInteger(String.valueOf(numArr[ix])));
                // sumBig = addTwoBinaryNumbers(Integer.toBinaryString(sumBig) , numArr)
                ix += 1;
            }
            return sumBig;
        }

    }

    public static int productOf(Integer[] numArr) {
        int product = 1;
        for (int i = 0; i < numArr.length; i++) {
            if (numArr[i] == 0) {
                continue;
            }
            product *= numArr[i];
        }

        return product;
    }

    public static int productOf(int[] numArr) {
        int product = 1;
        for (int i = 0; i < numArr.length; i++) {
            if (numArr[i] == 0) {
                continue;
            }
            product *= numArr[i];
        }

        return product;
    }

    public static int trimZeroes(String currentSeq) {

        char[] numSeqArrP1 = StringUtil.numAccumalator(currentSeq.toCharArray());
        String numStr = new String(numSeqArrP1);
        int finalRes = Integer.parseInt(numStr);
        return finalRes;

    }

    public static int trimZeroes0(String currentSeq, TrimDirection currentTrimDir) {

        char[] numSeqArrP1 = new char[0];
        if (currentTrimDir.equals(TrimDirection.Trailing)) {
            numSeqArrP1 = StringUtil.nonZeroNumAccumalator(currentSeq.toCharArray());
        } else {
            numSeqArrP1 = StringUtil.numAccumalator(currentSeq.toCharArray());
        }

        String numStr = new String(numSeqArrP1);
        int finalRes = Integer.parseInt(numStr);
        return finalRes;

    }

    public static String trimZeroes(String currentSeq, TrimDirection currentTrimDir) {

        if (currentSeq.length() == 1) {
            return currentSeq;
        }

        char[] numSeqArrP1 = new char[0];
        if (currentTrimDir.equals(TrimDirection.Trailing)) {
            numSeqArrP1 = StringUtil.nonZeroNumAccumalator(currentSeq.toCharArray());
        } else {
            numSeqArrP1 = StringUtil.numAccumalator(currentSeq.toCharArray());
        }

        String numStr = new String(numSeqArrP1);
        // int finalRes = Integer.parseInt(numStr);
        return numStr;

    }

    public static int[] trimZeroes(int[] currentSeqArr, TrimDirection currentTrimDir) throws Exception {

        if (currentSeqArr == null) {
            throw new Exception("invalid");
        }

        if (currentSeqArr.length == 1) {
            return currentSeqArr;
        }

        int[] numSeqArrP1 = new int[0];
        if (currentTrimDir.equals(TrimDirection.Trailing)) {
            numSeqArrP1 = nonZeroNumAccumalator(currentSeqArr);
        } else {
            numSeqArrP1 = numAccumalator(currentSeqArr);
        }

        // String numStr = new String(intArrayToString(numSeqArrP1));
        // int finalRes = Integer.parseInt(numStr);
        return numSeqArrP1;

    }

    private static int[] nonZeroNumAccumalator(int[] seqArr) {

        int[] finalSeq = new int[0];
        int nzNumSeqStartInd = 0;
        // for (int i = 0; i < seqArr.length; i++) {
        var i = 0;
        while (i != seqArr.length) {

            if (seqArr[i] != 0) {
                ++nzNumSeqStartInd;
            } else {
                i += 1;
                continue;
            }

            finalSeq = addToIntArray(finalSeq, seqArr[i]);

            // if (i > 0 && nzNumSeqStartInd > 0) {
            // break;
            // }
            // continue;
            i += 1;
        }

        return finalSeq;
    }

    private static int[] numAccumalator(int[] seqArr) {

        // if (seqArr[seqArr.length - 1] == 1) {
        // return seqArr;
        // }

        int[] finalSeq = new int[0];
        int nzNumSeqStartInd = 0;
        // for (int i = 0; i < seqArr.length; i++) {
        var i = 0;
        while (i != seqArr.length) {

            if (seqArr[i] != 0) {
                nzNumSeqStartInd += 1;
            } else {
                if (nzNumSeqStartInd == 0) {
                    i += 1;
                    continue;
                }
            }

            finalSeq = addToIntArray(finalSeq, seqArr[i]);
            i += 1;
        }

        return finalSeq;
    }

    public static Integer[] purgeSort(Integer[] e, SortOrder sortOrder) {
        Integer[] e2 = new Integer[e.length];
        // for (int i = 0, pi = 0; i < e.length; i++) {
        var i = 0;
        int pi = 0;
        while (i != e.length) {

            if (e2[e2.length - 1] != null) {
                break;
            }

            Integer pe = null;
            if (sortOrder.equals(SortOrder.Desc)) {
                pe = maxOfArray(e);
            } else {
                pe = minOfArray(e);
            }

            e = ContainerUtil.removeItem(e, pe);
            e2[pi] = pe;
            pi++;
            --i;
        }

        return e2;

    }

    public static int[] purgeSort(int[] mainArr, SortOrder sortOrder) {
        int[] sortedArr = new int[mainArr.length];
        // for (int i = 0, pi = 0; i < mainArr.length; i++) {
        var i = 0;
        int pi = 0;
        while (i != mainArr.length) {

            if (sortedArr[sortedArr.length - 1] != 0) {
                break;
            }

            int pe = 0;
            if (sortOrder.equals(SortOrder.Desc)) {
                pe = maxOfArray(mainArr);
            } else {
                pe = minOfArray(mainArr);
            }

            mainArr = removeFromIntArray(mainArr, pe);
            sortedArr[pi] = pe;
            pi++;
            --i;
        }

        return sortedArr;

    }

    public static int maxOfArray(Integer[] e) {

        int max = e[0];
        try {

            // for (int j = 0; j < e.length; j++) {
            var j = 0;
            while (j != e.length) {

                if (e[j] > max) {
                    max = e[j];
                }

                j += 1;
            }
        } catch (Exception e1) {
            System.out.print(e1.toString());
        }

        return max;
    }

    public static int maxOfArray(int[] e) {

        int max = e[0];
        try {

            // for (int j = 0; j < e.length; j++) {
            var j = 0;
            while (j != e.length) {

                if (e[j] > max) {
                    max = e[j];
                }

                j += 1;
            }
        } catch (Exception e1) {
            System.out.print(e1.toString());
        }

        return max;
    }

    public static int minOfArray(Integer[] e) {

        int min = e[0];
        try {
            // for (int j = 0; j < e.length; j++) {
            var j = 0;
            while (j != e.length) {

                if (e[j] < min) {
                    min = e[j];
                }

                j += 1;
            }
        } catch (Exception e1) {
            System.out.print(e1.toString());
        }

        return min;
    }

    public static int minOfArray(int[] arr) {

        int min = arr[0];
        try {
            // for (int j = 0; j < arr.length; j++) {
            var j = 0;
            while (j != arr.length) {

                if (arr[j] < min) {
                    min = arr[j];
                }

                j += 1;
            }
        } catch (Exception e) {
            System.out.print(e.toString());
        }

        return min;
    }

    public static int[][] addToIntArray2D(final int[][] mainArr, final int[] item) {

        final int[][] finalArr = new int[mainArr.length + 1][];

        if (mainArr.length < mediumArrSize) {

            for (int i = 0; i < mainArr.length; i++) {
                finalArr[i] = mainArr[i];
            }

            finalArr[finalArr.length - 1] = item;

        } else {
            System.arraycopy(mainArr, 0, finalArr, 0, mainArr.length);
        }

        return finalArr;
    }

    public static int[][] addAllToIntArray2D(int[][] mainArr, final int[][] subArr) {

        if (mainArr.length < mediumArrSize) {
            for (int i = 0; i < subArr.length; i++) {
                mainArr = addToIntArray2D(mainArr, subArr[i]);
            }
        } else {
            System.arraycopy(mainArr, 0, subArr, 0, mainArr.length);
        }

        return mainArr;

    }

    public static boolean binaryEquals(int[] mainArr, int[] secArr) {
        return binaryEquals_(mainArr, secArr) == 1 ? true : false;
    }

    public static int binaryEquals_(int[] mainArr, int[] secArr) {

        try {

            var d1 = binaryArrayToString(mainArr);
            var d2 = binaryArrayToString(secArr);
            if (d1 == d2) {
                return 1;
            } else {
                return 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;

    }

    public static int equals0(int[] mainArr, int[] secArr) {

        if (mainArr.length != secArr.length) {
            return 0;
        }

        var ix = 0;
        var equalsCount = 0;
        while (ix != mainArr.length) {
            if (mainArr[ix] == secArr[ix]) {
                equalsCount += 1;
            }
            ix += 1;
        }

        if (equalsCount == mainArr.length) {
            return 1;
        } else {
            return 0;
        }

    }

    public static int[] addToIntArray(int[] mainArr, int item) {

        int[] finalArr = new int[mainArr.length + 1];
        if (mainArr.length < mediumArrSize) {
            for (int i = 0; i < mainArr.length; i++) {
                finalArr[i] = mainArr[i];
            }
        } else {
            System.arraycopy(mainArr, 0, finalArr, 0, mainArr.length);
        }

        finalArr[finalArr.length - 1] = item;
        return finalArr;

    }

    public static BigInteger[] addToBigIntArray(BigInteger[] mainArr, BigInteger item) {

        BigInteger[] finalArr = new BigInteger[mainArr.length + 1];
        if (mainArr.length < mediumArrSize) {
            for (int i = 0; i < mainArr.length; i++) {
                finalArr[i] = mainArr[i];
            }
        } else {
            System.arraycopy(mainArr, 0, finalArr, 0, mainArr.length);
        }

        finalArr[finalArr.length - 1] = item;
        return finalArr;

    }

    public static int[] addAllToIntArray_St(final int[] mainArr, final int[] subArr) {

        int parts = 0;
        Double partlen = 0D;
        // int remainingLen = 0;
        if (subArr.length < SplitTasks.splitTasksMinCount || subArr.length < SplitTasks.splitTasksMaxCount) {
            parts = 0;
            partlen = Double.valueOf(subArr.length);
        } else {
            parts = SplitTasks.splitTasksMaxCount;
            partlen = (subArr.length * 1.000D) / (parts * 1.000D);
            if (subArr.length % parts > 0) {
                // remainingLen = mainCharArr.length - partlen.intValue();
                parts += 1;
            }
        }

        var ixRangeArr = new int[parts][];

        var ixr = 0;
        var f1 = 0;
        var f2 = partlen.intValue();
        while (ixr != parts) {

            ixRangeArr[ixr] = new int[] { f1, f2 };
            f1 = f2 + 1;
            f2 = f2 + partlen.intValue();

            ixr += 1;
        }

        // int mainSeekPos = 0;
        // int endSeekPos = partlen.intValue();
        int outix = 0;
        while (outix != ixRangeArr.length - 1) {

            var ixRef = outix;
            var ract = new Runnable() {
                @Override
                public void run() {
                    try {

                        var ix = ixRangeArr[ixRef][0];
                        while (ix != ixRangeArr[ixRef][1]) {
                            mainArr[ix] = subArr[ix];
                            ix += 1;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            Thread thr = new Thread(ract);
            thr.setName("THR_" + outix);
            ///thr.setPriority(Thread.MAX_PRIORITY);
            threadsArr = add(threadsArr, thr);

            thr.start();
            outix += 1;
        }

        return mainArr;

    }

    public Integer[] addToIntegerArray_St(Integer[] mainArr, Integer item, String splitTasksID) {

        // Integer[] cArray = (Integer[]) mainArr;
        // Integer[] mainArrInc;
        if (hasTask(splitTasksID) != 1) {
            mainArrInc = new Integer[mainArr.length + 1];
        }

        int parts = 0;
        Double partlen = 0D;
        // int remainingLen = 0;
        if (mainArrInc.length < SplitTasks.splitTasksMinCount || mainArrInc.length < SplitTasks.splitTasksMaxCount) {
            parts = 0;
            partlen = Double.valueOf(mainArrInc.length);
        } else {
            parts = SplitTasks.splitTasksMaxCount;
            partlen = (mainArrInc.length * 1.000D) / (parts * 1.000D);
            if (mainArrInc.length % parts > 0) {
                // remainingLen = mainCharArr.length - partlen.intValue();
                parts += 1;
            }
        }

        var ixRangeArr = new int[parts][];

        var ixr = 0;
        var f1 = 0;
        var f2 = partlen.intValue();
        while (ixr != parts) {

            ixRangeArr[ixr] = new int[] { f1, f2 };
            f1 = f2 + 1;
            f2 = f2 + partlen.intValue();

            ixr += 1;
        }

        // int mainSeekPos = 0;
        // int endSeekPos = partlen.intValue();
        int outix = 0;
        while (outix != ixRangeArr.length - 1) {

            var ixRef = outix;
            var ract = new Runnable() {
                @Override
                public void run() {
                    try {

                        var ix = ixRangeArr[ixRef][0];
                        while (ix != ixRangeArr[ixRef][1]) {
                            mainArrInc[ix] = mainArr[ix];
                            ix += 1;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            Thread thr = new Thread(ract);
            thr.setName("THR_" + splitTasksID);
            ///thr.setPriority(Thread.MAX_PRIORITY);
            threadsArr = add(threadsArr, thr);

            thr.start();
            outix += 1;
        }

        clearTaskFromThreadsArr(splitTasksID);
        return mainArr;

        // Integer[] cArray = (Integer[]) target;
        // Integer[] cArrayInc = new Integer[cArray.length + 1];
        // for (int i = 0; i < cArray.length; i++) {
        // cArrayInc[i] = cArray[i];
        // }
        // // cArrayInc = cArray;
        // cArrayInc[cArrayInc.length - 1] = item;

        // return cArrayInc;

    }

    public static int[] addAllToIntArray(int[] mainArr, int[] subArr) {

        // for (int i = 0; i < subArr.length; i++) {
        // mainArr = addToIntArray(mainArr, subArr[i]);
        // }
        System.arraycopy(mainArr, 0, subArr, 0, mainArr.length);

        return mainArr;

    }

    public static Integer[] addToIntegerArray(Integer[] target, Integer item) {
        Integer[] cArray = (Integer[]) target;
        Integer[] cArrayInc = new Integer[cArray.length + 1];
        for (int i = 0; i < cArray.length; i++) {
            cArrayInc[i] = cArray[i];
        }
        // cArrayInc = cArray;
        cArrayInc[cArrayInc.length - 1] = item;

        return cArrayInc;

    }

    // public static int[] stringToIntArray(String[] contentArr) {
    // char[] contentArr = content.toCharArray();

    // int[] intArr = new int[0];
    // for (int i = 0; i < contentArr.length; i++) {
    // int cv = (int) contentArr[i];
    // intArr = addToIntArray(intArr, cv);
    // }

    // return intArr;

    // }

    public static int hasInt(int[] arr, int item) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == item) {
                return 1;
            }
        }
        return 0;
    }

    public static int hasIntAt(int[] currentArr, int target) throws Exception {

        // MatchCriteria matchCriteria = optCriteria == null ? MatchCriteria.First
        // : optCriteria;

        // if (!matchCriteria.name().equals("First") &&
        // !matchCriteria.name().equals("Last")) {
        // throw new Exception("Invalid criteria");
        // }

        int foundAt = -1;
        for (int i = 0; i < currentArr.length; i++) {

            if (currentArr[i] == target) {
                // foundAt = i;
                return foundAt;
            }

        }

        return foundAt;

    }

    public static int[] hasIntAt(int[] currentArr, int target, MatchCriteria optCriteria) throws Exception {

        // all indexes returned.

        int[] foundAtArr = new int[0];

        for (int i = 0; i < currentArr.length; i++) {

            if (currentArr[i] == target) {
                // foundAt = i;
                foundAtArr = addToIntArray(foundAtArr, i);
            }

        }

        return foundAtArr;

    }

    /*
     * public static int[] subdivide_uneven(int num, int partsCount) throws
     * Exception {
     * int[] partsArr = new int[0];
     * 
     * if (partsCount > 2) {
     * int currentNum = num / 2;
     * int currentDivisions = partsCount / 2;
     * int remainingParts = partsCount - currentDivisions;
     * int remainingNum = num - currentNum;
     * if (remainingParts > 0) {
     * var resArr = subdivide_uneven(remainingNum, remainingParts);
     * partsArr = addAllToIntArray(partsArr, resArr);
     * }
     * } else {
     * int cnum = num / 2;
     * partsArr = addToIntArray(partsArr, cnum);
     * int remaining = num - cnum;
     * partsArr = addToIntArray(partsArr, remaining);
     * }
     * 
     * return partsArr;
     * }
     */

    public static int[] subdivide_uneven0(int num, int partsCount) throws Exception {

        /*
         * if (distribution == null) {
         * distribution = distribution.Even;
         * }
         */

        int[] partsArr = new int[0];

        if (partsCount < 2) {
            partsArr[0] = num;
        } else {
            // int itr = partsCount / 2 + partsCount / 4;
            int part = 0;
            int quo = num / partsCount;

            if (quo < partsCount) {
                // System.err.println("will have duplicates");
                throw new Exception("cannot make subdivisions with num of parts");
            }
            // int factor = num / 2;
            int append = partsCount + 2;
            // int max = num / partsCount + partsCount / 2; /* num / partsCount + num /
            // (partsCount * 9); */ // //
            // int min = num / partsCount - num / partsCount / 2; // max / 2 + max / 4; //
            // max - variance - variance/2; //

            int max = num / partsCount + 2; /* num / partsCount + num / (partsCount * 9); */ // //
            int min = num / partsCount - 2; // max / 2 + max / 4; // max - variance - variance/2; //

            int diff = max - min;
            int ri = 0;

            if (diff < partsCount) {
                System.err.println("will have duplicates with even val");
            }

            int findPartInd = 1;
            for (int i = 0; i < partsCount - 1; i++) {
                while (findPartInd == 1) {
                    part = Double.valueOf(Math.random() * max).intValue();

                    if (hasInt(partsArr, part) == 1 || part < min) {
                        findPartInd = 1;
                    } else {
                        findPartInd = 0;
                    }

                    ++ri;
                }

                partsArr = addToIntArray(partsArr, part);
                part = 0;
                findPartInd = 1;

            }

            int sumofUP = (int) sumOf(partsArr);
            int remaining = num - sumofUP;
            partsArr = addToIntArray(partsArr, remaining);

        }

        return partsArr;
    }

    public static int[] subdivide_uneven(int num, int partsCount, Flux fluxDef) throws Exception {
        // unique uneven distribution

        int flux = 2;
        if (fluxDef != null) {
            if (fluxDef.equals(Flux.ONE)) {
                flux = 1;
            } else if (fluxDef.equals(Flux.TWO)) {
                flux = 2;
            } else if (fluxDef.equals(Flux.THREE)) {
                flux = 3;
            } else if (fluxDef.equals(Flux.FOUR)) {
                flux = 4;
            }
        } else {

            flux = partsCount / 2;
            if (flux > 6) {
                throw new Exception("cannot make subdivisions with num of parts : HF");
            }
        }

        int[] partsArr = new int[0];

        if (partsCount < 2) {
            partsArr[0] = num;
        } else {

            int part = 0;
            int quo = num / partsCount;

            if (quo < partsCount) {
                throw new Exception("cannot make subdivisions with num of parts");
            }

            int max = num / partsCount + flux;
            int min = num / partsCount - flux;

            int diff = max - min;
            int ri = 0;

            if (diff < partsCount) {
                throw new Exception("cannot make subdivisions with num of parts");
            }

            partsArr = addToIntArray(partsArr, max);
            // --max;
            int findPartInd = 1;
            for (int i = 1; i < partsCount - 1; i++) {
                while (findPartInd == 1) {
                    part = Double.valueOf(Math.random() * max).intValue();

                    if (hasInt(partsArr, part) == 1 || part < min) {
                        findPartInd = 1;
                    } else {
                        findPartInd = 0;
                    }

                    ++ri;
                }

                // part = max;
                partsArr = addToIntArray(partsArr, part);
                part = 0;
                findPartInd = 1;
                max = max - 1;

            }

            int sumofUP = (int) sumOf(partsArr);
            int remaining = num - sumofUP;

            int exValIndex = -1;
            if ((exValIndex = hasIntAt(partsArr, remaining)) != -1) {
                remaining = remaining - 1;
                partsArr[exValIndex] = partsArr[exValIndex] + 1;
            }

            int item1 = partsArr[0];
            partsArr = removeFromIntArray(partsArr, 0);
            int item1newpos = 0;
            while (item1newpos == 0) {
                item1newpos = Double.valueOf(Math.random() * partsArr.length).intValue();
            }

            partsArr = addIntAt(partsArr, item1newpos, item1);
            partsArr = addToIntArray(partsArr, remaining);

        }

        return partsArr;
    }

    public static int[] removeFromIntArray(int[] mainArr, int ti) {

        int[] procArr = new int[0];
        for (int i = 0; i < mainArr.length; i++) {

            if (i == ti) {
                continue;
            } else {
                procArr = addToIntArray(procArr, mainArr[i]);
            }
        }

        return procArr;
    }

    @SuppressWarnings("unchecked")
    public static int[] addIntAt(int[] mainArr, int targetiIndex, int item) throws Exception {

        int[] mainArrInc = (int[]) Array.newInstance(mainArr.getClass().getComponentType(), mainArr.length + 1);

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

        return (int[]) mainArrInc;

    }

    @SuppressWarnings("unchecked")
    public static int[][] addToIntArray2DAt(int[][] mainArr, int targetiIndex, int[] item) throws Exception {

        int[][] mainArrInc = (int[][]) Array.newInstance(mainArr.getClass().getComponentType(), mainArr.length + 1);

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

        return (int[][]) mainArrInc;

    }

    public static int randomNumberGenerator(int max) {

        int rnum = Double.valueOf(Math.random() * max).intValue();
        return rnum;
    }

    public static int[] randomNumberSequence(int maxDig) {

        int[] seqArr = new int[0];
        int findPartInd = 1;
        int part = 0;
        for (int i = 0; i < maxDig; i++) {

            while (findPartInd == 1) {
                part = Double.valueOf(Math.random() * maxDig).intValue();

                if (hasInt(seqArr, part) == 1) {
                    findPartInd = 1;
                } else {
                    findPartInd = 0;
                }

            }

            seqArr = addToIntArray(seqArr, part);
            findPartInd = 1;
        }

        return seqArr;

    }

    public static int[] randomNumberSequence(int minDig, int maxDig, int totalNumbers) {

        int[] seqArr = new int[0];
        int findPartInd = 1;
        int part = 0;
        for (int i = 0; i < totalNumbers; i++) {

            while (findPartInd == 1) {
                part = Double.valueOf(Math.random() * maxDig).intValue();

                if (hasInt(seqArr, part) == 1 || part < minDig) {
                    findPartInd = 1;
                } else {
                    findPartInd = 0;
                }

            }

            seqArr = addToIntArray(seqArr, part);
            findPartInd = 1;
        }

        return seqArr;

    }

    public static int meanOfArray(int[] mainArr) {
        int sum = (int) sumOf(mainArr);
        int res = sum / mainArr.length;
        return res;
    }

    public static int meanIntervalOfArray(int[] mainArr) {

        int inAggArr[] = new int[0];
        for (int i = 0; i < mainArr.length; i++) {

            if (i + 1 < mainArr.length - 1) {
                int csum = mainArr[i + 1] - mainArr[i];
                inAggArr = addToIntArray(inAggArr, csum);
            }
        }

        int intAggSum = (int) sumOf(inAggArr);
        int res = intAggSum / mainArr.length;
        return res;

    }

    public static BigInteger[] binaryToDecimalArray(String... binNumArr) throws Exception {

        var finalArr = new BigInteger[binNumArr.length];
        var ix = 0;

        while (ix != binNumArr.length) {
            finalArr[ix] = binaryToDecimal(binNumArr[ix]);
            ix += 1;
        }

        return finalArr;
    }

    public static boolean isBinarySequence(int[] intArr) {
        return isBinarySequence_(intArr) == 1 ? true : false;
    }

    public static int isBinarySequence_(int[] intArr) {
        // negativeS?
        var i = 0;
        while (i != intArr.length) {

            if (intArr[i] != 0) {
                if (intArr[i] != 1) {
                    return 0;
                }
            }

            i += 1;

        }

        return 1;
    }

    // e
    public static BigInteger binaryToDecimal(String bseq) throws Exception {
        return new BigInteger(binaryToDecimal(stringToIntArray(bseq)));
    }

    public static String binaryToDecimal(int[] bseqArr) throws Exception {

        bseqArr = reverse(bseqArr);
        if (isBinarySequence_(bseqArr) == 0) {
            throw new Exception("Not a binary sequence");
        }

        BigInteger dsum = BigInteger.ZERO;
        BigInteger prevun = BigInteger.ONE;

        int i = 0;
        while (i != bseqArr.length) {

            var curBnm = bseqArr[i];
            prevun = solve(i, 0) ? BigInteger.TWO.multiply(prevun) : prevun;
            if (curBnm == 1) {
                dsum = dsum.add(prevun);
            }

            i += 1;
        }

        if (i == 1) {
            return String.valueOf(bseqArr[0]);
        }

        return dsum.toString();

    }

    public static String binaryToDecimal0(int[] bseqArr) throws Exception {

        // char[] bsArr = bseq.toCharArray();
        bseqArr = reverse(bseqArr);
        if (isBinarySequence_(bseqArr) == 0) {
            throw new Exception("Not a binary sequence");
        }

        BigInteger dsum = BigInteger.ZERO;
        BigInteger prevun = BigInteger.ONE;
        for (int i = 0; i < bseqArr.length; i++) {

            var curBnm = bseqArr[i];
            prevun = i > 0 ? BigInteger.TWO.multiply(prevun) : prevun;
            if (curBnm == 1) {
                dsum = dsum.add(prevun);
            }

        }

        return dsum.toString();

    }

    public static String invertBinarySeq(String bseq) throws Exception {

        char[] bsArr = bseq.toCharArray();
        if (StringUtil.isBinarySequence(bsArr) == 0) {
            throw new Exception("Not a binary sequence");
        }

        for (int i = 0, bu = 1; i < bsArr.length; i++) {

            if (bsArr[i] == '1') {
                bsArr[i] = '0';
            } else if (bsArr[i] == '0') {
                bsArr[i] = '1';
            }

        }
        return new String(bsArr);
    }

    public static int[] digits_decimal() {
        int[] digArr = new int[10];
        for (int i = 0; i < 10; i++) {
            digArr[i] = i;
        }

        return digArr;

    }

    public static int[] charArrayToIntArray(char[] numCharSeq) {

        int[] numSeq = new int[numCharSeq.length];

        try {

            for (int j = 0; j < numCharSeq.length; j++) {
                int numInt = Integer.valueOf(new String(new char[] { numCharSeq[j] }));
                numSeq[j] = numInt;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return numSeq;

    }

    // public static int sumOf(char[] numCharArr) {
    // return sumOf(charArrayToIntArray(numCharArr));
    // }

    @Deprecated
    public static String intToBinarySequence(int num) {
        String binarySeq = "";

        int[] buArr = new int[0];
        int n = 1;
        buArr = addToIntArray(buArr, n);

        while (n < num) {
            n = 2 * n;
            buArr = addToIntArray(buArr, n);
        }

        char[] bcharSeq = new char[0];
        int numReduce = num;
        for (int i = buArr.length - 1; i >= 0; i--) {

            if (buArr[i] <= numReduce) {
                bcharSeq = StringUtil.addToCharArray(bcharSeq, '1');
                numReduce -= buArr[i];
            } else {
                bcharSeq = StringUtil.addToCharArray(bcharSeq, '0');
            }

        }

        /*
         * if (bcharSeq.length < 4) {
         * bcharSeq = StringUtil.pad(bcharSeq, '0', 4 - bcharSeq.length,
         * StringUtil.Direction.Leading);
         * }
         */

        // if (num % 2 == 0 && num % 3 != 0 && num % 5 != 0 && num % 7 != 0 &&
        // bcharSeq.length % 4 != 0) {
        if (bcharSeq.length > 4 && bcharSeq.length % 4 != 0 && bcharSeq[0] == '0') {
            bcharSeq = StringUtil.trimZeroes(bcharSeq, StringUtil.Direction.Leading);
        } else {
            bcharSeq = StringUtil.pad(bcharSeq, '0', 4 - bcharSeq.length, StringUtil.Direction.Leading);
        }

        return new String(bcharSeq);
    }

    // e
    public static String stringToBinary0(String num) throws Exception {

        BigInteger bigNum = BigInteger.valueOf(Long.valueOf(num));
        BigInteger[] buArr = new BigInteger[0];
        BigInteger n = BigInteger.ONE;
        buArr = add(buArr, n);

        while (n.compareTo(bigNum) < 1) {
            // n = 2 * n;
            n = BigInteger.TWO.multiply(n);
            buArr = add(buArr, n);
        }

        int[] bigNumSeq = new int[0];
        BigInteger numReduce = bigNum;
        for (int i = buArr.length - 1; i == 0; i--) {

            if (buArr[i].compareTo(numReduce) <= 0) {
                bigNumSeq = addToIntArray(bigNumSeq, 1);
                // numReduce -= buArr[i];
                numReduce = numReduce.subtract(buArr[i]);
            } else {
                bigNumSeq = addToIntArray(bigNumSeq, 0);
            }

        }

        var finalStr = "";
        if (bigNumSeq.length > 4 && bigNumSeq.length % 4 != 0 && bigNumSeq[0] == 0) {
            finalStr = new String(binaryArrayToString(trimZeroes(bigNumSeq, MathUtil.TrimDirection.Leading)));
        } else {
            finalStr = binaryArrayToString(pad(bigNumSeq, 0, 4 - bigNumSeq.length, MathUtil.TrimDirection.Leading));
        }

        return finalStr;
    }

    public static int[] stringToBinaryArray0(String num) throws Exception {

        BigInteger bigNum = BigInteger.valueOf(Long.valueOf(num));
        BigInteger[] buArr = new BigInteger[0];
        BigInteger n = BigInteger.ONE;
        buArr = add(buArr, n);

        while (n.compareTo(bigNum) < 1) {
            // n = 2 * n;
            n = BigInteger.TWO.multiply(n);
            buArr = add(buArr, n);
        }

        int[] bigNumSeq = new int[0];
        BigInteger numReduce = bigNum;
        for (int i = buArr.length - 1; i >= 0; i--) {

            if (buArr[i].compareTo(numReduce) <= 0) {
                bigNumSeq = addToIntArray(bigNumSeq, 1);
                // numReduce -= buArr[i];
                numReduce = numReduce.subtract(buArr[i]);
            } else {
                bigNumSeq = addToIntArray(bigNumSeq, 0);
            }

        }

        var finalStr = "";
        if (bigNumSeq.length > 4 && bigNumSeq.length % 4 != 0 && bigNumSeq[0] == 0) {
            bigNumSeq = trimZeroes(bigNumSeq, MathUtil.TrimDirection.Leading);
        } else {
            bigNumSeq = pad(bigNumSeq, 0, 4 - bigNumSeq.length, MathUtil.TrimDirection.Leading);
        }

        return bigNumSeq;
    }

    @SuppressWarnings("unchecked")
    public static int[] addAt(int[] mainArr, int targetiIndex, int item) throws Exception {

        try {

            if (targetiIndex < 0) {
                throw new Exception("Invalid index for method addAt");
            }

            //
            if (targetiIndex > mainArr.length - 1) {
                return addToIntArray(mainArr, item);
            }

            int[] finalArr = new int[mainArr.length + 1];

            if (mainArr.length < mediumArrSize) {
                int itemAddedInd = 0;
                for (int i = 0, pi = 0; i < mainArr.length; i++, pi++) {
                    if (i == targetiIndex) {
                        finalArr[pi] = item;
                        ++pi;
                        ++itemAddedInd;
                    }
                    finalArr[pi] = mainArr[i];
                }

                if (itemAddedInd == 0) {
                    finalArr[finalArr.length - 1] = item;
                }

            } else {
                try {
                    // xfimprovpt
                    if (targetiIndex != 0) {
                        System.arraycopy(mainArr, 0, finalArr, 0, targetiIndex - 1);
                    }
                    System.arraycopy(new int[] { item }, 0, finalArr, targetiIndex, 1);
                    if (targetiIndex != finalArr.length - 1) {
                        System.arraycopy(mainArr, targetiIndex, finalArr, targetiIndex + 1,
                                (mainArr.length - targetiIndex));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    // throw new Exception();
                }
            }

            return finalArr;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mainArr;

    }

    public static int[] pad(int[] mainArr, int padNum, int iterCount, MathUtil.TrimDirection dir) {
        // pos -1(prefix) 0(?) 1(postfix)
        try {

            var solveRes = solve_(iterCount, 0);
            if (solveRes == -1) {
                throw new Exception("invalid iterCount");
            }

            if (solveRes == 0) {
                return mainArr;
            }
            // for (int i = 0; i < iterCount; i++) {
            int i = 0;
            while (i != iterCount) {

                if (dir.equals(MathUtil.TrimDirection.Leading)) {
                    mainArr = addAt(mainArr, 0, padNum);
                } else if (dir.equals(MathUtil.TrimDirection.Trailing)) {
                    mainArr = addToIntArray(mainArr, padNum);
                }
                i += 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mainArr;

    }

    public static String addBinaryNumbers(String... numsArr) throws Exception {

        if (numsArr.length == 0) {
            return "0";
        }

        if (numsArr.length == 1) {
            return numsArr[0];
        }

        var ix = 1;
        var eol = 0;
        var p1 = stringToIntArray(numsArr[0]);
        var p2 = stringToIntArray(numsArr[1]);

        while (eol == 0) {
            p1 = addTwoBinaryNumbers(p1, p2, 0); // improvepoint
            ix += 1;
            if (ix == numsArr.length) {
                break;
            }

            p2 = stringToIntArray(numsArr[ix]);
        }

        return binaryArrayToString(p1);
    }

    public static int[] addBinaryNumbers(int[][] numsArr2D, int validated) throws Exception {

        if (validated != 1) {
            if (numsArr2D.length == 0) {
                return new int[] { 0 };
            }

            if (numsArr2D.length == 1) {
                return numsArr2D[0];
            }
        }

        var ix = 1;
        var p1 = numsArr2D[0];
        var p2 = numsArr2D[1];
        while (ix != numsArr2D.length) {

            p1 = addTwoBinaryNumbers(p1, p2, 0);// improvept
            ix += 1;
            if (ix == numsArr2D.length) {
                break;
            }
            p2 = numsArr2D[ix];

        }

        return p1;

    }

    public static String multiplyTwoNumbers(String num1, String num2) {

        // var accVal =
        return "0";
    }

    public static String addNumbers(String... numsArr) throws Exception {

        if (numsArr == null) {
            throw new Exception("invalid");
        }

        if (numsArr.length == 1) {
            return numsArr[0];
        }

        var eol = 0;
        var ix = 1;
        var p1 = numsArr[0];
        var p2 = numsArr[1];
        while (eol == 0) {

            p1 = addTwoNumbers(p1, p2);
            ix += 1;
            if (ix == numsArr.length) {
                break;
            }

            p2 = numsArr[ix];

        }

        return p1;
    }

    public static String addTwoNumbers(String num1, String num2) throws Exception {
        var res = addTwoBinaryNumbers(stringToBinary(num1), stringToBinary(num2), 0);
        return binaryToDecimal(res);
    }

    public static int[] subtractTwoBinaryNumbers(int[] num1Arr, int[] num2Arr, int validated) throws Exception {

        var p1 = new int[0];
        var p2 = new int[0];
        var solveRes = 0;
        if (validated != 1) {
            if (isBinarySequence_(num1Arr) != 1) {
                throw new Exception("num1Arr is invalid");
            }

            if (isBinarySequence_(num2Arr) != 1) {
                throw new Exception("num2Arr is invalid");
            }
            // commented for ptimizat validations done ahead

            if ((int) sumOf(num1Arr) == 0) {
                return num2Arr;
            }

            var solveMap = solveBinary2_(num1Arr, num2Arr, 0);
            solveRes = (int) solveMap.get("result");

            if (or(solveRes == 1, solveRes == 0)) {
                p1 = (int[]) solveMap.get("d1");
                p2 = (int[]) solveMap.get("d2");
            } else if (solveRes == -1) {
                p1 = (int[]) solveMap.get("d2");
                p2 = (int[]) solveMap.get("d1");
            }

            p1 = trimZeroes(p1, TrimDirection.Leading);
            p2 = trimZeroes(p2, TrimDirection.Leading);
            p2 = pad(p2, 0, p1.length - p2.length, TrimDirection.Leading);

        } else {
            p1 = num1Arr;
            p2 = num2Arr;

            if (p1.length != p2.length) {

            }
        }

        var accNumArr = new int[p1.length + 1];
        var prevCrx = 0;
        var ix = 0;

        while (ix != accNumArr.length) {

            var curixp1 = (p1.length - 1) - ix;
            var curixp2 = (p2.length - 1) - ix;
            if (ix == accNumArr.length - 1) {
                accNumArr[(accNumArr.length - 1) - ix] = prevCrx;
            } else {
                var subtractLogResArr = subtractLogic(p1[curixp1], solve(0, curixp2) ? 0 : p2[curixp2], prevCrx);
                prevCrx = subtractLogResArr[0];
                accNumArr[(accNumArr.length - 1) - ix] = subtractLogResArr[1];
            }
            ix += 1;
        }

        // includesignedbit
        if (solveRes == -1) {

        }

        return accNumArr;

    }

    public static int[] addTwoBinaryNumbers(int[] num1Arr, int[] num2Arr, int validated) throws Exception {

        var p1 = new int[0];
        var p2 = new int[0];

        if (validated != 1) {
            if (isBinarySequence_(num1Arr) != 1) {
                throw new Exception("num1Arr is invalid");
            }

            if (isBinarySequence_(num2Arr) != 1) {
                throw new Exception("num2Arr is invalid");
            }
            // commented for ptimizat validations done ahead

            if ((int) sumOf(num1Arr) == 0) {
                return num2Arr;
            }

            var solveMap = solveBinary2_(num1Arr, num2Arr, 0);
            var solveRes = (int) solveMap.get("result");

            if (or(solveRes == 1, solveRes == 0)) {
                p1 = (int[]) solveMap.get("d1");
                p2 = (int[]) solveMap.get("d2");
            } else if (solveRes == -1) {
                p1 = (int[]) solveMap.get("d2");
                p2 = (int[]) solveMap.get("d1");
            }

            p1 = trimZeroes(p1, TrimDirection.Leading);
            p2 = trimZeroes(p2, TrimDirection.Leading);
            p2 = pad(p2, 0, p1.length - p2.length, TrimDirection.Leading);

            // if (solveRes == 1) {
            // p1 = (int[]) solveMap.get("d1");
            // p2 = pad(num2Arr, 0, num1Arr.length - num2Arr.length, TrimDirection.Leading);
            // } else if (solveRes == -1) {
            // p1 = (int[]) solveMap.get("d2");
            // p2 = pad(num1Arr, 0, num2Arr.length - num1Arr.length, TrimDirection.Leading);
            // } else {
            // p1 = (int[]) solveMap.get("d1");
            // p2 = (int[]) solveMap.get("d2");
            // }

            // as of 04092026 dnr
            // var solveRes = solveBinary_(num1Arr, num2Arr, 0);
            // if (solveRes == 1) {
            // p1 = num1Arr;
            // p2 = pad(num2Arr, 0, num1Arr.length - num2Arr.length, TrimDirection.Leading);
            // } else if (solveRes == -1) {
            // p1 = num2Arr;
            // p2 = pad(num1Arr, 0, num2Arr.length - num1Arr.length, TrimDirection.Leading);
            // } else {
            // p1 = num1Arr;
            // p2 = num2Arr;
            // }

        } else {
            p1 = num1Arr;
            p2 = num2Arr;

            if (p1.length != p2.length) {

            }
        }

        var accNumArr = new int[p1.length + 1];
        var prevCrx = 0;
        var ix = 0;

        while (ix != accNumArr.length) {

            var curixp1 = (p1.length - 1) - ix;
            var curixp2 = (p2.length - 1) - ix;
            if (ix == accNumArr.length - 1) {
                accNumArr[(accNumArr.length - 1) - ix] = prevCrx;
            } else {
                // if (crix == 33) {
                // var p22 = 234;
                // }
                var addLogResArr = addLogic(p1[curixp1], solve(0, curixp2) ? 0 : p2[curixp2], prevCrx);
                prevCrx = addLogResArr[0];
                accNumArr[(accNumArr.length - 1) - ix] = addLogResArr[1];
            }
            ix += 1;
        }

        return accNumArr;
    }

    // e
    public static int[] addTwoBinaryNumbers0(int[] num1Arr, int[] num2Arr) throws Exception {

        if (isBinarySequence_(num1Arr) != 1) {
            throw new Exception("num1Arr is invalid");
        }

        if (isBinarySequence_(num2Arr) != 1) {
            throw new Exception("num2Arr is invalid");
        }

        if ((int) sumOf(num1Arr) == 0) {
            return num2Arr;
        }

        var finalArr = new int[0];
        var solveRes = solveTwoBinaryNumbers(num1Arr, num2Arr, 0);
        // if (solveRes == 1) {
        // // totAddIterations = num1Arr.length;
        // solveRes = 1;
        // } else {
        // // totAddIterations = num2Arr.length;
        // }

        var eol = 0;
        // binary numbers
        var primaryNumArr = new int[0];
        var secondaryNumArr = new int[0];

        if (solveRes == 1) {
            primaryNumArr = num1Arr;
            secondaryNumArr = pad(num2Arr, 0, num1Arr.length - num2Arr.length, TrimDirection.Leading);
        } else if (solveRes == -1) {
            primaryNumArr = num2Arr;
            secondaryNumArr = pad(num1Arr, 0, num2Arr.length - num1Arr.length, TrimDirection.Leading);
        } else {
            primaryNumArr = num1Arr;
            secondaryNumArr = num2Arr;
        }

        finalArr = addTwoBinaryNumbers0(primaryNumArr, secondaryNumArr);

        // var accNumArr = new int[primaryNumArr.length];
        // var ix = 0;
        // var nxtCmpArr = new int[primaryNumArr.length + 1];
        // var nxtCmpInitInd = 0;

        // while (ix != primaryNumArr.length) {

        // var addResArr = new int[0];
        // // var primaryIx = (primaryNumArr.length - 1) - ix;

        // if (primaryNumArr[(primaryNumArr.length - 1) - ix] == 1) {
        // addResArr = addToIntArray(addResArr, primaryNumArr[(primaryNumArr.length - 1)
        // - ix]);
        // }

        // if (ix < secondaryNumArr.length) {
        // if (secondaryNumArr[(secondaryNumArr.length - 1) - ix] == 1) {
        // addResArr = addToIntArray(addResArr, secondaryNumArr[(secondaryNumArr.length
        // - 1) - ix]);
        // }
        // }

        // // e
        // if (addResArr.length != 0) {
        // if (addResArr.length != 1) {
        // nxtCmpInitInd = 1;
        // nxtCmpArr[((nxtCmpArr.length - 1) - ix) - 1] = 1;
        // accNumArr[(accNumArr.length - 1) - ix] = 0; // delib
        // } else {
        // accNumArr[(accNumArr.length - 1) - ix] = 1;
        // }
        // } else {
        // accNumArr[(accNumArr.length - 1) - ix] = 0;
        // }

        // if (ix == primaryNumArr.length - 1) {
        // if (nxtCmpInitInd == 1) {
        // primaryNumArr = nxtCmpArr;
        // secondaryNumArr = accNumArr;
        // accNumArr = new int[primaryNumArr.length + 1];
        // nxtCmpArr = new int[primaryNumArr.length + 1];
        // nxtCmpInitInd = 0;
        // ix = 0;
        // continue;
        // } else {
        // finalArr = numAccumalator(accNumArr);
        // eol = 1;
        // }
        // }

        // ix += 1;
        // }

        return finalArr;
    }

    /**
     * should be better when first arr is longer
     * compile or pre runtime param check for optimiz?
     * 
     * @param num1Arr
     * @param num2Arr
     * @param validated
     * @return
     * @throws Exception
     */
    public static int[] multiplyTwoBinaryNumbers(int[] num1Arr, int[] num2Arr, int validated) throws Exception {

        var sumOfNum1Arr = 0;
        var sumOfNum2Arr = 0;
        if (validated != 1) {

            if (isBinarySequence_(num1Arr) != 1) {
                throw new Exception("num1Arr is invalid");
            }

            if (isBinarySequence_(num2Arr) != 1) {
                throw new Exception("num2Arr is invalid");
            }

            sumOfNum1Arr = (int) sumOf(num1Arr);
            if (or(num1Arr.length == 0, sumOfNum1Arr == 0)) {
                return new int[] { 0 };
            }

            sumOfNum2Arr = (int) sumOf(num2Arr);
            if (or(num2Arr.length == 0, sumOfNum2Arr == 0)) {
                return new int[] { 0 };
            }

            num1Arr = trimZeroes(num1Arr, TrimDirection.Leading);
            num2Arr = trimZeroes(num2Arr, TrimDirection.Leading);

        }

        var solveRes = solveBinary_(num1Arr, num2Arr, 1);
        // var solveRes = (int) solveMap.get("result");
        // num1Arr = (int[]) solveMap.get("d1");
        // num2Arr = (int[]) solveMap.get("d2");

        var p1 = new int[0];
        var p2 = new int[0];
        var sumOfP1 = 0;
        var sumOfP2 = 0;
        // var p3

        if (solveRes == 1) {
            p1 = num1Arr;
            sumOfP1 = sumOfNum1Arr;
            // p2 = pad(num2Arr, 0, num1Arr.length - num2Arr.length, TrimDirection.Leading);
            p2 = num2Arr;
            sumOfP2 = sumOfNum2Arr;
        } else if (solveRes == -1) {
            p1 = num2Arr;
            sumOfP1 = sumOfNum2Arr;
            // p2 = pad(num1Arr, 0, num2Arr.length - num1Arr.length, TrimDirection.Leading);
            p2 = num1Arr;
            sumOfP2 = sumOfNum1Arr;
        } else { // rmos
            p1 = num1Arr;
            p2 = num2Arr;
            sumOfP1 = sumOfNum1Arr;
            sumOfP2 = sumOfNum2Arr;
        }

        var addSetArr = prepAdditionSet(p1, p2, sumOfP1, sumOfP2);

        if (addSetArr.length == 1) {
            return addSetArr[0];
        }

        return addBinaryNumbers(addSetArr, 0);

    }

    public static int[] divideTwoBinaryNumbers(int[] num1Arr, int[] num2Arr) throws Exception {

        var solveRes1 = solveBinary_(num1Arr, num2Arr, 0);
        if (num2Arr[num2Arr.length - 1] != 1) {
            // expon

            if (solveRes1 == 1) {
                var exponMap = exponentBinary(num1Arr, num2Arr);
                var exponRes = (int) exponMap.get("result");
                var dArr = (int[]) exponMap.get("d");

                if (exponMap.get("exponResArr") != null) {
                    var exponResArr = (int[]) exponMap.get("exponResArr");
                    if (and(exponRes == -1, exponResArr != null, exponResArr.length != 0)) {
                        if (solve(exponResArr.length, 0)) {
                            var ixArr = new int[] { 0 };
                            var p1 = new int[] { 0 };
                            var p2 = new int[] { 0 };
                            var res2 = solveBinary_(exponResArr, num1Arr, 0);
                            if (res2 == 1) {
                                p1 = exponResArr;
                                p2 = num1Arr;
                            } else {
                                p1 = num1Arr;
                                p2 = exponResArr;
                            }

                            while (solveBinary_(p1, p2, 0) != 0) {
                                p2 = incrementBinaryNumberByOne(p2);
                                ixArr = incrementBinaryNumberByOne(ixArr);
                                // chkarrlenstoopt
                            }

                            var finalArr = new int[] { 0 };
                            if (res2 == 1) {
                                finalArr = ixArr;
                            } else {
                                finalArr = addTwoBinaryNumbers(dArr, ixArr, 0);
                            }

                            return finalArr;
                        }
                    }
                } else {
                    // var finalArr = new int[] { 0 };
                    // var ix = 0; // dArr
                    dArr = subtractTwoBinaryNumbers(dArr, new int[] { 1 }, 0);
                    return powBinary(dArr);
                }
            }

        } else {
            return new int[] { 0 };
        }
        var p1 = new int[0];
        var finalArr = addTwoBinaryNumbers(p1, pad(p1, 0, largeArrSize, TrimDirection.Leading), 1);
        return finalArr;
    }

    private static int[] powBinary(int[] pArr) throws Exception {
        var baseNumArr = new int[] { 1, 0 };
        var finalArr = new int[] { 1, 0 };
        var ixArr = new int[] { 0 };
        var ix = 0;
        while (solveBinary_(ixArr, pArr, 0) != 0) {
            finalArr = multiplyTwoBinaryNumbers(baseNumArr, finalArr, 0);
            ixArr = incrementBinaryNumberByOne(ixArr);
            ix += 1;
        }
        return finalArr;
    }

    private static int[][] prepAdditionSet(int[] num1Arr, int[] num2Arr, int sumOfNum1Arr, int sumOfNum2Arr)
            throws Exception {

        num1Arr = trimZeroes(num1Arr, TrimDirection.Leading);
        num2Arr = trimZeroes(num2Arr, TrimDirection.Leading);
        var addSetArr2D = new int[sumOfNum2Arr][];

        var p1 = num1Arr;
        var ix = 0;
        var trPadCount = 0;
        var lePadCount = num2Arr.length - 1;
        var addArrIx = 0;
        while (ix != num2Arr.length) {
            // var chkIx = (num2Arr.length - 1) - ix;
            // if (chkIx == -1) {
            // break;
            // }
            if (num2Arr[(num2Arr.length - 1) - ix] == 1) {
                if (trPadCount != 0) {
                    p1 = pad(num1Arr, 0, trPadCount, TrimDirection.Trailing);
                }
                if (lePadCount != -1) {
                    p1 = pad(p1, 0, lePadCount, TrimDirection.Leading);
                }

                addSetArr2D[addArrIx] = p1;
                addArrIx += 1;
            }

            ix += 1;
            trPadCount += 1;
            lePadCount -= 1;
        }

        return addSetArr2D;
    }

    private static int invert(int num) {

        // enum?
        if (num == 1) {
            return 0;
        } else {
            return 1;
        }
    }

    public static int binaryAdd(int num1, int num2) {
        var scr = "";
        if (num1 + num2 != 0) {
            return 1;
        } else {
            return 0;
        }
    }

    public static int and_(boolean... values) {
        return and(values) ? 1 : 0;
    }

    public static boolean and(boolean... values) {
        var ix = 0;
        while (ix != values.length) {
            if (!values[ix]) {
                return false;
            }
            ix += 1;
        }
        return true;
    }

    public static boolean or(boolean... values) {
        var ix = 0;
        while (ix != values.length) {
            if (values[ix]) {
                return true;
            }
            ix += 1;
        }
        return false;
    }

    public static int or_(boolean... values) {
        return or(values) ? 1 : 0;
    }

    public static int and_(int num1, int num2) {
        var res = num1 + num2;
        if (res != 0) {
            if (res != 1) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public static int booleanEquivalent(boolean booleanval) {
        if (booleanval) {
            return 1;
        } else {
            return 0;
        }
    }

    public static int or_(boolean booleanVal1, boolean booleanVal2) {
        return or_(booleanEquivalent(booleanVal1), booleanEquivalent(booleanVal2));
    }

    private static int or_(int num1, int num2) {
        return num1 + num2 != 0 ? 1 : 0;
    }

    //
    public static int[] addLogic(int num1, int num2, int crxNum) {

        var res = (int) sumOf(new int[] { num1, num2, crxNum });
        if (res == 3) {
            return new int[] { 1, 1 };
        }
        if (res == 2) {
            return new int[] { 1, 0 };
        }
        if (res == 1) {
            return new int[] { 0, 1 };
        }

        return new int[] { 0, 0 };

    }

    public static int[] subtractLogic(int num1, int num2, int crxNum) {

        var res1 = (int) sumOf(new int[] { num1, num2 });
        var res2 = (int) sumOf(new int[] { res1, crxNum });

        if (res2 == 3) {
            return new int[] { 1, 1 };
        } else if (res2 == 2) {
            if (num1 == 1) {
                if (crxNum == 1) {
                    return new int[] { 0, 0 };
                }
            } else if (num2 == 1) {
                if (crxNum == 1) {
                    return new int[] { 1, 0 };
                }
            }
        } else if (res2 == 1) {
            if (crxNum == 1) {
                return new int[] { 1, 1 };
            } else if (num1 == 1) {
                return new int[] { 0, 1 };
            } else if (num2 == 1) {
                return new int[] { 1, 1 };
            }
        } else if (res2 == 0) {
            if (num1 == 1) {
                if (num2 == 0) {
                    return new int[] { 0, 1 };
                }
            } else if (num2 == 1) {
                if (num1 == 0) {
                    return new int[] { 1, 1 };
                }
            } else {
                return new int[] { 0, 0 };
            }
        }

        return new int[] { 0, 0 };
    }

    public static int findBinaryIndex(int num) {
        int[] binSeq = { 0, 1 };
        int i = 0;
        while (i != binSeq.length) {
            if (binSeq[i] == num) {
                return i;
            }
            i += 1;
        }
        return -1;
    }

    public static int findDecimalNumberIndex(int num) {
        int[] decSeq = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        int i = 0;
        while (i != decSeq.length) {
            if (decSeq[i] == num) {
                return i;
            }
            i += 1;
        }

        return -1;
    }

    // ver1
    public static String[] defaultBinaryNumbers() {
        return new String[] { "0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010" };
    }

    public enum Decimal {
       _0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10
    }

    public static int[] binaryOf(Decimal decimalNumber) {
        var dnum = decimalNumber.name().replace("_", "");
        return stringToIntArray(String.valueOf(defaultBinaryNumbers()[Integer.valueOf(dnum)]));
    }

    private static int[] binaryOf(int dnum) {
        return stringToIntArray(String.valueOf(defaultBinaryNumbers()[dnum]));
    }

    // ver1
    public static int[] decimalToBinary(String dnumStr) throws Exception {
        int[] finalArr = new int[0];
        int[] dNumArr = stringToIntArray(dnumStr);
        // 546

        var ix = 0;
        var ndecpow = dNumArr.length - 1;
        // var p3 = new int[dNumArr.length][];
        var p3 = new int[0];
        var p4 = new int[0];
        while (ix != dNumArr.length) {
            // var p1 = dNumArr[ix];
            var p1 = binaryOf(dNumArr[ix]);

            var ndecIx = 0;
            var p2 = binaryOf(1);
            //ndecIx != ndecpow
            while (solve_(ndecIx, ndecpow) != 0) {
                p2 = multiplyTwoBinaryNumbers(binaryOf(10), p2, 0);
                ndecIx += 1;
            }

            // p3 = addToIntArray2D(p3, multiplyTwoBinaryNumbers(p1, p2, 1));
            p3 = multiplyTwoBinaryNumbers(p1, p2, 0);

            if (p4.length == 0) {
                p4 = new int[p3.length];
            }
            p4 = addTwoBinaryNumbers(p4, p3, 0);

            ndecpow -= 1;
            ix += 1;
        }

        return trimZeroes(p4, TrimDirection.Leading);
    }

    // ver1
    public static int[] decimalToBinary0(String numberStr) {
        int[] finalArr = new int[0];
        int[] dNumArr = stringToIntArray(numberStr);

        var base2Arr = MathUtil.stringToIntArray(Integer.toBinaryString(2));
        // var res1Map = MathUtil.exponentBinary(binResArr, base2Arr);
        // var exponRes1Dec = res1Map.get("exponResDec");

        var ix = 0;
        while (ix != dNumArr.length) {

            ix += 1;
        }

        return finalArr;
    }

    // solve
    public static int solveTwoSingleDigitBinaryNumbers(int num1, int num2) {

        if (num1 == num2) {
            return 0;
        } else {

            if (num1 == 1) {
                return 1;
            } else {
                return -1;
            }

        }

    }

    // solveTwoSingleDigitBinaryNumbers
    public static int solveSingleDigitBinary(int num1, int num2) throws Exception {

        var dix1 = findBinaryIndex(num1);
        var dix2 = findBinaryIndex(num2);

        if (dix1 == -1 || dix2 == -1) {
            throw new Exception("invalid");
        }

        if (num1 == num2) {
            return 0;
        } else {

            if (num1 == 1) {
                return 1;
            } else {
                return -1;
            }

        }

    }

    public static boolean solveBinary(int[] d1, int[] d2, int validated) throws Exception {
        return solveTwoBinaryNumbers(d1, d2, validated) == 1 ? true : false;
    }

    public static int solveBinary_(int[] d1, int[] d2, int validated) throws Exception {
        return solveTwoBinaryNumbers(d1, d2, validated);
    }

    private static int solveTwoBinaryNumbers(int[] d1, int[] d2, int validated) throws Exception {

        try {

            if (validated != 1) {
                if (or(!isBinarySequence(d1), !isBinarySequence(d2))) {
                    throw new Exception("invalid numbers");
                }

                d1 = trimZeroes(d1, TrimDirection.Leading);
                d2 = trimZeroes(d2, TrimDirection.Leading);
            }

            int absoluteMax = d1.length;
            if (d1.length != d2.length) {

                if (d1.length == 0) {
                    return -1;
                }

                if (d2.length == 0) {
                    return 1;
                }

                absoluteMax = d1.length + d2.length;
                d1 = pad(d1, 0, absoluteMax - d1.length, TrimDirection.Leading);
                d2 = pad(d2, 0, absoluteMax - d2.length, TrimDirection.Leading);
            } else {

                if (d1.length == 0) {
                    return 0;
                }

            }

            // var eol = 0;
            var ix = 0;
            while (ix != absoluteMax) {
                if (d1[ix] == 1) {
                    if (d2[ix] == 1) {
                        ix += 1;
                        continue;
                    } else {
                        return 1;
                    }
                } else if (d2[ix] == 1) {
                    return -1;
                }

                ix += 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 
     * to be finite not to be _
     **/
    public class MathUtilType {

        private int size;
        private String[] names;
        private Object[] values;
        // private int result;

        public void setSize(int size) {
            this.size = size;
        }

        // public void setResult(int result) {
        // this.result = result;
        // }

        public MathUtilType(int size) {
            this.size = size;
            this.names = new String[size];
            this.values = new Object[size];
        }

        public MathUtilType(String[] names, Object[] values) {
            this.size = names.length;
            this.names = new String[size];
            this.values = new Object[size];
            // this.result = result;
        }

        // public MathUtilType(Object... objects) {

        // // for(var item : objects){
        // // Object.
        // // }

        // }

        public String[] getNames() {
            return names;
        }

        public void setNames(String[] names) {
            this.names = names;
        }

        public Object[] getValues() {
            return values;
        }

        public void setValues(Object[] values) {
            this.values = values;
        }

    }

    /**
     * not for general use cases
     * aimed for perf improvpts
     **/
    public static Map<String, Object> solveBinary2_(int[] d1, int[] d2, int validated) throws Exception {
        return solveTwoBinaryNumbers2(d1, d2, validated);
    }

    private static Map<String, Object> solveTwoBinaryNumbers2(int[] d1, int[] d2, int validated) throws Exception {

        try {

            if (validated != 1) {
                if (isBinarySequence_(d1) != 1 || isBinarySequence_(d2) != 1) {
                    throw new Exception("invalid numbers");
                }

                d1 = trimZeroes(d1, TrimDirection.Leading);
                d2 = trimZeroes(d2, TrimDirection.Leading);
            }

            int absoluteMax = d1.length;
            if (d1.length != d2.length) {

                if (d1.length == 0) {
                    // return -1;
                    return Map.of("d1", d1, "d2", d2, "result", -1);
                }

                if (d2.length == 0) {
                    // return 1;
                    return Map.of("d1", d1, "d2", d2, "result", 1);
                }

                absoluteMax = d1.length + d2.length;
                d1 = pad(d1, 0, absoluteMax - d1.length, TrimDirection.Leading);
                d2 = pad(d2, 0, absoluteMax - d2.length, TrimDirection.Leading);
            } else {

                if (d1.length == 0) {
                    // return 0;
                    return Map.of("d1", d1, "d2", d2, "result", 0);
                }

            }

            // var eol = 0;
            var ix = 0;
            while (ix != absoluteMax) {
                if (d1[ix] == 1) {
                    if (d2[ix] == 1) {
                        ix += 1;
                        continue;
                    } else {
                        // return 1;
                        return Map.of("d1", d1, "d2", d2, "result", 1);
                    }
                } else if (d2[ix] == 1) {
                    // return -1;
                    // return Map.of("d1", d2, "d2", d1, "result", -1);
                    return Map.of("d1", d1, "d2", d2, "result", -1);
                }

                ix += 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // return 0;
        return Map.of("d1", d1, "d2", d2, "result", 0);
    }

    //
    public static int solveTwoBinaryNumbers0(int[] d1, int[] d2) throws Exception {

        try {

            if (isBinarySequence_(d1) != 1 || isBinarySequence_(d2) != 1) {
                throw new Exception("invalid numbers");
            }

            d1 = trimZeroes(d1, TrimDirection.Leading);
            d2 = trimZeroes(d2, TrimDirection.Leading);

            var maxNumOfDigitArr = findMaxNumberOfBinaryDigitAsArr(d1);
            maxNumOfDigitArr = findMaxNumberOfBinaryDigitWithPrevAsArr(d2, maxNumOfDigitArr);
            // e
            if (binaryEquals_(d1, d2) != 1) {

                // e
                // var lenSolve = solveTwoNumbers(d1.length, d2.length);
                // if (lenSolve != 0) {
                // return lenSolve;
                // }

                var rix = 0;
                // var maxruns = d1.length;
                while (binaryEquals_(d1, d2) != 1) {

                    if (binaryEquals_(d1, maxNumOfDigitArr) == 1) {
                        return 1;
                    } else if (binaryEquals_(d2, maxNumOfDigitArr) == 1) {
                        return -1;
                    }

                    d1 = addTwoBinaryNumbers(d1, pad(d1, 0, d1.length - 1, TrimDirection.Trailing), 1);
                    // d1 = incrementBinaryNumberByOne(d1);
                    // d1 = incrementNumberByOne(d1);
                    rix += 1;

                    if (rix == d1.length) {
                        return 1;
                    }

                }

                if (rix != 0) {
                    return -1;
                }

            } else {
                return 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static boolean solve(int num1, int num2) throws Exception {
        return solveTwoNumbers(num1, num2) == 1 ? true : false;
    }

    public static int solve_(int num1, int num2) throws Exception {
        return solveTwoNumbers(num1, num2);
    }

    public static int solveTwoNumbers(int num1, int num2) throws Exception {
        return solveTwoNumbers(String.valueOf(num1), String.valueOf(num2));
    }

    public static int solveTwoBinaryNumbers0(String num1, String num2) throws Exception {

        // validations?
        BigInteger d1 = new BigInteger(num1);
        BigInteger d2 = new BigInteger(num2);

        var maxNumOfDigit = new BigInteger(findMaxNumberOfBinaryDigit(String.valueOf(num1)));
        maxNumOfDigit = new BigInteger(findMaxNumberOfBinaryDigitWithPrev(num2, maxNumOfDigit.toString()));
        // e
        if (!d1.equals(d2)) {

            var rix = 0;
            while (!d1.equals(d2)) {

                if (d1.equals(maxNumOfDigit)) {
                    return 1;
                } else if (d2.equals(maxNumOfDigit)) {
                    return -1;
                }

                d1 = d1.add(BigInteger.ONE);
                // d1 = incrementNumberByOne(d1);
                rix += 1;

            }

            if (rix != 0) {
                return -1;
            }

        } else {
            return 0;
        }

        return 0;
    }

    public static int solveTwoNumbers(String num1, String num2) throws Exception {

        // validations?
        BigInteger d1 = new BigInteger(num1);
        BigInteger d2 = new BigInteger(num2);

        var maxNumOfDigit = new BigInteger(findMaxNumberOfDigit(String.valueOf(num1)));
        maxNumOfDigit = new BigInteger(findMaxNumberOfDigitWithPrev(num2, maxNumOfDigit.toString()));
        // e
        if (!d1.equals(d2)) {

            var rix = 0;
            while (!d1.equals(d2)) {

                if (d1.equals(maxNumOfDigit)) {
                    return 1;
                } else if (d2.equals(maxNumOfDigit)) {
                    return -1;
                }

                d1 = d1.add(BigInteger.ONE);
                // d1 = incrementNumberByOne(d1);
                rix += 1;

            }

            if (rix != 0) {
                return -1;
            }

        } else {
            return 0;
        }

        return 0;
    }

    public static String incrementNumberByOne(String number) throws Exception {

        var resArr = incrementBinaryNumberByOne(stringToBinary(number));
        return binaryArrayToString(resArr);

    }

    public static int[] stringToIntArray(String number) {
        return charArrayToIntArray(number.toCharArray());
    }

    public static int[] stringToBinary00(String number) {
        BigInteger bnumber = new BigInteger(number).subtract(BigInteger.ONE);
        BigInteger ix = BigInteger.ZERO;
        int[] primaryNumArr = { 1 };
        while (!ix.equals(bnumber)) {
            primaryNumArr = incrementBinaryNumberByOne(primaryNumArr);
            ix = ix.add(BigInteger.ONE);
        }

        return primaryNumArr;
    }

    public static int[] stringToBinary(String number) {
        BigInteger bnumber = new BigInteger(number).subtract(BigInteger.ONE);
        BigInteger ix = BigInteger.ZERO;
        int[] primaryNumArr = { 1 };
        while (!ix.equals(bnumber)) {
            primaryNumArr = incrementBinaryNumberByOne(primaryNumArr);
            ix = ix.add(BigInteger.ONE);
        }

        return primaryNumArr;
    }

    // e
    public static int[] incrementBinaryNumberByOne(int[] primaryNumArr) {

        var finalArr = new int[0];
        // var primaryNumArr = charArrayToIntArray(binaryNumber.toCharArray());
        int[] secondaryNumArr = { 1 };
        secondaryNumArr = pad(secondaryNumArr, regPassCount, regPassCount, TrimDirection.Leading);
        var eol = 0;
        var accNumArr = new int[primaryNumArr.length];
        var ix = 0;
        var nxtCmpArr = new int[primaryNumArr.length + 1];
        var nxtCmpInitInd = 0;

        while (eol == 0) {

            var addResArr = new int[0];

            if (primaryNumArr[(primaryNumArr.length - 1) - ix] == 1) {
                addResArr = addToIntArray(addResArr, primaryNumArr[(primaryNumArr.length - 1) - ix]);
            }

            if (ix < secondaryNumArr.length) {
                if (secondaryNumArr[(secondaryNumArr.length - 1) - ix] == 1) {
                    addResArr = addToIntArray(addResArr, secondaryNumArr[(secondaryNumArr.length - 1) - ix]);
                }
            }

            // e
            if (addResArr.length != 0) {
                if (addResArr.length != 1) {
                    nxtCmpInitInd = 1;
                    nxtCmpArr[((nxtCmpArr.length - 1) - ix) - 1] = 1;
                    accNumArr[(accNumArr.length - 1) - ix] = 0; // delib
                } else {
                    accNumArr[(accNumArr.length - 1) - ix] = 1;
                }
            } else {
                accNumArr[(accNumArr.length - 1) - ix] = 0;
            }

            if (ix == primaryNumArr.length - 1) {
                if (nxtCmpInitInd == 1) {
                    primaryNumArr = nxtCmpArr;
                    secondaryNumArr = accNumArr;
                    accNumArr = new int[primaryNumArr.length + 1];
                    nxtCmpArr = new int[primaryNumArr.length + 1];
                    nxtCmpInitInd = 0;
                    ix = 0;
                    continue;
                } else {
                    finalArr = numAccumalator(accNumArr);
                    eol = 1;
                }
            }

            ix += 1;
        }

        return finalArr;

    }

    private static int[] findMaxNumberOfBinaryDigitAsArr(int[] binaryNumArr) throws Exception {

        binaryNumArr = trimZeroes(binaryNumArr, TrimDirection.Leading);

        var ix = 0;
        var maxdigitAsArr = new int[0];
        while (ix != binaryNumArr.length) {
            maxdigitAsArr = addToIntArray(maxdigitAsArr, 1);
            ix += 1;
        }

        return maxdigitAsArr;

    }

    private static String findMaxNumberOfBinaryDigit(String binaryNumber) {

        binaryNumber = trimZeroes(binaryNumber, TrimDirection.Leading);

        var ix = 0;
        var maxdigit = "";
        while (ix != binaryNumber.length()) {
            maxdigit += "1";
            ix += 1;
        }

        return maxdigit;

    }

    private static String findMaxNumberOfDigit(String number) {

        var ix = 0;
        var maxdigit = "";
        while (ix != number.length()) {
            maxdigit += "9";
            ix += 1;
        }

        return maxdigit;

    }

    private static int[] findMaxNumberOfBinaryDigitWithPrevAsArr(int[] binaryNumArr, int[] prevMaxDigitAsArr)
            throws Exception {

        binaryNumArr = trimZeroes(binaryNumArr, TrimDirection.Leading);
        prevMaxDigitAsArr = trimZeroes(prevMaxDigitAsArr, TrimDirection.Leading); // ensure

        var ix = 0;
        var maxdigitAsArr = new int[0];
        var prevMaxDigitFound = 0;
        while (ix != binaryNumArr.length) {
            maxdigitAsArr = addToIntArray(maxdigitAsArr, 1);

            if (binaryEquals(maxdigitAsArr, prevMaxDigitAsArr)) {
                prevMaxDigitFound = 1;
            }
            ix += 1;
        }

        if (prevMaxDigitFound == 1) {
            if (ix != 0) {
                return maxdigitAsArr;
            }
        } else {
            return prevMaxDigitAsArr;
        }

        return maxdigitAsArr;

    }

    private static String findMaxNumberOfBinaryDigitWithPrev(String binaryNumber, String prevMaxDigit) {

        binaryNumber = trimZeroes(binaryNumber, TrimDirection.Leading);
        prevMaxDigit = trimZeroes(prevMaxDigit, TrimDirection.Leading);

        var ix = 0;
        var maxdigit = "";
        var prevMaxDigitFound = 0;
        while (ix != binaryNumber.length()) {
            maxdigit += "1";

            if (maxdigit.equals(prevMaxDigit)) {
                prevMaxDigitFound = 1;
            }
            ix += 1;
        }

        if (prevMaxDigitFound == 1) {
            if (ix != 0) {
                return maxdigit;
            }
        } else {
            return prevMaxDigit;
        }

        return maxdigit;

    }

    private static String findMaxNumberOfDigitWithPrev(String number, String prevMaxDigit) {

        var ix = 0;
        var maxdigit = "";
        var prevMaxDigitFound = 0;
        while (ix != number.length()) {
            maxdigit += "9";

            if (maxdigit.equals(prevMaxDigit)) {
                prevMaxDigitFound = 1;
            }
            ix += 1;
        }

        if (prevMaxDigitFound == 1) {
            if (ix != 0) {
                return maxdigit;
            }
        } else {
            return prevMaxDigit;
        }

        return maxdigit;

    }

    public static int solveTwoSingleDigitNumbers(int num1, int num2) throws Exception {

        if (num1 == num2) {
            return 0;
        } else {

            if (num1 == 0) {
                return -1;
            }

            if (num2 == 0) {
                return 1;
            }

            var d1 = findDecimalNumberIndex(num1);
            var d2 = findDecimalNumberIndex(num2);

            if (d1 == -1 || d2 == -1) {
                throw new Exception("invalid numbers");
            }

            var rix = 0;
            while (d1 != d2) {

                if (d1 == 9) {
                    return 1;
                } else if (d2 == 9) {
                    return -1;
                }

                d1 += 1;
                rix += 1;

            }

            if (rix != 0) {
                return -1;
            }

        }

        return 0;

    }

    private static int solveBinaryArrsOfEqualLength(int[] num1Arr, int[] num2Arr) {

        var eoc = 0;
        var i = 0;
        while (eoc == 0) {

            if (i == num1Arr.length) {
                // break;
                return 0;
            }
            if (num1Arr[i] == 1 && num2Arr[i] == 0) {
                return 1; // yes
            } else if (num1Arr[i] == 0 && num2Arr[i] == 1) {
                return 0; // no
            }
            i++;
        }

        return 0; // no
    }

    public static String numToBinarySequence(int num) {
        String binarySeq = "";

        int[] buArr = new int[0];
        int n = 1;
        buArr = addToIntArray(buArr, n);

        while (n < num) {
            n = 2 * n;
            buArr = addToIntArray(buArr, n);
        }

        char[] bcharSeq = new char[0];
        int numReduce = num;
        for (int i = buArr.length - 1; i >= 0; i--) {

            if (buArr[i] <= numReduce) {
                bcharSeq = StringUtil.addToCharArray(bcharSeq, '1');
                numReduce -= buArr[i];
            } else {
                bcharSeq = StringUtil.addToCharArray(bcharSeq, '0');
            }

        }

        /*
         * if (bcharSeq.length < 4) {
         * bcharSeq = StringUtil.pad(bcharSeq, '0', 4 - bcharSeq.length,
         * StringUtil.Direction.Leading);
         * }
         */

        // if (num % 2 == 0 && num % 3 != 0 && num % 5 != 0 && num % 7 != 0 &&
        // bcharSeq.length % 4 != 0) {
        if (bcharSeq.length > 4 && bcharSeq.length % 4 != 0 && bcharSeq[0] == '0') {
            bcharSeq = StringUtil.trimZeroes(bcharSeq, StringUtil.Direction.Leading);
        } else {
            bcharSeq = StringUtil.pad(bcharSeq, '0', 4 - bcharSeq.length, StringUtil.Direction.Leading);
        }

        return new String(bcharSeq);
    }

    public static int factorial(int size) {

        int result = 1;

        for (int i = size; i > 0; i--) {
            result *= i;
        }

        return result;

    }

    public static int[] generateNumArrByRange(final int size, final int from, final int dir) {

        int[] finalArr = new int[0];
        int inc = from;

        int last = dir > 0 ? size : 0;
        do {

            finalArr = addToIntArray(finalArr, inc);
            if (dir > 0) {
                ++inc;
            } else {
                --inc;
            }

        } while (inc != last);

        return finalArr;
    }

    public static int[] generateNumArrByRange(final int size, final int from, final int dir, final int progfactor,
            final int progtype) {

        // int progfactor = Integer.parseInt(progfactorDesc.replace("x", ""));
        int progfactorC = progfactor;
        int[] finalArr = new int[size];
        int inc = from;

        // int last = from;
        // for (int i = 0; i < size; i++) {
        // if (dir > 0) {
        // last += progfactorC;
        // } else if (dir < 0) {
        // last -= progfactorC;
        // }

        // }

        var ix = 0;
        while (ix != finalArr.length) {

            finalArr[ix] = progfactor == 0 ? 0 : inc; // addToIntArray(finalArr, progfactor == 0 ? 0 : inc);
            if (dir == 1) {

                if (progtype == 1) {
                    inc = progfactor > 1 ? inc + progfactor : ++inc;
                } else if (progtype == 2) {
                    inc = progfactor > 1 ? inc * progfactor : ++inc;
                } else {
                    break;
                }

                // if (inc >= last) {
                // // eol = 1;
                // break;
                // }
            } else if (dir == -1) {
                inc = progfactor > 1 ? inc - progfactor : --inc;
                // if (inc <= last) {
                // // eol = 1;
                // break;
                // }
            } else {

            }

            ix += 1;

        }

        return finalArr;
    }

    public static int[] generateNumArrByRange(final int size, final int from, final int dir, final int progfactor) {

        int[] finalArr = new int[size];
        int inc = from;

        int last = from;
        for (int i = 0; i < size; i++) {
            if (dir > 0) {
                last += progfactor;
            } else if (dir < 0) {
                last -= progfactor;
            }

        }

        var ix = 0;
        while (ix != finalArr.length) {

            finalArr[ix] = progfactor == 0 ? 0 : inc; // addToIntArray(finalArr, progfactor == 0 ? 0 : inc);
            if (dir == 1) {
                inc = progfactor > 1 ? inc + progfactor : ++inc;
                if (inc >= last) {
                    // eol = 1;
                    break;
                }
            } else if (dir == -1) {
                inc = progfactor > 1 ? inc - progfactor : --inc;
                if (inc <= last) {
                    // eol = 1;
                    break;
                }
            } else {

            }

            ix += 1;

        }

        return finalArr;
    }

    public static int randomSelectionFrom(int... numbers) {

        var finalNum = 0;
        var accNumArr = new int[numbers.length * 7];

        var ix = 0;
        while (ix != accNumArr.length) {

            var ract = new Runnable() {
                @Override
                public void run() {

                }
            };

            Thread thr = new Thread(ract);
            thr.setName("THR_RAND");
            ///thr.setPriority(Thread.MAX_PRIORITY);
            threadsArr = add(threadsArr, thr);

            thr.start();

        }

        return finalNum;
    }

    public static int[] generateRandomNumArrWith(final int size, final int... numbers) throws Exception {

        int maxNum = maxOfArray(numbers);
        int[] finalArr = new int[size];
        var ix = 0;
        while (ix != finalArr.length) {
            var curNum = -1;
            while (hasInt(numbers, curNum) == 0) {
                var r = Math.random();
                // if(r < 0.1){
                // System.out.println("23525");
                // }
                curNum = Double.valueOf(r * (maxNum + 1)).intValue();
                // System.out.print("r:"+r+":"+curNum);
                finalArr[ix] = curNum;
            }

            ix += 1;
        }

        return finalArr;
    }

    @SuppressWarnings("unchecked")
    public static <E extends Object> E[] add(E[] mainArr, E item) {

        E[] mainArrInc = (E[]) Array.newInstance(mainArr.getClass().getComponentType(), mainArr.length + 1);

        for (int i = 0; i < mainArr.length; i++) {
            mainArrInc[i] = mainArr[i];
        }

        mainArrInc[mainArrInc.length - 1] = item;
        return (E[]) mainArrInc;

    }

    // class NumericObject extends Object {

    // }

    // @SuppressWarnings("unchecked")
    // public static <E extends Object>E[] generateNumArrByRange(final E size, final
    // E from, final int dir, final int progfactor) {

    // E[] finalArr = (E[]) Array.newInstance(from.getClass().getComponentType(),
    // 0);
    // E inc = from;
    // E progfactorE = progfactor;

    // int last = dir > 0 ? size : 0;
    // int eol = 0;
    // do {

    // finalArr = (E[]) add(finalArr, progfactor == 0 ? 0 : inc);
    // if (dir > 0) {
    // inc = progfactor > 1 ? inc + progfactor : ++inc;
    // if (inc >= last) {
    // eol = 1;
    // }
    // } else {
    // inc = progfactor > 1 ? inc - progfactor : --inc;
    // if (inc <= last) {
    // eol = 1;
    // }
    // }

    // } while (eol == 0);

    // return finalArr;
    // }

    public static String binaryArrayToString(int[] mainArr) throws Exception {

        mainArr = trimZeroes(mainArr, TrimDirection.Leading);
        byte[] strByteArr = new byte[mainArr.length];
        try {

            var ix = 0;
            while (ix != mainArr.length) {

                if (mainArr[ix] != 0) {
                    if (mainArr[ix] != 1) {
                        throw new Exception("not a binary sequence");
                    }
                }

                var x = mainArr[ix] + "";
                System.arraycopy(x.getBytes(), 0, strByteArr, ix, x.length());
                ix += 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new String(strByteArr);

    }


    public static String intArrayToString(int[] mainArr) {

        var ix = 0;
        byte[] strByteArr = new byte[mainArr.length];
        while (ix != mainArr.length) {
            var x = mainArr[ix] + "";
            System.arraycopy(x.getBytes(), 0, strByteArr, ix, x.length());
            ix += 1;
        }

        return new String(strByteArr);

    }

    public static String intArrayToString(Integer[] mainArr) {

        var ix = 0;
        byte[] strByteArr = new byte[mainArr.length];
        while (ix != mainArr.length) {
            var x = mainArr[ix] + "";
            System.arraycopy(x.getBytes(), 0, strByteArr, ix, x.length());
            ix += 1;
        }

        return new String(strByteArr);

    }

    public static String intArrayToString(BigInteger[] mainArr) {

        var ix = 0;
        byte[] strByteArr = new byte[mainArr.length];
        while (ix != mainArr.length) {
            var x = mainArr[ix] + "";
            System.arraycopy(x.getBytes(), 0, strByteArr, ix, x.length());
            ix += 1;
        }

        return new String(strByteArr);

    }

    public static Integer[] intArrayToIntegerArray(int[] mainArr) {
        // MathUtil.addToIntegerArray
        Integer[] finalArr = new Integer[0];

        for (int i = 0; i < mainArr.length; i++) {
            finalArr = MathUtil.addToIntegerArray(finalArr, Integer.valueOf(mainArr[i]));
        }

        return finalArr;
    }

    public static int[] shuffle(int[] mainArr) {

        final int[] finalArr = new int[0];
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

    private static int[] doShuffle(int[] currentArr, int cascades) {

        try {

            final int maxIndex = currentArr.length - 1;
            int[] finalCutArr = new int[0];
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

                    finalCutArr = addToIntArray(finalCutArr, currentArr[cutAt]);
                    currentArr = removeFromIntArray(currentArr, cutAt);
                }

            }

            int appendAt = Double.valueOf(Math.random() * maxIndex / 2).intValue();
            while (cutAt == appendAt) {
                appendAt = Double.valueOf(Math.random() * maxIndex / 2).intValue();
            }

            for (int x = 0; x < finalCutArr.length; x++, appendAt++) {
                currentArr = addIntAt(currentArr, appendAt, finalCutArr[x]);
            }

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return currentArr;
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

    public static Integer[] distinct(final Integer[] mainArr) {

        Integer[] finalArr = new Integer[0];

        for (int i = 0; i < mainArr.length; i++) {

            if (ContainerUtil.has(finalArr, mainArr[i], 0) == 0) {
                finalArr = ContainerUtil.add(finalArr, mainArr[i]);
            }

        }

        return finalArr;

    }

    public static int[][] splitBy(final int[] dataRowArr, final int from, final int splitBy,
            final int[] filterItems) {

        int[][] splitArr = new int[0][0];
        int[] currentArr = new int[0];
        for (int i = from; i < dataRowArr.length; i++) {

            if (filterItems != null && hasInt(filterItems, dataRowArr[i]) == 1 && i != dataRowArr.length - 1) {
                continue;
            }

            if (i > 0 && (dataRowArr[i] == splitBy || i == dataRowArr.length - 1)) {

                if (i == dataRowArr.length - 1 && currentArr.length > 0) {
                    currentArr = addToIntArray(currentArr, dataRowArr[i]);
                }

                // currentArr = trim(currentArr);
                splitArr = addToIntArray2D(splitArr, currentArr);
                currentArr = new int[0];
                continue;
            }

            currentArr = addToIntArray(currentArr, dataRowArr[i]);
        }

        return splitArr;
    }

    public static int[][] splitByInterval(final int[] dataRowArr, final int from, final int splitInterval,
            final int[] filterItems) {

        int[][] splitArr = new int[0][0];
        int[] currentArr = new int[0];
        for (int i = from; i < dataRowArr.length; i++) {

            if (filterItems != null && hasInt(filterItems, dataRowArr[i]) == 1 && i != dataRowArr.length - 1) {
                continue;
            }

            if (i > 0 && (i % splitInterval == 0 || i == dataRowArr.length - 1)) {

                if (i == dataRowArr.length - 1 && currentArr.length > 0) {
                    currentArr = addToIntArray(currentArr, dataRowArr[i]);
                }

                // currentArr = trim(currentArr);
                splitArr = addToIntArray2D(splitArr, currentArr);
                currentArr = new int[0];
                continue;
            }

            currentArr = addToIntArray(currentArr, dataRowArr[i]);
        }

        return splitArr;
    }

    public static int[] reverse(int[] currentArr) {
        int[] reverseArr = new int[currentArr.length];
        var ix = currentArr.length - 1;
        while (ix != -1) {
            reverseArr[(currentArr.length - 1) - ix] = currentArr[ix];
            ix -= 1;
        }

        return reverseArr;
    }

    public static int[][] reverse(int[][] currentArr) {
        int[][] reverseArr = new int[currentArr.length][];

        var ix = currentArr.length - 1;
        while (ix != -1) {
            reverseArr[(currentArr.length - 1) - ix] = currentArr[ix];
            ix -= 1;
        }

        return reverseArr;
    }

    public static int[][] rotate(int[][] mainArr2D, int deg, RotationalDirection rdir) {

        int rixfactor = deg;

        for (int i = 0; i < mainArr2D.length; i++) {

            int[] crowArr = mainArr2D[i];

            for (int j = 0; j < crowArr.length; j++) {

                if (rdir.equals(RotationalDirection.AntiClockwise)) {

                    if (j - rixfactor <= crowArr.length - 1 && j - rixfactor > 0 && crowArr[j - rixfactor] == 1) {

                        crowArr[j - rixfactor] = 0;
                        crowArr[j] = 1;

                    }

                } else if (rdir.equals(RotationalDirection.Clockwise)) {

                    if (j + rixfactor <= crowArr.length - 1 && j + rixfactor > 0 && crowArr[j + rixfactor] == 1) {

                        crowArr[j + rixfactor] = 0;
                        crowArr[j] = 1;
                    }
                }

            }

        }

        return mainArr2D;
    }

    public static int[][] pad2DArray(int[][] mainArr2D, int padFactor, PadDirection pdir) {

        if (pdir == null) {
            pdir = PadDirection.All;
        }

        int[][] finalArr2D = new int[0][];
        try {

            if (pdir.equals(PadDirection.All) || pdir.equals(PadDirection.Left_Right)) {
                // left right
                for (int i = 0; i < mainArr2D.length; i++) {

                    int[] crowArr = mainArr2D[i];

                    for (int j = 0; j < padFactor; j++) {
                        crowArr = MathUtil.addIntAt(crowArr, 0, 0);
                        crowArr = MathUtil.addToIntArray(crowArr, 0);
                    }

                    finalArr2D = MathUtil.addToIntArray2DAt(finalArr2D, i, crowArr);

                }

            }

            if (pdir.equals(PadDirection.All) || pdir.equals(PadDirection.Top_Down)) {

                // top down
                for (int i = 0; i < padFactor; i++) {
                    int[] padArr = MathUtil.generateNumArrByRange(mainArr2D[0].length, 0, 1, 0);
                    mainArr2D = MathUtil.addToIntArray2DAt(mainArr2D, 0, padArr);
                    mainArr2D = MathUtil.addToIntArray2D(mainArr2D, padArr);
                }

                finalArr2D = mainArr2D;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return finalArr2D;
    }

    public static int[][] merge(int[][][] dataRows, int mergeItem) {

        // assuming all inner arr lens are equal

        int[][] finalArr = new int[0][];
        finalArr = addAllToIntArray2D(finalArr, dataRows[0]);

        for (int i = 1; i < dataRows.length; i++) {

            int[][] crowArr = dataRows[i];

            int diff = 0;
            if ((diff = Math.abs(crowArr.length - finalArr.length)) > 0) {
                crowArr = pad2DArray(crowArr, diff, PadDirection.Left_Right);
            }

            for (int j = 0; j < crowArr.length; j++) {

                int[] ccellArr = crowArr[j];

                for (int k = 0; k < ccellArr.length; k++) {

                    if (ccellArr[k] == mergeItem) {
                        finalArr[j][k] = mergeItem;
                    }

                }

            }

        }

        return finalArr;

    }

    public static int[][] removeFromIntArray2D(int[][] mainArr, int ti) {

        int[][] procArr = new int[0][];
        for (int i = 0; i < mainArr.length; i++) {

            if (i == ti) {
                continue;
            } else {
                procArr = addToIntArray2D(procArr, mainArr[i]);
            }
        }

        return procArr;
    }

    public static int[][] canvas(int cwidth, int cheight, int padFactor) {

        int[][] canvasArr = new int[0][];

        for (int i = 0; i < cheight + padFactor; i++) {
            int[] padArr = generateNumArrByRange(cwidth + padFactor, 0, 1, 0);
            canvasArr = addToIntArray2D(canvasArr, padArr);
        }

        return canvasArr;
    }

    public static int[][] addToCanvas(LinkedHashMap<String, Object> canvasInfo) {

        int[][] canvasArr = (int[][]) canvasInfo.get("Canvas2D");
        int[][][] dataRows = (int[][][]) canvasInfo.get("DataRows");
        int mergeVal = (int) canvasInfo.get("MergeVal");
        ElementPosition epos = (ElementPosition) canvasInfo.get("ElementPosition");

        int ri = 0;
        try {

            for (int i = 0; i < dataRows.length; i++, ri++) {

                int[][] crowArr = dataRows[i];
                int diff = 0;
                if (epos.equals(ElementPosition.Center)) {

                    if ((diff = Math.abs(crowArr[0].length - canvasArr[0].length)) > 0) {

                        crowArr = pad2DArray(crowArr, 8, PadDirection.Top_Down);
                        // printInkPoints(crowArr);
                        crowArr = pad2DArray(crowArr, diff / 2, PadDirection.Left_Right);
                        // printInkPoints(crowArr);

                    }

                }

                for (int j = 0; j < crowArr.length; j++) {

                    int[] ccellArr = crowArr[j];
                    for (int k = 0; k < ccellArr.length; k++) {

                        if (ccellArr[k] == mergeVal) {
                            canvasArr[j][k] = mergeVal;
                        }

                    }

                }

            }

        } catch (Exception e) {
            System.err.println("canvas error @ " + ri);
            e.printStackTrace();
        }

        return canvasArr;

    }

    public static void printInkPoints(LinkedHashMap<String, Object> cprintInfo) {

        // static int width = 25;
        // static int height = 25;
        // int gridsize = width * height;
        // int[] columnsArr = new int[0];
        // static int[] inkPos1Arr = new int[0];
        // static String tipShape = "*";

        int width = (int) cprintInfo.get("Width");
        String tipShape = (String) cprintInfo.get("TipShape");
        int[][] grid2DArr = (int[][]) cprintInfo.get("Grid2DArr");

        try {
            int ri = 0;
            for (int i = 0; i < grid2DArr.length; i++) {
                ri += i;
                int inkedInd = 0;

                var row = grid2DArr[i];

                for (int j = 0; j < row.length; j++) {

                    var pt = row[j];

                    if (pt == 1) {
                        System.out.print(tipShape);
                        // inkPos1Arr = MathUtil.removeFromIntArray(inkPos1Arr, 0);
                        inkedInd = 1;
                    } else {
                        inkedInd = 0;
                    }

                    if (inkedInd == 0) {
                        System.out.print(".");
                        // System.out.print("\s");
                    }

                }

                ri += width;
                System.out.println("\r");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static boolean contains(Integer[] arr, int element) {

        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] == element) {
                return true;
            }
        }
        return false;
    }

    public static Integer[] swap(Integer[] e) {
        int temp = e[0];
        if (e[0] > e[1]) {
            e[1] = e[0];
            e[0] = temp;
        }
        return e;
    }

    public static Integer[] slice(Integer[] e, int from, int to) {

        int size = to > 0 && to > from ? to - from : from;
        Integer[] e1 = new Integer[size];
        for (int i = 0, j = from; i <= size - 1; j++, i++) {
            e1[i] = e[j];
            /*
             * if (to != 0 && i == (to - 1)) {
             * break; || j <= e.length - 1
             * }
             */
        }

        return e1;

    }

    public static Integer[] copyTo(Integer[] des, int desFrom, Integer[] src, int srcFrom) {

        Integer[] consDes = new Integer[des.length];
        if (src.length > des.length) {

            // check vacant spaces

            int vcount = vacantCount(des);

            if (vcount > 0) {
                des = distinct(des);
                // des = defrag(des, vcount); wrong
            }

            // resize des
            int diff = src.length - des.length;
            consDes = new Integer[des.length + diff];
        } else {
            consDes = des;
        }

        for (int d = 0, pi = desFrom; d < src.length; d++, pi++) {
            consDes[pi] = src[d];
        }
        return consDes;

    }

    public static int vacantCount(Integer[] arr) {
        int vcount = 0;

        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] == null) {
                vcount += 1;
            }
        }

        return vcount;
    }

    public static int skew() {
        return 0;
    }

    public static int randomWithinRange(int minDuration, int maxDuration) {
        var validDuration = 0;
        var lix = 0;
        while (validDuration < minDuration) {
            validDuration = Double.valueOf(Math.random() * maxDuration).intValue();
            lix += 1;

            if (lix > 4) {
                // System.out.println(`lix @ ${lix}`);
            }
        }
        return validDuration;
    }

    public static int hasTask(String splitTaskID) {

        var ix = 0;
        while (ix != threadsArr.length) {
            if (threadsArr[ix].getName().equals(splitTaskID)) {
                return 1;
            }
            ix += 1;
        }

        return 0;

    }

    public static int clearTaskFromThreadsArr(String splitTaskID) {

        var ix = 0;
        var removeInd = 0;
        while (ix != threadsArr.length) {

            if (threadsArr[ix].getName().equals(splitTaskID)) {
                removeFrom(threadsArr, ix);
                removeInd = 1;
                break;
            }

            ix += 1;
        }

        return removeInd;

    }

    /*
     * public static void main(String a[]) {
     * // prime(1, 100);
     * // System.out.print("\n\n");
     * // fibo(1, 100);
     * // primeFactorization(18);
     * 
     * // findGCF(List.of(1,4, 0));
     * findLCM(List.of(10,18,25,21));
     * 
     * }
     */

}
