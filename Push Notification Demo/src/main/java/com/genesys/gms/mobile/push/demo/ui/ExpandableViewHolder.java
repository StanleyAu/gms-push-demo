package com.genesys.gms.mobile.push.demo.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.genesys.gms.mobile.push.demo.R;

/**
 * Created by stau on 12/2/2014.
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

    public ExpandableViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        ButterKnife.inject(this, itemView);
    }

    @OnClick(R.id.toggle_bar)
    public void toggleContent() {
        if(expandable_content.isShown()) {
            // animate
            AnimatorHelper.slide_up(context, expandable_content);
            expandable_content.setVisibility(View.GONE);
        } else {
            expandable_content.setVisibility(View.VISIBLE);
            AnimatorHelper.slide_down(context, expandable_content);
        }
    }
}
