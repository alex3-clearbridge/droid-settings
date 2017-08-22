package com.livingspaces.proshopper.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.livingspaces.proshopper.R;

/**
 * Created by alexeyredchets on 2017-08-15.
 */

public class SettingsFrag extends BaseStackFrag {

    private static final String TAG = SettingsFrag.class.getSimpleName();

    private View v_terms, v_policy, v_about;

    public static SettingsFrag newInstance(){
        return new SettingsFrag();
    }

    public SettingsFrag() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        v_terms = view.findViewById(R.id.v_terms);
        v_policy = view.findViewById(R.id.v_policy);
        v_about = view.findViewById(R.id.v_about);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        v_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fm = getActivity().getSupportFragmentManager();
                DialogFragment dialogFragment = new DialogFrag();
                dialogFragment.show(fm, "dialogFragment");

                Toast.makeText(getContext(), "onTerms", Toast.LENGTH_SHORT).show();
            }
        });

        v_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "onPolicy", Toast.LENGTH_SHORT).show();
            }
        });

        v_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "onAbout", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public String getTitle() {
        return NavigationFrag.NavItem.SETTINGS.title();
    }
}
