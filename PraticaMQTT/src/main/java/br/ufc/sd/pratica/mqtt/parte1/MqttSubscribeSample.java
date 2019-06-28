package br.ufc.sd.pratica.mqtt.parte1;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttSubscribeSample {

	public static void main(String[] args) {

		String myTopic = "SD2019/mqtt";
		String broker = "tcp://iot.eclipse.org:1883";
		String clientId = "434704-pub";

		try {

			MqttCallback mqttCallback = new MqttCallbackImpl();

			MqttConnectOptions mqOptions = new MqttConnectOptions();
			mqOptions.setCleanSession(true);

			MqttClient client = new MqttClient(broker, clientId);
			client.setCallback(mqttCallback);// create here callback

			client.connect(mqOptions); // connecting to broker
			client.subscribe(myTopic); // subscribing to the topic name test/topic
			client.disconnect();
			
			
		} catch (MqttException me) {
			
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
		}
	}

}
