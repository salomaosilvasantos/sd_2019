package br.ufc.sd.pratica.mqtt.parte1;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttCallbackImpl implements MqttCallback {

	@Override
	public void connectionLost(Throwable cause) {
		System.err.println(cause);
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		System.out.println("topic: " + topic);
		System.out.println("mensagem: " + message.toString());
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		System.out.println(token.toString());
	}

}
