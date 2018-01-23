package com.livingspaces.proshopper.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.utilities.Layout;

/**
 * Created by rugvedambekar on 15-09-28.
 */
public class LSTextView extends TextView {

    public LSTextView(Context context) {
        super(context);
    }

    public LSTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LSTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attrArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LSTextView, 0, 0);

        int font = -1;
        try { font = attrArray.getInt(R.styleable.LSTextView_font, 0); }
        finally { attrArray.recycle(); }

        this.setTypeface(Layout.Font.get(font));
    }
}
