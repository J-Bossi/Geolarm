package de.jbossi.geolarm.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import de.jbossi.geolarm.R;
import de.jbossi.geolarm.models.Alarm;

/**
 * Created by Johannes on 19.06.2015.
 */
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {
    private List<Alarm> mAlarmList;


    public AlarmAdapter(List<Alarm> alarms) {
        super();
        this.mAlarmList = alarms;

    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView itemName;
        public TextView itemDistance;
        public TextView itemPlace;
        public Switch itemArmedSwitch;

        public ViewHolder(View view) {
            super(view);
            itemName = (TextView) view.findViewById(R.id.item_name);
            itemDistance = (TextView) view.findViewById(R.id.item_distance);
            itemPlace = (TextView) view.findViewById(R.id.item_place);
            itemArmedSwitch = (Switch) view.findViewById(R.id.item_armedswitch);
        }
    }


    public AlarmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_alarm, parent, false);
        return new ViewHolder(view);
    }


    public void onBindViewHolder(AlarmAdapter.ViewHolder holder, int position) {
        Alarm alarm = mAlarmList.get(position);
        holder.itemName.setText(alarm.getName());
        holder.itemDistance.setText(String.format("Distance: %.0f", alarm.getDistance()));
        holder.itemPlace.setText(String.format("lat=%.3f long=%.3f", alarm.getPosition().latitude, alarm.getPosition().longitude));
        holder.itemArmedSwitch.setChecked(alarm.isArmed());
    }

    public int getItemCount() {
        return mAlarmList.size();
    }

}
