package com.livingspaces.proshopper.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.livingspaces.proshopper.fragments.SocialFrag;
import com.livingspaces.proshopper.fragments.WebViewFrag;
import com.livingspaces.proshopper.utilities.Global;
import com.livingspaces.proshopper.utilities.Layout;
import com.livingspaces.proshopper.utilities.sizes.PhoneSizes;

/**
 * Created by rugvedambekar on 15-09-30.
 */
public class SocialAdapter extends BaseAdapter {

    private Context mContext;

    public SocialAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return SocialFrag.Channel.values().length;
    }

    @Override
    public Object getItem(int position) {
        return SocialFrag.Channel.values()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(8, 8, 8, 8);

        } else imageView = (ImageView) convertView;

        final SocialFrag.Channel thisChannel = SocialFrag.Channel.values()[position];

        imageView.setImageResource(thisChannel.imgId());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.FragManager.stackFrag(WebViewFrag.newInstance(mContext.getResources().getString(thisChannel.title()), thisChannel.url()));
            }
        });

        Layout.setViewSize(imageView, PhoneSizes.socialImg, Layout.SizeType.HEIGHT);

        return imageView;
    }


}
