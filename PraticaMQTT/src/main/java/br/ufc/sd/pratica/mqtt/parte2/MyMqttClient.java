package br.ufc.sd.pratica.mqtt.parte2;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.MqttTopic;

public class MyMqttClient {

	String myGameId = "434704-salomaosantos"; // create here your unique id and share it with your friend
	String myGameTopic = "SD2019/praticajogo/" + myGameId;
	String broker = "tcp://iot.eclipse.org:1883";
	String clientId = String.valueOf(System.nanoTime());
	int qos = 2;// change QOS and make some tests
	private MqttClient client;
	private int playerId;

	public MyMqttClient(int playerId) throws MqttException {
		this.playerId = playerId;
		client = new MqttClient(broker, clientId); // Instantiate here mqtt client
	}

	public void connect() throws MqttSecurityException, MqttException {
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setKeepAliveInterval(1000);
		connOpts.setCleanSession(true);

		client.connect(connOpts); // connect to mqtt broker using the connOpts
	}

	public void subscribe(MqttCallback mqttCalback) throws MqttException {

		client.setCallback(mqttCalback); // set client callback using MqttClient.setCallback

		client.subscribe(myGameTopic); // subscribing to the topic of your game using MqttClient.subscribe
	}

	public void publishMessage(int lin, int col) {

		MqttMessage message = new MqttMessage(getMessageString(lin, col).getBytes()); // create a new MqttMessage using
																						// aspayload the string of
																						// method getMessageString(lin,
																						// col)

		message.setQos(qos); // set your QoS using MqttMessage.setQos(qos);

		MqttTopic topic = client.getTopic(myGameTopic); // get the topic of your game MqttClient.getTopic

		try {
			topic.publish(message); // publish the message
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	private String getMessageString(int lin, int col) {
		return String.format("x: %s, y: %s, player: %s", lin, col, playerId);
	}

}
