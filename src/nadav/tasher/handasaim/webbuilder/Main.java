package nadav.tasher.handasaim.webbuilder;

import nadav.tasher.handasaim.webbuilder.appcore.AppCore;
import nadav.tasher.handasaim.webbuilder.appcore.components.Schedule;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.regex.Pattern;

public class Main {
    private static final String schedulePage = "http://handasaim.co.il/2018/08/31/%D7%9E%D7%A2%D7%A8%D7%9B%D7%AA-%D7%95%D7%A9%D7%99%D7%A0%D7%95%D7%99%D7%99%D7%9D-2/";
    private static final String homePage = "http://handasaim.co.il/";
    private static final File scheduleFileXLSX = new File(System.getProperty("user.dir"), "schedule.xlsx");
    private static final File scheduleFileXLS = new File(System.getProperty("user.dir"), "schedule.xls");
    private static JSONObject result = new JSONObject();
    private final String topColor = "#456789";

    public static void main(String[] args) {
        if (args.length > 0) {
            String currentLink = getScheduleLink();
            Schedule schedule = downloadSchedule(currentLink);
            if (schedule != null) {
            }
            result.put("schedule_is_null", schedule == null);
        }
        result.put("enough_args", args.length > 0);
        System.out.println(result.toString());
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
}
