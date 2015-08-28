package me.fahien.android.projectist.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.security.Key;
import java.text.DateFormat;
import java.util.ArrayList;

import me.fahien.android.projectist.R;
import me.fahien.android.projectist.activity.ProjectPagerActivity;
import me.fahien.android.projectist.model.Project;
import me.fahien.android.projectist.model.ProjectLab;

/**
 * ProjectListFragment
 *
 * @author Fahien
 */
public class ProjectListFragment extends ListFragment {
	private boolean subtitleVisible;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.projects_title);

		ArrayList<Project> projects = ProjectLab.get(getActivity()).getProjects();

		ProjectAdapter adapter = new ProjectAdapter(projects);
		setListAdapter(adapter);

		setHasOptionsMenu(true);
		setRetainInstance(true);
		subtitleVisible = false;
	}

	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_fragment, parent, false);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (subtitleVisible) {
				AppCompatActivity activity = (AppCompatActivity)getActivity();
				if (activity != null) {
					ActionBar bar = activity.getSupportActionBar();
					if (bar != null) {
						bar.setSubtitle(R.string.subtitle);
					}
				}
			}
		}

			ListView listView = (ListView) view.findViewById(android.R.id.list);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			// Use floating context menus on Froyo and Gingerbread
			registerForContextMenu(listView);
		} else {
			initContextualActionBar(listView);
		}

		return view;
	}


	/**
	 * Use Contextual action bar on Honeycomb and higher
	 */
	@TargetApi(11)
	private void initContextualActionBar(ListView listView) {
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
			@Override public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
				// Required, but not used in this implementation
			}

			@Override public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.project_list_item_context, menu);
				return true;
			}

			@Override public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
				// Required, but not used in this implementation
				return false;
			}

			@Override public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
					case R.id.menu_item_delete_project:
						ProjectAdapter adapter = (ProjectAdapter) getListAdapter();
						ProjectLab lab = ProjectLab.get(getActivity());
						for (int i = adapter.getCount() - 1; i >= 0; i--) {
							if (getListView().isItemChecked(i)) {
								lab.deleteProject(adapter.getItem(i));
							}
						}
						mode.finish();
						adapter.notifyDataSetChanged();
						return true;
					default:
						return false;
				}
			}

			@Override public void onDestroyActionMode(ActionMode actionMode) {
				// Required, but not used in this implementation
			}
		});
	}

	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.fragment_project_list, menu);
		MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
		if (subtitleVisible && showSubtitle != null) {
			showSubtitle.setTitle(R.string.hide_subtitle);
		}
	}

	@TargetApi(11)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_new_project:
				Project project = new Project();
				ProjectLab.get(getActivity()).addProject(project);
				startProjectActivity(project);
				return true;
			case R.id.menu_item_show_subtitle:
				AppCompatActivity activity = (AppCompatActivity)getActivity();
				if (activity != null) {
					ActionBar bar = activity.getSupportActionBar();
					Log.i("Bar", "getting Bar");
					if (bar != null) {
						if (bar.getSubtitle() == null) {
							bar.setSubtitle(R.string.subtitle);
							subtitleVisible = true;
							item.setTitle(R.string.hide_subtitle);
							Log.i("Bar", "Setting Subtitle");
						}
						else {
							bar.setSubtitle(null);
							subtitleVisible = false;
							item.setTitle(R.string.show_subtitle);
							Log.i("Bar", "Nulling Subtitle");
						}
					}
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Starts Project Activity
	*/
	private void startProjectActivity(Project project) {
		Intent i = new Intent(getActivity(), ProjectPagerActivity.class);
		i.putExtra(ProjectFragment.EXTRA_PROJECT_ID, project.getId());
		startActivity(i);
	}

	@Override public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
		AppCompatActivity activity = (AppCompatActivity) getActivity();
		if (activity != null) {
			activity.getMenuInflater().inflate(R.menu.project_list_item_context, menu);
		}
	}

	@Override public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int position = info.position;
		ProjectAdapter adapter = (ProjectAdapter) getListAdapter();
		Project project = adapter.getItem(position);

		switch (item.getItemId()) {
			case R.id.menu_item_delete_project:
				ProjectLab.get(getActivity()).deleteProject(project);
				adapter.notifyDataSetChanged();
				return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override public void onListItemClick(ListView list, View view, int position, long id) {
		Project project = ((ProjectAdapter)getListAdapter()).getItem(position);

		startProjectActivity(project);
	}

	@Override public void onResume() {
		super.onResume();
		((ProjectAdapter)getListAdapter()).notifyDataSetChanged();
	}

	private class ProjectAdapter extends ArrayAdapter<Project> {

		public ProjectAdapter(ArrayList<Project> projects) {
			super(getActivity(), 0, projects);
		}

		@Override public View getView(int position, View convertView, ViewGroup parent) {
			// If we weren't given a view, inflate one
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_project, null);
			}

			// Configure the view for this Project
			Project p = getItem(position);
			TextView nameTextView = (TextView) convertView.findViewById(R.id.project_list_item_nameTextView);
			nameTextView.setText(p.getName());
			TextView dateTextView = (TextView) convertView.findViewById(R.id.project_list_item_dateTextView);
			dateTextView.setText(DateFormat.getDateInstance().format(p.getDate()));
			CheckBox finishedCheckBox = (CheckBox) convertView.findViewById(R.id.project_list_item_finishedCheckBox);
			finishedCheckBox.setChecked(p.isFinished());

			return convertView;
		}
	}
}
