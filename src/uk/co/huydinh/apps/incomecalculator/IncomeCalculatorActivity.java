package uk.co.huydinh.apps.incomecalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

public class IncomeCalculatorActivity extends Activity implements OnClickListener {
	
	private Spinner paymentTermPicker;
	private EditText amountInput;
	private TimePicker hoursPerDayTimePicker;
	private Spinner daysPerWeekPicker;
	private EditText holidaysInput;
	private CheckBox repayStudentLoanCheckBox;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    
    public void onClick(View v) {
		Intent intent = new Intent(this, ResultActivity.class);
		intent.putExtra(ResultActivity.PAYMENT_TERM, paymentTermPicker.getSelectedItem().toString());
		intent.putExtra(ResultActivity.AMOUNT, Float.parseFloat(amountInput.getText().toString()));
		intent.putExtra(ResultActivity.DAYS_PER_WEEK, Integer.parseInt(daysPerWeekPicker.getSelectedItem().toString()));
		intent.putExtra(ResultActivity.HOURS_PER_DAY, hoursPerDayTimePicker.getCurrentHour() + (hoursPerDayTimePicker.getCurrentMinute() / 60.0f));
		intent.putExtra(ResultActivity.HOLIDAYS, Float.parseFloat(holidaysInput.getText().toString()));
		intent.putExtra(ResultActivity.REPAY_STUDENT_LOAN, repayStudentLoanCheckBox.isChecked());
		startActivity(intent);
    }
}