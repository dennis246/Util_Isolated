package harp.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TxtRep {

    public enum TxtRepPrintMode {
        Console, File
    }

    private static BasicRow[] gridModel;
    private static Object[] reportDataList;
    private static String[] contentAggArr;

    private static TxtRepPrintMode currentPrintMode;

    private static String destfileAt;

    public static void sampleRep() {

    }

    public static void buildTextReport(Object reportInfo, TxtRepPrintMode printMode, String fileDest) {

        try {
            currentPrintMode = printMode;
            destfileAt = fileDest;
            printSections(reportInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static void printSections(Object reportInfo) {

        gridModel = new BasicRow[0];
        var reportInfoMap = (LinkedHashMap<String, Object>) reportInfo;
        var reportContainer = reportInfoMap.get("reportData");

        try {

            int maxRL = 0;
            reportDataList = (LinkedHashMap<String, Object>[]) reportContainer;

            int rowIndex = 0;
            int[] maxCellWidths = new int[0];

            for (var item : reportDataList) {

                BasicCell[] detailCells = new BasicCell[0];
                BasicCell[] headerCells = new BasicCell[0];

                var crowL1Map = (LinkedHashMap) item;

                if (rowIndex == 0) {
                    maxCellWidths = new int[crowL1Map.keySet().size()];
                }

                var crowL1MapKeys = crowL1Map.keySet();
                int cellIndex = 0;

                for (var crowL2 : crowL1MapKeys) {

                    var crowL2Id = (String) crowL2;
                    String itemval = "";
                    if (crowL1Map.get(crowL2Id) instanceof String) {
                        itemval = (String) crowL1Map.get(crowL2Id);
                    } else if (crowL1Map.get(crowL2Id) instanceof char[]) {
                        itemval = new String((char[]) crowL1Map.get(crowL2Id));
                    }

                    detailCells = ContainerUtil.add(detailCells, new BasicCell(6, 6, itemval, itemval.length()));

                    if (rowIndex == 0) {

                        var firstCell = new BasicCell(6, 6, crowL2Id, itemval.length());
                        headerCells = ContainerUtil.add(headerCells,
                                firstCell);
                    }

                    maxCellWidths[cellIndex] = maxCellWidths[cellIndex] == 0 ? itemval.length()
                            : Math.max(maxCellWidths[cellIndex], itemval.length());

                    cellIndex++;

                }

                if (rowIndex == 0) {
                    gridModel = ContainerUtil.add(gridModel, new BasicRow(rowIndex, headerCells));
                }

                gridModel = ContainerUtil.add(gridModel, new BasicRow(rowIndex, detailCells));
                rowIndex++;

            }

            maxRL += MathUtil.sumOf(maxCellWidths) + 10;

            contentAggArr = new String[0];

            // max column width adjustment
            for (int i = 0; i < gridModel.length; i++) {

                BasicCell[] cells = gridModel[i].getCells();
                for (int j = 0; j < cells.length; j++) {

                    if (maxCellWidths[j] % 2 == 0) {
                        maxCellWidths[j] -= 1;
                    }

                    cells[j].setTextWidth(maxCellWidths[j] > 30 ? 30 : maxCellWidths[j]);
                    // cells[j].setTextWidth(maxCellWidths[j]);
                }

                if (currentPrintMode.equals(TxtRepPrintMode.Console)) {
                    printRow(cells, maxRL);
                } else if (currentPrintMode.equals(TxtRepPrintMode.File)) {
                    // printRow(cells, maxRL);
                    printableRow(cells, maxRL);
                }

            }

            if (currentPrintMode.equals(TxtRepPrintMode.File)) {
                var fop = String.join("", contentAggArr);
                FileUtil.stringToFile(fop, destfileAt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void printRow(BasicCell[] cellsH, int rs) {

        try {

            // int padding_X = 4;
            // int rs = totalRL + (padding_X * items.size());
            int adjustFact = 5;
            line_X(rs + adjustFact);

            boolean overflowExists = false;

            System.out.print("\n|");

            for (int x = 0; x < cellsH.length; x++) {

                int tw = cellsH[x].getTextWidth();
                String currentText = cellsH[x].getText();

                if (cellsH[x].getText().length() > tw) {
                    currentText = cellsH[x].getText().substring(0, tw);
                    cellsH[x].setOverflowInd(true);
                    overflowExists = true;
                }
                /*
                 * int additionalSpace = tw - cellsH[x].getText().length() -
                 * cellsH[x].getLeftPadding()
                 * - cellsH[x].getRightPadding();
                 */
                int additionalSpace = cellsH[x].getTextWidth() - cellsH[x].getText().length();
                additionalSpace = additionalSpace < 0 ? 0 : additionalSpace;
                if (cellsH[x].getAlign().equals(CellAlign.right)) {
                    basicPadding(additionalSpace + cellsH[x].getLeftPadding());
                    System.out.print(currentText + (cellsH[x].getText().length() % 2 != 0 ? "\s" : ""));
                    basicPadding(cellsH[x].getRightPadding());
                    System.out.print("|");
                } else if (cellsH[x].getAlign().equals(CellAlign.left)) {
                    basicPadding(cellsH[x].getLeftPadding());
                    System.out.print(currentText + (cellsH[x].getText().length() % 2 != 0 ? "\s" : ""));
                    basicPadding(cellsH[x].getRightPadding() + additionalSpace);
                    System.out.print("|");
                } else if (cellsH[x].getAlign().equals(CellAlign.center)) {
                    basicPadding(cellsH[x].getLeftPadding() + additionalSpace / 2);
                    System.out.print(currentText + (cellsH[x].getText().length() % 2 != 0 ? "\s" : ""));
                    basicPadding(cellsH[x].getRightPadding() + additionalSpace / 2);
                    System.out.print("|");
                }

            }

            if (overflowExists) {
                handleCellsOverflow(cellsH);
            }

            System.out.print("\n");

            line_X(rs + adjustFact);
            System.out.print("\n");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void handleCellsOverflow(BasicCell[] cellsH) {

        System.out.print("\n|");
        boolean overflowExists = false;

        for (int x = 0; x < cellsH.length; x++) {

            int tw = cellsH[x].getTextWidth();

            int additionalSpace = cellsH[x].getTextWidth() - cellsH[x].getText().length();
            if (!cellsH[x].getOverflowInd()) {
                // printSpaces(additionalSpace+tw+cellsH[x].getLeftPadding()+cellsH[x].getRightPadding());

                if (cellsH[x].getAlign().equals(CellAlign.right)) {
                    basicPadding(additionalSpace + cellsH[x].getLeftPadding());
                    // System.out.print(currentText + (cellsH[x].getText().length() % 2 != 0 ?
                    // "\s" : ""));
                    printSpaces(additionalSpace + tw);
                    basicPadding(cellsH[x].getRightPadding());
                    System.out.print("|");
                } else if (cellsH[x].getAlign().equals(CellAlign.left)) {
                    basicPadding(cellsH[x].getLeftPadding());
                    // System.out.print(currentText + (cellsH[x].getText().length() % 2 != 0 ?
                    // "\s" : ""));
                    printSpaces(additionalSpace + tw);
                    basicPadding(cellsH[x].getRightPadding() + additionalSpace);
                    System.out.print("|");
                } else if (cellsH[x].getAlign().equals(CellAlign.center)) {

                    basicPadding(cellsH[x].getLeftPadding() + additionalSpace / 2);
                    // System.out.print(currentText + (cellsH[x].getText().length() % 2 != 0 ?
                    // "\s" : ""));
                    printSpaces(additionalSpace + tw);
                    basicPadding(cellsH[x].getRightPadding() + additionalSpace / 2);
                    System.out.print("|");
                }

            } else {
                String overflowedText = "";

                int parts = cellsH[x].getText().length() / cellsH[x].getTextWidth();

                if (cellsH[x].getOverflowDX() == 0) {

                    overflowedText = cellsH[x].getText().substring(tw);
                    if (overflowedText.length() > cellsH[x].getTextWidth()) {
                        cellsH[x].setOverflowDX(cellsH[x].getOverflowDX() + 1);
                    }

                } else {
                    int stopfrom = tw * cellsH[x].getOverflowDX();
                    int stopto = stopfrom + cellsH[x].getTextWidth();

                    if (stopto > cellsH[x].getText().length()) {
                        stopto = cellsH[x].getText().length();
                    }

                    overflowedText = cellsH[x].getText().substring(stopfrom, stopto);
                }

                if (cellsH[x].getOverflowDX() < (parts)) {
                    cellsH[x].setOverflowDX(cellsH[x].getOverflowDX() + 1);
                    overflowExists = true;
                } else {
                    overflowExists = false;
                }

                int ctstopto = overflowedText.length() < cellsH[x].getTextWidth()
                        ? overflowedText.length()
                        : cellsH[x].getTextWidth();
                String currentText = overflowedText.substring(0, ctstopto);

                if (overflowedText.length() < cellsH[x].getTextWidth()) {
                    currentText = currentText + addSpaces(cellsH[x].getTextWidth() - overflowedText.length());
                }

                // int additionalSpace = cellsH[x].getTextWidth() -
                // cellsH[x].getText().length();
                if (cellsH[x].getAlign().equals(CellAlign.right)) {
                    basicPadding(additionalSpace + cellsH[x].getLeftPadding());
                    System.out.print(currentText + (cellsH[x].getText().length() % 2 != 0 ? "\s" : ""));
                    basicPadding(cellsH[x].getRightPadding());
                    System.out.print("|");
                } else if (cellsH[x].getAlign().equals(CellAlign.left)) {
                    basicPadding(cellsH[x].getLeftPadding());
                    System.out.print(currentText + (cellsH[x].getText().length() % 2 != 0 ? "\s" : ""));
                    basicPadding(cellsH[x].getRightPadding() + additionalSpace);
                    System.out.print("|");
                } else if (cellsH[x].getAlign().equals(CellAlign.center)) {
                    basicPadding(cellsH[x].getLeftPadding() + additionalSpace / 2);
                    System.out.print(currentText + (cellsH[x].getText().length() % 2 != 0 ? "\s" : ""));
                    basicPadding(cellsH[x].getRightPadding() + additionalSpace / 2);
                    System.out.print("|");
                }

            }

        }

        if (overflowExists) {
            handleCellsOverflow(cellsH);
        }

    }

    private static void handleCellsOverflow_Printable(BasicCell[] cellsH) {

        contentAggArr = StringUtil.add(contentAggArr, "\n|");
        boolean overflowExists = false;

        for (int x = 0; x < cellsH.length; x++) {

            int tw = cellsH[x].getTextWidth();

            int additionalSpace = cellsH[x].getTextWidth() - cellsH[x].getText().length();
            if (!cellsH[x].getOverflowInd()) {
                // printSpaces(additionalSpace+tw+cellsH[x].getLeftPadding()+cellsH[x].getRightPadding());

                if (cellsH[x].getAlign().equals(CellAlign.right)) {
                    basicPadding_Printable(additionalSpace + cellsH[x].getLeftPadding());
                    // System.out.print(currentText + (cellsH[x].getText().length() % 2 != 0 ?
                    // "\s" : ""));
                    printSpaces_Printable(additionalSpace + tw);
                    basicPadding_Printable(cellsH[x].getRightPadding());
                    contentAggArr = StringUtil.add(contentAggArr, "|");
                } else if (cellsH[x].getAlign().equals(CellAlign.left)) {
                    basicPadding_Printable(cellsH[x].getLeftPadding());
                    // System.out.print(currentText + (cellsH[x].getText().length() % 2 != 0 ?
                    // "\s" : ""));
                    printSpaces_Printable(additionalSpace + tw);
                    basicPadding_Printable(cellsH[x].getRightPadding() + additionalSpace);
                    contentAggArr = StringUtil.add(contentAggArr, "|");
                } else if (cellsH[x].getAlign().equals(CellAlign.center)) {

                    basicPadding_Printable(cellsH[x].getLeftPadding() + additionalSpace / 2);
                    // System.out.print(currentText + (cellsH[x].getText().length() % 2 != 0 ?
                    // "\s" : ""));
                    printSpaces_Printable(additionalSpace + tw);
                    basicPadding_Printable(cellsH[x].getRightPadding() + additionalSpace / 2);
                    contentAggArr = StringUtil.add(contentAggArr, "|");
                }

            } else {
                String overflowedText = "";

                int parts = cellsH[x].getText().length() / cellsH[x].getTextWidth();

                if (cellsH[x].getOverflowDX() == 0) {

                    overflowedText = cellsH[x].getText().substring(tw);
                    if (overflowedText.length() > cellsH[x].getTextWidth()) {
                        cellsH[x].setOverflowDX(cellsH[x].getOverflowDX() + 1);
                    }

                } else {
                    int stopfrom = tw * cellsH[x].getOverflowDX();
                    int stopto = stopfrom + cellsH[x].getTextWidth();

                    if (stopto > cellsH[x].getText().length()) {
                        stopto = cellsH[x].getText().length();
                    }

                    overflowedText = cellsH[x].getText().substring(stopfrom, stopto);
                }

                if (cellsH[x].getOverflowDX() < (parts)) {
                    cellsH[x].setOverflowDX(cellsH[x].getOverflowDX() + 1);
                    overflowExists = true;
                } else {
                    overflowExists = false;
                }

                int ctstopto = overflowedText.length() < cellsH[x].getTextWidth()
                        ? overflowedText.length()
                        : cellsH[x].getTextWidth();
                String currentText = overflowedText.substring(0, ctstopto);

                if (overflowedText.length() < cellsH[x].getTextWidth()) {
                    currentText = currentText + addSpaces(cellsH[x].getTextWidth() - overflowedText.length());
                }

                // int additionalSpace = cellsH[x].getTextWidth() -
                // cellsH[x].getText().length();
                if (cellsH[x].getAlign().equals(CellAlign.right)) {
                    basicPadding_Printable(additionalSpace + cellsH[x].getLeftPadding());
                    contentAggArr = StringUtil.add(contentAggArr,
                            currentText + (cellsH[x].getText().length() % 2 != 0 ? "\s" : ""));
                    basicPadding_Printable(cellsH[x].getRightPadding());
                    System.out.print("|");
                } else if (cellsH[x].getAlign().equals(CellAlign.left)) {
                    basicPadding_Printable(cellsH[x].getLeftPadding());
                    contentAggArr = StringUtil.add(contentAggArr,
                            currentText + (cellsH[x].getText().length() % 2 != 0 ? "\s" : ""));
                    basicPadding_Printable(cellsH[x].getRightPadding() + additionalSpace);
                    System.out.print("|");
                } else if (cellsH[x].getAlign().equals(CellAlign.center)) {
                    basicPadding_Printable(cellsH[x].getLeftPadding() + additionalSpace / 2);
                    contentAggArr = StringUtil.add(contentAggArr,
                            currentText + (cellsH[x].getText().length() % 2 != 0 ? "\s" : ""));
                    basicPadding_Printable(cellsH[x].getRightPadding() + additionalSpace / 2);
                    contentAggArr = StringUtil.add(contentAggArr, "|");
                }

            }

        }

        if (overflowExists) {
            handleCellsOverflow_Printable(cellsH);
        }

    }

    public static void printSpaces(int size) {
        for (int i = 0; i < size; ++i) {
            System.out.print(" ");
        }
    }

    public static void printSpaces_Printable(int size) {
        for (int i = 0; i < size; ++i) {
            contentAggArr = StringUtil.add(contentAggArr, " ");
        }
    }

    public static String addSpaces(int size) {
        String spaceText = "";
        for (int i = 0; i < size; ++i) {
            spaceText = spaceText + "\s";
        }
        return spaceText;
    }

    static List<BasicCell> setColumnSizeAsPerMaxLen(List<BasicCell> cells) {
        Integer[] lens = cells.stream().map(item -> Integer.valueOf(item.getText().length())).toArray(Integer[]::new);
        int ccolumnmaxval = findMaxLen(lens);

        if (ccolumnmaxval % 2 != 0) {
            ccolumnmaxval += 1;
        }

        if (ccolumnmaxval > 40) {
            ccolumnmaxval = 40;
        }

        var maxlValfinal = ccolumnmaxval;
        cells.forEach(item -> {
            item.setTextWidth(maxlValfinal);

            if (item.getText().length() % 2 != 0) {
                item.setText(item.getText() + "\s");
            }

        });
        return cells;
    }

    public static int findMaxLen(Integer[] dataRowLengths) {

        int maxVal = 0;
        for (int i = 0; i < dataRowLengths.length; i++) {
            if (dataRowLengths[i] > maxVal) {
                maxVal = dataRowLengths[i];
            }
        }

        return maxVal;

    }

    public static int findMaxLen(int[] dataRowLengths) {

        int maxVal = 0;
        for (int i = 0; i < dataRowLengths.length; i++) {
            if (dataRowLengths[i] > maxVal) {
                maxVal = dataRowLengths[i];
            }
        }

        return maxVal;

    }

    static void basicPadding(int pf) {

        for (int i = 0; i < pf; i++) {
            System.out.print("\s");
        }

    }

    static void basicPadding_Printable(int pf) {

        for (int i = 0; i < pf; i++) {
            // System.out.print("\s");
            contentAggArr = StringUtil.add(contentAggArr, "\s");
        }

    }

    public static void line_X(int n) {
        System.out.print("\s");
        for (var c = 1; c < n; c++) {

            System.out.print("-");

        }
        // System.out.print("\n");
    }

    public static void line_X_Printable(int n) {
        // System.out.print("\s");
        contentAggArr = StringUtil.add(contentAggArr, "\s");
        for (var c = 1; c < n; c++) {
            if (currentPrintMode.equals(TxtRepPrintMode.Console)) {
                contentAggArr = StringUtil.add(contentAggArr, "-");
            }
        }

    }

    public static void printableRow(BasicCell[] cellsH, int rs) {

        try {

            // int padding_X = 4;
            // int rs = totalRL + (padding_X * items.size());
            int adjustFact = 5;
            line_X_Printable(rs + adjustFact);

            boolean overflowExists = false;
            contentAggArr = StringUtil.add(contentAggArr, "\n");
            // System.out.print("\n|");

            for (int x = 0; x < cellsH.length; x++) {

                int tw = cellsH[x].getTextWidth();
                String currentText = cellsH[x].getText();

                if (cellsH[x].getText().length() > tw) {
                    currentText = cellsH[x].getText().substring(0, tw);
                    cellsH[x].setOverflowInd(true);
                    overflowExists = true;
                }
                /*
                 * int additionalSpace = tw - cellsH[x].getText().length() -
                 * cellsH[x].getLeftPadding()
                 * - cellsH[x].getRightPadding();
                 */
                int additionalSpace = cellsH[x].getTextWidth() - cellsH[x].getText().length();
                additionalSpace = additionalSpace < 0 ? 0 : additionalSpace;
                if (cellsH[x].getAlign().equals(CellAlign.right)) {
                    basicPadding_Printable(additionalSpace + cellsH[x].getLeftPadding());

                    contentAggArr = StringUtil.add(contentAggArr,
                            currentText + (cellsH[x].getText().length() % 2 != 0 ? "\s" : ""));
                    basicPadding_Printable(cellsH[x].getRightPadding());
                    contentAggArr = StringUtil.add(contentAggArr, "|");
                } else if (cellsH[x].getAlign().equals(CellAlign.left)) {
                    basicPadding_Printable(cellsH[x].getLeftPadding());
                    contentAggArr = StringUtil.add(contentAggArr,
                            currentText + (cellsH[x].getText().length() % 2 != 0 ? "\s" : ""));
                    basicPadding_Printable(cellsH[x].getRightPadding() + additionalSpace);
                    contentAggArr = StringUtil.add(contentAggArr, "|");
                } else if (cellsH[x].getAlign().equals(CellAlign.center)) {
                    basicPadding_Printable(cellsH[x].getLeftPadding() + additionalSpace / 2);
                    contentAggArr = StringUtil.add(contentAggArr,
                            currentText + (cellsH[x].getText().length() % 2 != 0 ? "\s" : ""));
                    basicPadding_Printable(cellsH[x].getRightPadding() + additionalSpace / 2);
                    contentAggArr = StringUtil.add(contentAggArr, "|");
                }

            }

            if (overflowExists) {
                handleCellsOverflow_Printable(cellsH);
            }

            contentAggArr = StringUtil.add(contentAggArr, "\n");

            line_X_Printable(rs + adjustFact);
            contentAggArr = StringUtil.add(contentAggArr, "\n");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

class BasicRow {

    enum RowType {
        Header, Detail;
    }

    private int leftPadding = 2;
    private int rightPadding = 2;
    private int topPadding = 2;
    private int bottomPadding = 2;
    private String rowID;
    private int rowIndex;
    private String rowLabel;

    private RowType rowType;
    private BasicCell[] cells;

    public BasicRow(int rowIndex, BasicCell[] cells) {
        try {

            this.rowID = StringUtil.randomIDGenerator(8, true, true, false);
            this.rowIndex = rowIndex;
            this.cells = cells;
            this.rowType = RowType.Detail;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BasicRow() {
        // this.topPadding = topPadding;
    }

    public String getRowID() {
        return rowID;
    }

    public void setRowID(String rowID) {
        this.rowID = rowID;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public String getRowLabel() {
        return rowLabel;
    }

    public void setRowLabel(String rowLabel) {
        this.rowLabel = rowLabel;
    }

    public int getLeftPadding() {
        return leftPadding;
    }

    public void setLeftPadding(int leftPadding) {
        this.leftPadding = leftPadding;
    }

    public int getRightPadding() {
        return rightPadding;
    }

    public void setRightPadding(int rightPadding) {
        this.rightPadding = rightPadding;
    }

    public int getTopPadding() {
        return topPadding;
    }

    public void setTopPadding(int topPadding) {
        this.topPadding = topPadding;
    }

    public int getBottomPadding() {
        return bottomPadding;
    }

    public void setBottomPadding(int bottomPadding) {
        this.bottomPadding = bottomPadding;
    }

    public BasicCell[] getCells() {
        return cells;
    }

    public void setCells(BasicCell[] cells) {
        this.cells = cells;
    }

    public RowType getRowType() {
        return rowType;
    }

    public void setRowType(RowType rowType) {
        this.rowType = rowType;
    }

}

enum CellAlign {
    left, right, center
}

class BasicCell {

    private int leftPadding = 2;
    private int rightPadding = 2;

    private String text;

    private int textWidth;

    private CellAlign align;

    private Boolean overflowInd = false;

    private int overflowDX = 0;

    public Boolean getOverflowInd() {
        return overflowInd;
    }

    public void setOverflowInd(Boolean ind) {
        this.overflowInd = ind;
    }

    public int getOverflowDX() {
        return overflowDX;
    }

    public void setOverflowDX(int dx) {
        this.overflowDX = dx;
    }

    public CellAlign getAlign() {
        return align;
    }

    public void setAlign(CellAlign align) {
        this.align = align;
    }

    public int getTextWidth() {
        return textWidth;
    }

    public void setTextWidth(int textWidth) {
        this.textWidth = textWidth;
    }

    public BasicCell(int leftPadding, int rightPadding, String text, int textWidth) {
        this.leftPadding = leftPadding;
        this.leftPadding = rightPadding;
        this.text = text;
        this.textWidth = textWidth;
        this.align = CellAlign.center;
    }

    public BasicCell(int leftPadding, int rightPadding, String text, int textWidth, CellAlign align) {
        this.leftPadding = leftPadding;
        this.leftPadding = rightPadding;
        this.text = text;
        this.textWidth = textWidth;
        this.align = align;
    }

    public int getLeftPadding() {
        return this.leftPadding;
    }

    public int getRightPadding() {
        return this.rightPadding;
    }

    public String getText() {
        return this.text;
    }

    public void setLeftPadding(int padding) {
        this.leftPadding = padding;
    }

    public void setRightPadding(int padding) {
        this.rightPadding = padding;
    }

    public void setText(String text) {
        this.text = text;
    }

}
