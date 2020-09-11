import java.util.ArrayList;

public class Investment {
	private String investmentTag;
	private double dollarsInvested;
	private double futureValue;

		
	public Investment(String investmentTag, double dollarsInvested, double futureValue) {
		this.investmentTag = investmentTag;
		this.dollarsInvested = dollarsInvested;
		this.futureValue = futureValue;
	}


	public String getInvestmentTag() {
		return investmentTag;
	}


	public double getDollarsInvested() {
		return this.dollarsInvested;
	}

	public double getFutureValue() {
		return this.futureValue;
	}
	
	public String tagToString() {
		return "" + this.investmentTag;
	}
	
	public String dollarsInvestedToString() {
		return "" + this.dollarsInvested;
	}
	
	public String futureValueToString() {
		return "" + this.futureValue;
	}
	
	
	
	/*
	 * private double calculateReturn(String tag, Assets assets) { for(Asset asset :
	 * assets) { if(this.investmentTag == asset.getTag()) { return
	 * this.dollarsInvested * Math.pow(1 + asset.getExpectedReturn(), 10); } else {
	 * continue; } } return -1; }
	 */
}
