package com.sdsmdg.harjot.MusicDNA;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class AnalogControllerFragment extends Fragment {

    CustomImageHolder cih;

    public AnalogControllerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_analog_controller, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cih = (CustomImageHolder) view.findViewById(R.id.cih);

        Drawable d1 = getResources().getDrawable(R.drawable.ic_album_white_48dp);
        Drawable d2 = getResources().getDrawable(R.drawable.ic_default);
        Drawable d3 = getResources().getDrawable(R.drawable.ic_play_arrow_white_48dp);
        Drawable d4 = getResources().getDrawable(R.drawable.ic_heart_filled);

        cih.setDrawables(d1, d2, d3, d4);

    }
}
