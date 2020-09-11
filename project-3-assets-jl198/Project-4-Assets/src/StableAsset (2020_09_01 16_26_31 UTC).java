
public class StableAsset extends Asset{
	private String tag;
	private String name;
	private String weightScheme;
	private double expectedReturn;
	

	public StableAsset(String tag, String name, double expectedReturn) {
		this.tag = tag;
		this.name = name;
		this.weightScheme = "C";
		this.expectedReturn = expectedReturn;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getTag() {
		return this.tag;
	}

	public double getExpectedReturn() {
		return this.expectedReturn;
	}
	
	public String getWeightScheme() {
		return this.weightScheme;
	}
	
	public String toString() {
		return "";
	}
}
