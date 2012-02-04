package uk.co.huydinh.apps.incomecalculator;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

public class IncomeCalculatorActivity extends Activity implements
		OnClickListener {

	private Spinner paymentTermPicker;
	private EditText amountInput;
	private TimePicker hoursPerDayTimePicker;
	private Spinner daysPerWeekPicker;
	private EditText holidaysInput;
	private CheckBox repayStudentLoanCheckBox;
	private Locale locale;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Resources resources = getBaseContext().getResources();
		SharedPreferences appPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String language = appPreferences.getString("language",
				resources.getStringArray(R.array.languages_values)[0]);
		Locale locale;
		if (language.length() == 2) {
			locale = new Locale(language);
		} else {
			locale = new Locale(language.substring(0, 2), language.substring(3));
		}
		Configuration config = getBaseContext().getResources().getConfiguration();
		if (!config.locale.getLanguage().equals(locale.getLanguage())
				|| !config.locale.getCountry().equals(locale.getCountry())) {
			Locale.setDefault(locale);
			config.locale = locale;
			resources
					.updateConfiguration(config, resources.getDisplayMetrics());
			this.locale = locale;
		}

		setContentView(R.layout.main);

		paymentTermPicker = (Spinner) findViewById(R.id.paymentTermSpinner);
		paymentTermPicker.setSelection(3);

		amountInput = (EditText) findViewById(R.id.amountInput);

		hoursPerDayTimePicker = (TimePicker) findViewById(R.id.hoursPerDayTimePicker);
		hoursPerDayTimePicker.setIs24HourView(true);
		hoursPerDayTimePicker.setCurrentHour(7);
		hoursPerDayTimePicker.setCurrentMinute(30);

		daysPerWeekPicker = (Spinner) findViewById(R.id.daysPerWeekSpinner);
		daysPerWeekPicker.setSelection(4);

		holidaysInput = (EditText) findViewById(R.id.holidaysInput);

		repayStudentLoanCheckBox = (CheckBox) findViewById(R.id.repayStudentLoanCheckBox);

		Button calculateButton = (Button) findViewById(R.id.calculateButton);
		calculateButton.setOnClickListener(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (locale != null) {
			newConfig.locale = locale;
			Locale.setDefault(locale);
			getBaseContext().getResources().updateConfiguration(newConfig,
					getBaseContext().getResources().getDisplayMetrics());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent(this, AppPreferences.class));
			return true;
		case R.id.exit:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onClick(View v) {
		Intent intent = new Intent(this, ResultActivity.class);
		intent.putExtra(ResultActivity.PAYMENT_TERM, paymentTermPicker
				.getSelectedItem().toString());
		intent.putExtra(ResultActivity.AMOUNT,
				Float.parseFloat(amountInput.getText().toString()));
		intent.putExtra(ResultActivity.DAYS_PER_WEEK, Integer
				.parseInt(daysPerWeekPicker.getSelectedItem().toString()));
		intent.putExtra(
				ResultActivity.HOURS_PER_DAY,
				hoursPerDayTimePicker.getCurrentHour()
						+ (hoursPerDayTimePicker.getCurrentMinute() / 60.0f));
		intent.putExtra(ResultActivity.HOLIDAYS,
				Float.parseFloat(holidaysInput.getText().toString()));
		intent.putExtra(ResultActivity.REPAY_STUDENT_LOAN,
				repayStudentLoanCheckBox.isChecked());
		startActivity(intent);
	}
}