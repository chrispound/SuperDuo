package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class About extends Fragment {

    public About(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        TextView source = (TextView) rootView.findViewById(R.id.source);
        source.setText(Html.fromHtml("<a href=\"https://github.com/googlesamples/android-vision/tree/master/visionSamples\">Source</a>"));
        source.setMovementMethod(LinkMovementMethod.getInstance());
        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(getActivity() != null)
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.about);
    }

}
