package uk.co.huydinh.apps.incomecalculator;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class AppPreferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

}
