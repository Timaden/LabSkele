package com.example.labskeletest;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static android.content.Context.LOCATION_SERVICE;

public class Map extends Fragment implements LocationListener{


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private List<LatLng> decodedPath;
    private DirectionsResult result ;
    float distance = 0;
    private ArrayList<Building> listOfBldgs;
    Building b = new Building("Dept of Information Technology\n(CEIT)", "P.O. Box 8150 Statesboro, GA 30460", "(912) 478-4848", 14, true, 32.423297, -81.786482);
    MapView mMapView;
    private PopupWindow mapPopupHUD;


    private GoogleMap googleMap;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private Button visitBtn;
    private Button viewBtn;
    private ImageButton closeBtn;
    private RelativeLayout mRelativeLayout;
    private PopupWindow mPopupWindow;
    private Location userLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;
    public Activity activity;
    private static int overview = 0;
    Polyline route;
    String gold = "#87714D";
    String white = "#F7F7F7";
    String red = "#FF0000";
    String black = "#000000";

    String lineColor = gold;
    private boolean routing = false;
    public Map() {

    }


    public static Map newInstance(String param1, String param2) {
        Map fragment = new Map();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        mRelativeLayout = rootView.findViewById(R.id.mapParent);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

//MAP SECTION
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mMap) {

               /* locationManager = (LocationManager) rootView.getContext().getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                        0, locationListener);*/

                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        ReadableInstant now = new DateTime();
                        Location l = new Location("");
                        l.setLatitude(b.getLat());
                        l.setLongitude(b.getLng());
                        distance = location.distanceTo(l);
                        try {
                            result = DirectionsApi.newRequest(getGeoContext()).mode(TravelMode.DRIVING).origin(Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude())).destination(Double.toString(b.getLat()) + "," + Double.toString(b.getLng())).departureTime(now).await();
                        } catch (ApiException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        route = mMap.addPolyline(new PolylineOptions().addAll(decodedPath).width(16).color(Color.parseColor(lineColor)));

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        if (provider.equals(LocationManager.GPS_PROVIDER)) {
                            if (status == LocationProvider.OUT_OF_SERVICE) {
                                notifyLocationProviderStatusUpdated(false);
                            } else {
                                notifyLocationProviderStatusUpdated(true);
                            }
                        }
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        if (provider.equals(LocationManager.GPS_PROVIDER)) {
                            notifyLocationProviderStatusUpdated(true);
                        }
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        if (provider.equals(LocationManager.GPS_PROVIDER)) {
                            notifyLocationProviderStatusUpdated(false);
                        }
                    }
                };
                googleMap = mMap;
                // For dropping a marker at a point on the Map
                float zoom = 18.0f;
                LatLng itBldg = new LatLng(32.423297, -81.786482);
                googleMap.addMarker(new MarkerOptions().position(itBldg).title("Department of Information Technology")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.labbubble));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(itBldg, zoom));
                if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                } else {
                    Toast.makeText(getContext(), "Allow Lab Locator to access location", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            1);
                }
                //Location listener

                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setPowerRequirement(Criteria.POWER_HIGH);
                criteria.setAltitudeRequired(false);
                criteria.setSpeedRequired(false);
                criteria.setCostAllowed(true);
                criteria.setBearingRequired(false);

                //API level 9 and up
                criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
                LocationManager locationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
                String provider = locationManager.getBestProvider(criteria, true);

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , 5000 , 5 , locationListener);

                Location location = locationManager.getLastKnownLocation(provider);


                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng myPosition = new LatLng(latitude, longitude);

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(itBldg).zoom(16).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                MarkerInfoWindowAdapter markerInfoWindowAdapter = new MarkerInfoWindowAdapter(getContext());

                markerInfoWindowAdapter.setB(b);
                googleMap.setInfoWindowAdapter(markerInfoWindowAdapter);

                //Marker Implementations
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(final Marker marker) {

                        if (marker != null && marker.getTitle().equals("Department of Information Technology")) {
                            LayoutInflater inflater = (LayoutInflater) rootView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View customView = inflater.inflate(R.layout.map_popup_window, null);

                            mPopupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                            visitBtn = customView.findViewById(R.id.visitBtn);
                            viewBtn = customView.findViewById(R.id.viewBtn);
                            closeBtn = customView.findViewById(R.id.closeBtn);
                            closeBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mPopupWindow.dismiss();
                                }
                            });

                            viewBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ViewPager p = getActivity().findViewById(R.id.pager);
                                    p.setCurrentItem(2);
                                    mPopupWindow.dismiss();
                                }
                            });

                            visitBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Location user = null;

                                    LocationManager locationManager = (LocationManager)
                                            getActivity().getSystemService(LOCATION_SERVICE);
                                    Criteria criteria = new Criteria();

                                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                        return;
                                    }
                                    Location location = locationManager.getLastKnownLocation(locationManager
                                            .getBestProvider(criteria, false));
                                    double latitude = location.getLatitude();
                                    double longitude = location.getLongitude();

                                    LatLng src = new LatLng(latitude , longitude);
                                    LatLng dest = new LatLng(b.getLat(), b.getLng());
                                    ReadableInstant now = new DateTime();
                                    // Toast.makeText(getContext() , src.toString() , Toast.LENGTH_LONG).show();
                                    Toast.makeText(getContext() , dest.toString() , Toast.LENGTH_LONG).show();
                                    try {
                                        result = DirectionsApi.newRequest(getGeoContext()).mode(TravelMode.DRIVING).origin(Double.toString(src.latitude) + "," + Double.toString(src.longitude)).destination(Double.toString(dest.latitude) + "," + Double.toString(dest.longitude)).departureTime(now).await();
                                        decodedPath = PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath());
                                       route = mMap.addPolyline(new PolylineOptions().addAll(decodedPath).width(16).color(Color.parseColor(lineColor)));

                                        marker.hideInfoWindow();
                                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(src, 19);

                                        mMap.moveCamera(cameraUpdate);

                                        //addMarkersToMap(result , googleMap);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    //Popup HUD

                                }
                            });
                            mPopupWindow.showAtLocation(mRelativeLayout, Gravity.BOTTOM,0,0);
                        }

                    }

                });

                Button t1 = rootView.findViewById(R.id.theme1);
                Button t2 = rootView.findViewById(R.id.theme2);
                Button t3 = rootView.findViewById(R.id.theme3);
                Button t4 = rootView.findViewById(R.id.theme4);

                t1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.assassins_creed_theme_json));
                        lineColor = white;
                        if(route != null) {
                            route.setColor(Color.parseColor(lineColor));
                        }
                    }
                });
                t2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.desert_theme_json));
                        lineColor = black;
                        if(route != null) {
                            route.setColor(Color.parseColor(lineColor));
                        }
                    }
                });
                t3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.gta_san_theme_json));
                        lineColor = red;
                        if(route != null) {
                            route.setColor(Color.parseColor(lineColor));
                        }
                    }
                });
                t4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.retro_style_json));
                        lineColor = gold;
                        if(route != null) {
                            route.setColor(Color.parseColor(lineColor));
                        }
                    }
                });

            }
        });

        return rootView;
    }

    private void notifyLocationProviderStatusUpdated(boolean isLocationProviderAvailable) {
        //Broadcast location provider status change here
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext
                .setQueryRateLimit(3)
                .setApiKey(getString(R.string.directionsApiKey))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }
    private void setupGoogleMapScreenSettings(GoogleMap mMap) {
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(true);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }

    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].startLocation.lat,results.routes[overview].legs[overview].startLocation.lng)).title(results.routes[overview].legs[overview].startAddress));
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].endLocation.lat,results.routes[overview].legs[overview].endLocation.lng)).title(results.routes[overview].legs[overview].startAddress).snippet(getEndLocationTitle(results)));
    }

    private void positionCamera(DirectionsRoute route, GoogleMap mMap) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(route.legs[overview].startLocation.lat, route.legs[overview].startLocation.lng), 12));
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath).width(16).color(Color.parseColor(lineColor)));
    }

    private String getEndLocationTitle(DirectionsResult results){
        return  "Time :"+ results.routes[overview].legs[overview].duration.humanReadable + " Distance :" + results.routes[overview].legs[overview].distance.humanReadable;
    }



    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //Service functions


}

