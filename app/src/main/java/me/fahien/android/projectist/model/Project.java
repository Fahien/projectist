package me.fahien.android.projectist.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Project
 *
 * @author Fahien
 */
public class Project {
	private static final String JSON_ID = "id";
	private static final String JSON_NAME = "name";
	private static final String JSON_FINISHED = "finished";
	private static final String JSON_DATE = "date";
	private static final String JSON_COWORKER = "coworker";

	private UUID id;
	private String name;
	private Date date;
	private boolean finished;
	private String coworker;

	public Project() {
		id = UUID.randomUUID();
		date = new Date();
	}

	public Project(JSONObject json) throws JSONException{
		id = UUID.fromString(json.getString(JSON_ID));
		name = json.getString(JSON_NAME);
		finished = json.getBoolean(JSON_FINISHED);
		date = new Date(json.getLong(JSON_DATE));
		if (json.has(JSON_COWORKER)) {
			coworker = json.getString(JSON_COWORKER);
		}
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public String getCoworker() {
		return coworker;
	}

	public void setCoworker(String coworker) {
		this.coworker = coworker;
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_ID, id.toString());
		json.put(JSON_NAME, name);
		json.put(JSON_FINISHED, finished);
		json.put(JSON_DATE, date.getTime());
		json.put(JSON_COWORKER, coworker);
		return json;
	}

	@Override public String toString() {
		return name;
	}
}