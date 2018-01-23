package com.livingspaces.proshopper.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import com.livingspaces.proshopper.utilities.Layout;

/**
 * Created by rugvedambekar on 15-09-22.
 */
public class EditTextKeyCB extends EditText {

    private IKeyListener keyListener;

    public EditTextKeyCB(Context context) {
        super(context);
        init();
    }

    public EditTextKeyCB(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTextKeyCB(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setTypeface(Layout.Font.regular);
    }

    public void setKeyListener(IKeyListener listener) { keyListener = listener; }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && keyListener != null) {
            keyListener.onUserDismiss();
            return true;
        }
        return super.onKeyPreIme(keyCode, event);
    }

    public interface IKeyListener {
        void onUserDismiss();
    }
}
