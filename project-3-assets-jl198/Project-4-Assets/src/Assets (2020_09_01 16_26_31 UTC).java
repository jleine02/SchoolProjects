import java.util.ArrayList;
import java.util.Scanner;

public class Assets {
	private ArrayList<Asset> assetList;
	
	public Assets() {
		assetList = new ArrayList<Asset>();
	}
	
	public void inputAssets(Scanner scnr) throws IllegalArgumentException {
	      Asset currentAsset;
	      String currentTag;
	      String currentName;
	      Double firstDouble;
	      Double secondDouble;
	      Double thirdDouble;
	      String weightScheme;
	      String[] line = new String[5];
	      
	      try {
		      while (scnr.hasNextLine()) {
		         line = scnr.nextLine().split(",");
		         currentTag = line[0];
		         currentName = line[1];
		         try {
		        	 firstDouble = Double.parseDouble(line[2]);
		         } catch (IllegalArgumentException e){
		        	 if(line.length == 3) {
		        		 continue;
		        	 } else {
		        		 firstDouble = 0.0;
		        	 }
		         }
		         if(line.length == 3) {
		        	 currentAsset = new StableAsset(currentTag, currentName, firstDouble);
		        	 getAssetList().add(currentAsset);
		         } else {
		        	 if(firstDouble == 0.0) {
		        		 weightScheme = "B";
		        		 firstDouble = 0.0;
		        		 secondDouble = Double.parseDouble(line[3]);
		        		 thirdDouble = Double.parseDouble(line[4]);
		        		 currentAsset = new VariableAsset(currentTag, currentName, firstDouble, secondDouble, thirdDouble, weightScheme);
		        		 getAssetList().add(currentAsset);
		        	 } else {
		        		 weightScheme = "A";
		        		 secondDouble = Double.parseDouble(line[3]);
		        		 thirdDouble = Double.parseDouble(line[4]);
		        		 currentAsset = new VariableAsset(currentTag, currentName, firstDouble, secondDouble, thirdDouble, weightScheme);
		        		 getAssetList().add(currentAsset);
		        	 }
		         }
		      }
	      } catch (IllegalArgumentException e) {
	    	  System.out.print(e.getClass() + " " + e.getMessage());
	      }
	}	
	
	public String toString() {
		String result = "";
		for(Asset asset : getAssetList()) {
			result += asset.getName() + " (" + asset.getTag() + ")\n";
		}
		return result;
	}

	
	public String getAssetNameAtIndex(int index) {
		return this.getAssetList().get(index).getName();
	}

	public ArrayList<Asset> getAssetList() {
		return assetList;
	}
	
	public int checkInList(String tag) {
		int inList = -1;
		for(Asset asset : this.assetList) {
			if(asset.getTag().equals(tag)) {
				inList = 1;
			}
		}
		return inList;
	}
	
	public double getExpectedReturn(String tag) {
		double expectedReturn = 0.0;
		for(Asset asset : this.assetList) {
			if(asset.getTag().equals(tag)) {
				expectedReturn = asset.getExpectedReturn();
			}
		}
		return expectedReturn;
		
	}
}

