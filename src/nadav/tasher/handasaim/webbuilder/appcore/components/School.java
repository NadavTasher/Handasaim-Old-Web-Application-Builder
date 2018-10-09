package nadav.tasher.handasaim.webbuilder.appcore.components;

public class School {
    private int[] startTimes;

    public School(int[] startTimes) {
        this.startTimes = startTimes;
    }

    public int[] getStartTimes() {
        return startTimes;
    }

    public void setStartTimes(int[] startTimes) {
        this.startTimes = startTimes;
    }

    public int getStartingMinute(Subject subject) {
        return getStartingMinute(subject.getHour());
    }

    public int getStartingMinute(int hour) {
        return (hour < startTimes.length && hour >= 0) ? startTimes[hour] : (startTimes.length > 0) ? startTimes[0] : 0;
    }

    public int getEndingMinute(Subject subject) {
        return getEndingMinute(subject.getHour());
    }

    public int getEndingMinute(int hour) {
        return getStartingMinute(hour) + 45;
    }

    public int getBreakLength(int hour1, int hour2) {
        return getStartingMinute(hour2) - getEndingMinute(hour1);
    }
}
