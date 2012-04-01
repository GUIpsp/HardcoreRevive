package net.guipsp.hardcorerevive;

import java.util.Stack;

import javax.persistence.*;

import com.avaje.ebean.validation.*;

@Entity
@Table(name = "HardcoreRevive")
public class Databaseficator {
	@Id
	private int id;
	@Length(max = 30)
	@NotEmpty
	private String playerName;
	@NotEmpty
	private String status;
	private Stack<int[]> coords = new Stack<int[]>();

	public Stack<int[]> getCoords() {
		return coords;
	}

	public int[] getSingleCoords() {
		int[] coords = this.coords.get(0);
		this.coords.remove(0);
		return coords;
	}

	public int getId() {
		return id;
	}

	public String getPlayerName() {
		return playerName;
	}

	public String getStatus() {
		return status;
	}

	public void setCoords(Stack<int[]> coords) {
		this.coords = coords;
	}

	public void setSingleCoords(int[] coords) {
		this.coords.push(coords);
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
