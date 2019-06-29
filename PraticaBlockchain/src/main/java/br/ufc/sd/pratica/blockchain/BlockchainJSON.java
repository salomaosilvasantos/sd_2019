package br.ufc.sd.pratica.blockchain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import br.ufc.sd.pratica.blockchain.model.Vote;

/**
 * @author salomao
 *
 */
public class BlockchainJSON {

	/*
	 * Metodo que gera blocos e adiciona em um arquivo Json
	 */
	private static void addVotes(String filePath) {

		ArrayList<Vote> votes = new ArrayList<Vote>();

		Vote v0 = new Vote(1, "Joao", "0");

		votes.add(v0);

		Vote v1 = new Vote(2, "Jose ", v0.getHash());

		votes.add(v1);

		Vote v2 = new Vote(3, "Maria", v1.getHash());

		votes.add(v2);

		Vote v3 = new Vote(4, "Joao", v2.getHash());

		votes.add(v3);

		Vote v4 = new Vote(5, "Jose", v3.getHash());

		votes.add(v4);

		Vote v5 = new Vote(6, "Jose", v4.getHash());

		votes.add(v5);

		Gson gson = new Gson();

		try {

			String jsonString = gson.toJson(votes);
			FileWriter writer = new FileWriter(filePath);
			writer.write(jsonString);
			writer.close();

			System.out.println("The votes of election were saved in the folder: " + filePath);

		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * Metodo que ler votos de um arquivo Json e valida o blockchain. Verifica se o
	 *  bloco sucessor possui o hash do bloco anterior igual.
	 */
	
	private static boolean valid(String filePath) {
		Gson gson = new Gson();

		try {

			BufferedReader bufferReader = new BufferedReader(new FileReader(filePath));

			Vote[] votes = gson.fromJson(bufferReader, Vote[].class);

			for (int j = votes.length - 1; j > 1; j--) {

				if (!votes[j].getPreviousHash().equalsIgnoreCase(votes[j - 1].getHash())) {

					return false;

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;

	}

	public static void main(String[] args) {

		String filePath = new File("src/main/resources/votes.json").getAbsolutePath().toString();

		addVotes(filePath);

		boolean isValid = valid(filePath);

		if (isValid)
			System.out.println("The blockchain is Valid!");
		else
			System.out.println("The blockchain is Invalid!");
	}

}
