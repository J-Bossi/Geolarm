package de.jbossi.geolarm.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import de.jbossi.geolarm.R;
import de.jbossi.geolarm.adapter.AlarmAdapter;
import de.jbossi.geolarm.data.AlarmRepository;
import de.jbossi.geolarm.helper.GeofenceHandler;


/**
 * Created by Johannes on 19.06.2015.
 */
public class AlarmListActivity extends Activity {

    protected static final String TAG = "AlarmListActivity";
    public static final String GEOFENCE_ENTERED = "GEOFENCE_ENTERED";

    private RecyclerView mAlarmListRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private AlarmRepository mAlarmRepository;

    private GeofenceHandler mGeofenceHandler;

    private BroadcastReceiver mAlarmChangeReceiver;

    public AlarmListActivity() {
        super();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGeofenceHandler = new GeofenceHandler(this);

        mAlarmChangeReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String requestId = intent.getExtras().getString("REQUEST_ID");
                //removeGeofence(requestId);
                mAlarmRepository.disarmAlarm(requestId);
                mAlarmListRecyclerView.getAdapter().notifyItemChanged(mAlarmRepository.getPositionFromID(requestId));
                Log.i(TAG, "Geofence Entered. Trying to disarm geofence with ID: " + requestId);
            }
        };

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        broadcastManager.registerReceiver(mAlarmChangeReceiver, new IntentFilter(GEOFENCE_ENTERED));

        mAlarmRepository = AlarmRepository.getInstance(this);

        setContentView(R.layout.activity_list);
        mAlarmListRecyclerView = (RecyclerView) findViewById(R.id.alarm_list);

        mLayoutManager = new LinearLayoutManager(this);
        mAlarmListRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new AlarmAdapter(mAlarmRepository.getmAlarms());

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                //Removes Geofence
                mGeofenceHandler.removeGeofence(mAlarmRepository.getmAlarms().get(position).getId());

                //Deletes data
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
        super.onStop();
    }
}
