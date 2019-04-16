package com.example.labskeletest;

import android.content.Context;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ExpandableListLabInfoAdapater extends BaseExpandableListAdapter {
    private Context context;
    private java.util.List<String> listDataHeader;
    private HashMap<String,java.util.List<String>> listHashMap;
    private Lab lab;
    DBConfiguration dbc = new DBConfiguration();
    DBAccess db = new DBAccess();

    public ExpandableListLabInfoAdapater(Context content, java.util.List<String> listDataHeader, HashMap<String, List<String>> listHashMap, Lab lab) {
        this.context = content;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
        this.lab = lab;

    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return listHashMap.get(listDataHeader.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return listDataHeader.get(i);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_textview, null);
        }//
        System.out.println("Header Title: " + headerTitle + " Group Position: " + groupPosition);

        TextView listLabel = (TextView) view.findViewById(R.id.lvHeaders);
        listLabel.setTypeface(null, Typeface.BOLD);
        listLabel.setText(headerTitle);
        view.setBackgroundColor(context.getResources().getColor(R.color.colorGeorgiaSouthernGold));
        System.out.println(groupPosition + "---GROUP POSITION---- " + headerTitle);

//            if (groupPosition == 4){
//                //view = setUpSoftware(view);
//                view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        labSoftwareClicked(v, lab);
//                    }
//                });
//            }
    //}//
        return view;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);
//        if(view == null){
//            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(LAYOUT_INFLATER_SERVICE);
//            view = inflater.inflate(R.layout.listitem, null);
//        }
//        final TextView childLabel = (TextView) view.findViewById(R.id.tvLabNumber);
//        childLabel.setText(childText);
//        System.out.println("group position" + groupPosition);
//        System.out.println("childPosition  position" + childPosition);
//        if(view == null) {
            if (groupPosition == 0) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.listitem_header, null);
                final TextView childLabel = (TextView) view.findViewById(R.id.tvLabHeader);
                childLabel.setText(childText);
            } else if (groupPosition == 1) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.listitem_occupancy, null);
                setUpOccupancyChild(childPosition, groupPosition, view);
            } else if (groupPosition == 2) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.listitem_printer, null);
                final TextView childLabel = (TextView) view.findViewById(R.id.tvLabPrinter);
                childLabel.setText(childText);
            } else if (groupPosition == 3) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.listitem_hours, null);
                final TextView childLabel = (TextView) view.findViewById(R.id.tvLabHours);
                childLabel.setText(childText);
            } else if (groupPosition == 4){
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.listitem_software, null);
                final TextView childLabel = (TextView) view.findViewById(R.id.tvLabSoftware);
                childLabel.setText(childText);
            }
