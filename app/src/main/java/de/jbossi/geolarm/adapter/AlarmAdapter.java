package de.jbossi.geolarm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

import com.rey.material.app.Dialog;
import com.rey.material.widget.Slider;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import de.jbossi.geolarm.R;
import de.jbossi.geolarm.data.AlarmRepository;
import de.jbossi.geolarm.models.Alarm;

import static de.jbossi.geolarm.R.styleable.RecyclerView;

/**
 * Created by Johannes on 19.06.2015.
 */
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> implements Observer {

    protected static final String TAG = "alarmadapter";

    private List<Alarm> mAlarmList;
    private AlarmRepository mAlarmRepository;
    private Context context;

    public AlarmAdapter(List<Alarm> alarms, Context context) {
        super();
        this.mAlarmList = alarms;
        mAlarmRepository = AlarmRepository.getInstance(context);
        mAlarmRepository.addObserver(this);
        this.context = context;
    }

    /**
     * Listens to Changes in the AlarmRepository and notifies that a single alarm changed its value
     *
     * @param observable AlarmRepository
     * @param data       The position of the Alarm in the List as Integer value
     */
    public void update(Observable observable, Object data) {
        if (observable != mAlarmRepository) {
            return;
        }
        notifyItemChanged((int) data);
    }

    public AlarmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_alarm, parent, false);
        return new ViewHolder(view, new SwitchListener());
    }

    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Alarm alarm = mAlarmList.get(position);
        holder.itemName.setText(alarm.getName());
        holder.itemDistance.setText(String.format("Distance: %.0f", alarm.getDistance()));
        holder.itemPlace.setText(String.format("lat=%.3f long=%.3f", alarm.getPosition().latitude, alarm.getPosition().longitude));
        holder.switchListener.updatePosition(position);
        holder.itemArmedSwitch.setChecked(alarm.isArmed());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                final Dialog setUpDialog = new Dialog(context);
                setUpDialog.title("Alarm wählen!")
                        .contentView(R.layout.set_alarm_dialog)
                        .positiveAction(R.string.ok).show();
                final Slider slider = (Slider) setUpDialog.findViewById(R.id.setAlarmDialog_Distance);
                slider.setValue(alarm.getDistance(), true);
                slider.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
                    @Override
                    public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
                        alarm.setDistance(slider.getExactValue());
                    }
                });
                alarm.setDistance(slider.getExactValue());

                setUpDialog.positiveActionClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        mAlarmRepository.updateAlarm(alarm);
                        setUpDialog.dismiss();
                        notifyItemChanged(position);
                    }
                });

                TextView editLocation = (TextView) setUpDialog.findViewById(R.id.setAlarmDialog_Location);
                editLocation.setText(alarm.getName());
                setUpDialog.show();

                return true;
            }
        });
    }

    public int getItemCount() {
        return mAlarmList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public TextView itemDistance;
        public TextView itemPlace;
        public Switch itemArmedSwitch;
        public SwitchListener switchListener;

        public ViewHolder(View view, SwitchListener switchListener) {
            super(view);
            itemName = (TextView) view.findViewById(R.id.item_name);
            itemDistance = (TextView) view.findViewById(R.id.item_distance);
            itemPlace = (TextView) view.findViewById(R.id.item_place);
            itemArmedSwitch = (Switch) view.findViewById(R.id.item_armedswitch);
            this.switchListener = switchListener;
            itemArmedSwitch.setOnCheckedChangeListener(this.switchListener);
        }
    }

    private class SwitchListener implements OnCheckedChangeListener {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mAlarmList.get(position).setArmed(isChecked);
            if (isChecked) {
                mAlarmRepository.rearmAlarm(mAlarmList.get(position).getId());
            } else {
                mAlarmRepository.disarmAlarm(mAlarmList.get(position).getId());
            }
        }
    }


}
