package com.example.toni.casillas2;


import android.net.Uri;
import android.support.v4.app.Fragment;


public class BaseFragment extends Fragment {

    public static final String ARG_SECTION_NUMBER = "ARG_SECTION_NUMBER";


    public BaseFragment() {
        // Required empty public constructor
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



}