//        }
        view.setBackgroundColor(context.getResources().getColor(R.color.colorGeorgiaSouthernBlue));

       return view;
    }

    private void setUpOccupancyChild(int childPosition, int groupPosition, View view) {
        final TextView roomLabel = (TextView) view.findViewById(R.id.tvLabNumber2);
        roomLabel.setText(lab.getRoom().substring(lab.getRoom().length() - 4));

        TextView percentLabel = (TextView) view.findViewById(R.id.tvLabPercent2);
        percentLabel.setText(lab.getPercentage());

        final ToggleButton favoriteBtn = (ToggleButton) view.findViewById(R.id.toggleButton2);
        final boolean isFavorite = MainActivity.listOfFavorites.contains(lab.getRoom());
        if(isFavorite){
            favoriteBtn.setBackgroundResource(R.drawable.btn_favorite);
            favoriteBtn.setChecked(true);
        }else{
            favoriteBtn.setBackgroundResource(R.drawable.btn_unfavorite);
            favoriteBtn.setChecked(false);
        }
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                System.out.println("Favorite has been clicked. Lab room:" + lab.getRoom());

 //               System.out.println("Child.getRoom ----" + lab.getRoom());
                if(favoriteBtn.isChecked()) {
                    favoriteBtn.setBackgroundResource(R.drawable.btn_favorite);
                    String UUID = MainActivity.uniqueID;
                    String room = lab.getRoom();
                    db = new DBAccess();
                    db.saveFavorite(UUID, room);
                }
                else{
                    favoriteBtn.setBackgroundResource(R.drawable.btn_unfavorite);
                    String UUID = MainActivity.uniqueID;
                    String room = lab.getRoom();
                    db = new DBAccess();
                    db.deleteFavorite(UUID, room);

                    for(int i = 0; i < MainActivity.listOfFavorites.size(); i++){
                        if(MainActivity.listOfFavorites.get(i).contains(lab.getRoom())){
                            MainActivity.listOfFavorites.remove(i);
                        }
                    }
                }
            }
        });
        setColor(lab, percentLabel);

        Calendar currentTime = Calendar.getInstance();
        boolean classInSession = lab.checkClassInSession(currentTime);
        boolean buildingOpen = lab.checkBuildingOpen(currentTime);
        TextView classInSessionTV = view.findViewById(R.id.tvLabSched2);
        if(buildingOpen == true) {
            if (classInSession == true) {
                classInSessionTV.setText("IN CLASS");    /*"Status: IN USE!"*/
                classInSessionTV.setTextColor(context.getResources().getColor(R.color.colorRed));
            } else {
                System.out.println(lab.getRoom());
                if (lab.getRoom().equals("CEIT2551202") || lab.getRoom().equals("CEIT2551204")) {
                    System.out.println("lab is 1202 or 1204");
                    classInSessionTV.setText("EAGLE LAB");      /*"Status: OPEN!"*/
                    classInSessionTV.setTextColor(context.getResources().getColor(R.color.colorGreen));
                } else {

                    classInSessionTV.setText("OPEN");      /*"Status: OPEN!"*/
                    classInSessionTV.setTextColor(context.getResources().getColor(R.color.colorGreen));
                }
            }
        }else{
        classInSessionTV.setText("CLOSED");        /*"Status: IN USE!"*/
        classInSessionTV.setTextColor(context.getResources().getColor(R.color.colorRed));
        percentLabel.setText("--/--");
        percentLabel.setTextColor(context.getResources().getColor(R.color.colorWhite));
    }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    private View setUpLabName(View view){
        view.setBackgroundColor(context.getResources().getColor(R.color.colorGeorgiaSouthernGold));
        return view;
    }
    public void setColor(Lab child, TextView percentLabel){
        int inUse = child.getInUseComputers();
        int total = child.getTotalComputers();
        int totalPreset = child.getPresetTotalComputers();
        int difference = totalPreset - total;
        inUse = inUse + difference;
        double percentInUse = (double) inUse /totalPreset;


        if(percentInUse > .75){
            //view.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
            percentLabel.setTextColor(context.getResources().getColor(R.color.colorRed));

        }
        else if(percentInUse > .50){
            //view.setBackgroundColor(context.getResources().getColor(R.color.colorYellow));
            percentLabel.setTextColor(context.getResources().getColor(R.color.colorYellow));
        }
        else{
            //view.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
            percentLabel.setTextColor(context.getResources().getColor(R.color.colorGreen));
        }
    }
    private View setUpOccupancy(View view){

//        int inUse = lab.getInUseComputers();
//        int total = lab.getTotalComputers();
//        int totalPreset = lab.getPresetTotalComputers();
//        int difference = totalPreset - total;
//        inUse = inUse + difference;
//        double percentInUse = (double) inUse /totalPreset;
//        if(percentInUse > .75){
//            view.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
//        }
//        else if(percentInUse > .50){
//            view.setBackgroundColor(context.getResources().getColor(R.color.colorYellow));
//        }
//        else{
//            view.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
//        }

        view.setBackgroundColor(context.getResources().getColor(R.color.colorGeorgiaSouthernGold));
        return view;
    }
    private View setUpPrinter(View view){
        view.setBackgroundColor(context.getResources().getColor(R.color.colorGeorgiaSouthernGold));
        return view;
    }
    private View setUpLabHours(View view){
        view.setBackgroundColor(context.getResources().getColor(R.color.colorGeorgiaSouthernGold));
        return view;
    }
    private View setUpSoftware(View view){
        view.setBackgroundColor(context.getResources().getColor(R.color.colorGeorgiaSouthernGold));
        return view;
    }
    public void labSoftwareClicked(View view, Lab lab){
        System.out.println("Software CLicked");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        ArrayList<String> softwareList = lab.getSoftware();
        for(int i = 0; i < softwareList.size(); i ++){
            System.out.println(softwareList.get(i));
        }
        final View popup_listview = (View) inflater.inflate(R.layout.popup_listview,null);
        ListView softwareListView = (ListView) popup_listview.findViewById(R.id.softwareListView);
        final PopupWindow mPopupWindow = new PopupWindow(popup_listview, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        labInfoListViewAdapter adapter = new labInfoListViewAdapter(context,softwareList);

        softwareListView.setAdapter(adapter);
        mPopupWindow.showAtLocation(view, Gravity.CENTER,0,0);

        //Create listener for exit button
        ImageView popup_closeButton = (ImageView) popup_listview.findViewById(R.id.popup_btnClose);
        popup_closeButton.setClickable(true);
        popup_closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ((ViewGroup) v.getParent()).removeView(mPopupWindow);
                mPopupWindow.dismiss();
            }
        });
    }
}