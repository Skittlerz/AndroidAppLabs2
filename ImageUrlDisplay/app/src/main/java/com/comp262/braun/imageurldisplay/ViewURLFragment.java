package com.comp262.braun.imageurldisplay;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import java.util.ArrayList;

/**
 * Created by acb on 2017-05-24.
 */

public class ViewURLFragment extends Fragment {
    ListView imageURLs;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.view_url_fragment,container,false);
        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinnerURL);
        imageURLs = (ListView) rootView.findViewById(R.id.lvLinks);
        setHasOptionsMenu(true);
        Bundle bundle = this.getArguments();
        if(bundle != null){
            //take passed arraylist of webpages and display in spinner
            ArrayList<String> webpages = bundle.getStringArrayList("webpages");
            ArrayAdapter<String> dataAdapter =
                    new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, webpages);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);
            spinner.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener(){
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view,
                                                   int pos, long id){

                            //each entry in the spinner begins with a number corresponding to its
                            //id in the database
                            //that id is parsed out and passed to the getImages method to retrieve
                            //all the images and list them in a listview
                            String text = adapterView.getSelectedItem().toString();
                            String web_id = text.substring(0,text.indexOf(','));
                            ArrayList<String> images = ((MainActivity)getActivity()).getImages(web_id);
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                    getActivity(),
                                    android.R.layout.simple_list_item_1,
                                    images);

                            imageURLs.setAdapter(arrayAdapter);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView){}
                    }
            );
        }

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.view_title);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.action_view).setVisible(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set app bar title when fragment is resumed
        // ex. User hits back button
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.view_title);
    }
}
