package me.fahien.android.projectist.model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.UUID;

import me.fahien.android.projectist.util.ProjectistJSONSerializer;

/**
 * ProjectLab
 *
 * @author Fahien
 */
public class ProjectLab {
	private static final String TAG = ProjectLab.class.getSimpleName();
	private static final String FILENAME = "projects.json";

	private ArrayList<Project> projects;

	private ProjectistJSONSerializer serializer;

	private static ProjectLab projectLab;
	private Context context;

	private ProjectLab(Context context) {
		this.context = context;
		serializer = new ProjectistJSONSerializer(context, FILENAME);
		try {
			projects = serializer.loadProjects();
		} catch (Exception e) {
			projects = new ArrayList<>();
			Log.e(TAG, "Error loading projects: ", e);
		}
	}

	public static ProjectLab get(Context context) {
		if (projectLab == null) {
			projectLab = new ProjectLab(context.getApplicationContext());
		}
		return projectLab;
	}

	public void addProject(Project project) {
		projects.add(project);
	}

	public ArrayList<Project> getProjects() {
		return projects;
	}

	public Project getProject(UUID id) throws NoSuchElementException {
		for (Project p : projects) {
			if (p.getId().equals(id)) {
				return p;
			}
		}
		throw new NoSuchElementException("No project found with id: " + id.toString());
	}

	public void deleteProject(Project project) {
		projects.remove(project);
	}

	public boolean saveProjects() {
		try {
			serializer.saveProjects(projects);
			Log.d(TAG, "projects saved to file");
			return true;
		} catch (Exception e) {
			Log.e(TAG, "Error saving projects: ", e);
			return false;
		}
	}
}
