package com.chuangba.homeinn.util;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.chuangba.homeinn.R;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Created by jinyh on 2017/10/30.
 */

public class KeyboardUtil {

    private Context ctx;
    private Activity act;
    private KeyboardView keyboardView;
    private Keyboard k1;// 中文键盘  //暂时用不到
    private Keyboard k2;// 数字键盘
    public boolean isNumber = false;// 是否数字键盘
    public boolean isUpper = false;// 是否大写
    private EditText ed;
    public KeyboardUtil(Activity act, Context ctx, EditText edit, KeyboardView keyboard,boolean isPassword) {
        this.act = act;
        this.ctx = ctx;
        this.ed = edit;
        if (isPassword){
            k2 = new Keyboard(this.ctx,R.xml.symbol_password);
        }else {
            k2 = new Keyboard(this.ctx, R.xml.symbol);
        }

        keyboardView = keyboard;
        if (keyboardView!=null){
            keyboardView.setKeyboard(k2);
            keyboardView.setEnabled(true);
            keyboardView.setPreviewEnabled(false);
            keyboardView.setOnKeyboardActionListener(listener);
        }


    }

    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {

        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            playClick(primaryCode);
            Editable editable = ed.getText();
            int start = ed.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_CANCEL ){
                hideKeyboard();
            } else if (primaryCode == Keyboard.KEYCODE_DELETE){
                if (editable!= null && editable.length()>0){
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            }else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {//数字键盘切换
                if (isNumber) {
                    isNumber = false;
                    keyboardView.setKeyboard(k1);
                } else {
                    isNumber = true;
                    keyboardView.setKeyboard(k2);
                }
            }
            else if (primaryCode == Keyboard.KEYCODE_DONE){
                if( editable.length() == 18){
                    //完成
                    //EntryActivity activity = (EntryActivity) act;
                    //activity.swipeNext();
                } else {
                    Toast.makeText(act,"请正确输入身份证",Toast.LENGTH_SHORT).show();
                }

            }else if (primaryCode == 57419) { // go left
                if (start > 0) {
                    ed.setSelection(start - 1);
                }
            } else if (primaryCode == 57421) { // go right
                if (start < ed.length()) {
                    ed.setSelection(start + 1);
                }
            } else if (primaryCode == -7){
                if (editable.length()>0)
                ed.setText(null);
            }else {
                editable.insert(start, Character.toString((char) primaryCode));
            }

        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };

//按键音
    private void playClick(int keyCode){
        AudioManager am = (AudioManager)ctx.getSystemService(AUDIO_SERVICE);
        switch(keyCode){
            case Keyboard.KEYCODE_DONE:
                am.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }


    public void showKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
        }
    }

    public void hideKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            keyboardView.setVisibility(View.INVISIBLE);
        }
    }

    public void showNumber() {
        showKeyboard();
        isNumber = true;
        keyboardView.setKeyboard(k2);
    }

}
