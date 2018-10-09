package nadav.tasher.handasaim.webbuilder.appcore;

import nadav.tasher.handasaim.webbuilder.appcore.components.Classroom;
import nadav.tasher.handasaim.webbuilder.appcore.components.Schedule;
import nadav.tasher.handasaim.webbuilder.appcore.components.School;
import nadav.tasher.handasaim.webbuilder.appcore.components.Subject;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class AppCore {
    public static double APPCORE_VERSION = 2.0;

    /*
        Note That XSSF Resemmbles XLSX,
        While HSSF Resembles XLS.
        XSSF Is The Newer Format.

        Since 1.8, AppCore is ONLY for essentials (e.g. reading excel, returning a school, fetching a link)
     */

    private static int getReadingRow(Sheet sheet) {
        Cell secondCell = sheet.getRow(0).getCell(1);
        if (!readCell(secondCell).isEmpty()) {
            return 0;
        } else {
            return 1;
        }
    }

    private static int getReadingColumn(Sheet sheet) {
        return 1;
    }

    private static void parseMessages(Schedule.Builder builder, Sheet sheet) {
        try {
            if (sheet.getWorkbook() instanceof HSSFWorkbook) {
                HSSFPatriarch patriarch = (HSSFPatriarch) sheet.createDrawingPatriarch();
                List<HSSFShape> shapes = patriarch.getChildren();
                for (int s = 0; s < shapes.size(); s++) {
                    if (shapes.get(s) instanceof HSSFTextbox) {
                        try {
                            HSSFShape mShape = shapes.get(s);
                            if (mShape != null) {
                                HSSFTextbox mTextShape = (HSSFTextbox) mShape;
                                HSSFRichTextString mString = mTextShape.getString();
                                if (mString != null) {
                                    builder.addMessage(mString.getString());
                                }
                            }
                        } catch (NullPointerException ignored) {
                        }
                    }
                }
            } else {
                XSSFSheet convertedSheet = (XSSFSheet) sheet;
                XSSFDrawing drawing = convertedSheet.createDrawingPatriarch();
                List<XSSFShape> shapes = drawing.getShapes();
                for (int s = 0; s < shapes.size(); s++) {
                    if (shapes.get(s) instanceof XSSFSimpleShape) {
                        try {
                            XSSFSimpleShape mShape = (XSSFSimpleShape) shapes.get(s);
                            if (mShape != null) {
                                if (mShape.getText() != null) {
                                    String mString = mShape.getText();
                                    if (mString != null) {
                                        builder.addMessage(mString);
                                    }
                                }
                            }
                        } catch (NullPointerException ignored) {
                        }
                    }
                }
            }
        } catch (Exception e) {
            builder.addMessage("Failed: Reading Messages");
        }
    }

    private static Sheet getSheet(File f) {
        try {
            if (f.toString().endsWith(".xls")) {
                POIFSFileSystem fileSystem = new POIFSFileSystem(new FileInputStream(f));
                Workbook workBook = new HSSFWorkbook(fileSystem);
                Sheet foundSheet = null;
                for (int s = 0; s < workBook.getNumberOfSheets() && foundSheet == null; s++) {
                    Sheet current = workBook.getSheetAt(s);
                    if (current.getLastRowNum() - 1 > 0) {
                        foundSheet = current;
                    }
                }
                return foundSheet;
            } else {
                XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(f));
                Sheet foundSheet = null;
                for (int s = 0; s < workBook.getNumberOfSheets() && foundSheet == null; s++) {
                    Sheet current = workBook.getSheetAt(s);
                    if (current.getLastRowNum() - 1 > 0) {
                        foundSheet = current;
                    }
                }
                return foundSheet;
            }
        } catch (IOException ignored) {
            return null;
        }
    }

    private static String readCell(Cell cell) {
        if (cell != null) {
            try {
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        return cell.getStringCellValue();
                    case Cell.CELL_TYPE_NUMERIC:
                        return String.valueOf((int) cell.getNumericCellValue());
                    case Cell.CELL_TYPE_BOOLEAN:
                        return String.valueOf(cell.getBooleanCellValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private static void parseClassrooms(Schedule.Builder builder, Sheet sheet) {
        try {
            int readingRow = getReadingRow(sheet);
            int readingColumn = getReadingColumn(sheet);
            int rows = sheet.getLastRowNum();
            int columns = sheet.getRow(readingRow).getLastCellNum();
            for (int c = readingColumn; c < columns; c++) {
                Classroom.Builder classroom = new Classroom.Builder();
                classroom.setName(readCell(sheet.getRow(readingRow).getCell(c)).split(" ")[0]);
                int startRow = readingRow + 1;
                for (int r = startRow; r < rows; r++) {
                    Row row = sheet.getRow(r);
                    String description = readCell(row.getCell(c));
                    if (!description.isEmpty()) {
                        Subject.Builder subject = new Subject.Builder();
                        subject.setName(description.split("\\r?\\n")[0].replaceAll(",", "/"));
                        subject.setNames(new ArrayList<>(Arrays.asList(description.substring(description.indexOf("\n") + 1).trim().split("\\r?\\n")[0].split(","))));
                        subject.setHour(r - startRow);
                        classroom.addSubject(subject);
                    }
                }
                builder.addClassroom(classroom);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getDay(Sheet s) {
        return readCell(s.getRow(0).getCell(0));
    }

    public static Schedule getSchedule(File anyfile) {
        if (anyfile.getName().endsWith(".xlsx") || anyfile.getName().endsWith(".xls")) {
            return getScheduleFromExcel(anyfile);
        } else if (anyfile.getName().endsWith(".json")) {
            // TODO read file
            return Schedule.Builder.fromJSON(new JSONObject()).build();
        }
        return new Schedule.Builder().build();
    }

    private static Schedule getScheduleFromExcel(File excel) {
        Schedule.Builder mBuilder = new Schedule.Builder();
        Sheet sheet = getSheet(excel);
        if (sheet != null) {
            parseClassrooms(mBuilder, sheet);
            parseMessages(mBuilder, sheet);
            mBuilder.setDay(getDay(sheet));
        }
        return mBuilder.build();
    }

    public static Schedule getSchedule(File anyfile, String name, String date, String origin) {
        Schedule.Builder builder = Schedule.Builder.fromSchedule(getSchedule(anyfile));
        builder.setName(name);
        builder.setOrigin(origin);
        builder.setDate(date);
        return builder.build();
    }

    public static String getLink(String schedulePage, String homePage, String githubFallback) {
        String file = null;
        if (file == null) {
            try {
                // Main Search At Schedule Page
                Document document = Jsoup.connect(schedulePage).get();
                Elements elements = document.select("a");
                for (int i = 0; (i < elements.size() && file == null); i++) {
                    String attribute = elements.get(i).attr("href");
                    if (attribute.endsWith(".xls") || attribute.endsWith(".xlsx")) {
                        file = attribute;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (file == null) {
            try {
                // Fallback Search At Home Page
                Document documentFallback = Jsoup.connect(homePage).get();
                Elements elementsFallback = documentFallback.select("a");
                for (int i = 0; (i < elementsFallback.size() && file == null); i++) {
                    String attribute = elementsFallback.get(i).attr("href");
                    //                    Log.i("LinkFallback",attribute);
                    if ((attribute.endsWith(".xls") || attribute.endsWith(".xlsx")) && Pattern.compile("(/.[^a-z]+\\..+)$").matcher(attribute).find()) {
                        file = attribute;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // GitHub Fallback
        if (file == null) {
            file = githubFallback;
        }
        // Return Link
        return file;
    }

    public static School getSchool() {
        return new School(new int[]{465, 510, 555, 615, 660, 730, 775, 830, 875, 930, 975, 1020, 1065});
    }
}
