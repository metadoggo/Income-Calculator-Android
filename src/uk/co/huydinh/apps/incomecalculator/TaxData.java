package uk.co.huydinh.apps.incomecalculator;

public class TaxData {
	public String label;
	public TaxBand[] incomeTaxBands;
	public TaxBand[] niBands;
	
	public TaxData(String label, TaxBand[] incomeTaxBands, TaxBand[] niBands) {
		this.label = label;
		this.incomeTaxBands = incomeTaxBands;
		this.niBands = niBands;
	}
}
