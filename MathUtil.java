package harp.util;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @note experimental utility methods
 * @author Dennis Thomas
 */

public class MathUtil {

    public static List<Integer> numStore = new ArrayList<>();
    public static int regPassCount = 0;

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

    public static void primeSequence(int from, int to) {

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
                System.out.print(i + "\s");
            }

        }
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

    //     int[] pool = { 0, 1 };
    //     System.out.print(pool[0] + "\s" + pool[1] + "\s");

    //     for (int i = from; i < count + 1; i++) {

    //         int res = pool[0] + pool[1];
    //         System.out.print(res + "\s");

    //         pool[0] = pool[1];
    //         pool[1] = res;

    //     }
    // }

    public static Object[] fiboSeq(int from, int count) {

        var bfrom = BigInteger.valueOf(from);
        // var bcount = BigInteger.valueOf(count);

        BigInteger[] finalArr = new BigInteger[0];

        BigInteger[] pool = new BigInteger[2];
        pool[0] = BigInteger.ZERO;
        pool[1] = bfrom.equals(BigInteger.ZERO) ? BigInteger.ONE : bfrom;

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

    public static BigInteger powBig(int factor, int base) {

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

    public static int pow(int expfactor, int base) {

        if (expfactor == 0) {
            return 1;
        }

        int res = base;
        for (int i = 1; i < expfactor; i++) {
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

    public static int sumOf(Integer[] numArr) {
        int sum = 0;
        for (int i = 0; i < numArr.length; i++) {
            sum += numArr[i];
        }

        return sum;
    }

    public static int sumOf(int[] numArr) {
        int sum = 0;
        for (int i = 0; i < numArr.length; i++) {
            sum += numArr[i];
        }

        return sum;
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

        char[] numSeqArrP1 = numAccumalator(currentSeq.toCharArray());
        String numStr = new String(numSeqArrP1);
        int finalRes = Integer.parseInt(numStr);
        return finalRes;

    }

    public static int trimZeroes(String currentSeq, TrimDirection currentTrimDir) {

        char[] numSeqArrP1 = new char[0];
        if (currentTrimDir.equals(TrimDirection.Trailing)) {
            numSeqArrP1 = nonZeroNumAccumalator(currentSeq.toCharArray());
        } else {
            numSeqArrP1 = numAccumalator(currentSeq.toCharArray());
        }

        String numStr = new String(numSeqArrP1);
        int finalRes = Integer.parseInt(numStr);
        return finalRes;

    }

    private static char[] nonZeroNumAccumalator(char[] seqArr) {

        char[] finalSeq = new char[0];
        int nzNumSeqStartInd = 0;
        for (int i = 0; i < seqArr.length; i++) {

            int cp = (int) seqArr[i];
            if (cp >= 48 && cp <= 57) {

                if (seqArr[i] != '0') {
                    ++nzNumSeqStartInd;
                } else {
                    // if (nzNumSeqStartInd == 0) {
                    continue;
                    // }
                }

                finalSeq = StringUtil.addToCharArray(finalSeq, seqArr[i]);
            } else {

                if (i > 0 && nzNumSeqStartInd > 0) {
                    break;
                }

                continue;

            }

        }

        return finalSeq;
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

                finalSeq = StringUtil.addToCharArray(finalSeq, seqArr[i]);
            } else {

                if (i > 0 && nzNumSeqStartInd > 0) {
                    break;
                }

                continue;

            }

        }

        return finalSeq;
    }

    public static Integer[] purgeSort(Integer[] e, SortMode sortOrder) {
        Integer[] e2 = new Integer[e.length];
        for (int i = 0, pi = 0; i < e.length; i++) {

            if (e2[e2.length - 1] != null) {
                break;
            }

            Integer pe = null;
            if (sortOrder.equals(SortMode.Desc)) {
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

    public static int[] purgeSort(int[] mainArr, SortMode sortOrder) {
        int[] sortedArr = new int[mainArr.length];
        for (int i = 0, pi = 0; i < mainArr.length; i++) {

            if (sortedArr[sortedArr.length - 1] != 0) {
                break;
            }

            int pe = 0;
            if (sortOrder.equals(SortMode.Desc)) {
                pe = maxOfArray(mainArr);
            } else {
                pe = minOfArray(mainArr);
            }

            mainArr = ContainerUtil.removeItem(mainArr, pe);
            sortedArr[pi] = pe;
            pi++;
            --i;
        }

        return sortedArr;

    }

    public static int maxOfArray(Integer[] e) {

        int max = e[0];
        try {

            for (int j = 0; j < e.length; j++) {

                if (e[j] > max) {
                    max = e[j];
                }
            }
        } catch (Exception e1) {
            System.out.print(e1.toString());
        }

        return max;
    }

    public static int maxOfArray(int[] e) {

        int max = e[0];
        try {

            for (int j = 0; j < e.length; j++) {

                if (e[j] > max) {
                    max = e[j];
                }
            }
        } catch (Exception e1) {
            System.out.print(e1.toString());
        }

        return max;
    }

    public static int minOfArray(Integer[] e) {

        int min = e[0];
        try {
            for (int j = 0; j < e.length; j++) {

                if (e[j] < min) {
                    min = e[j];
                }
            }
        } catch (Exception e1) {
            System.out.print(e1.toString());
        }

        return min;
    }

    public static int minOfArray(int[] arr) {

        int min = arr[0];
        try {
            for (int j = 0; j < arr.length; j++) {

                if (arr[j] < min) {
                    min = arr[j];
                }
            }
        } catch (Exception e) {
            System.out.print(e.toString());
        }

        return min;
    }

    public static int[][] addToIntArray2D(final int[][] target, final int[] item) {
        final int[][] cArray = (int[][]) target;
        final int[][] cArrayInc = new int[cArray.length + 1][cArray.length + 1];
        for (int i = 0; i < cArray.length; i++) {
            cArrayInc[i] = cArray[i];
        }
        // cArrayInc = cArray;
        cArrayInc[cArrayInc.length - 1] = item;

        return cArrayInc;
    }

    public static int[][] addAllToIntArray2D(int[][] mainArr, final int[][] subArr) {

        for (int i = 0; i < subArr.length; i++) {
            mainArr = addToIntArray2D(mainArr, subArr[i]);
        }

        return mainArr;

    }

    public static int[] addToIntArray(int[] mainArr, int item) {

        int[] cArrayInc = new int[mainArr.length + 1];
        for (int i = 0; i < mainArr.length; i++) {
            cArrayInc[i] = mainArr[i];
        }

        cArrayInc[cArrayInc.length - 1] = item;
        return cArrayInc;

    }

    public static BigInteger[] addToBigIntArray(BigInteger[] mainArr, BigInteger item) {

        BigInteger[] cArrayInc = new BigInteger[mainArr.length + 1];
        for (int i = 0; i < mainArr.length; i++) {
            cArrayInc[i] = mainArr[i];
        }

        cArrayInc[cArrayInc.length - 1] = item;
        return cArrayInc;

    }

    public static int[] addAllToIntArray(int[] mainArr, int[] subArr) {

        for (int i = 0; i < subArr.length; i++) {
            mainArr = addToIntArray(mainArr, subArr[i]);
        }

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

    public static int[] stringToIntArray(String content) {
        char[] contentArr = content.toCharArray();

        int[] intArr = new int[0];
        for (int i = 0; i < contentArr.length; i++) {
            int cv = (int) contentArr[i];
            intArr = addToIntArray(intArr, cv);
        }

        return intArr;

    }

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

            int sumofUP = sumOf(partsArr);
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

            int sumofUP = sumOf(partsArr);
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
        int sum = sumOf(mainArr);
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

        int intAggSum = sumOf(inAggArr);
        int res = intAggSum / mainArr.length;
        return res;

    }

    public static int binarySeqToDecimal(String bseq) throws Exception {

        char[] bsArr = bseq.toCharArray();
        if (StringUtil.isBinarySequence(bsArr) == 0) {
            throw new Exception("Not a binary sequence");
        }

        var bsArrOrd = StringUtil.reverse(bsArr);
        int dsum = 0;
        int prevun = 1;
        for (int i = 0; i < bsArrOrd.length; i++) {

            char cc = bsArrOrd[i];
            prevun = i > 0 ? 2 * prevun : prevun;
            if (cc == '1') {
                dsum += prevun;
            }

        }

        return dsum;

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

    public static int sumOf(char[] numCharArr) {
        return sumOf(charArrayToIntArray(numCharArr));
    }

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

    public static int[] generateNumArrByRange(final int size, final int from, final int dir, final int progfactor) {

        int[] finalArr = new int[0];
        int inc = from;

        int last = dir > 0 ? size : 0;
        int eol = 0;
        do {

            finalArr = addToIntArray(finalArr, progfactor == 0 ? 0 : inc);
            if (dir > 0) {
                inc = progfactor > 1 ? inc + progfactor : ++inc;
                if (inc >= last) {
                    eol = 1;
                }
            } else {
                inc = progfactor > 1 ? inc - progfactor : --inc;
                if (inc <= last) {
                    eol = 1;
                }
            }

        } while (eol == 0);

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
        int[] reverseArr = new int[0];

        for (int i = currentArr.length - 1; i >= 0; i--) {
            reverseArr = addToIntArray(reverseArr, currentArr[i]);
        }

        return reverseArr;
    }

    public static int[][] reverse(int[][] currentArr) {
        int[][] reverseArr = new int[0][];

        for (int i = currentArr.length - 1; i >= 0; i--) {
            reverseArr = addToIntArray2D(reverseArr, currentArr[i]);
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