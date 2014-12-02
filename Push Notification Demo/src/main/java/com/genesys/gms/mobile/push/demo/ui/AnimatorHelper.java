package com.genesys.gms.mobile.push.demo.ui;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.genesys.gms.mobile.push.demo.R;

/**
 * Created by stau on 12/2/2014.
 */
public class AnimatorHelper {
    public static void slide_down(Context context, View view) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        if(anim != null) {
            anim.reset();
        }
        if(view != null) {
            view.clearAnimation();
            view.startAnimation(anim);
        }
    }

    public static void slide_up(Context context, View view) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        if(anim != null) {
            anim.reset();
        }
        if(view != null) {
            view.clearAnimation();
            view.startAnimation(anim);
        }
    }
}
