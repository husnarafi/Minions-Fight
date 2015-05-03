package com.husnarafi.xo.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewWithRobotoFont extends TextView {

    public TextViewWithRobotoFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typefaces.get(context, "fonts/Roboto-Light.ttf"));
    }

    public TextViewWithRobotoFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTypeface(Typefaces.get(context, "fonts/Roboto-Light.ttf"));
    }

    public TextViewWithRobotoFont(Context context) {
        super(context);
        this.setTypeface(Typefaces.get(context, "fonts/Roboto-Light.ttf"));
    }

}