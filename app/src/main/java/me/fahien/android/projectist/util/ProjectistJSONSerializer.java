package me.fahien.android.projectist.util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import me.fahien.android.projectist.model.Project;

/**
 * This has the responsibility of taking the existing
 * ArrayList of Projects and writing it as JSON
 */
public class ProjectistJSONSerializer {

	private Context context;
	private String fileName;

	public ProjectistJSONSerializer(Context context, String fileName) {
		this.context = context;
		this.fileName = fileName;
	}

	public void saveProjects(ArrayList<Project> projects) throws JSONException, IOException {
		// Build an array in JSON
		JSONArray array = new JSONArray();
		for (Project p : projects) {
			array.put(p.toJSON());
		}
		// Write the file to disk
		Writer writer = null;
		try {
			OutputStream out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public ArrayList<Project> loadProjects() throws IOException, JSONException {
		ArrayList<Project> projects = new ArrayList<>();
		BufferedReader reader = null;
		try {
			// Open and read the file into a StringBuilder
			InputStream in = context.openFileInput(fileName);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				// Line breaks are omitted and irrelevant
				jsonString.append(line);
			}
			// Parse the JSON using JSONTokener
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			// Build the array of crimes from JSONObjects
			for (int i = 0; i < array.length(); i++) {
				projects.add(new Project(array.getJSONObject(i)));
			}
		} catch (FileNotFoundException e) {
			// Ignore this one; it happens when starting fresh
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return projects;
	}
}
