package com.livingspaces.proshopper.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.adapters.SocialAdapter;
import com.livingspaces.proshopper.utilities.Layout;
import com.livingspaces.proshopper.utilities.sizes.PhoneSizes;

/**
 * A simple {@link Fragment} subclass.
 */
public class SocialFrag extends BaseStackFrag {

    GridView socialGrid;

    public static SocialFrag newInstance() {
        return new SocialFrag();
    }

    public SocialFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        socialGrid = (GridView) inflater.inflate(R.layout.fragment_social, container, false);
        socialGrid.setAdapter(new SocialAdapter(getActivity()));

        int vSpace = (int) Layout.Calc(PhoneSizes.socialSpacing).height;
        socialGrid.setPadding(0, vSpace, 0, vSpace);
        socialGrid.setVerticalSpacing(vSpace);

        return socialGrid;
    }

    @Override
    public String getTitle() {
        return NavigationFrag.NavItem.SOCIAL.title();
    }

    public enum Channel {
        facebook(R.string.facebook, R.drawable.ls_sm_btn_fb, "http://www.facebook.com/livingspaces/"),
        google(R.string.google, R.drawable.ls_sm_btn_gp, "http://plus.google.com/+LivingSpaces/"),
        youtube(R.string.youtube, R.drawable.ls_sm_btn_yt, "http://www.youtube.com/user/livingspaces/"),
        pintrest(R.string.pinterest, R.drawable.ls_sm_btn_pin, "http://www.pinterest.com/LivingSpaces/"),
        houzz(R.string.houzz, R.drawable.ls_sm_btn_h, "http://www.houzz.com/pro/livingspaces"),
        twitter(R.string.twitter, R.drawable.ls_sm_btn_tw, "http://www.twitter.com/livingspaces"),
        instagram(R.string.instagram, R.drawable.ls_sm_btn_in, "http://www.instagram.com/livingspaces"),
        blog(R.string.blog, R.drawable.ls_sm_btn_blog, "http://blog.livingspaces.com");

        private int title;
        private int imgId;
        private String url;

        private Channel(int titleId, int id, String u) {
            title = titleId;
            imgId = id;
            url = u;
        }

        public int title() {
            return title;
        }

        public int imgId() {
            return imgId;
        }

        public String url() {
            return url;
        }
    }

}
