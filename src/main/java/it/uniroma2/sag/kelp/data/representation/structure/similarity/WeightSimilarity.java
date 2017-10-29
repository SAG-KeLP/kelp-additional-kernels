package it.uniroma2.sag.kelp.data.representation.structure.similarity;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;

@JsonTypeName("WeightSimilarity")
@JsonTypeIdResolver(StructureElementSimilarityTypeResolver.class)
public class WeightSimilarity implements StructureElementSimilarityI {

	private static final String DEFAULT_WEIGHT_FIELD_NAME = "weight";
	private String weightFieldName;
	private float defaultWeight;
	
	public WeightSimilarity() {
		weightFieldName = DEFAULT_WEIGHT_FIELD_NAME;
	}
	public WeightSimilarity(float defaultWeight) {
		this();
		this.defaultWeight = defaultWeight;
	}

	public WeightSimilarity(float defaultWeight, String newWeightFieldName) {
		this(defaultWeight);
		weightFieldName = newWeightFieldName;
	}
	
	public String describe() {
		return String.format("WeightSimilarity Class: default weight = %f, "
				+ "weigth field name = %s", defaultWeight, weightFieldName);
	}
	
//	@Override
	public float sim(StructureElement sx, StructureElement sd) {
		float weightSx;
		float weightDx;
		if(!sx.containsAdditionalInfo(weightFieldName)) {
			weightSx = defaultWeight;
		}else{
			weightSx = (Float.parseFloat(sx.getAdditionalInformation(weightFieldName).toString()));
		}
		if(!sd.containsAdditionalInfo(weightFieldName)) {
			weightDx = defaultWeight;
		}else{
			weightDx = (Float.parseFloat(sd.getAdditionalInformation(weightFieldName).toString()));
		}
		return weightSx*weightDx;
	}

	public String getWeightFieldName() {
		return weightFieldName;
	}

	public void setWeightFieldName(String weightFieldName) {
		this.weightFieldName = weightFieldName;
	}

	public float getDefaultWeight() {
		return defaultWeight;
	}

	public void setDefaultWeight(float defaultWeight) {
		this.defaultWeight = defaultWeight;
	}

}
