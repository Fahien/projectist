package me.fahien.android.projectist.activity;

import android.support.v4.app.Fragment;

import me.fahien.android.projectist.fragment.ProjectListFragment;

/**
 * ProjectListActivity
 *
 * @author Fahien
 */
public class ProjectListActivity extends SingleFragmentActivity {

	@Override protected Fragment createFragment() {
		return new ProjectListFragment();
	}
}

