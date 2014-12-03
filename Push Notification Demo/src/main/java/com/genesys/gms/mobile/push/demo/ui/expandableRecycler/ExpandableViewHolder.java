package com.genesys.gms.mobile.push.demo.ui.expandableRecycler;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.genesys.gms.mobile.push.demo.R;
import com.genesys.gms.mobile.push.demo.ui.AnimatorHelper;
import timber.log.Timber;

/**
 * Created by stau on 12/2/2014.
 * Pretty nasty code
 */
public class ExpandableViewHolder extends RecyclerView.ViewHolder {
    protected Context context;
    @InjectView(R.id.img_icon) ImageView icon;
    @InjectView(R.id.txt_title) TextView title;
    @InjectView(R.id.checkbox_expand) CheckBox expand;
    @InjectView(R.id.toggle_bar) LinearLayout toggle_bar;
    @InjectView(R.id.expandable_content) LinearLayout expandable_content;
    @InjectView(R.id.txt_content) TextView content;
    @InjectView(R.id.txt_timestamp) TextView timestamp;

    public ExpandableViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        ButterKnife.inject(this, itemView);
    }

    @OnClick({R.id.toggle_bar, R.id.checkbox_expand})
    public void toggleContent() {
        if(expandable_content.isShown()) {
            expand.setChecked(false);
            collapse();
        } else {
            expand.setChecked(true);
            expand();
        }
    }

    protected void reset() {
        if(expandable_content!=null) {
            expandable_content.setVisibility(View.GONE);
        }
        if(expand!=null) {
            expand.setChecked(false);
        }
    }

    @TargetApi(11)
    private void collapse() {
        int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.HONEYCOMB) {
            expandable_content.setVisibility(View.GONE);
            return;
        }
        int finalHeight = expandable_content.getHeight();
        ValueAnimator animator = slideAnimator(finalHeight, 0);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {;}
            @Override
            public void onAnimationEnd(Animator animation) {
                expandable_content.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationCancel(Animator animation) {;}
            @Override
            public void onAnimationRepeat(Animator animation) {;}
        });
        animator.start();
    }

    @TargetApi(11)
    private void expand() {
        int sdk = Build.VERSION.SDK_INT;
        expandable_content.setVisibility(View.VISIBLE);
        if (sdk < Build.VERSION_CODES.HONEYCOMB) {
            return;
        }
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(
                context.getResources().getDimensionPixelSize(R.dimen.drawer_width),
                View.MeasureSpec.EXACTLY
        );
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        expandable_content.measure(widthSpec, heightSpec);
        Timber.d("Expandable content width: " + expandable_content.getWidth());
        Timber.d("Expandable content measured height: " + expandable_content.getMeasuredHeight());
        ValueAnimator animator = slideAnimator(0, expandable_content.getMeasuredHeight());
        animator.start();
    }

    @TargetApi(11)
    private ValueAnimator slideAnimator(int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer)animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = expandable_content.getLayoutParams();
                layoutParams.height = value;
                expandable_content.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }
}
