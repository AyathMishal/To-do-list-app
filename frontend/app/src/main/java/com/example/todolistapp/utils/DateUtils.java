package com.example.todolistapp.utils;

import com.example.todolistapp.adapters.DateAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateUtils {

    private static final SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("dd MMM", Locale.getDefault());
    private static final SimpleDateFormat DAY_NUMBER_FORMAT = new SimpleDateFormat("dd", Locale.getDefault());
    private static final SimpleDateFormat DAY_NAME_FORMAT = new SimpleDateFormat("EEE", Locale.getDefault());

    public static List<DateAdapter.DateModel> getPresentAndFutureDates(int numberOfDays) {
        List<DateAdapter.DateModel> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < numberOfDays; i++) {
            String dayNumber = DAY_NUMBER_FORMAT.format(calendar.getTime());
            String dayName = DAY_NAME_FORMAT.format(calendar.getTime());
            String fullDate = DISPLAY_DATE_FORMAT.format(calendar.getTime());

            dates.add(new DateAdapter.DateModel(dayNumber, dayName, fullDate));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return dates;
    }

    public static String getTodayFormatted() {
        return DISPLAY_DATE_FORMAT.format(Calendar.getInstance().getTime());
    }

    public static boolean isTaskExpired(String taskDateStr, String timeRangeStr) {
        if (taskDateStr == null || taskDateStr.trim().isEmpty()) {
            return false;
        }

        Calendar taskCal = parseDateString(taskDateStr);

        if (taskCal == null) {
            return false;
        }

        // Truncate today and taskCal to midnight for date comparison
        Calendar todayMidnight = Calendar.getInstance();
        todayMidnight.set(Calendar.HOUR_OF_DAY, 0);
        todayMidnight.set(Calendar.MINUTE, 0);
        todayMidnight.set(Calendar.SECOND, 0);
        todayMidnight.set(Calendar.MILLISECOND, 0);

        Calendar taskMidnight = (Calendar) taskCal.clone();
        taskMidnight.set(Calendar.HOUR_OF_DAY, 0);
        taskMidnight.set(Calendar.MINUTE, 0);
        taskMidnight.set(Calendar.SECOND, 0);
        taskMidnight.set(Calendar.MILLISECOND, 0);

        // Only expire tasks if the task DATE is strictly before Today (yesterday or older)
        return taskMidnight.before(todayMidnight);
    }

    private static Calendar parseDateString(String dateStr) {
        String cleanStr = dateStr.trim();
        String[] formats = new String[]{"dd MMM yyyy", "dd MMM", "yyyy-MM-dd", "d MMM"};

        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                Date date = sdf.parse(cleanStr);
                if (date != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);

                    // If year wasn't specified, default to current year
                    if (!format.contains("yyyy")) {
                        cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                    }
                    return cal;
                }
            } catch (ParseException ignored) {
            }
        }
        return null;
    }
}
