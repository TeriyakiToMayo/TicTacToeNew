package com.wiley.fordummies.androidsdk.tictactoe;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Hashtable;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by adamcchampion on 2017/08/14.
 */

public class SensorsFragment extends Fragment implements SensorEventListener {
    private RecyclerView mSensorRecyclerView;
    private SensorAdapter mAdapter;
    private SensorManager mSensorManager;
    private List<Sensor> mSensorList;
    private Hashtable<String, float[]> lastSensorValues = new Hashtable<String, float[]>();

    private final String TAG = getClass().getSimpleName();
    private static final float TOLERANCE = (float) 10.0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sensor_list, container, false);

        mSensorRecyclerView = (RecyclerView) v.findViewById(R.id.sensor_recycler_view);
        mSensorRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        if (mSensorManager != null) {
            mSensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
            mAdapter = new SensorAdapter(mSensorList);
            mSensorRecyclerView.setAdapter(mAdapter);
            mSensorRecyclerView.setItemAnimator(new DefaultItemAnimator());
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(getResources().getString(R.string.sensors));
            }
        }
        catch (NullPointerException npe) {
            Log.e(TAG, "Could not set subtitle");
        }

        // Start listening to sensor updates
        for (Sensor sensor : mSensorList) {
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "Entering onPause");
        super.onPause();
        // Stop updates when paused
        mSensorManager.unregisterListener(this);
        Log.d(TAG, "Leaving onPause");
    }


    public String getSensorDescription(Sensor sensor) {
        return "Sensor: " + sensor.getName() + "; Ver :" + sensor.getVersion() + "; Range: " +
                sensor.getMaximumRange() + "; Power: " + sensor.getPower() + "; Res: " + sensor.getResolution();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        String sensorName = sensorEvent.sensor.getName();
        String lastValueString = "No previous value";
        String sensorEventString = sensorEventToString(sensorEvent);
        float percentageChange = (float) 1000.0 + TOLERANCE; // Some value greater than tolerance
        float distanceOfLastValue = (float) 0.0;
        float distanceOfThisValue = (float) 0.0;
        float change = (float) 0.0;

        float[] lastValue = lastSensorValues.get(sensorName);
        lastSensorValues.remove(sensorName); // Hash table is "open" and can store multiple entries for the same key
        lastSensorValues.put(sensorName, sensorEvent.values.clone()); // update the value
        if (lastValue != null) {
            // Compute distance of new value, change and percentage change
            StringBuilder builder = new StringBuilder();
            distanceOfLastValue = (float) 0.0;
            for (int i = 0; i < sensorEvent.values.length; i++) {
                distanceOfLastValue = distanceOfLastValue + (float) Math.pow(lastValue[i], 2);
                distanceOfThisValue = distanceOfThisValue + (float) Math.pow(sensorEvent.values[i], 2);
                change = change + (float) Math.pow((sensorEvent.values[i] - lastValue[i]), 2);
                builder.append("   [");
                builder.append(i);
                builder.append("] = ");
                builder.append(lastValue[i]);
            }
            lastValueString = builder.toString();
            change = (float) Math.sqrt(change);
            distanceOfLastValue = (float) Math.sqrt(distanceOfLastValue);
            distanceOfThisValue = (float) Math.sqrt(distanceOfThisValue);

            percentageChange = (float) 1000.0 + TOLERANCE; // large value > tolerance
            if (distanceOfLastValue != 0.0)
                percentageChange = change * (float) 100.0 / distanceOfLastValue;
            else if (distanceOfThisValue != 0.0)
                percentageChange = change * (float) 100.0 / distanceOfThisValue;
            else percentageChange = (float) 0.0; // both distances are zero

        }
        Log.d(TAG, "--- EVENT Raw Values ---\n" + sensorName + "\n" +
                "Distance  Last= >" + distanceOfLastValue + "<\n" +
                "Distance  This= >" + distanceOfThisValue + "<\n" +
                "Change = >" + change + "<\n" +
                "Percent = >" + percentageChange + "%\n" +
                "Last value = " + lastValueString + "<\n" +
                sensorEventString);
        if (lastValue == null ||
                percentageChange > TOLERANCE) {
            Log.d(TAG + sensorName,
                    "--- Event Changed --- \n" +
                            "Change = >" + change + "<\n" +
                            "Percent = >" + percentageChange + "%\n" +
                            sensorEventString);
        }
    }

    private String sensorEventToString(SensorEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append("Sensor: ");
        builder.append(event.sensor.getName());
        builder.append("\nAccuracy: ");
        builder.append(event.accuracy);
        builder.append("\nTimestamp: ");
        builder.append(event.timestamp);
        builder.append("\nValues:\n");
        for (int i = 0; i < event.values.length; i++) {
            builder.append("   [");
            builder.append(i);
            builder.append("] = ");
            builder.append(event.values[i]);
        }
        builder.append("\n");
        return builder.toString();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private class SensorHolder extends RecyclerView.ViewHolder {
        private Sensor mSensor;
        private String mDescriptionStr;
        private TextView mSensorInfoTextView;

        public SensorHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_sensor, parent, false));

            mSensorInfoTextView = (TextView) itemView.findViewById(R.id.sensor_data);
        }

        public void bind(Sensor sensor) {
            mSensor = sensor;
            mDescriptionStr = getSensorDescription(sensor);
            mSensorInfoTextView.setText(mDescriptionStr);
        }
    }

    private class SensorAdapter extends RecyclerView.Adapter<SensorHolder> {
        private List<Sensor> mSensorList;

        public SensorAdapter(List<Sensor> sensorList) {
            mSensorList = sensorList;
        }

        @Override
        public SensorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new SensorHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(SensorHolder holder, int position) {
            Sensor sensor = SensorsFragment.this.mSensorList.get(position);
            String sensorDescription = getSensorDescription(sensor);
            holder.bind(sensor);
        }

        @Override
        public int getItemCount() {
            return mSensorList.size();
        }
    }
}
