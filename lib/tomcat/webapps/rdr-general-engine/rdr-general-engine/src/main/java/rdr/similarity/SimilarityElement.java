package rdr.similarity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import rdr.cases.Case;
import rdr.model.Value;
import rdr.model.ValueType;

class DescendingSimilarity implements Comparator<SimilarityElement>
{
	@Override
	public int compare(SimilarityElement lh, SimilarityElement rh)
	{
		//내림차순
		return rh.getValue().compareTo(lh.getValue());
	}
}

public class SimilarityElement {

	private Double value = 0.0;
	
	private Case baseCase;
	private Case compareCase;

	private ArrayList<Object> baseValueList = new ArrayList<Object>();
	private ArrayList<Object> compareValueList = new ArrayList<Object>();
	
	public SimilarityElement(Case aBase, Case aCompare)
	{
		baseCase = aBase;
		compareCase = aCompare;
		value = 0.0;
	}
	
	public void setValue(Double pValue)
	{
		this.value = pValue;
	}
	
	public Double getValue()
	{
		return this.value;
	}
	
	public Case getBaseCase()
	{
		return baseCase;
	}
	
	public Case getCompareCase()
	{
		return compareCase;
	}
	
	public ArrayList<Object> getBaseValueList()
	{
		return baseValueList;
	}
	
	public ArrayList<Object> getCompareValueList()
	{
		return compareValueList;
	}
	
	public void setValueList(ArrayList<String> attrNames)
	{
		/** baseValueList와 compareValueList는 개수가 같아야 하고 number 또는  string으로 유형이 같아야 한다.
		 *  유사도 모듈이 null 을 처리할 수 있어야 한다.
		 */
		for (int i = 0; i < attrNames.size(); i++)
		{
			Value baseValue = this.baseCase.getValue(attrNames.get(i));
			Value compValue = this.compareCase.getValue(attrNames.get(i));
			
			if (baseValue == null || compValue == null)
				continue;
			
			int typeCode = baseValue.getValueType().getTypeCode();
			
			if (typeCode == ValueType.CATEGORICAL ||
			    typeCode == ValueType.CONTINUOUS ||
			    typeCode == ValueType.TEXT)
			{
				if (baseValue.isNullValue()) baseValueList.add(null);
				else baseValueList.add(baseValue.getActualValue());
				
				if (compValue.isNullValue()) compareValueList.add(null);
				else compareValueList.add(compValue.getActualValue());
			}
			else
			{
				if (baseValue.isNullValue()) baseValueList.add(null);
				else baseValueList.add(baseValue.toString());
				
				if (compValue.isNullValue()) compareValueList.add(null);
				else compareValueList.add(compValue.toString());
			}
		}
	}
	
	/** 유사도 모듈이 null 을 고려하지 않을 경우 사용해야 함 
	 */
//	public void setValueList(ArrayList<String> attrNames)
//	{
//		/** baseValueList와 compareValueList는 개수가 같아야 하고 number 또는  string으로 유형이 같아야 한다.
//		 *  따라서 한쪽이 null 인 경우는 string으로 통일해야 한다.
//		 */
//		for (int i = 0; i < attrNames.size(); i++)
//		{
//			Value baseValue = this.baseCase.getValue(attrNames.get(i));
//			Value compValue = this.compareCase.getValue(attrNames.get(i));
//			
//			if (baseValue == null || compValue == null)
//				continue;
//			
//			if (baseValue.isNullValue() || compValue.isNullValue())
//			{
//				baseValueList.add(baseValue.toString());
//				compareValueList.add(compValue.toString());
//			}
//			
//			int typeCode = baseValue.getValueType().getTypeCode();
//			
//			if (typeCode == ValueType.CATEGORICAL ||
//			    typeCode == ValueType.CONTINUOUS ||
//			    typeCode == ValueType.TEXT)
//			{
//				baseValueList.add(baseValue.getActualValue());
//				compareValueList.add(compValue.getActualValue());
//			}
//			else
//			{
//				baseValueList.add(baseValue.toString());
//				compareValueList.add(compValue.toString());
//			}
//		}
//	}
}

