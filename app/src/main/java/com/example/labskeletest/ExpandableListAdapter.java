package com.example.labskeletest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List <String> listDataHeader;
    private HashMap<String,List<Lab>> listHashMap;

    DBConfiguration dbc = new DBConfiguration();
    DBAccess db = new DBAccess();

    public ExpandableListAdapter(Context content, List<String> listDataHeader, HashMap<String, List<Lab>> listHashMap) {
        this.context = content;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
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
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_textview, null);
        }

        TextView listLabel = (TextView) view.findViewById(R.id.lvHeaders);
        listLabel.setTypeface(null , Typeface.BOLD);
        listLabel.setText(headerTitle);
        view.setBackgroundColor(context.getResources().getColor(R.color.colorGeorgiaSouthernGold));

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        final Lab child = (Lab) getChild(groupPosition, childPosition);
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listitem, null);
        }

        final TextView roomLabel = (TextView) view.findViewById(R.id.tvLabNumber);
        roomLabel.setText(child.getRoom().substring(child.getRoom().length() - 4));

        TextView percentLabel = (TextView) view.findViewById(R.id.tvLabPercent);
        percentLabel.setText(child.getPercentage());

        final ToggleButton favoriteBtn = (ToggleButton) view.findViewById(R.id.toggleButton);
        final boolean isFavorite = MainActivity.listOfFavorites.contains(child.getRoom());
        if(isFavorite){
            favoriteBtn.setBackgroundResource(R.drawable.btn_favorite);
            favoriteBtn.setChecked(true);
        }else{
            favoriteBtn.setBackgroundResource(R.drawable.btn_unfavorite);
            favoriteBtn.setChecked(false);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLabClicked(roomLabel.getText().toString(), child);

            }
        });
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favoriteBtn.isChecked()) {
                    favoriteBtn.setBackgroundResource(R.drawable.btn_favorite);
                    String UUID = MainActivity.uniqueID;
                    String room = child.getRoom();
                    db = new DBAccess();
                    db.saveFavorite(UUID, room);
                }
                else{
                    favoriteBtn.setBackgroundResource(R.drawable.btn_unfavorite);
                    String UUID = MainActivity.uniqueID;
                    String room = child.getRoom();
                    db = new DBAccess();
                    db.deleteFavorite(UUID, room);

                    for(int i = 0; i < MainActivity.listOfFavorites.size(); i++){
                        if(MainActivity.listOfFavorites.get(i).contains(child.getRoom())){
                            MainActivity.listOfFavorites.remove(i);
                        }
                    }
                }
            }
        });
        setColor(child, percentLabel);

        Calendar currentTime = Calendar.getInstance();
        boolean classInSession = child.checkClassInSession(currentTime);
        TextView classInSessionTV = view.findViewById(R.id.tvLabSched);
        if(classInSession == true){
            classInSessionTV.setText("IN CLASS");        /*"Status: IN USE!"*/
            classInSessionTV.setTextColor(context.getResources().getColor(R.color.colorRed));
            percentLabel.setText("--/--");
            percentLabel.setTextColor(context.getResources().getColor(R.color.colorWhite));
        }
        else {
            if (child.getRoom().equals("CEIT2551202") || child.getRoom().equals("CEIT2551204")) {
                classInSessionTV.setText("EAGLE LAB");      /*"Status: OPEN!"*/
                classInSessionTV.setTextColor(context.getResources().getColor(R.color.colorGreen));
            } else {

                classInSessionTV.setText("OPEN");      /*"Status: OPEN!"*/
                classInSessionTV.setTextColor(context.getResources().getColor(R.color.colorGreen));
            }
        }
        return view;
    }
    public void setLabClicked(String lab, Lab object){
        Intent i = new Intent(context, LabInformation.class);
        i.putExtra("labClicked", lab);
        i.putExtra("object", object);
        context.startActivity(i);
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
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
}