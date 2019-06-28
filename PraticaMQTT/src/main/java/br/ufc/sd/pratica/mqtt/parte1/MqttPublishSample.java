package br.ufc.sd.pratica.mqtt.parte1;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

public class MqttPublishSample {

	public static void main(String[] args) {

		while (true) {

			String topic = "SD2019/mqtt";
			String content = "Message from Salomao Santos";
			int qos = 2;
			String broker = "tcp://iot.eclipse.org:1883";
			String clientId = "434704-pub";

			try {
				MqttClient mqttClient = new MqttClient(broker, clientId);
				// Mqtt ConnectOptions is used to set the additional features to mqtt message

				MqttConnectOptions connOpts = new MqttConnectOptions();

				connOpts.setCleanSession(true); // no persistent session
				connOpts.setKeepAliveInterval(1000);

				MqttMessage message = new MqttMessage(content.getBytes());
				
				message.setQos(qos); // sets qos level 1
				
				message.setRetained(true); // sets retained message

				MqttTopic mqttTopic = mqttClient.getTopic(topic);// add topic to publish

				mqttClient.connect(connOpts); // connects the broker with connect options
				
				mqttTopic.publish(message); // publishes the message to the topic(test/topic)

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				mqttClient.disconnect();
				

			} catch (MqttException me) {

				System.out.println("reason " + me.getReasonCode());

				System.out.println("msg " + me.getMessage());

				System.out.println("loc " + me.getLocalizedMessage());

				System.out.println("cause " + me.getCause());

				System.out.println("excep " + me);

				me.printStackTrace();
			}

		}
	}

}
