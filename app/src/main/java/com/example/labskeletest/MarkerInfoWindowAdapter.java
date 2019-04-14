package com.example.labskeletest;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
   private Context ctx;

    public Building getB() {
        return b;
    }

    public void setB(Building b) {
        this.b = b;
    }

    private Building b = new Building();

   public MarkerInfoWindowAdapter(Context context){
       this.ctx = context.getApplicationContext();
   }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.map_lab_marker_info_window , null);
        LatLng latLng = marker.getPosition();
        TextView bldgName = v.findViewById(R.id.bldgName);
        TextView noOfLabs = v.findViewById(R.id.noOfLabs);
        TextView available = v.findViewById(R.id.available);

        bldgName.setText(b.getBldgName());

        noOfLabs.setText(Integer.toString(b.getNoOfLabs()));
        if(b.isOneLabFree()){
            available.setBackgroundColor(Color.GREEN);
        }else{
            available.setBackgroundColor(Color.RED);
        }
   return v;
   }

}
