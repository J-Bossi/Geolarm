package de.jbossi.geolarm.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import de.jbossi.geolarm.R;
import de.jbossi.geolarm.adapter.AlarmAdapter;
import de.jbossi.geolarm.data.AlarmRepository;
import de.jbossi.geolarm.helper.GeofenceHandler;


/**
 * Created by Johannes on 19.06.2015.
 */
public class AlarmListActivity extends Activity {

    protected static final String TAG = "alarmlist-activty";


    private RecyclerView mAlarmListRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private AlarmRepository mAlarmRepository;

    private GeofenceHandler mGeofenceHandler;

    public AlarmListActivity() {
        super();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGeofenceHandler = new GeofenceHandler(this);

        mAlarmRepository = AlarmRepository.getInstance(this);

        setContentView(R.layout.activity_list);
        mAlarmListRecyclerView = (RecyclerView) findViewById(R.id.alarm_list);

        mLayoutManager = new LinearLayoutManager(this);
        mAlarmListRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new AlarmAdapter(mAlarmRepository.getmAlarms(), this);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                mGeofenceHandler.removeGeofence(mAlarmRepository.getmAlarms().get(position).getId());
                mAlarmRepository.getmAlarms().remove(position);
                mAlarmListRecyclerView.getAdapter().notifyItemRemoved(position);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mAlarmListRecyclerView);

        mAlarmListRecyclerView.setAdapter(mAdapter);
    }

    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        mAlarmRepository.save();
        super.onStop();
    }
}
