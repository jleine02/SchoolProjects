import java.io.FileInputStream;
import java.util.ArrayList;

public class Portfolio {
	private ArrayList<Investment> investments;
	private double totalInvested;
	private double totalFutureValue;
	Assets availableAssets;
	
	public Portfolio(Assets assetList) {
		investments = new ArrayList<Investment>();
		availableAssets = assetList;
	}
	
	public void addInvestmentToPortfolio(Investment investment) {
		investments.add(investment);
		totalInvested += investment.getDollarsInvested();
		totalFutureValue += investment.getFutureValue();
	}
	
	public String assetsToString() {
		return availableAssets.toString();			
	}
	
	public int getInvestmentSize() {
		return investments.size();
	}
	public ArrayList<Investment> getInvestments() {
		return this.investments;
	}
	
	public int checkInList(String tag) {
		int inList = -1;
		for(Asset asset : availableAssets.getAssetList()) {
			if(tag.equals(asset.getTag())) {
				inList = 1;
			}
		}
		return inList;
	}
	
	public double getExpectedAssetReturn(String tag) {
		return availableAssets.getExpectedReturn(tag);
	}
	
	public String getTotalInvestedToString() {
		return "" + this.totalInvested;
	}
	
	public String getTotalFutureValueToString() {
		return "" + this.totalFutureValue;
	}

	public double getTotalInvested() {
		return this.totalInvested;
	}
	
	public double getTotalFutureValue() {
		return this.totalFutureValue;
	}
}