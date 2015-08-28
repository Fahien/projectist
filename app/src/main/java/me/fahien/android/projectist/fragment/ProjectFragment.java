package me.fahien.android.projectist.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

import me.fahien.android.projectist.R;
import me.fahien.android.projectist.model.Project;
import me.fahien.android.projectist.model.ProjectLab;

/**
 * Project Fragment
 *
 * @author Fahien
 */
public class ProjectFragment extends Fragment {
	public static final String EXTRA_PROJECT_ID = "me.fahien.android.projectist.project_id";

	private static final String DIALOG_DATE = "date";
	private static final int REQUEST_DATE = 0;
	private static final int REQUEST_CONTACT = 1;

	private Project project;
	private EditText nameField;
	private Button dateButton;
	private CheckBox finishedCheckBox;
	private Button coworkerButton;

	public static ProjectFragment newInstance(UUID projectId) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_PROJECT_ID, projectId);
		ProjectFragment fragment = new ProjectFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		UUID projectId = (UUID) getArguments().getSerializable(EXTRA_PROJECT_ID);
		project = ProjectLab.get(getActivity()).getProject(projectId);
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_project, parent, false);
		turnOnUpButton();

		nameField = (EditText) v.findViewById(R.id.project_title);
		nameField.setText(project.getName());
		nameField.addTextChangedListener(new TextWatcher() {
			@Override public void beforeTextChanged(CharSequence c, int start, int count, int after) {}

			@Override public void onTextChanged(CharSequence c, int start, int before, int count) {
				project.setName(c.toString());
			}

			@Override public void afterTextChanged(Editable c) {}
		});

		dateButton = (Button) v.findViewById(R.id.project_date);
		dateButton.setText(DateFormat.getDateInstance().format(project.getDate()));
		dateButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {
				FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
				DatePickerFragment dialog = DatePickerFragment.newInstance(project.getDate());
				dialog.setTargetFragment(ProjectFragment.this, REQUEST_DATE);
				dialog.show(fragmentManager, DIALOG_DATE);
			}
		});

		finishedCheckBox = (CheckBox) v.findViewById(R.id.project_finished);
		finishedCheckBox.setChecked(project.isFinished());
		finishedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				project.setFinished(isChecked);
			}
		});

		Button reportButton = (Button) v.findViewById(R.id.project_reportButton);
		reportButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {
				// This constructor accepts a string that is a constant defining the action
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("text/plain");
				i.putExtra(Intent.EXTRA_SUBJECT, R.string.project_report_subject);
				i.putExtra(Intent.EXTRA_TEXT, getProjectReport());
				i = Intent.createChooser(i, getString(R.string.send_report));
				startActivity(i);
			}
		});

		coworkerButton = (Button) v.findViewById(R.id.project_coworkerButton);
		coworkerButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {
				Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(i, REQUEST_CONTACT);
			}
		});

		if (project.getCoworker() != null) {
			coworkerButton.setText(project.getCoworker());
		}

		return v;
	}

	@TargetApi(11) private void turnOnUpButton() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			AppCompatActivity activity = (AppCompatActivity) getActivity();
			if (activity != null) {
				// No parent, no caret
				if (NavUtils.getParentActivityName(activity) != null) {
					ActionBar bar = activity.getSupportActionBar();
					if (bar != null) {
						bar.setDisplayHomeAsUpEnabled(true);
					}
				}
			}
		}
	}

	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.fragment_project, menu);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				// Check to see if there is a parent activity named in the metadata
				if (NavUtils.getParentActivityName(getActivity()) != null) {
					// Navigate to the parent activity
					NavUtils.navigateUpFromSameTask(getActivity());
				}
				return true;
			case R.id.menu_delete_project:
				ProjectLab.get(getActivity()).deleteProject(project);
				// Check to see if there is a parent activity named in the metadata
				if (NavUtils.getParentActivityName(getActivity()) != null) {
					// Navigate to the parent activity
					NavUtils.navigateUpFromSameTask(getActivity());
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}


	@Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) return;
		if (requestCode == REQUEST_DATE) {
			Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			project.setDate(date);
			dateButton.setText(DateFormat.getDateInstance().format(project.getDate()));
		} else if (requestCode == REQUEST_CONTACT) {
			Uri contactUri = data.getData();
			// Specify which fields you want your query to return
			String[] queryFields = new String[] { ContactsContract.Contacts.DISPLAY_NAME };
			// Perform your query
			Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
			// Double check that you actually got results
			if (c.getCount() == 0) {
				c.close();
				return;
			}
			// Pull out the first column of the first row of data
			c.moveToFirst();
			String coworker = c.getString(0);
			project.setCoworker(coworker);
			coworkerButton.setText(coworker);
			c.close();
		}
	}

	@Override public void onPause() {
		super.onPause();
		ProjectLab.get(getActivity()).saveProjects();
	}

	/**
	 * Returns a complete report
	 */
	private String getProjectReport() {
		String finishedString;
		if (project.isFinished()) {
			finishedString = getString(R.string.project_report_finished);
		} else {
			finishedString = getString(R.string.project_report_unfinished);
		}

		String dateString = DateFormat.getInstance().format(project.getDate());

		String coworker = project.getCoworker();
		if (coworker == null) {
			coworker = getString(R.string.project_report_no_coworker);
		} else {
			coworker = getString(R.string.project_report_coworker, coworker);
		}

		return getString(R.string.project_report, project.getName(), dateString, finishedString, coworker);
	}
}
