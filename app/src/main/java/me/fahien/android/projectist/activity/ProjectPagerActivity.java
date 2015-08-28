package me.fahien.android.projectist.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.UUID;

import me.fahien.android.projectist.R;
import me.fahien.android.projectist.fragment.ProjectFragment;
import me.fahien.android.projectist.model.Project;
import me.fahien.android.projectist.model.ProjectLab;

/**
 * Project Pager Activity
 *
 * @author Fahien
 */
public class ProjectPagerActivity extends AppCompatActivity {
	private ViewPager viewPager;
	private ArrayList<Project> projects;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewPager = new ViewPager(this);
		viewPager.setId(R.id.viewPager);
		setContentView(viewPager);

		// Get the data set from ProjectLab
		projects = ProjectLab.get(this).getProjects();

		// Get the activity instance of FragmentManager
		FragmentManager fragmentManager = getSupportFragmentManager();
		viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
			@Override public Fragment getItem(int position) {
				Project project = projects.get(position);
				return ProjectFragment.newInstance(project.getId());
			}

			@Override public int getCount() {
				return projects.size();
			}
		});

		UUID projectId = (UUID) getIntent().getSerializableExtra(ProjectFragment.EXTRA_PROJECT_ID);
		for (int i = 0; i < projects.size(); i++) {
			if (projects.get(i).getId().equals(projectId)) {
				viewPager.setCurrentItem(i);
				break;
			}
		}

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override public void onPageScrollStateChanged(int state) {}

			@Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

			@Override public void onPageSelected(int position) {
				Project project = projects.get(position);
				if (project.getName() != null) {
					setTitle(project.getName());
				}
			}
		});
	}
}
