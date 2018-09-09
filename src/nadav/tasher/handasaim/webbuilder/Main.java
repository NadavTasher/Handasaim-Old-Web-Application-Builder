package nadav.tasher.handasaim.webbuilder;

import nadav.tasher.handasaim.webbuilder.appcore.AppCore;
import nadav.tasher.handasaim.webbuilder.appcore.components.Classroom;
import nadav.tasher.handasaim.webbuilder.appcore.components.Schedule;
import nadav.tasher.handasaim.webbuilder.appcore.components.Subject;
import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.regex.Pattern;

public class Main {
    /*
        Shortcuts:
        n = name
        ns = names
        sjs = subjects
        h = hour
        bm = beginning minute
        em = ending minute
        d = description
        g = grade
     */
    public static final String compilerVersion = "0.1";
    private static final String schedulePage = "http://handasaim.co.il/2018/08/31/%D7%9E%D7%A2%D7%A8%D7%9B%D7%AA-%D7%95%D7%A9%D7%99%D7%A0%D7%95%D7%99%D7%99%D7%9D-2/";
    private static final String homePage = "http://handasaim.co.il/";
    private static final File scheduleFileXLSX = new File(System.getProperty("user.dir"), "schedule.xlsx");
    private static final File scheduleFileXLS = new File(System.getProperty("user.dir"), "schedule.xls");
    //    private static final File sourceHTML = new File(Main.class.getResource("/index.html").getFile());
    private static JSONObject result = new JSONObject();

    public static void main(String[] args) {
        if (args.length > 0) {
            String currentLink = getScheduleLink();
            Schedule schedule = downloadSchedule(currentLink);
            if (schedule != null) {
                File outputFile = new File(args[0]);
                JSONObject injectableJSON = new JSONObject();
                JSONArray classroomsJSON = new JSONArray();
                JSONArray teachersJSON = new JSONArray();
                JSONArray messagesJSON = new JSONArray();
                for (String m : schedule.getMessages()) {
                    messagesJSON.put(StringEscapeUtils.escapeJava(m));
                }
                for (Classroom c : schedule.getClassrooms()) {
                    JSONObject classroom = new JSONObject();
                    classroom.put("n", StringEscapeUtils.escapeJava(c.getName()));
                    classroom.put("g", c.getGrade());
                    JSONArray subjectsJSON = new JSONArray();
                    for (Subject s : c.getSubjects()) {
                        JSONObject subject = new JSONObject();
                        JSONArray teacherNames = new JSONArray();
                        for (String n : s.getTeacherNames()) {
                            teacherNames.put(StringEscapeUtils.escapeJava(n));
                        }
                        subject.put("n", StringEscapeUtils.escapeJava(s.getName()));
//                        subject.put("d", StringEscapeUtils.escapeJava(s.getDescription()));
                        subject.put("bm", s.getBeginingMinute());
                        subject.put("em", s.getEndingMinute());
                        subject.put("h", s.getSchoolHour());
                        subject.put("ns", teacherNames);
                        subjectsJSON.put(subject);
                    }
                    classroom.put("sjs", subjectsJSON);
                    classroomsJSON.put(classroom);
                }
                injectableJSON.put("day", schedule.getDay());
                injectableJSON.put("messages", messagesJSON);
                injectableJSON.put("classrooms", classroomsJSON);
                if (outputFile.getParentFile().exists()) {
                    try {
                        String rawSource = read(Main.class.getResourceAsStream("/nadav/tasher/handasaim/webbuilder/resources/index.html"));
                        // Load JS Replacements
                        rawSource = rawSource.replaceFirst(basicSearch("var schedule"), "var schedule = " + injectableJSON.toString() + ";");
                        rawSource = rawSource.replaceAll("compilerVersion", "WebC v" + compilerVersion);
                        rawSource = rawSource.replaceAll("appCoreVersion", "AppCore v" + AppCore.APPCORE_VERSION);
                        FileWriter fileWriter = new FileWriter(outputFile);
                        fileWriter.write(rawSource);
                        fileWriter.flush();
                        fileWriter.close();
                        result.put("success", true);
                    } catch (IOException e) {
                        e.printStackTrace();
                        result.put("success", false);
                    }
                }
                result.put("output_directory_exists", outputFile.getParentFile().exists());
            }
            result.put("schedule_is_null", schedule == null);
        }
        result.put("enough_args", args.length > 0);
        System.out.println(result.toString());
    }

    private static String read(InputStream is) throws IOException {
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));
        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();
        while (line != null) {
            sb.append(line).append("\n");
            line = buf.readLine();
        }
        return sb.toString();
    }

    private static Schedule downloadSchedule(String link) {
        try {
            URL website = new URL(link);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos;
            if (link.endsWith(".xlsx")) {
                fos = new FileOutputStream(scheduleFileXLSX);
            } else {
                fos = new FileOutputStream(scheduleFileXLS);
            }
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            Schedule schedule;
            if (link.endsWith(".xlsx")) {
                schedule = AppCore.getSchedule(scheduleFileXLSX);
            } else {
                schedule = AppCore.getSchedule(scheduleFileXLS);
            }
            return schedule;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getScheduleLink() {
        String file = null;
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
            // Fallback Search At Home Page
            if (file == null) {
                Document documentFallback = Jsoup.connect(homePage).get();
                Elements elementsFallback = documentFallback.select("a");
                for (int i = 0; (i < elementsFallback.size() && file == null); i++) {
                    String attribute = elementsFallback.get(i).attr("href");
                    if ((attribute.endsWith(".xls") || attribute.endsWith(".xlsx")) && Pattern.compile("(/.[^a-z]+\\..+)$").matcher(attribute).find()) {
                        file = attribute;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static String basicSearch(String s) {
        return "(" + s + "(|.+);)";
    }
}
