package com.example.labskeletest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link List.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link List#newInstance} factory method to
 * create an instance of this fragment.
 */




public class List extends Fragment  {
    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private ArrayList<String> listBuildingHeader;
    private HashMap<String, java.util.List<Lab>> listHashMap;
    //DB TEST
    DBConfiguration dbc = new DBConfiguration();
    DBAccess dba = new DBAccess();
    ResultSet computersRS;






    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    static ListView testList;
    static View inflatedView = null;

    private OnFragmentInteractionListener mListener;

    public List() {
        // Required empty public constructor
    }

    public ListView getListView(){
        return testList;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment List.
     */
    // TODO: Rename and change types and number of parameters
    public static List newInstance(String param1, String param2) {
        List fragment = new List();
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
    public void initData(){

        listHashMap = new HashMap<String, java.util.List<Lab>>();
        listBuildingHeader = new ArrayList<>();

        listBuildingHeader.add("IT Building");
        //ArrayList<Lab> listOfLabsIT = populateLabList("CEIT"/*listBuildingHeader.get(0)*/);

        MainActivity mainActivity = new MainActivity();
        ArrayList<Lab> listOfLabsCEIT = MainActivity.listOfLabsCEIT;


        listHashMap.put(listBuildingHeader.get(0),listOfLabsCEIT);

        listBuildingHeader.add("COBA Building");

        ArrayList<Lab> listOfLabsCOBA = MainActivity.listOfLabsCOBA;
        listHashMap.put(listBuildingHeader.get(1),listOfLabsCOBA);

    }

      @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        testList = container.findViewById(R.id.listView);
        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.fragment_list, container, false);
        listView = (ExpandableListView) inflatedView.findViewById(R.id.listView);
        initData();
        listAdapter = new ExpandableListAdapter(getActivity(), listBuildingHeader, listHashMap);
        listView.setAdapter(listAdapter);

        Button refreshBtn = inflatedView.findViewById(R.id.refreshDataButton);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.setData();
                listAdapter.notifyDataSetChanged();
            }
        });

        return inflatedView;
        //return inflater.inflate(R.layout.fragment_list, container, false);
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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}