package harp.util;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

public class TxtClip {

    public static String clipContent(String content) {

        /*
         * String sampleContent = """
         * A program consist of set of instructions which are either variables or
         * functions.\n
         * By default, external variables and functions have the property that all
         * references to the same concept.
         * """;
         */

        int vpWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        int p2cEst = 16;
        int lineWidthEst = vpWidth / 64;
        int bpad = 10;
        int mainSeekPos = 0;

        // System.out.print("width: "+vpWidth);
        char[] sccArr = content.toCharArray();
        char[] line = new char[0];

        List<String> lines = new ArrayList<>();

        int endOfContent = 0;
        int pi = 0, clw = 0;
        do {

            line = ContainerUtil.addToCharArray(line, sccArr[pi]);
            pi++;
            clw++;

            if (clw == lineWidthEst || sccArr[pi] == '\n') {
                // line[pi] = '\n';
                // line = ContainerUtil.addToCharArray(line, '\n');
                lines.add(new String(line));
                line = new char[0];
                clw = 0;

            }

            if (pi == sccArr.length - 1) {
                endOfContent = 1;
            }

        } while (endOfContent != 1);

        var x = String.join("\n", lines);
       // System.out.println(x);

        return x;
    }

}
