package de.jbossi.geolarm.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import de.jbossi.geolarm.R;
import de.jbossi.geolarm.adapter.AlarmAdapter;
import de.jbossi.geolarm.data.AlarmRepository;
import de.jbossi.geolarm.models.Alarm;


/**
 * Created by Johannes on 19.06.2015.
 */
public class AlarmList extends Activity {

    private RecyclerView mAlarmListRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Alarm> mAlarms;

    private GoogleApiClient mGoogleApiClient;

    public AlarmList() {
        super();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mAlarmListRecyclerView = (RecyclerView) findViewById(R.id.alarm_list);

        mLayoutManager = new LinearLayoutManager(this);
        mAlarmListRecyclerView.setLayoutManager(mLayoutManager);

        mAlarms = AlarmRepository.getInstance(this).getmAlarms();
        mAdapter = new AlarmAdapter(mAlarms);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                removeGeofence(mAlarms.get(position).getId());
                AlarmRepository.getInstance(AlarmList.this).removeAlarm(mAlarms.get(position));
                mAlarmListRecyclerView.getAdapter().notifyItemRemoved(position);


            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mAlarmListRecyclerView);

        mAlarmListRecyclerView.setAdapter(mAdapter);
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }


    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private void removeGeofence(String geofenceId) {
        List<String> geofenceList = new ArrayList<String>();
        geofenceList.add(geofenceId);
        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, geofenceList);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

}
