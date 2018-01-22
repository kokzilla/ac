import java.util.ArrayList;
import java.util.List;

public class PRISMMining extends AprioriTFP_CBA {

	protected short[] classLabelArray = null;

	// -- count attribute values frequency separated by class labels
	protected int classFreqArray[][] = null;

	protected int attributeArray[][] = null;
	
	protected double entropyD = 0;

	public PRISMMining(String[] args) {
		super(args);
	}

	public void buildClassLabelArray() {
		// -init classLabel Array
		classLabelArray = new short[numClasses];
		int classStartIndex = numCols - numClasses + 1;
		for (int i = 0; i < numClasses; i++) {
			classLabelArray[i] = (short) conversionArray[classStartIndex + i][0];
		}
	}

	public void EntropyD() {

		double[] classProb = new double[numClasses];
		double[] classEntropy = new double[numClasses];
		

		// -- calculate P for each classes
		for (int i = 0; i < numClasses; i++) {

			classProb[i] = (double) conversionArray[classLabelArray[i]][1] / numRows;
			classEntropy[i] = classProb[i] * logb(classProb[i], numClasses);
			entropyD += -1 * classEntropy[i];
//			System.out.println(
//					"class " + i + " prob : " + classProb[i] + " // log" + numClasses + " : " + classEntropy[i]);
		}

		System.out.println("Target Entropy:" + entropyD);
	}

	/*
	 * The entropy for each branch is calculated
	 */
	public void getInformationGain() {
		int chosenAttribute = 0;
		double maxIG = 0;
		
		for (int attrNo = 1; attrNo < attributeArray.length; attrNo++) {

			double branchEntropy = 0;
//			System.out.println("Attribute no.:" + attrNo);
			// -- loop by number of attribute values
			for (int valIndex = attributeArray[attrNo][0]; valIndex <= attributeArray[attrNo][1]; valIndex++) {
				// System.out.println(" === Attribute no.:" + attrNo + " Value:" + valIndex +
				// "");

				double valueEntropy = 0;
				double[] valueProbArray = new double[numClasses];
				double[] valueEntropyArray = new double[numClasses];

				// -- values frequency
				int attributeValueNumRows = conversionArray[valIndex][1];

				// -- evaluate attribute value entropy
				for (int classIndex = 0; classIndex < classLabelArray.length; classIndex++) {
					valueProbArray[classIndex] = (double) classFreqArray[valIndex][classIndex] / attributeValueNumRows;
					valueEntropyArray[classIndex] = valueProbArray[classIndex] * logb(valueProbArray[classIndex], numClasses);
					valueEntropy += -1 * valueEntropyArray[classIndex];

				}

				// -- summary value entropy;
				// A branch with entropy of 0 is a leaf node.
				branchEntropy += (double) attributeValueNumRows / numRows * valueEntropy;				;
				// System.out.println("P:" + attributeValueNumRows + " E:" + entropyV );

			}
//			System.out.println("E(Attribute=" + attrNo + ") = " + entropyA);
			System.out.println("GAIN of attr." + attrNo + ") = " + (entropyD -branchEntropy));
			if (maxIG < (entropyD -branchEntropy)) {
				maxIG = entropyD -branchEntropy;
				chosenAttribute = attrNo;
			}
		}
		System.out.println("Chosen Attribute:" + chosenAttribute);
	}

	private static double logb(double a, double b) {
		if (a == 0) {
			return 0;
		}
		return Math.log(a) / Math.log(b);
	}

	private static double log2(double a) {
		return logb(a, 2);
	}

	/* COUNT NUMBER OF COLUMNS */
	/**
	 * Counts number of columns represented by input data group by attribute number.
	 */

	protected void countNumCols() {
		int maxAttribute = 0;

		// Loop through data array by column first
		// for (int index = 0; index < dataArray.length; index++) {

		// Loop through data array
		for (int index = 0; index < dataArray.length; index++) {
			int lastIndex = dataArray[index].length - 1;
			if (dataArray[index][lastIndex] > maxAttribute)
				maxAttribute = dataArray[index][lastIndex];
		}

		numCols = maxAttribute;
		numOneItemSets = numCols; // default value only
	}

	protected void InputDataGrouping() {

		// Data columns number;
		numDataCols = dataArray[0].length;

		classFreqArray = new int[numCols + 1][numClasses];

		// count member of attribute , class
		for (int rowIndex = 0; rowIndex < dataArray.length; rowIndex++) {
			if (dataArray[rowIndex] != null) {
				for (int colIndex = 0; colIndex < numDataCols - 1; colIndex++) {
					int classLabelIndex = findClassesIndex(dataArray[rowIndex][numDataCols - 1]);

					// -- count attribute values frequency separated by class labels
					classFreqArray[dataArray[rowIndex][colIndex]][classLabelIndex]++;
				}
			}
		}

		// -- find Max,Min value of each Attributes
		// 2nd Dimension index 0 = min value, 1= max value

		attributeArray = new int[numDataCols][2];
		for (int colIndex = 0; colIndex < numDataCols - 1; colIndex++) {
			int maxValue = 1;
			int minValue = 1;
			if (colIndex > 0) {
				minValue = attributeArray[colIndex][1] + 1;
			}

			for (int rowIndex = 0; rowIndex < dataArray.length; rowIndex++) {
				// -- find Max value
				if (maxValue < dataArray[rowIndex][colIndex]) {
					maxValue = dataArray[rowIndex][colIndex];
				}
				// -- find Min value
				if (minValue > dataArray[rowIndex][colIndex]) {
					minValue = dataArray[rowIndex][colIndex];
				}
			}
			attributeArray[colIndex + 1][0] = minValue;
			attributeArray[colIndex + 1][1] = maxValue;

		}
	}

	protected int findClassesIndex(short classLabel) {
		int classIndex = -1;
		for (int index = 0; index < numClasses; index++) {
			if (classLabelArray[index] == classLabel) {
				return index;
			}
		}
		return classIndex;
	}

}
