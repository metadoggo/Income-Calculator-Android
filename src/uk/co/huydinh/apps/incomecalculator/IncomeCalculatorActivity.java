package uk.co.huydinh.apps.incomecalculator;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.XmlResourceParser;
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
	private ArrayList<TaxData> taxDatas;
	private String defaultYear;
	
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
        amountInput = (EditText) findViewById(R.id.amountInput);
        
        hoursPerDayTimePicker = (TimePicker) findViewById(R.id.hoursPerDayTimePicker);
        hoursPerDayTimePicker.setIs24HourView(true);
        hoursPerDayTimePicker.setCurrentHour(7);
        hoursPerDayTimePicker.setCurrentMinute(30);
        
        daysPerWeekPicker = (Spinner) findViewById(R.id.daysPerWeekSpinner);
        holidaysInput = (EditText) findViewById(R.id.holidaysInput);
        repayStudentLoanCheckBox = (CheckBox) findViewById(R.id.repayStudentLoanCheckBox);
        
        Button calculateButton = (Button) findViewById(R.id.calculateButton);
		calculateButton.setOnClickListener(this);
		
		this.taxDatas = new ArrayList<TaxData>();
		
		XmlResourceParser p = getResources().getXml(R.xml.tax_data);
		
		try {
			p.next();
			int eventType = p.getEventType();
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
				p.next();

				eventType = p.getEventType();
				if (eventType == XmlPullParser.START_TAG) {
					String name = p.getName();
					if (name.equalsIgnoreCase("default")) {
						if (defaultYear == null) {
							defaultYear = p.getAttributeValue(null, "year");
						}
					} else if (name.equalsIgnoreCase("year")) {
						this.taxDatas.add(parseTaxData(p));
					}
				}
			}
		}
		catch (XmlPullParserException e) {
			Log.i("XmlPullParserException",  e.toString());
		}
		catch (IOException e) {
			Log.i("IOException",  e.toString());
		}
		finally {
			p.close();
		}
    }
    
    public void onClick(View v) {
    	for (TaxData td : this.taxDatas) {
    		float grossSalary = Float.parseFloat(amountInput.getText().toString());
    		IncomeService is = new IncomeService(td, grossSalary, repayStudentLoanCheckBox.isChecked());
    	}
		Intent intent = new Intent(this, ResultActivity.class);
		intent.putExtra(name, value)
		startActivity(intent);
    }
    
    private TaxData parseTaxData(XmlResourceParser p) throws XmlPullParserException, IOException {
    	int depth = p.getDepth();
    	String label = p.getAttributeValue(null, "label");
    	p.next();
    	ArrayList<TaxBand> incomeTaxBands = new ArrayList<TaxBand>();
    	ArrayList<TaxBand> niBands = new ArrayList<TaxBand>();
    	while (depth < p.getDepth()) {
    		String name = p.getName();
    		if (name.equalsIgnoreCase("incometax")) {
    			incomeTaxBands = parseTaxBands(p);
    		} else if (name.equalsIgnoreCase("ni")) {
    			niBands = parseTaxBands(p);
    		}
    		p.next();
    	}
    	
    	TaxBand[] it = new TaxBand[incomeTaxBands.size()];
		it = incomeTaxBands.toArray(it);
		TaxBand[] ni = new TaxBand[niBands.size()];
		ni = niBands.toArray(ni);
		return new TaxData(label, it, ni);
    }
    
    private ArrayList<TaxBand> parseTaxBands(XmlResourceParser p) throws XmlPullParserException, IOException {
    	ArrayList<TaxBand> taxBands = new ArrayList<TaxBand>();
    	int depth = p.getDepth();
    	p.next();
    	while (depth < p.getDepth()) {
    		taxBands.add(parseTaxBand(p));
    		p.next();
    	}
    	return taxBands;
    }
    
    private TaxBand parseTaxBand(XmlResourceParser p) throws XmlPullParserException, IOException {
    	// Parse a float (or an int as a float)
    	float rate;
		try {
			rate = p.getAttributeFloatValue(null, "rate", 0.0f);
		}
		catch (RuntimeException e) {
			rate = p.getAttributeIntValue(null, "rate", 0);
		}
		int limit = p.getAttributeIntValue(null, "limit", 0);
		
		// Advances to the end tag before returning
		p.next();
		
		return new TaxBand(rate, limit);
    }
}