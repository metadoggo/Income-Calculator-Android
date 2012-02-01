package uk.co.huydinh.apps.incomecalculator;

public class IncomeService {
	public static final int DAYS_PER_YEAR = 365;
	public static final int MONTHS_PER_YEAR = 12;
	public static final int WEEKS_PER_YEAR = 52;
	public static final int DAYS_PER_WEEK = 7;
	
	public static final int STUDENT_LOAN_THRESHOLD = 15000;
	public static final float STUDENT_LOAN_RATE = 0.09f;
	
	public float grossSalary;
	public boolean repayStudentLoan;
	public TaxData taxData;
	
	
	public IncomeService(TaxData taxData, float grossSalary, boolean repayStudentLoan) {
		this.taxData = taxData;
		this.grossSalary = grossSalary;
		this.repayStudentLoan = repayStudentLoan;
	}
	
	public IncomeService(TaxData taxData, float grossSalary) {
		this.taxData = taxData;
		this.grossSalary = grossSalary;
	}
	
	public float getIncomeTax() {
		float grossIncome = this.grossSalary;
		float remainingIncome = 0;
		float totalIncomeTax = 0;
		
		for (int i = 0, j = this.taxData.incomeTaxBands.length; i < j; i++) {
			TaxBand incomeTaxBand = this.taxData.incomeTaxBands[i];
			if (grossIncome >= incomeTaxBand.limit && incomeTaxBand.limit > 0) {
				totalIncomeTax += (incomeTaxBand.limit - remainingIncome) * incomeTaxBand.rate;
				if (incomeTaxBand.rate == 0) {
					grossIncome -= incomeTaxBand.limit;
				} else {
					remainingIncome = incomeTaxBand.limit - remainingIncome;
					grossIncome -= remainingIncome;
				}
			} else {
				totalIncomeTax += grossIncome * incomeTaxBand.rate;
				grossIncome = 0;
				break;
			}
		}
		return totalIncomeTax;
	}
	
	public float getNationalInsurance() {
		float remainingIncome = this.grossSalary / WEEKS_PER_YEAR;
		float totalNi = 0;
		TaxBand currentNiBand;
		TaxBand nextNiBand;
		int i = this.taxData.niBands.length;
		while (i-- > 1) {
			currentNiBand = this.taxData.niBands[i];
			nextNiBand = this.taxData.niBands[i - 1];
			if (remainingIncome > nextNiBand.limit) {
				totalNi += (remainingIncome - nextNiBand.limit) * currentNiBand.rate;
			}
			remainingIncome = Math.min(remainingIncome, nextNiBand.limit);
		}
		return totalNi * WEEKS_PER_YEAR;
	}
	
	public int getStudentLoan() {
		if (this.repayStudentLoan) {
			float remainingIncome = this.grossSalary - STUDENT_LOAN_THRESHOLD;
			if (remainingIncome > 0) {
				return (int) (remainingIncome * STUDENT_LOAN_RATE);
			}
		}
		return 0;
	}
	
	public float getTotalDeductions() {
		return this.getIncomeTax() + this.getNationalInsurance() + this.getStudentLoan();
	}
	
	public float getNetSalary() {
		return this.grossSalary - this.getTotalDeductions();
	}
}
