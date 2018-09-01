package nadav.tasher.handasaim.webbuilder.appcore.components;

import java.util.ArrayList;
import java.util.Calendar;

public class Schedule {
    public static final int TYPE_REGULAR = 0;
    public static final int TYPE_PREDICTED = 1;
    private ArrayList<Classroom> classrooms;
    private ArrayList<Teacher> teachers;
    private ArrayList<String> messages;
    private String name, day, date, origin, signature;
    private int type;

    private Schedule() {
    }

    public String getSignature() {
        return signature;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getDay() {
        return day;
    }

    public String getOrigin() {
        return origin;
    }

    public ArrayList<Teacher> getTeachers() {
        return teachers;
    }

    public ArrayList<Classroom> getClassrooms() {
        return classrooms;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public static class Builder {
        // Schedule Assembler
        private static final int COMPARE_CORRECT = 0;
        private static final int COMPARE_ADD_NAME = 1;
        private static final int COMPARE_ADD_SUBJECT = 2;
        private static final int COMPARE_INCORRECT = 3;
        private ArrayList<Classroom> classrooms = new ArrayList<>();
        private ArrayList<Teacher> teachers = new ArrayList<>();
        private ArrayList<String> messages = new ArrayList<>();
        private String name = "Generic Schedule", date = "Unknown Date", origin = "Unknown Origin", signature = "Unsigned", day = "?";
        private int type = TYPE_REGULAR;

        public Builder(int type) {
            this.type = type;
        }

        public static Builder fromSchedule(Schedule schedule) {
            Builder builder = new Builder(schedule.type);
            builder.classrooms = schedule.classrooms;
            builder.messages = schedule.messages;
            builder.day = schedule.day;
            builder.date = schedule.date;
            builder.name = schedule.name;
            builder.origin = schedule.origin;
            return builder;
        }

        public void addMessage(String message) {
            messages.add(message);
        }

        public void addClassroom(Classroom classroom) {
            classrooms.add(classroom);
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public Schedule build() {
            assemble();
            return generate();
        }

        private void assemble() {
            assembleTeachers();
            assembleSignature();
        }

        private int compareTeachers(Teacher scanTeacher, String teacherName, Subject subject) {
            boolean subjectFound = false;
            for (Subject scanSubject : scanTeacher.getSubjects()) {
                if (scanSubject.getName().equals(subject.getName())) {
                    subjectFound = true;
                }
            }
            if (scanTeacher.getName().equals(teacherName)) {
                if (teacherName.length() > 2) {
                    return COMPARE_CORRECT;
                } else {
                    if (subjectFound)
                        return COMPARE_CORRECT;
                }
            } else {
                if ((scanTeacher.getName().length() > teacherName.length() && scanTeacher.getName().contains(teacherName))) {
                    if (!scanTeacher.getNames().contains(teacherName))
                        return COMPARE_ADD_NAME;
                    else
                        return COMPARE_CORRECT;
                } else if ((scanTeacher.getName().length() < teacherName.length() && teacherName.contains(scanTeacher.getName()))) {
                    if (subjectFound)
                        return COMPARE_CORRECT;
                }
            }
            return COMPARE_INCORRECT;
        }

        private void assembleSignature() {
            StringBuilder signatureBuilder = new StringBuilder();
            signatureBuilder.append("Signed By Schedule.Builder At ");
            Calendar calendar = Calendar.getInstance();
            signatureBuilder.append(calendar.get(Calendar.HOUR_OF_DAY));
            signatureBuilder.append(":");
            signatureBuilder.append(calendar.get(Calendar.MINUTE));
            signature = signatureBuilder.toString();
        }

        private void assembleTeachers() {
            // Changed Back To Regular For To Add The Teachers To The Subjects
            for (int a = 0; a < classrooms.size(); a++) {
                Classroom currentClassroom = classrooms.get(a);
                for (int b = 0; b < currentClassroom.getSubjects().size(); b++) {
                    Subject currentSubject = currentClassroom.getSubjects().get(b);
                    for (String currentTeacher : currentSubject.getTeacherStrings()) {
                        if (!currentSubject.getDescription().isEmpty() && !currentSubject.getName().isEmpty() && !currentTeacher.isEmpty()) {
                            // Look For The Same Teacher In The Existing List
                            boolean foundTeacher = false;
                            for (Teacher scanTeacher : teachers) {
                                if (foundTeacher) break;
                                switch (compareTeachers(scanTeacher, currentTeacher, currentSubject)) {
                                    case COMPARE_INCORRECT:
                                        break;
                                    case COMPARE_ADD_NAME:
                                        scanTeacher.addName(currentTeacher);
                                    case COMPARE_CORRECT:
                                        scanTeacher.addSubject(currentSubject);
                                        currentSubject.addTeacher(scanTeacher);
                                        foundTeacher = true;
                                        break;
                                }
                            }
                            if (!foundTeacher) {
                                // Fallback For Scan, Add A New Teacher To The List.
                                Teacher newTeacher = new Teacher();
                                newTeacher.addName(currentTeacher);
                                newTeacher.addSubject(currentSubject);
                                currentSubject.addTeacher(newTeacher);
                                teachers.add(newTeacher);
                            }
                        }
                    }
                }
            }
        }

        private Schedule generate() {
            Schedule schedule = new Schedule();
            schedule.type = type;
            schedule.messages = messages;
            schedule.name = name;
            schedule.day = day;
            schedule.date = date;
            schedule.origin = origin;
            schedule.signature = signature;
            schedule.classrooms = classrooms;
            schedule.teachers = teachers;
            return schedule;
        }
    }
}
