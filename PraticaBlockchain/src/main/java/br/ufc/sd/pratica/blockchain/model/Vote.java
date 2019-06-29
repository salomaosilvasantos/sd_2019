package br.ufc.sd.pratica.blockchain.model;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import com.google.common.hash.Hashing;

public class Vote {

	private int identifier;

	private String data;

	private String hash;

	private String previousHash;

	
	private String dateTime;

	public Vote(int identifier, String data, String PreviousHash) {

		super();
		this.identifier = identifier;


		if (identifier == 0) {

			this.previousHash = "0";

		} else {

			this.previousHash = PreviousHash;

		}

		this.hash = getSHA256(identifier, dateTime, data, previousHash);

		this.dateTime = LocalDateTime.now().toString();

		this.data = data;
 
	}

	public Vote(int identifier, String dateTime, String data, String hash, String previousHash) {
		super();
		this.identifier = identifier;
		this.data = data;
		this.hash = hash;
		this.previousHash = previousHash;
		this.dateTime = dateTime; 
	}

	/*
	 * Metodo que gera um bloco usando um identificador, data e horário, o nome do
	 * presidente votado e hash do bloco anterior.
	 */
	private String getSHA256(int identifier, String dateTime, String data, String previousHash) {

		CharSequence sequence = Integer.toString(identifier) + dateTime + data + previousHash;

		String sha256 = Hashing.sha256().hashString(sequence, StandardCharsets.UTF_8).toString();

		return sha256;
	}

	public int getIdentifier() {
		return identifier;
	}

	public String getDateTime() {
		return dateTime;
	}

	public String getData() {
		return data;
	}

	public String getHash() {
		return hash;
	}

	public String getPreviousHash() {
		return previousHash;
	}

}
