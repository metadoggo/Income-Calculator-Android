package uk.co.huydinh.apps.incomecalculator;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.TabActivity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ResultActivity extends TabActivity {
	public static final String PAYMENT_TERM = "payment_term";
	public static final String AMOUNT = "annual_salary";
	public static final String DAYS_PER_WEEK = "days_per_weeki";
	public static final String HOURS_PER_DAY = "hours_per_day";
	public static final String HOLIDAYS = "holidays";
	public static final String REPAY_STUDENT_LOAN = "repay_student_loan";
	
	private ArrayList<TaxData> taxDatas;
	private String defaultYear;
	private TabHost tabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		
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
		
		
		
		// TODO: Er...
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			tabHost = getTabHost();
			
			String paymentTerm = extras.getString(PAYMENT_TERM);
			float amount = extras.getFloat(AMOUNT);
			int daysPerWeek = extras.getInt(DAYS_PER_WEEK);
			float hoursPerDay = extras.getFloat(HOURS_PER_DAY);
			float holidays = extras.getFloat(HOURS_PER_DAY);
			boolean repayStudentLoan = extras.getBoolean(REPAY_STUDENT_LOAN);
			
			final String[] paymentTerms = getResources().getStringArray(R.array.payment_terms);
			
			float annualSalary;
			
			if (paymentTerm.equals(paymentTerms[1])) {
				annualSalary = amount * IncomeService.MONTHS_PER_YEAR;
			} else  if (paymentTerm.equals(paymentTerms[2])) {
				annualSalary = amount * IncomeService.WEEKS_PER_YEAR;
			} else if (paymentTerm.equals(paymentTerms[3])) {
				annualSalary = amount * IncomeService.DAYS_PER_YEAR;
			} else if (paymentTerm.equals(paymentTerms[4])) {
				annualSalary = amount * IncomeService.DAYS_PER_YEAR * hoursPerDay;
			} else {
				annualSalary = amount;
			}
			
			final float workingDaysPerYear = (daysPerWeek * IncomeService.WEEKS_PER_YEAR) - holidays;
			final float workingHoursPerYear = workingDaysPerYear * hoursPerDay;
			
			for (TaxData td : this.taxDatas) {
				final IncomeService is = new IncomeService(td, annualSalary, repayStudentLoan);
				
				TabSpec tab = tabHost.newTabSpec(td.label).setIndicator(td.label).setContent(new TabHost.TabContentFactory() {
					
					private NumberFormat formatter = new DecimalFormat("#0.00");
					
					private TextView createTextView(String label) {
						TextView tv = new TextView(ResultActivity.this);
						tv.setText(label);
						return tv;
					}
					
					private TextView createTextView(String label, int gravity) {
						TextView tv = createTextView(label);
						tv.setGravity(gravity);
						return tv;
					}
					private TextView createTextView(float amount) {
						return this.createTextView(formatter.format(amount), Gravity.RIGHT);
					}
					
					private TableRow createRow(String label, float amount) {
						TableRow row = new TableRow(ResultActivity.this);
						row.addView(createTextView(label));
						row.addView(createTextView(amount));
						row.addView(createTextView(amount / IncomeService.MONTHS_PER_YEAR));
						row.addView(createTextView(amount / IncomeService.WEEKS_PER_YEAR));
						row.addView(createTextView(amount / workingDaysPerYear));
						row.addView(createTextView(amount / workingHoursPerYear));
						return row;
					}
					
					public View createTabContent(String tag) {
						LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						View view = inflater.inflate(R.layout.result_table, null);
						
						TableLayout table = (TableLayout) view.findViewById(R.id.result_table);
						table.setStretchAllColumns(true);
						
						TableRow row = new TableRow(ResultActivity.this);
						row.addView(createTextView(""));
						row.addView(createTextView(paymentTerms[0], Gravity.RIGHT));
						row.addView(createTextView(paymentTerms[1], Gravity.RIGHT));
						row.addView(createTextView(paymentTerms[2], Gravity.RIGHT));
						row.addView(createTextView(paymentTerms[3], Gravity.RIGHT));
						row.addView(createTextView(paymentTerms[4], Gravity.RIGHT));
						table.addView(row);
						
						table.addView(createRow("Gross salary", is.grossSalary));
						table.addView(createRow("Income tax", is.getIncomeTax()));
						table.addView(createRow("National insurance", is.getNationalInsurance()));
						if (is.repayStudentLoan) {
							table.addView(createRow("Student loan", is.getStudentLoan()));
						}
						table.addView(createRow("Total deductions", is.getTotalDeductions()));
						table.addView(createRow("Net salary", is.getNetSalary()));
						
						return view;
					}
				});
				tabHost.addTab(tab);
			}
		}

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
