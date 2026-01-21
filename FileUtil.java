package harp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import harp.util.MapUtil.SeekPosExclusionCriteria;

/**
 * @note experimental utility methods
 * @author Dennis Thomas
 */

public class FileUtil {

    public enum ResourceType {
        html, json, xml, pdf, docx, xls, png, jpg, mp3, mp4, avi, mpeg
    }

    static byte[] opArr = null;
    static byte[] currArrFrag = null;

    static int mainSeekPos = 0;
    static int endSeekPos = 0;

    public static synchronized void writeBytesToFile(byte[] contentBytes, String floca) {

        try {

            File afile = new File(floca);
            OutputStream os = new FileOutputStream(afile);
            os.write(contentBytes);
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static synchronized void stringToFile(String content, String floca) {

        try {

            File afile = new File(floca);
            OutputStream os = new FileOutputStream(afile);
            os.write(content.getBytes());
            os.close();
            os.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static synchronized void stringToFile(String content, String floca, Charset charset) {
        try {

            File afile = new File(floca);
            OutputStream os = new FileOutputStream(afile);
            os.write(content.getBytes(charset));
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String fileToString(String floca) {

        String content = null;
        try {

            File afile = new File(floca);
            InputStream is = new FileInputStream(afile);
            byte[] abs = is.readAllBytes();
            System.out.println("data size : " + abs.length);
            content = new String(abs, Charset.forName("UTF8"));
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;

    }

    public static String fileToString(String floca, Charset charset) {

        if (charset == null) {
            charset = Charset.defaultCharset();
        }

        String content = null;
        try {

            File afile = new File(floca);
            InputStream is = new FileInputStream(afile);
            byte[] rbArr = is.readAllBytes();
            // byte[] abs = is.readNBytes(is.available());
            // byte[] rbArr = is.readNBytes(rbcount);
            content = new String(rbArr, charset);
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;

    }

    public static byte[] fileToBytesWhole(String floca) throws Exception {

        File aFile = new File(floca);
        if (aFile.exists()) {
            Long fsize = aFile.length();
            return fileToBytes(floca, fsize);
        } else {
            throw new Exception("Invalid path or file doesn't exist");
        }

    }

    public static byte[] fileToBytes(String floca, Long arrLen) {

        byte[] opArr = null;
        int exceedsF = 0;
        int remaining = 0;
        int cpUpto = 0;
        int cpSize = 0;
        if ((exceedsF = arrLen > Integer.MAX_VALUE ? arrLen.intValue() / Integer.MAX_VALUE
                : 0) == 1) {
            remaining = arrLen.intValue() - Integer.MAX_VALUE;
            exceedsF += 1;
            cpSize = Integer.MAX_VALUE;
        } else {
            cpSize = arrLen.intValue();
        }

        for (int i = 0; i <= exceedsF; i++) {

            byte[] currArr = fread(floca, cpSize, i == 0 ? 0 : cpUpto);

            cpSize = i != exceedsF ? Integer.MAX_VALUE : remaining;
            cpUpto += cpSize;

            if (exceedsF == 0) {
                return currArr;
            } else {
                System.arraycopy(currArr, 0, opArr, 0, cpSize);
            }

        }

        return opArr;

    }

    public static byte[] fread(String floca, int cpSize, int from) {

        byte[] currArr = null;
        try {

            File afile = new File(floca);
            InputStream is = new FileInputStream(afile);
            is.skipNBytes(from > 0 ? from - 1 : from);
            currArr = is.readNBytes(cpSize);
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return currArr;
    }

    public static void splitToFileParts(String filepath, String destpath) {
        splitToFileParts0(filepath, destpath);
    }

    public static void splitToFiles(String filepath) {
        String desloca = filepath.substring(0, filepath.lastIndexOf(".")) + "_" + LocalDate.now().toString();
        splitToFileParts0(filepath, desloca);
    }

    private static void splitToFileParts0(String floca, String desloca) {

        var t1 = System.currentTimeMillis();

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

            // : nt axlim = opArr.length - 100;
            // byte[] axArr = new byte[100];
            // for (int mi = 0, ax = axlim; ax < opArr.length; mi++, ax++) {
            // axArr[mi] = opArr[ax];
            // }
            // var strAx = new String(axArr);
            // System.out.println(strAx);

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

                FileUtil.writeBytesToFile(currArrFrag, desloca + "_p" + (i) + ".txt");

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

    }

    public static boolean isValidType(String resPath) {

        var restypes = Set.of(ResourceType.values());
        var restypeext = resPath.substring(resPath.lastIndexOf("."));

        for (var crow : restypes) {
            if (crow.name().equals(restypeext)) {
                return true;
            }
        }

        return false;
        // if (restypes.contains(restypeext)) {
        // return true;
        // } else {
        // return false;
        // }
    }

    @Deprecated
    public static byte[] trimResourceArr(Map<String, Object> resInfo) {
        
        byte[] mainArr = (byte[]) resInfo.get("mainArr");
        byte[] finalArr = new byte[0];
        int thsld = (int) resInfo.get("imgWidth");
        int zalpix = -1;
        int zomeix = -1;
        int zcount = 0;
        for (int i = 0; i < mainArr.length; i++) {

            if (mainArr[i] == 0) {
                ++zcount;
                zalpix = i;
            } else {

                if (zcount > 0) {
                    zcount = 0;
                    zalpix = -1;
                }

            }

            if (zcount >= thsld) {

            }

        }

        return mainArr;
    }

    public static Map<String, Object> validateFile(String resPath) {

        var fileInfo = new LinkedHashMap<String, Object>();
        var restypes = Set.of(ResourceType.values());
        var restypeext = resPath.substring(resPath.lastIndexOf(".") + 1).toLowerCase();

        for (var crow : restypes) {
            if (crow.name().equals(restypeext)) {
                // return true;
                fileInfo.put("extension", restypeext);
                break;
            }
        }

        File afile = new File(resPath);
        if (afile.exists()) {

        }

        return fileInfo;

    }

    public static String[] findLinksFromHTML(String mainfpath) {

        try {
 
            String content = fileToString(mainfpath);
            var intLinksArr = MapUtil.sliceToParts(content, new String[] { "href=\"", "src=\"" },
                    new String[] { "\"\srel", "\"" },
                    SeekPosExclusionCriteria.excludeFirst, SeekPosExclusionCriteria.excludeLast, new String[] {},
                    new String[] {});

            String mfDirPath = mainfpath.substring(0, mainfpath.lastIndexOf("\\"));
            String[] finalQPathArr = new String[0];
            String curfpath = mfDirPath;
            File mfile = new File(mainfpath);

            for (int i = 0; i < intLinksArr.length; i++) {
                curfpath = mfDirPath;
                String cfilename = intLinksArr[i].substring(intLinksArr[i].lastIndexOf("/") + 1,
                        intLinksArr[i].length());
                if (intLinksArr[i].startsWith("..")) {

                    var splitArr = intLinksArr[i].split("/");
                    var bkupSteps = 0;
                    for (int ik = 0; ik < splitArr.length; ik++) {
                        if (splitArr[ik].equals("..")) {
                            ++bkupSteps;
                        }
                    }

                    if (bkupSteps > 0) {

                        for (int bk = 0; bk < bkupSteps; bk++) {
                            curfpath = curfpath.substring(0, curfpath.lastIndexOf("\\"));
                        }

                        mfile = FileUtil.findFile(curfpath, cfilename);
                        if (mfile == null) {
                            // var x = 29;
                            continue;
                        }
                        finalQPathArr = StringUtil.add(finalQPathArr, mfile.getAbsolutePath());
                    } else {
                        curfpath = mainfpath.substring(0, mainfpath.lastIndexOf("\\")) + cfilename;
                    }

                }

            }

            return finalQPathArr;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public static File findFile(String frompath, String targetfilename) {

        File afile = new File(frompath);
        if (afile.exists()) {
            if (afile.isDirectory()) {
                for (File frow : afile.listFiles()) {
                    var resfile = findFile(frow.getAbsolutePath(), targetfilename);
                    if (resfile != null) {
                        return resfile;
                    }
                }
            } else {
                if (afile.getName().equals(targetfilename)) {
                    return afile;
                }
            }
        }

        return null;

    }

}
