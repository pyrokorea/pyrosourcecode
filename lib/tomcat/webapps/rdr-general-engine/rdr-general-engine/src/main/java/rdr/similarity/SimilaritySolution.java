package rdr.similarity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import rdr.cases.Case;

public class SimilaritySolution {

	private Case currentCase;
	
	private ArrayList<SimilarityElement> compareElements = new ArrayList<SimilarityElement>();
	
	private ArrayList<String> attrNames;
	
	public SimilaritySolution(Case pCurrentCase, 
			                  ArrayList<Case> pCompareCases, 
			                  ArrayList<String> pAttrNames)
	{
		this.currentCase = pCurrentCase;
		for (int i = 0; i < pCompareCases.size(); i++)
		{
			this.compareElements.add(new SimilarityElement(currentCase, pCompareCases.get(i)));
		}
		this.attrNames = pAttrNames;
	}
	
	public ArrayList<SimilarityElement> buildSolution()
	{
		ArrayList<SimilarityElement> result = new ArrayList<SimilarityElement>();
		
		for (int i = 0; i < compareElements.size(); i++)
		{
			SimilarityElement elem = compareElements.get(i);
			
			elem.setValueList(this.attrNames);
			
			BodyVector bvBase = new BodyVector(0.0, 0.0, 0.0, elem.getBaseValueList());
			BodyVector bvCompare = new BodyVector(0.0, 0.0, 0.0, elem.getCompareValueList());
			double value = BodyDistance.calculate(bvBase, bvCompare);
			
			if (Double.isNaN(value) == false)
			{
				elem.setValue(value);
				result.add(elem);
			}
		}
		
		DescendingSimilarity asc = new DescendingSimilarity();
		Collections.sort(result, asc);
		
		return result;
	}
	
}
