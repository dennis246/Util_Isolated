package harp.util;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @note experimental utility methods
 * @author Dennis Thomas
 */

public class ContainerUtil {

    public enum MatchCriteria {
        First, Last, All, Any
    }

    public static <E extends Object> E[] defrag(E[] arr, int actsize) {
        E[] anewArr = (E[]) Array.newInstance(arr.getClass().getComponentType(), 0);

        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] != null) {
                anewArr = add(arr, arr[i]);
            }
        }

        return anewArr;
    }

    public static Integer[] defrag(Integer[] arr, int actsize) {
        Integer[] anewArr = new Integer[actsize];

        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] != 0) {
                // vcount += 1;
                anewArr[i] = arr[i];
            }
        }

        return anewArr;
    }

    public static Integer[] fullCopy(Integer[] arr) {
        Integer[] cpArr = new Integer[arr.length];

        for (int i = 0; i <= arr.length - 1; i++) {
            cpArr[i] = arr[i];
        }

        return cpArr;

    }

    public static <E extends Object> E[] removeItem(E[] arr, E[] narr, E item) {
        // narr to be init before method call
        // E[] narr = new E[arr.length - 1];
        try {
            for (int i = 0, pi = 0; i < arr.length; i++) {

                if (arr[i].equals(item)) {
                    continue;
                } else {
                    narr[pi] = arr[i];
                    pi++;
                }

            }
        } catch (Exception e) {
            System.out.print(e.toString());
        }
        return narr;
    }

    public static Integer[] removeItem(Integer[] arr, int item) {

        Integer[] narr = new Integer[arr.length - 1];
        try {
            for (int i = 0, pi = 0; i < arr.length; i++) {
                if (arr[i] == item) {
                    continue;
                } else {
                    narr[pi] = arr[i];
                    pi++;
                }
            }
        } catch (Exception e) {
            System.out.print(e.toString());
        }
        return narr;
    }

    public static int[] removeItem(int[] arr, int item) {

        int[] narr = new int[arr.length - 1];
        try {
            int itemFoundInd = 0;
            for (int i = 0, pi = 0; i < arr.length; i++) {
                if (arr[i] == item && itemFoundInd == 0) {
                    ++itemFoundInd;
                    continue;
                } else {
                    narr[pi] = arr[i];
                    pi++;
                }
            }
        } catch (Exception e) {
            System.out.print(e.toString());
        }
        return narr;
    }

    public static char[] removeItem(char[] arr, char item) {

        char[] narr = new char[arr.length - 1];
        try {
            for (int i = 0, pi = 0; i < arr.length; i++) {
                if (arr[i] == item) {
                    continue;
                } else {
                    narr[pi] = arr[i];
                    pi++;
                }
            }
        } catch (Exception e) {
            System.out.print(e.toString());
        }
        return narr;
    }

    public static char[] removeItem(char[] arr, int targetIndex) {

        char[] narr = new char[arr.length - 1];
        try {
            for (int i = 0, pi = 0; i < arr.length; i++) {
                if (i == targetIndex) {
                    continue;
                } else {
                    narr[pi] = arr[i];
                    pi++;
                }
            }
        } catch (Exception e) {
            System.out.print(e.toString());
        }
        return narr;
    }

    public static char[][] removeItem(char[][] arr, int targetIndex) {

        char[][] narr = new char[arr.length - 1][];
        try {
            for (int i = 0, pi = 0; i < arr.length; i++) {
                if (i == targetIndex) {
                    continue;
                } else {
                    narr[pi] = arr[i];
                    pi++;
                }
            }
        } catch (Exception e) {
            System.out.print(e.toString());
        }
        return narr;
    }

    public static int minOfArray(Integer[] e) {

        int min = 0;
        try {
            min = e[0];
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

    public static int minOfArray(int[] e) {

        int min = 0;
        try {
            min = e[0];
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

    public static int maxOfArray(Integer[] e) {

        int max = 0;
        try {
            max = e[0];
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

        int max = 0;
        try {
            max = e[0];
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

    public static char[] addToCharArray(char[] target, char item) {
        char[] cArray = target;
        char[] cArrayInc = new char[cArray.length + 1];
        for (int i = 0; i < cArray.length; i++) {
            cArrayInc[i] = cArray[i];
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

    public static int[] addToIntArray(int[] target, Integer item) {
        int[] cArray = (int[]) target;
        int[] cArrayInc = new int[cArray.length + 1];
        for (int i = 0; i < cArray.length; i++) {
            cArrayInc[i] = cArray[i];
        }
        // cArrayInc = cArray;
        cArrayInc[cArrayInc.length - 1] = item;

        return cArrayInc;

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

    @SuppressWarnings("unchecked")
    public static <E> E[] addAt(E[] mainArr, int targetiIndex, E item) throws Exception {

        E[] mainArrInc = (E[]) Array.newInstance(mainArr.getClass().getComponentType(), mainArr.length + 1);

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

    public static <E> E[] remove(E[] target, int from) throws Exception {

        if (target == null || from > target.length - 1 || from < 0) {
            throw new Exception("invalid index for method char[] removeFrom(char[] arr, int targetIndex)");
        }

        E[] cArray = (E[]) target;
        var cArrayInc = Arrays.copyOf(target, target.length - 1, target.getClass());
        for (int i = 0; i < cArray.length; i++) {

            if (i == from) {
                continue;
            }

            cArrayInc[i] = cArray[i];
        }

        return (E[]) cArrayInc;

    }

    @SuppressWarnings("unchecked")
    private static Object[] initArray(Object[] target, Class cc) {
        return new Object[0];
    }

    public static <E> E[] addAll(E[] mainArr, E[] subArr) {
        for (int i = 0; i < subArr.length; i++) {
            mainArr = add(mainArr, subArr[i]);
        }

        return mainArr;
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
                    } else if (item instanceof File) {

                        var crowf = (File) arr[i];
                        var itemf = (File) item;
                        if (ignoreCase == 1) {
                            if (crowf.getAbsoluteFile().getName().toLowerCase()
                                    .equals(itemf.getAbsoluteFile().getName().toLowerCase())) {
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
            } else if (item instanceof File) {
                currentRow = ((File) currentRow).getAbsoluteFile().getName().toLowerCase();
                targetItem = ((File) item).getAbsoluteFile().getName().toLowerCase();
            }

            if (currentRow.equals(targetItem)) {
                foundAt = i;
                if (matchCriteria.equals(MatchCriteria.First)) {
                    return foundAt;
                }
            }

        }

        return foundAt;

    }

    public static char[] removeFrom(char[] arr, int targetIndex) throws Exception {

        if (arr == null || targetIndex > arr.length - 1 || targetIndex < 0) {
            throw new Exception("invalid index for method char[] removeFrom(char[] arr, int targetIndex)");
        }

        char[] newArr = new char[0];
        for (int i = 0; i < arr.length; i++) {

            if (i == targetIndex) {
                continue;
            }
            newArr = addToCharArray(newArr, arr[i]);

        }

        return newArr;
    }

    public static int[] addAllToIntArray(int[] mainArr, int[] subArr) {
        for (int i = 0; i < subArr.length; i++) {
            mainArr = addToIntArray(mainArr, subArr[i]);
        }

        return mainArr;
    }

    public static int hasInt(int[] arr, int item) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == item) {
                return 1;
            }
        }
        return 0;
    }

    public static <E extends Object> int contains(E[] mainList, String item) {

        for (var part : mainList) {
            // var res = equate(part.toCharArray(), item.toCharArray());
            // if (res == 1) {
            // return 1;
            // }
        }

        return 0;

    }

    public static int equateObjectFields(Object obj1, Object obj2) {

        try {
            int eqc = 0;
            int nnfc = 0;
            for (Field field : obj1.getClass().getFields()) {

                field.setAccessible(true);

                if (field.get(obj1) != null) {
                    ++nnfc;
                    if (field.get(obj1).toString() == field.get(obj2).toString()) {
                        ++eqc;
                    }

                }

                field.setAccessible(false);

            }

            if (eqc == nnfc) {
                return 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;

    }

    private static int validateSortable(Object currentObject) throws Exception {
        /*
         * Class[] interfaces;
         * if(currentObject instanceof Array){
         * } else if(currentObject instanceof List){
         * }
         */

        var interfaces = currentObject.getClass().getInterfaces();
        int sortableImplExists = 0;
        for (var crow : interfaces) {

            if (crow.getName().equals("Sortable")) {
                sortableImplExists = 1;
                break;
            }
        }

        if (sortableImplExists == 0) {
            throw new Exception("Entity doesn't implment Sortable interface");
        }

        return 1;
    }

    private static <E> int validateContainer(E currentObject) throws Exception {
        var superClass = currentObject.getClass().componentType().getSuperclass();
        /*
         * Class superClass;
         * if (currentObject instanceof Array) {
         * 
         * superClass = currentObject.getClass().componentType().getSuperclass();
         * 
         * } else if (currentObject instanceof List) {
         * }
         */

        /*
         * for (var crow : contructors) {
         * 
         * if (crow.getName().equals("Sortable")) {
         * containerConstructprExists = 1;
         * break;
         * }
         * }
         */

        /*
         * int containerConstructprExists = 0;
         * if (containerConstructprExists == 0) {
         * throw new Exception("Entity doesn't container Container Constructor");
         * }
         */

        if (superClass.getSimpleName().equals("Container")) {
            return 1;
        }

        return 0;

    }

    public static List<?> sortBy(List<?> objList, String sortByFieldName, SortMode asc) {
        try {

            Field[] fields = objList.get(0).getClass().getDeclaredFields();

            int fieldExists = 0;
            for (var field : fields) {

                if (field.getName().toLowerCase().equals(sortByFieldName.toLowerCase())) {
                    fieldExists = 1;
                    break;
                }

            }

            if (fieldExists == 0) {
                throw new Exception("No such field found for current object list");
            }

            // System.out.println(res);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return objList;
    }

    public static <E extends Container> E[] sort(E[] objArr, SortMode sortMode) throws Exception {
        return purgeSort(objArr, sortMode);
    }

    public static <E extends Container> E[] purgeSort(E[] mainArr, SortMode sortOrder) throws Exception {

        E[] sortedArr = (E[]) Array.newInstance(mainArr.getClass().getComponentType(), mainArr.length);

        for (int i = 0, pi = 0; i < mainArr.length; i++) {

            if (sortedArr[sortedArr.length - 1] != null) {
                break;
            }

            E pe = null;
            if (sortOrder.equals(SortMode.Desc)) {
                pe = maxOfArray(mainArr);
            } else {
                pe = minOfArray(mainArr);
            }

            mainArr = removeItem(mainArr, pe);
            sortedArr[pi] = pe;
            pi++;
            --i;
        }

        return sortedArr;

    }

    @SuppressWarnings("unchecked")
    private static <E> E[] removeItem(E[] arr, E item) {
        E[] narr = (E[]) Array.newInstance(arr.getClass().getComponentType(), arr.length - 1);
        try {
            for (int i = 0, pi = 0; i < arr.length; i++) {
                if (arr[i] == item) {
                    continue;
                } else {
                    narr[pi] = arr[i];
                    pi++;
                }
            }
        } catch (Exception e) {
            System.out.print(e.toString());
        }
        return narr;
    }

    private static <E extends Container> E minOfArray(E[] mainArr) throws Exception {

        E lastMinObj = mainArr[0];
        int min = 0;
        try {
            min = mainArr[0].sequenceNumber;
            for (int j = 0; j < mainArr.length; j++) {

                if (mainArr[j].sequenceNumber < min) {
                    min = mainArr[j].sequenceNumber;
                    lastMinObj = mainArr[j];
                }
            }
        } catch (Exception e) {
            System.out.print(e.toString());
        }

        return lastMinObj;

    }

    private static <E extends Container> E maxOfArray(E[] mainArr) throws Exception {

        E lastMaxObj = mainArr[0];
        int max = 0;
        try {
            max = mainArr[0].sequenceNumber;
            for (int j = 0; j < mainArr.length; j++) {

                if (mainArr[j].sequenceNumber > max) {
                    max = mainArr[j].sequenceNumber;
                    lastMaxObj = mainArr[j];
                }
            }
        } catch (Exception e1) {
            System.out.print(e1.toString());
        }

        return lastMaxObj;
    }

    public static <E> E initiateWithRandomValues(Class<?> currentClass) {
        E object = null;
        try {

            object = (E) currentClass.getConstructor().newInstance();

            Field[] fields = currentClass.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getType().equals(Date.class)) {
                    field.set(object, Date.from(Instant.now()));
                } else if (field.getType().equals(String.class)) {
                    field.set(object, StringUtil.randomIDGenerator(10, true, true, false));
                } else if (field.getType().equals(Double.class) || field.getType().equals(Integer.class)) {
                    field.set(object, MathUtil.randomNumberGenerator(0));
                }

                field.setAccessible(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;
    }

    @SuppressWarnings("unchecked")
    public static <E extends Object> E[] extractByIndexRange(final E[] currentArr, final Integer from, final Integer to,
            final E[] filterChars)
            throws Exception {

        E[] finalArr = (E[]) Array.newInstance(currentArr.getClass().getComponentType(), 0);

        if (from < 0 || to < 0 || to < from || from > to || from > currentArr.length || to > currentArr.length) {
            throw new Exception("invalid range values");
        }

        for (int i = 0; i < currentArr.length; i++) {

            if (has(filterChars, currentArr[i], 0) == 1) {
                continue;
            }

            if (i >= from && i < to + 1) {
                finalArr = add(finalArr, currentArr[i]);
            }

            if (i == to) {
                break;
            }
        }

        return finalArr;

    }

    public static byte[] mergeToMainArr(byte[] mainArr,
            int[] mainArrInPtArr,
            byte[][] sub2DArr, int[] subArrFromIxArr, int[] subArrToIxArr)
            throws Exception {

        // merge others into first
        int finalSize = mainArr.length;
        byte[] finalArr = null;

        int maxMainArrInPt = MathUtil.maxOfArray(mainArrInPtArr);

        int maxSubArrFromIx = MathUtil.maxOfArray(subArrFromIxArr);
        // int maxSubArrToIx = MathUtil.maxOfArray(subArrToIxArr);

        // int[] sub2DArrSizeArr = new int[0];
        // for (int i = 0; i < sub2DArr.length; i++) {

        // if (sub2DArr[i].length > mainArr.length) {
        // // ignores subarr range pref to main
        // }
        // //sub2DArrSizeArr = MathUtil.addToIntArray(sub2DArrSizeArr,
        // sub2DArr[i].length);
        // }

        // int maxBrSize = MathUtil.maxOfArray(sub2DArrSizeArr);

        try {

            if (maxMainArrInPt > mainArr.length - 1) {
                throw new Exception("Invalid entry index for main array for mergeToMainArr");
            }

            if (subArrFromIxArr.length != sub2DArr.length ||
                    subArrToIxArr.length != sub2DArr.length) {
                throw new Exception("Invalid range indexes");
            }

            // sub arr ix items comparison
            for (int cf = 0; cf < subArrFromIxArr.length; cf++) {

                if (subArrFromIxArr[cf] > subArrToIxArr[cf]) {
                    throw new Exception("Invalid range indexes");
                }
            }

            // if (secondArr == null) {
            // throw new Exception("Invalid second array for cparray0");
            // }

            // if (mainArrFrom < 0 || mainArrFrom >= mainArr.length) {
            // throw new Exception("Invalid index of main array for cparray0");
            // }

            // // if (mainArr.length < secondArr.length) {
            // // throw new Exception("Invalid src array for cparray0");
            // // }

            // if (secArrFrom < 0 || secArrFrom > secondArr.length) {
            // throw new Exception("Invalid index of second array for cparray0");
            // }

            // if (secArrFrom > mainArrFrom && secArrFrom > mainArr.length) {
            // throw new Exception("Invalid index of second array for cparray0");
            // }

            // if (mainArrFrom > mainArr.length - 1) {
            // finalSize += secondArr.length; // c gaps
            // }

            if (maxSubArrFromIx > mainArr.length - 1) {
                // finalSize += secondArr.length; // c gaps
                int ixgp = (mainArr.length - 1) - maxSubArrFromIx;
                finalSize += ixgp > 0 ? ixgp : 0;

            }

            finalArr = new byte[finalSize];

            for (int j = 0; j < mainArr.length; j++) {
                finalArr[j] = mainArr[j];
            }

            for (int k = 0; k < sub2DArr.length; k++) {

                for (int m = mainArrInPtArr[k], pi = subArrFromIxArr[k]; m < subArrToIxArr[k] - subArrFromIxArr[k]; m++, pi++) {
                    try {
                        finalArr[m] = sub2DArr[k][pi];
                        if (m == finalSize - 1) {
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return finalArr;

    }

    public static byte[] mergeToMainArr(byte[] mainArr,
            byte[][] sub2DArr, int[] subArrFromIxArr, int[] subArrToIxArr)
            throws Exception {

        // merge others into first
        int finalSize = mainArr.length;
        byte[] finalArr = null;

        int maxSubArrFromIx = MathUtil.maxOfArray(subArrFromIxArr);
        // int maxSubArrToIx = MathUtil.maxOfArray(subArrToIxArr);

        // int[] sub2DArrSizeArr = new int[0];
        // for (int i = 0; i < sub2DArr.length; i++) {

        // if (sub2DArr[i].length > mainArr.length) {
        // // ignores subarr range pref to main
        // }
        // //sub2DArrSizeArr = MathUtil.addToIntArray(sub2DArrSizeArr,
        // sub2DArr[i].length);
        // }

        // int maxBrSize = MathUtil.maxOfArray(sub2DArrSizeArr);

        try {

            if (subArrFromIxArr.length != sub2DArr.length ||
                    subArrToIxArr.length != sub2DArr.length) {
                throw new Exception("Invalid range indexes");
            }

            // sub arr ix items comparison
            for (int cf = 0; cf < subArrFromIxArr.length; cf++) {

                if (subArrFromIxArr[cf] > subArrToIxArr[cf]) {
                    throw new Exception("Invalid range indexes");
                }
            }

            // if (secondArr == null) {
            // throw new Exception("Invalid second array for cparray0");
            // }

            // if (mainArrFrom < 0 || mainArrFrom >= mainArr.length) {
            // throw new Exception("Invalid index of main array for cparray0");
            // }

            // // if (mainArr.length < secondArr.length) {
            // // throw new Exception("Invalid src array for cparray0");
            // // }

            // if (secArrFrom < 0 || secArrFrom > secondArr.length) {
            // throw new Exception("Invalid index of second array for cparray0");
            // }

            // if (secArrFrom > mainArrFrom && secArrFrom > mainArr.length) {
            // throw new Exception("Invalid index of second array for cparray0");
            // }

            // if (mainArrFrom > mainArr.length - 1) {
            // finalSize += secondArr.length; // c gaps
            // }

            if (maxSubArrFromIx > mainArr.length - 1) {
                // finalSize += secondArr.length; // c gaps
                int ixgp = (mainArr.length - 1) - maxSubArrFromIx;
                finalSize += ixgp > 0 ? ixgp : 0;

            }

            finalArr = new byte[finalSize];

            for (int j = 0; j < mainArr.length; j++) {
                finalArr[j] = mainArr[j];
            }

            for (int k = 0; k < sub2DArr.length; k++) {

                for (int m = 0, pi = subArrFromIxArr[k]; m < subArrToIxArr[k] - subArrFromIxArr[k]; m++, pi++) {
                    try {
                        finalArr[m] = sub2DArr[k][pi];
                        if (m == finalSize - 1) {
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return finalArr;
    }



    public static byte[] addToByteArray(byte[] mainArr, byte item) {
        // char[] cArray = (char[]) target;
        byte[] cArrayInc = new byte[mainArr.length + 1];
        for (int i = 0; i < mainArr.length; i++) {
            cArrayInc[i] = mainArr[i];
        }
        // cArrayInc = cArray;
        cArrayInc[cArrayInc.length - 1] = item;
        return cArrayInc;

    }

    public static byte[] addAllToByteArray(byte[] mainArr, byte[] subArr) {

        for (int i = 0; i < subArr.length; i++) {
            mainArr = addToByteArray(mainArr, subArr[i]);
        }

        return mainArr;

    }




}
