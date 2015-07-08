package de.jbossi.geolarm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import de.jbossi.geolarm.R;
import de.jbossi.geolarm.models.Alarm;

/**
 * Created by Johannes on 19.06.2015.
 */
public class AlarmAdapter extends BaseAdapter {
    private List<Alarm> mAlarmList;
    private Context mContext;

    public AlarmAdapter(List<Alarm> alarms, Context context) {
        super();
        this.mAlarmList = alarms;
        this.mContext = context;
    }


    @Override
    public int getCount() {
        return mAlarmList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAlarmList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View currentRow, ViewGroup parent) {
        View view = currentRow;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listitem_alarm, parent, false);
            TextView itemName = (TextView) view.findViewById(R.id.item_name);
            TextView itemDistance = (TextView) view.findViewById(R.id.item_distance);
            TextView itemPlace = (TextView) view.findViewById(R.id.item_place);
            Switch itemArmedSwitch = (Switch) view.findViewById(R.id.item_armedswitch);

            Alarm alarm = mAlarmList.get(position);
            itemName.setText(alarm.getName());
            itemDistance.setText(Float.toString(alarm.getDistance()));
            itemPlace.setText(alarm.getName());
            itemArmedSwitch.setChecked(alarm.isArmed());
        }

        return view;
    }
}
