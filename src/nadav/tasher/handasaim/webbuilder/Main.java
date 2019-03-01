package nadav.tasher.handasaim.webbuilder;

import nadav.tasher.handasaim.webbuilder.appcore.AppCore;
import nadav.tasher.handasaim.webbuilder.appcore.components.Schedule;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {
    private static final String schedulePage = "http://handasaim.co.il/2018/08/31/%D7%9E%D7%A2%D7%A8%D7%9B%D7%AA-%D7%95%D7%A9%D7%99%D7%A0%D7%95%D7%99%D7%99%D7%9D-2/";
    private static final String homePage = "http://handasaim.co.il/";
    private static final String source = "/nadav/tasher/handasaim/webbuilder/resources/";
    private static final String scheduleName = "GitHub Schedule";
    private static final File scheduleFileXLSX = new File(System.getProperty("user.dir"), "schedule.xlsx");
    private static final File scheduleFileXLS = new File(System.getProperty("user.dir"), "schedule.xls");
    private static JSONObject result = new JSONObject();

    public static void main(String[] args) {
        if (args.length > 0) {
            File outputFolder = new File(args[0]);
            if (outputFolder.exists()) {
                if (outputFolder.isDirectory()) {
                    try {
                        emptyDirectory(outputFolder);
                        copyResources(outputFolder);
                        result.put("success_resources", true);
                        try {
                            Schedule schedule = getSchedule(getScheduleLink());
                            if (schedule != null) {
                                JSONObject injectableJSON = schedule.toJSON();
                                write(new File(new File(outputFolder, "javascript"), "schedule.js"), ("var schedule = " + injectableJSON.toString() + ";"));
                                write(new File(new File(outputFolder, "files"), "schedule.json"), (injectableJSON.toString()));
                            }
                            result.put("success_schedule", schedule != null);
                        } catch (IOException e) {
                            e.printStackTrace();
                            result.put("success_schedule", false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        result.put("success_resources", false);
                    }
                }
                result.put("is_directory", outputFolder.isDirectory());
            }
            result.put("directory_exists", outputFolder.exists());
        }
        result.put("enough_args", args.length > 0);
        System.out.println(result.toString());
    }

    private static void emptyDirectory(File directory) {
        File[] list = directory.listFiles();
        if (list != null) {
            for (File f : list) delete(f);
        }
    }

    private static void delete(File f) {
        if (!f.getName().startsWith(".")) {
            if (f.isDirectory()) {
                File[] list = f.listFiles();
                if (list != null) {
                    for (File f1 : list) delete(f1);
                }
            }
            f.delete();
        }
    }

    private static void write(File file, String contents) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(contents);
        fileWriter.flush();
        fileWriter.close();
    }

    private static Schedule getSchedule(String link) {
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
                schedule = AppCore.getSchedule(scheduleFileXLSX, link);
            } else {
                schedule = AppCore.getSchedule(scheduleFileXLS, link);
            }
            schedule = Schedule.Builder.fromSchedule(schedule).setName(scheduleName).build();
            return schedule;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void copyResources(File output) throws Exception {
        if (!output.exists()) {
            Files.createDirectories(output.toPath());
        }
        CodeSource src = Main.class.getProtectionDomain().getCodeSource();
        if (src != null) {
            URL jar = src.getLocation();
            if (jar.toString().endsWith(".jar")) {
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                ZipEntry e;
                while ((e = zip.getNextEntry()) != null) {
                    String name = e.getName();
                    if (name.contains(source.substring(1))) {
                        copyJAR(name, new File(output, name.replaceAll(source.substring(1), "")));
                    }
                }
            } else {
                copyIDE(new File(Main.class.getResource(source).toURI()), output);
            }
        } else {
            throw new Exception("Failed To Copy Resources");
        }
    }

    private static void copyJAR(String input, File output) throws Exception {
        if (input.endsWith("/")) {
            Files.createDirectories(output.toPath());
        } else {
            InputStream in = Main.class.getClassLoader().getResourceAsStream(input);
            if (output.exists())
                Files.delete(output.toPath());
            Files.createFile(output.toPath());
            Files.write(output.toPath(), in.readAllBytes());
            in.close();
        }
    }

    private static void copyIDE(File file, File output) throws IOException {
        if (file.isFile()) {
            Files.copy(file.toPath(), output.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else {
            File[] list = file.listFiles();
            Files.createDirectories(output.toPath());
            if (list != null) {
                for (File f : list) {
                    copyIDE(f, new File(output, f.getName()));
                }
            }
        }
    }

    private static String getScheduleLink() {
        return AppCore.getLink(schedulePage, homePage, null);
    }

    private static String basicSearch(String s) {
        return "(" + s + "(|.+);)";
    }
}
