package br.ufc.sd.pratica.mqtt.parte2;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

public class PlayGame {
	
	public static void main(String[] args) throws MqttSecurityException, MqttException {
		GameClass gameClass = new GameClass(1);//use 1 or 2
		gameClass.startGame();
	}
}
