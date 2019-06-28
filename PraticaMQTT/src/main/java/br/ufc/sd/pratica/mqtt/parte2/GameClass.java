package br.ufc.sd.pratica.mqtt.parte2;

import java.util.Scanner;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

public class GameClass {

	private static final String O_JOGADOR_COM_O_GANHOU = "O jogador com 'O' ganhou";
	private static final String O_JOGADOR_COM_X_GANHOU = "O jogador com 'X' ganhou";
	int playerId; // Use 1 or 2

	boolean isOver = false;
	boolean isMyMove = false;
	String statusText = "";
	MyMqttClient myMqttClient;

	int[][] gameMatrix = new int[][] { { -1, -1, -1 }, { -1, -1, -1 }, { -1, -1, -1 } };

	public GameClass(int i) {
		playerId = i;
	}

	public void jogada() {
		if (!isMyMove || isOver)
			return;

		System.out.println("digite linha que deseja jogar: \n");
		int lin = Integer.valueOf(readFromConsole());
		System.out.println("digite coluna que deseja jogar: \n");
		int col = Integer.valueOf(readFromConsole());

		if (gameMatrix[lin][col] != -1) {
			System.out.println("Posição já foi escolhida");
			jogada();
		} else {
			myMqttClient.publishMessage(lin, col);
			
			atualizarBoard(lin, col, playerId);
			checkGameState();
			printBoard();
			isMyMove = false;
		}
	}

	@SuppressWarnings("resource")
	private String readFromConsole() {
		return new Scanner(System.in).nextLine();
	}

	void atualizarBoard(int lin, int col, int playerId) {
		gameMatrix[lin][col] = Integer.valueOf(playerId);
	}

	void printBoard() {
		System.out.println("Game state: " + statusText);
		for (int i = 0; i <= 2; i++)
			System.out.println(String.format("| %s | %s | %s |", getStrJogo(gameMatrix[i][0]), getStrJogo(gameMatrix[i][1]),
					getStrJogo(gameMatrix[i][2])));

	}

	void checkGameState() {
		for (int i = 0; i <= 2; i++) {
			int lin0 = gameMatrix[i][0];
			int lin1 = gameMatrix[i][1];
			int lin2 = gameMatrix[i][2];
			boolean linHasEmpty = false;

			if (lin0 == -1 || lin1 == -1 || lin2 == -1) {
				linHasEmpty = true;
			}

			int resultLinha = lin0 + lin1 + lin2;

			if (!linHasEmpty && resultLinha == 3) {
				isOver = true;
				statusText = O_JOGADOR_COM_O_GANHOU;
				return;
			}

			if (!linHasEmpty && resultLinha == 6) {
				isOver = true;
				statusText = O_JOGADOR_COM_X_GANHOU;
				return;
			}

			int col0 = gameMatrix[0][i];
			int col1 = gameMatrix[1][i];
			int col2 = gameMatrix[2][i];
			boolean colHasEmpty = false;

			if (col0 == -1 || col1 == -1 || col2 == -1) {
				colHasEmpty = true;
			}

			int resultColuna = col0 + col1 + col2;

			if (!colHasEmpty && resultColuna == 3) {
				isOver = true;
				statusText = O_JOGADOR_COM_O_GANHOU;
				return;
			}

			if (!colHasEmpty && resultColuna == 6) {
				isOver = true;
				statusText = O_JOGADOR_COM_X_GANHOU;
				return;
			}
		}

		int diag00 = gameMatrix[0][0];
		int diag11 = gameMatrix[1][1];
		int diag22 = gameMatrix[2][2];
		boolean diag1HasEmpty = false;

		if (diag00 == -1 || diag11 == -1 || diag22 == -1) {
			diag1HasEmpty = true;
		}

		int ResultDiag1 = diag00 + diag11 + diag22;

		if (!diag1HasEmpty && ResultDiag1 == 3) {
			isOver = true;
			statusText = O_JOGADOR_COM_O_GANHOU;
			return;
		}

		if (!diag1HasEmpty && ResultDiag1 == 6) {
			isOver = true;
			statusText = O_JOGADOR_COM_X_GANHOU;
			return;
		}

		int diag02 = gameMatrix[0][2];
		int diag20 = gameMatrix[2][0];
		int ResultDiag2 = diag02 + diag11 + diag20;
		boolean diag2hasEmpty = false;

		if (diag02 == -1 || diag11 == -1 || diag20 == -1) {
			diag2hasEmpty = true;
		}

		if (!diag2hasEmpty && ResultDiag2 == 3) {
			isOver = true;
			statusText = O_JOGADOR_COM_O_GANHOU;
			return;
		}

		if (!diag2hasEmpty && ResultDiag2 == 6) {
			isOver = true;
			statusText = O_JOGADOR_COM_X_GANHOU;
			return;
		}

		boolean temVazio = false;
		for (int i = 0; i < gameMatrix.length; i++) {
			for (int j = 0; j < gameMatrix.length; j++) {
				if (gameMatrix[i][j] == -1)
					temVazio = true;
			}
		}

		if (!temVazio) {
			isOver = true;
			statusText = "Deu velha";
		}
	}

	private Object getStrJogo(int i) {
		if (i == 1)
			return " O ";

		if (i == 2)
			return " X ";

		return "  ";
	}

	public void setIsMyMove(boolean b) {
		this.isMyMove = b;
	}

	public void startGame() throws MqttSecurityException, MqttException {

		myMqttClient = new MyMqttClient(playerId);

		myMqttClient.connect();
		myMqttClient.subscribe(new MqttCallbackImpl(this));

		if (playerId == 1)
			isMyMove = true;

		while (true)
			jogada();

	}
}
