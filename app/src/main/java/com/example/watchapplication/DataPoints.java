package com.example.watchapplication;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Arrays;
import java.util.Objects;


public class DataPoints extends AppCompatActivity {
    private static final String BROKER_URI = "tcp://test.mosquitto.org:1883"; // Update with your MQTT broker URI
    private static final String CLIENT_ID = MqttClient.generateClientId();
    private static final String TOPIC_HR = "/test/topic/heartrate"; // Update with your MQTT topic
    private static final String TOPIC_RES = "/test/topic/respiratory"; // Update with your MQTT topic

    private MqttClient client;

    TextView TV_HR ;
    TextView TV_RES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_points);

        TV_HR = (TextView) findViewById(R.id.TV_HR);
        TV_RES = (TextView) findViewById(R.id.TV_RES);

        Button btn_home = findViewById(R.id.btn_home_data);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DataPoints.this, MainActivity.class);
                startActivity(intent);
            }
        });


        try {
            connect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        try {
            subscribe();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void connect() throws MqttException{

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        client = new MqttClient(BROKER_URI,CLIENT_ID,new MemoryPersistence());
        client.connect(options);
    }

    public void subscribe() throws MqttException{
        client.setCallback(new MqttEventCallback());
        client.subscribe(TOPIC_HR);
        client.subscribe(TOPIC_RES);
    }

    private class MqttEventCallback implements MqttCallback {
        @Override
        public void connectionLost(Throwable arg0) {
            // Do nothing
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            // Do nothing
        }
        int i = 0;
        @Override
        public void messageArrived(String topic, final MqttMessage msg) throws Exception {
            Log.i("TAG", "New Message Arrived from Topic - " + topic);
            String message = new String(msg.getPayload());
            Log.i("TAG", "Message - " + message);

            try {
                String sensorMessage = new String(msg.getPayload());
                if(Objects.equals(topic, TOPIC_HR)){
                    TV_HR.setText(sensorMessage);
                }
                else if (Objects.equals(topic, TOPIC_RES)){
                    TV_RES.setText(sensorMessage);
                }
            } catch (Exception ex) {
                Log.e("TAG", ex.getMessage());
            }
        }
    }

    public void toast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}