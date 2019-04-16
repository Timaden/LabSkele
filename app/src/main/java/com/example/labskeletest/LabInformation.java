package com.example.labskeletest;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;



public class LabInformation extends AppCompatActivity {
    private ExpandableListView listView;
    private ExpandableListLabInfoAdapater listAdapter;
    private ArrayList<String> listBuildingHeader;
    private HashMap<String, List<String>> listHashMap;
    private String passedLab;
    private int inUseComputers;
    private int totalComputers;
    private int presetTotalComputers;
    private Lab lab;
    //DB TEST
    DBConfiguration dbc = new DBConfiguration();
    DBAccess db = new DBAccess();
    ResultSet computersRS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_information);
        Intent i = getIntent();
        passedLab = i.getStringExtra("labClicked");
        listView = findViewById(R.id.listViewLabInfo);
        lab = (Lab) i.getSerializableExtra("object");

        loadLabOccupancy();

        initData();
        listAdapter = new ExpandableListLabInfoAdapater(this, listBuildingHeader, listHashMap, lab);
        listView.setAdapter(listAdapter);
//        onBackPressed();
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (groupPosition == 4) {

                    listAdapter.labSoftwareClicked(v,lab);

                    parent.collapseGroup(groupPosition);
                    //parent.setOnGroupClickListener(new );
                    System.out.println("CLOSED");
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Include the code here
        System.out.println("back button pressed");
        TextView tv = (TextView) findViewById(R.id.testBackButtonPressed);
        tv.setVisibility(View.VISIBLE);
        tv.setText("Updating Labs...");

//        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
//        final View addListItemView = (View) inflater.inflate(R.layout.loading_data_popup,null);
//        final PopupWindow mPopupWindow = new PopupWindow(addListItemView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
//
//        mPopupWindow.setFocusable(true);
//        mPopupWindow.showAtLocation(findViewById(R.id.labInfoConstraintView), Gravity.CENTER,0,0);
        // Toast.makeText(getApplicationContext(),"Getting Up-To-Date Data",Toast.LENGTH_LONG).show();

       // Toast.makeText(getApplicationContext(),"Getting Up-To-Date Data",Toast.LENGTH_LONG).show();
    }


    public void initData() {

        listHashMap = new HashMap<String, java.util.List<String>>();
        listBuildingHeader = new ArrayList<>();

        listBuildingHeader.add("Lab " + passedLab + ":");
        ArrayList<String> listLabHeader = populateLabHeader();
        listHashMap.put(listBuildingHeader.get(0), listLabHeader);

        listBuildingHeader.add("Occupancy:");
        ArrayList<String> listOccupancy = populateLabList(listBuildingHeader.get(1));
        listHashMap.put(listBuildingHeader.get(1), listOccupancy);

        listBuildingHeader.add("Printer:");
        ArrayList<String> listPrinter = populatePrinter();
        listHashMap.put(listBuildingHeader.get(2), listPrinter);

        listBuildingHeader.add("Class Hours:");
        ArrayList<String> listOfHours = populateHours();
        listHashMap.put(listBuildingHeader.get(3), listOfHours);

        listBuildingHeader.add("Software Available:");
        ArrayList<String> listOfSoftware = populateSoftware();
        listHashMap.put(listBuildingHeader.get(4), listOfSoftware);
    }

    public ArrayList<String> populateSoftware() {

        //ArrayList<String> softwareList = lab.getSoftware();
        ArrayList<String> softwareList = new ArrayList<>();
        softwareList.add(" ");

        return softwareList;
    }

    public ArrayList<String> populateHours() {
        ArrayList<String> listOfHours = new ArrayList<>();
        ArrayList<String> listOfStart = lab.getClassStart();
        ArrayList<String> listOfEnd = lab.getClassEnd();
        ArrayList<String> listOfDay = lab.getClassDay();
        Calendar currentTime = Calendar.getInstance();
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

        ArrayList<String> listOfHoursSortedStart = new ArrayList<>();
        for (int i = 0; i < listOfStart.size(); i++) {
            if (listOfDay.get(i).contains(day_of_week)) {
                String full = listOfStart.get(i);
                String start = listOfStart.get(i).substring(0,listOfStart.get(i).length() - 1);
                String ampm = full.substring(full.length() - 1, full.length());
                char first = start.charAt(0);
                String c = String.valueOf(first);
                if(c.equals("0")){
                    start = start.substring(1,start.length());
                }
                listOfHoursSortedStart.add(start + " " + ampm + "m");
            }
        }
        Collections.sort(listOfHoursSortedStart, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                try {
                    return new SimpleDateFormat("hh:mm a").parse(o1).compareTo(new SimpleDateFormat("hh:mm a").parse(o2));
                } catch (ParseException e) {
                    return 0;
                }
            }
        });
        ArrayList<String> listOfHoursSortedEnd = new ArrayList<>();
        for (int i = 0; i < listOfEnd.size(); i++) {
            if (listOfDay.get(i).contains(day_of_week)) {
                String full = listOfEnd.get(i);
                String end = listOfEnd.get(i).substring(0,listOfEnd.get(i).length() - 1);
                String ampm = full.substring(full.length() - 1, full.length());
                char first = end.charAt(0);
                String c = String.valueOf(first);
                if(c.equals("0")){
                    end = end.substring(1,end.length());
                }
                listOfHoursSortedEnd.add(end + " " + ampm + "m");
            }
        }
        Collections.sort(listOfHoursSortedEnd, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                try {
                    return new SimpleDateFormat("hh:mm a").parse(o1).compareTo(new SimpleDateFormat("hh:mm a").parse(o2));
                } catch (ParseException e) {
                    return 0;
                }
            }
        });


        for(int i =0; i < listOfHoursSortedStart.size(); i ++){
            listOfHours.add(listOfHoursSortedStart.get(i) + " - " + listOfHoursSortedEnd.get(i));
            System.out.println(listOfHours.get(i).toString());
        }
        if(listOfHours.isEmpty()){
            listOfHours.add("No classes scheduled today.");
        }
        return listOfHours;
    }

    public ArrayList<String> populateLabHeader() {
        ArrayList<String> listOfHeader = new ArrayList<String>();
        if(lab.getRoom().contains("CEIT")){
            listOfHeader.add("CEIT Building Hours:");
            listOfHeader.add("M - TR: 7:00 am - Midnight");
            listOfHeader.add("F: 7:00 am - 10:00 pm");
            listOfHeader.add("Sat: 2:00 pm - 10:00 pm");
            listOfHeader.add("Sun: 2:00 pm - Midnight");

        }
        else if (lab.getRoom().contains("COBA")){
            listOfHeader.add("COBA Building Hours:");
            listOfHeader.add("M - F: 7:30 am - 9:00 pm");
            listOfHeader.add("Sat - Sun: CLOSED");
        }
        return listOfHeader;
    }

    public ArrayList<String> populatePrinter() {
        ArrayList<String> listOfPrinter = new ArrayList<String>();
        listOfPrinter.add("This lab has a double sided printer!");
        return listOfPrinter;
    }

    public ArrayList<String> populateLabList(String building) {
        ArrayList<String> listOfLabs = new ArrayList<String>();
        String a = Integer.toString(inUseComputers);
        String b = Integer.toString(totalComputers);
        String c = "In Use: " + a + "/" + b;
        listOfLabs.add(c);
        return listOfLabs;
    }

    public void loadLabOccupancy() {
        inUseComputers = lab.getInUseComputers();
        presetTotalComputers = lab.getPresetTotalComputers();
        totalComputers = lab.getTotalComputers();
    }
}