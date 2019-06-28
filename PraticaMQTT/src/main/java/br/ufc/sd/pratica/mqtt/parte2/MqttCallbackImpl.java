package br.ufc.sd.pratica.mqtt.parte2;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttCallbackImpl implements MqttCallback {

	private GameClass gameClass;

	MqttCallbackImpl(GameClass gameClass) {
		this.gameClass = gameClass;
	}

	@Override
	public void connectionLost(Throwable cause) {
		System.err.println(cause);
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {

		System.out.println("topic: " + topic);
		System.out.println("jogada recebida");

		String msg = message.toString(); // retrieve topic msg from MqttMessage

		String[] split = msg.split(",");
		int x = Integer.valueOf(split[0].replace("x: ", ""));
		int y = Integer.valueOf(split[1].replace(" y: ", ""));
		int playerId = Integer.valueOf(split[2].replace(" player: ", ""));

		gameClass.gameMatrix[x][y] = playerId;

		gameClass.checkGameState();
		gameClass.printBoard();

		if (playerId != gameClass.playerId) {
			gameClass.setIsMyMove(true);
			gameClass.jogada();
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		if (gameClass.isOver)
			System.exit(0);
	}

}
