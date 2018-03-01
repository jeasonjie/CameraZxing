package com.planet.camerazxing.util;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;


import com.planet.camerazxing.R;

import java.util.List;

/**
 * Created by Administrator on 2016/11/9.
 */

public class Utils {

    public static void setIcon(String[] baseList, ImageView img, int position) {
        if (0 == position) {
            img.setImageResource(R.drawable.menu1);
        } else if (1 == position) {
            img.setImageResource(R.drawable.menu2);
        } else if (2 == position) {
            img.setImageResource(R.drawable.menu3);
        } else if (3 == position) {
            img.setImageResource(R.drawable.menu4);
        } else if (4 == position) {
            img.setImageResource(R.drawable.menu5);
        } else if (5 == position) {
            img.setImageResource(R.drawable.menu6);
        }
    }

}
