package com.livingspaces.proshopper.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.interfaces.IEditTextImeBackListener;
import com.livingspaces.proshopper.utilities.Layout;

/**
 * Created by justinwong on 2015-10-13.
 */
public class LSEditText extends EditText {

    private IEditTextImeBackListener mOnImeBack;

    public LSEditText(Context context) {
        super(context);
    }

    public LSEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LSEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attrArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LSTextView, 0, 0);

        int font = -1;
        try {
            font = attrArray.getInt(R.styleable.LSTextView_font, 0);
        } finally {
            attrArray.recycle();
        }

        this.setTypeface(Layout.Font.get(font));
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (mOnImeBack != null) mOnImeBack.onImeBack(this, this.getText().toString());
        }
        return super.dispatchKeyEvent(event);
    }

    public void setOnEditTextImeBackListener(IEditTextImeBackListener listener) {
        mOnImeBack = listener;
    }
}
