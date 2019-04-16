package com.example.labskeletest;

import android.database.Cursor;
import android.os.Parcelable;

import java.io.Serializable;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Lab implements Serializable {

    private String room;
    private String schedule;
    ArrayList<String> softwareList = new <String>ArrayList();
    ArrayList<String> classStart = new <String>ArrayList();
    ArrayList<String> classEnd = new <String>ArrayList();
    ArrayList<String> classDay = new <String>ArrayList();
    private int inUseComputers;
    private int totalComputers;
    private int presetTotalComputers;
    private boolean printerStatus;


    Lab(String labRoom) {
        room = labRoom;

        try {
            getItemStatus();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getItemStatus() throws SQLException {

        Cursor data = MainActivity.databaseHelper.getLabStatus(room);
        while(data.moveToNext()){
            String inUse = data.getString(0);
            String total = data.getString(1);
            String preset = data.getString(2);
            inUseComputers = Integer.parseInt(inUse);
            totalComputers = Integer.parseInt(total);
            presetTotalComputers = Integer.parseInt(preset);
        }
        data = MainActivity.databaseHelper.getSoftware(room);
        while(data.moveToNext()){
            if(data.getString(0).equals("Accessibility") || data.getString(0).equals("Accessories") ||
                    data.getString(0).equals("Administrative Tools") || data.getString(0).equals("Maintenance") ||
                    data.getString(0).equals("System Tools") || data.getString(0).equals("x264vfw64")){
                continue;
            }
            softwareList.add(data.getString(0));
        }
        data = MainActivity.databaseHelper.getClassTimes(room);
        while(data.moveToNext()){

            String class_start = data.getString(0);
            String class_end = data.getString(1);
            String class_day = data.getString(3);

            class_start = formatTime(class_start);
            class_end = formatTime(class_end);

            classStart.add(class_start);
            classEnd.add(class_end);
            classDay.add(class_day);
        }
    }

    public String formatTime(String correctFormat) {
        if (!(correctFormat.length() == 6)) {
            String addZero = "0";
            correctFormat = addZero + correctFormat;
        }
        return correctFormat;
    }

    public String getRoom() {
        return room;
    }

    public ArrayList<String> getSoftware() {
        return softwareList;
    }

    public ArrayList<String> getClassStart() {
        return classStart;
    }

    public ArrayList<String> getClassEnd() {
        return classEnd;
    }

    public ArrayList<String> getClassDay() {
        return classDay;
    }

    public boolean checkClassInSession(Calendar currentTime) {
        int day = currentTime.get(Calendar.DAY_OF_WEEK);
        String day_of_week = "";
        switch (day) {
            case Calendar.SUNDAY:
                day_of_week = "S";
                break;
            case Calendar.MONDAY:
                day_of_week = "M";
            case Calendar.TUESDAY:
                day_of_week = "T";
                break;
            case Calendar.WEDNESDAY:
                day_of_week = "W";
                break;
            case Calendar.THURSDAY:
                day_of_week = "R";
                break;
            case Calendar.FRIDAY:
                day_of_week = "F";
                break;
            case Calendar.SATURDAY:
                day_of_week = "S";
                break;
        }
//        System.out.println(day_of_week + "DAY OF WEEK");
        for (int i = 0; i < classEnd.size(); i++) {

            if (classDay.get(i).contains(day_of_week)) {
                try {

                    String trimmedStart = classStart.get(i).substring(0, classStart.get(i).length() - 1);
                    String ampmStart = classStart.get(i).substring(classStart.get(i).length() - 1, classStart.get(i).length());

                    String trimmedEnd = classEnd.get(i).substring(0, classEnd.get(i).length() - 1);
                    String ampmEnd = classEnd.get(i).substring(classEnd.get(i).length() - 1, classEnd.get(i).length());

                    if (ampmStart.equals("p")) {
//                    System.out.println("Before + 12 " + trimmedStart);
                        String firstTwo = trimmedStart.substring(0, 2);
                        int firstTwoNum = Integer.parseInt(firstTwo);
                        if (firstTwoNum != 12) {
                            firstTwoNum = firstTwoNum + 12;
                            String parsed = String.valueOf(firstTwoNum);
                            trimmedStart = parsed + trimmedStart.substring(2);
//                        System.out.println("After + 12 " + trimmedStart);
                        }
                    }
                    if (ampmEnd.equals("p")) {
//                    System.out.println("Before + 12 " + trimmedEnd);
                        String firstTwo = trimmedEnd.substring(0, 2);
                        int firstTwoNum = Integer.parseInt(firstTwo);
                        if (firstTwoNum != 12) {
                            firstTwoNum = firstTwoNum + 12;
                            String parsed = String.valueOf(firstTwoNum);
                            trimmedEnd = parsed + trimmedEnd.substring(2);
//                        System.out.println("After + 12 " + trimmedEnd);
                        }
                    }
                    Date startTime = new SimpleDateFormat("HH:mm").parse(trimmedStart);
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTime(startTime);


                    Date endTime = new SimpleDateFormat("HH:mm").parse(trimmedEnd);
                    Calendar calendar2 = Calendar.getInstance();
                    calendar2.setTime(endTime);


                    String currentTimeStringTest = new SimpleDateFormat("HH:mm").format(new Date());

                    Date checkTime = new SimpleDateFormat("HH:mm").parse(currentTimeStringTest);
                    Calendar calendar3 = Calendar.getInstance();
                    calendar3.setTime(checkTime);


                    if (endTime.compareTo(startTime) < 0) {
                        calendar2.add(Calendar.DATE, 1);
                        calendar3.add(Calendar.DATE, 1);
                    }

                    java.util.Date actualTime = calendar3.getTime();
                    if ((actualTime.after(calendar1.getTime()) ||
                            actualTime.compareTo(calendar1.getTime()) == 0) &&
                            actualTime.before(calendar2.getTime())) {

                        long differenceInMinutes = endTime.getTime() - startTime.getTime();
                        differenceInMinutes= differenceInMinutes/  (60 * 1000) % 60;

                        return true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
    public boolean checkBuildingOpen(Calendar currentTime) {
        int day = currentTime.get(Calendar.DAY_OF_WEEK);
        String day_of_week = "";
        switch (day) {
            case Calendar.SUNDAY:
                day_of_week = "S";
                break;
            case Calendar.MONDAY:
                day_of_week = "M";
            case Calendar.TUESDAY:
                day_of_week = "T";
                break;
            case Calendar.WEDNESDAY:
                day_of_week = "W";
                break;
            case Calendar.THURSDAY:
                day_of_week = "R";
                break;
            case Calendar.FRIDAY:
                day_of_week = "F";
                break;
            case Calendar.SATURDAY:
                day_of_week = "S";
                break;
        }
        ArrayList<String> buildingStart = new ArrayList<>();
        ArrayList<String> buildingClose = new ArrayList<>();
        ArrayList<String> buildingDays = new ArrayList<>();
        if(this.getRoom().contains("CEIT")){
            buildingStart.add("07:00a");
            buildingStart.add("07:00a");
            buildingStart.add("07:00a");
            buildingStart.add("07:00a");
            buildingStart.add("07:00a");
            buildingStart.add("02:00p");
            buildingStart.add("02:00p");

            buildingClose.add("11:59p");
            buildingClose.add("11:59p");
            buildingClose.add("11:59p");
            buildingClose.add("11:59p");
            buildingClose.add("10:00p");
            buildingClose.add("10:00p");
            buildingClose.add("11:59p");

            buildingDays.add("M");
            buildingDays.add("T");
            buildingDays.add("W");
            buildingDays.add("T");
            buildingDays.add("F");
            buildingDays.add("Sat");
            buildingDays.add("Sun");

        }
        else if(this.getRoom().contains("COBA")){

            buildingStart.add("07:30a");
            buildingStart.add("07:30a");
            buildingStart.add("07:30a");
            buildingStart.add("07:30a");
            buildingStart.add("07:30a");


            buildingClose.add("09:00p");
            buildingClose.add("09:00p");
            buildingClose.add("09:00p");
            buildingClose.add("09:00p");
            buildingClose.add("09:00p");


            buildingDays.add("M");
            buildingDays.add("T");
            buildingDays.add("W");
            buildingDays.add("T");
            buildingDays.add("F");


        }


        for (int i = 0; i < buildingStart.size(); i++) {
            if (buildingDays.get(i).contains(day_of_week)) {
                try {

                    String trimmedStart = buildingStart.get(i).substring(0, buildingStart.get(i).length() - 1);
                    String ampmStart = buildingStart.get(i).substring(buildingStart.get(i).length() - 1, buildingStart.get(i).length());

                    String trimmedEnd = buildingClose.get(i).substring(0, buildingClose.get(i).length() - 1);
                    String ampmEnd = buildingClose.get(i).substring(buildingClose.get(i).length() - 1, buildingClose.get(i).length());

                    if (ampmStart.equals("p")) {
//                    System.out.println("Before + 12 " + trimmedStart);
                        String firstTwo = trimmedStart.substring(0, 2);
                        int firstTwoNum = Integer.parseInt(firstTwo);
                        if (firstTwoNum != 12) {
                            firstTwoNum = firstTwoNum + 12;
                            String parsed = String.valueOf(firstTwoNum);
                            trimmedStart = parsed + trimmedStart.substring(2);
//                        System.out.println("After + 12 " + trimmedStart);
                        }
                    }
                    if (ampmEnd.equals("p")) {
//                    System.out.println("Before + 12 " + trimmedEnd);
                        String firstTwo = trimmedEnd.substring(0, 2);
                        int firstTwoNum = Integer.parseInt(firstTwo);
                        if (firstTwoNum != 12) {
                            firstTwoNum = firstTwoNum + 12;
                            String parsed = String.valueOf(firstTwoNum);
                            trimmedEnd = parsed + trimmedEnd.substring(2);
//                        System.out.println("After + 12 " + trimmedEnd);
                        }
                    }
                    Date startTime = new SimpleDateFormat("HH:mm").parse(trimmedStart);
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTime(startTime);


                    Date endTime = new SimpleDateFormat("HH:mm").parse(trimmedEnd);
                    Calendar calendar2 = Calendar.getInstance();
                    calendar2.setTime(endTime);

                    //String currentTimeStringTest = new SimpleDateFormat("HH:mm").format("22:55");
                    String currentTimeStringTest = new SimpleDateFormat("HH:mm").format(new Date());

                    Date checkTime = new SimpleDateFormat("HH:mm").parse(currentTimeStringTest);
                    Calendar calendar3 = Calendar.getInstance();
                    calendar3.setTime(checkTime);

                    if (endTime.compareTo(startTime) < 0) {
                        calendar2.add(Calendar.DATE, 1);
                        calendar3.add(Calendar.DATE, 1);
                    }

                    java.util.Date actualTime = calendar3.getTime();
                    if ((actualTime.after(calendar1.getTime()) ||
                            actualTime.compareTo(calendar1.getTime()) == 0) &&
                            actualTime.before(calendar2.getTime())) {
                        return true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public String getPercentage() {

        //String percentage = inUseComputers + "/" + totalComputers;
        int difference = totalComputers - inUseComputers;
        String percentage = Integer.toString(difference) + " Available";
        return percentage;
    }

    public int getInUseComputers() {
        return inUseComputers;
    }

    public int getTotalComputers() {
        return totalComputers;
    }

    public int getPresetTotalComputers() {
        return presetTotalComputers;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }


}