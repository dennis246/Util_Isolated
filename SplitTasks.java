package harp.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

/**
 * @note experimental utility methods
 * @author Dennis Thomas
 */

public class SplitTasks {

    public static int splitTasksMinCount = 800;
    public static int splitTasksMaxCount = 2600;
    private String currentStringFragment = "";
    private String[] stringAggArr = new String[0];

    private char[][] char2DAggArr_gpA8743 = new char[0][];
    private char[] currCharArrFragment_gpA8743 = new char[0];

    private char[][][] char3DAggArr_gpB3478 = new char[0][][];
    private char[][] currChar2DArrFragment_gpB3478 = new char[0][];

    private Object[] objectAggArr_gpD348u = new Object[0];
    private Object outputObject = null;

    public static enum SplitTasksMethods {
        StringTasksProcess, CharArrTasksProcess, yTasksProcess
    }

    public enum MethodType {
        Default, Custom
    }

    public static enum CharArrTasksMethodParamSeq {
        Default, Custom
        // default is first as content with range
        // custom is when first param is content
    }

    public synchronized Object processFor(SplitTasksMethods assignedMethod, LinkedHashMap<String, Object> paramSeq) {

        try {

            if (assignedMethod.equals(SplitTasksMethods.StringTasksProcess)) {
                return stringTasksProcessGo(paramSeq);
            } else if (assignedMethod.equals(SplitTasksMethods.CharArrTasksProcess)) {
                return charArrTasksProcessGo(paramSeq);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    synchronized Object stringTasksProcessGo(Object paramSeq) throws Exception {
        // applicable only for long string with method param 0

        var paramsMap = (LinkedHashMap<String, Object>) paramSeq;
        var targetMethodClass = (Class<?>) paramsMap.get("TargetMethodClass");
        String targetMethodName = (String) paramsMap.get("TargetMethodName");
        Object[] targetMethodParams = (Object[]) paramsMap.get("TargetMethodParams");
        Object targetMethodObject = targetMethodClass.getDeclaredConstructor();
        var targetMethodParamsTypesArr = (Class<?>[]) paramsMap.get("TargetMethodParamsTypes");

        Method method = targetMethodClass.getMethod(targetMethodName, targetMethodParamsTypesArr);
        var TargetMethodOutput = paramsMap.get("TargetMethodOutputClass");

        if (method == null) {
            throw new Exception("Invalid method description for stringTasksProcess");
        }

        if (TargetMethodOutput == null
                || (!TargetMethodOutput.equals(String.class) && !TargetMethodOutput.equals(String[].class))) {
            throw new Exception("output description either unspecified or invalid");
        }

        String content = null;
        if (TargetMethodOutput.equals(String.class)) {
            // String content = ((String[]) paramsMap.get("TargetMethodParams"))[0];
            content = (String) targetMethodParams[0];
        }

        String finalContent = "";
        // TODO: adjust dynamically
       

        int parts = 0;
        Double partlen = 0D;
        //int remainingLen = 0;
        if (content.length() < splitTasksMinCount || content.length() < splitTasksMaxCount) {
            parts = 0;
            partlen = Double.valueOf(content.length());
        } else {
            parts = splitTasksMaxCount;
            partlen = (content.length() * 1.000D) / (parts * 1.000D);

            if (content.length() % parts > 0) {
                //remainingLen = content.length() - partlen.intValue();
                parts += 1;
            }
        }

        int mainSeekPos = 0;
        int endSeekPos = partlen.intValue();

        for (int i = 0; i <= parts; i++) {

            if ((content.length() - mainSeekPos) < partlen) {
                currentStringFragment = content.substring(mainSeekPos);
            } else {
                currentStringFragment = content.substring(mainSeekPos, endSeekPos);
            }

            Object[] targetMethodParamsModArr = targetMethodParams;
            targetMethodParamsModArr[0] = currentStringFragment;

            // var curlen = currentStringFragment.length();

            var ract = new Runnable() {
                @Override
                public void run() {

                    try {
                        // TODO need of method spec descriptor!?
                        outputObject = method.invoke(targetMethodObject, targetMethodParamsModArr);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }

                }

            };

            ract.run();

            if (TargetMethodOutput == null || TargetMethodOutput.equals(String.class)) {
                String curSection = (String) outputObject;
                stringAggArr = StringUtil.add(stringAggArr, curSection);
            } else if (TargetMethodOutput.equals(char[].class)) {
                char[] curSection = (char[]) outputObject;
                char2DAggArr_gpA8743 = StringUtil.addToCharArray2D(char2DAggArr_gpA8743, curSection);
            }

            mainSeekPos = endSeekPos;
            endSeekPos += partlen;

            if (endSeekPos > content.length()) {
                endSeekPos = content.length();
            }

        }

        // final agg by type
        if (TargetMethodOutput == null || TargetMethodOutput.equals(String.class)) {
            // var swl = spwrkrsAggArr.length;
            finalContent = String.join("", stringAggArr);
            return finalContent;
        } else if (TargetMethodOutput == null || TargetMethodOutput.equals(String[].class)) {
            return stringAggArr;
        }

        return null;

    }

    synchronized Object charArrTasksProcessGo(Object paramSeq) throws Exception {

        try {
            // as per CharArrTasksMethodParamSeq
            // output can be variable as per invoking method req
            var paramsMap = (LinkedHashMap<String, Object>) paramSeq;
            var targetMethodClass = (Class<?>) paramsMap.get("TargetMethodClass");
            String targetMethodName = (String) paramsMap.get("TargetMethodName");
            Object[] targetMethodParams = (Object[]) paramsMap.get("TargetMethodParams");
            Object targetMethodObject = targetMethodClass.getDeclaredConstructor();
            var targetMethodParamsTypesArr = (Class<?>[]) paramsMap.get("TargetMethodParamsTypes");
            var targetMethodParamsSeq = (CharArrTasksMethodParamSeq) paramsMap.get("TargetMethodParamsSeq");

            Method method = targetMethodClass.getMethod(targetMethodName, targetMethodParamsTypesArr);
            var TargetMethodOutput = paramsMap.get("TargetMethodOutputClass");

            if (method == null) {
                throw new Exception("Invalid method description for stringTasksProcess");
            }

            if (TargetMethodOutput == null || (!TargetMethodOutput.equals(char[].class) &&
                    !TargetMethodOutput.equals(String[].class)
                    && !TargetMethodOutput.equals(char[][].class)
                    && !TargetMethodOutput.equals(char[][][].class))) {
                throw new Exception("output description either unspecified or invalid");
            }

            // String content = ((String[]) paramsMap.get("TargetMethodParams"))[0];
            var mainCharArr = (char[]) targetMethodParams[0];

            int parts = 0;
            Double partlen = 0D;
            //int remainingLen = 0;
            if (mainCharArr.length < splitTasksMinCount || mainCharArr.length < splitTasksMaxCount) {
                parts = 0;
                partlen = Double.valueOf(mainCharArr.length);
            } else {
                parts = splitTasksMaxCount;
                partlen = (mainCharArr.length * 1.000D) / (parts * 1.000D);
                if (mainCharArr.length % parts > 0) {
                    //remainingLen = mainCharArr.length - partlen.intValue();
                    parts += 1;
                }
            }

            int mainSeekPos = 0;
            int endSeekPos = partlen.intValue();

            for (int i = 0; i <= parts; i++) {

                // if ((mainCharArr.length - mainSeekPos) < partlen) {
                // currentStringFragment = content.substring(mainSeekPos);
                // } else {
                // currentStringFragment = content.substring(mainSeekPos, endSeekPos);
                // }
                // char[] currentCharArrFragment = StringUtil.extractByIndexRange(mainCharArr,
                // mainSeekPos, endSeekPos);

                Object[] targetMethodParamsModArr = targetMethodParams;
                if (targetMethodParamsSeq.equals(CharArrTasksMethodParamSeq.Default)) {
                    targetMethodParamsModArr[1] = mainSeekPos;
                    targetMethodParamsModArr[2] = endSeekPos;
                }

                // var curlen = currentStringFragment.length();
                var ract = new Runnable() {
                    @Override
                    public void run() {

                        try {
                            // TODO need of method spec descriptor!?
                            outputObject = method.invoke(targetMethodObject, targetMethodParamsModArr);

                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }

                    }

                };

                ract.run();

                if (TargetMethodOutput.equals(char[][].class)) {
                    char2DAggArr_gpA8743 = StringUtil.addToCharArray2D(char2DAggArr_gpA8743, (char[]) outputObject);
                } else if (TargetMethodOutput.equals(char[][][].class)) {
                    char3DAggArr_gpB3478 = StringUtil.addToCharArray3D(char3DAggArr_gpB3478, (char[][]) outputObject);
                } else if (TargetMethodOutput.equals(String[].class)) {

                }
                // char2DAggArr = StringUtil.addToCharArray2D(char2DAggArr, (char[])
                // outputObject);
                // }

                // update indexes for default seq
                mainSeekPos = endSeekPos;
                endSeekPos += partlen;

                if (endSeekPos > mainCharArr.length) {
                    endSeekPos = mainCharArr.length;
                }

            }

            // final agg by type
            if (TargetMethodOutput.equals(String.class)) {
                // var swl = spwrkrsAggArr.length;
                // return new String char2DAggArr);
                throw new Exception("unimplemented method within charArrTasksProc");
            } else if (TargetMethodOutput.equals(char[][].class)) {
                return char2DAggArr_gpA8743;
            } else if (TargetMethodOutput.equals(char[][][].class)) {
                return char3DAggArr_gpB3478;
            } else if (TargetMethodOutput.equals(String[].class)) {
                return StringUtil.charArray2DToStringArray(char2DAggArr_gpA8743);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }
}
