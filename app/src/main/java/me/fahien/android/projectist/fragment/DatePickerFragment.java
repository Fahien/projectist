package me.fahien.android.projectist.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import me.fahien.android.projectist.R;

/**
 * Date Picker Fragment
 *
 * @author Fahien
 */
public class DatePickerFragment extends DialogFragment {
	public static final String EXTRA_DATE = "me.fahien.android.projectist.date";

	private Date date;

	/*
	 * Creating and setting fragment arguments is typically done in a
	 * newInstance() method that replace the fragment constructor.
	 */
	public static DatePickerFragment newInstance(Date date) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_DATE, date);

		DatePickerFragment fragment = new DatePickerFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override public Dialog onCreateDialog(Bundle savedInstanceState) {
		date = (Date) getArguments().getSerializable(EXTRA_DATE);

		// Create a Calendar to get the year, month and day
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);

		DatePicker datePicker = (DatePicker) view.findViewById(R.id.dialog_date_datePicker);
		datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
			@Override public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
				// Translate year, month, day into a Date object using a calendar
				date = new GregorianCalendar(year, month, day).getTime();

				// Update argument to preserve selected value on rotation
				getArguments().putSerializable(EXTRA_DATE, date);
			}
		});
		return new AlertDialog.Builder(getActivity())
				.setView(view)
				.setTitle(R.string.date_picker_title)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override public void onClick(DialogInterface dialog, int which) {
								sendResult(Activity.RESULT_OK);
							}
						})
				.create();
	}

	private void sendResult(int resultCode) {
		if (getTargetFragment() == null)
			return;

		Intent i = new Intent();
		i.putExtra(EXTRA_DATE, date);

		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
	}
}
