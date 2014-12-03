package com.genesys.gms.mobile.push.demo.ui.expandableRecycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.genesys.gms.mobile.push.demo.R;
import org.joda.time.DateTimeZone;

import java.util.List;

/**
 * Created by stau on 12/2/2014.
 * Pretty nasty code
 */
public class ExpandableViewAdapter extends RecyclerView.Adapter<ExpandableViewHolder> {
    private List<LogEntry> logEntries;

    public ExpandableViewAdapter(List<LogEntry> logEntries) {
        this.logEntries = logEntries;
    }

    @Override
    public ExpandableViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.expandable_logcat_item,
                viewGroup,
                false
        );
        return new ExpandableViewHolder(viewGroup.getContext(), view);
    }

    @Override
    public void onBindViewHolder(ExpandableViewHolder vh, int i) {
        LogEntry entry = logEntries.get(i);
        vh.reset();
        vh.title.setText(entry.getTitle());
        // TODO: This might need to be a bit more flexible for theme changes
        if(entry.getMessageType() == LogEntry.MessageType.REQUEST) {
            vh.icon.setImageResource(R.drawable.ic_call_made_grey600_18dp);
        } else {
            vh.icon.setImageResource(R.drawable.ic_call_received_grey600_18dp);
        }
        vh.timestamp.setText(
                entry.getTimestamp().withZone(DateTimeZone.UTC).toString(
                        "yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'"
                )
        );
        vh.content.setText(entry.getContent());
    }

    @Override
    public int getItemCount() {
        return logEntries.size();
    }
}
