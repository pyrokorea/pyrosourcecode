package rdr.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import rdr.apps.Main;
import rdr.cases.Case;
import rdr.cases.CaseSet;
import rdr.cases.CaseStructure;
import rdr.gui.DomainEditorFrame;
import rdr.gui.StartupFrame;
import rdr.logger.Logger;
import rdr.model.AttributeFactory;
import rdr.model.IAttribute;
import rdr.model.Value;
import rdr.model.ValueType;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class ArffUtil {
	
	private String arffFn;
	private CaseStructure caseStructure;
	private CaseSet allCases;
	
	private StringBuilder arffHeader;
	private HashMap<Integer, String> arffData;

	public ArffUtil(String pArffFn) {
		try {
			arffFn = pArffFn;
			allCases = new CaseSet();
			readArffString(pArffFn);
			readArff(pArffFn);
			writeArffSingleClass();
		} catch (IOException ex) {
	        Logger.error(ex.getClass().getName() + ": " + ex.getMessage(), ex );
	    } catch (Exception ex) {
	        Logger.error(ex.getClass().getName() + ": " + ex.getMessage(), ex );
	    }
	}
	
	/** class(결론)은 마지막 항목인것으로 가정.
	 *  _sc.arff : 복수결론인 것은 첫번째 data 만 작성하고 나머지는 삭제
	 *  _dup.arff : sc.arff 작성에 제외된 중목항목
	 */
	private void writeArffSingleClass()
	{
		/** single class map, key : class를 제외한 case string */
		HashMap<String, String> scMap = new HashMap<String, String>();
		
		HashSet<String> allClass = new HashSet<String>();
		HashSet<String> scClass = new HashSet<String>();
		
		ArrayList<String> mcList = new ArrayList<String>();
		
		Iterator<Integer> keys = arffData.keySet().iterator();
		while ( keys.hasNext() )
		{
			Integer key = keys.next();
			String caseStr = arffData.get(key);
			
			String attrStr = caseStr.substring(0, caseStr.lastIndexOf(","));
			String classStr = caseStr.substring(caseStr.lastIndexOf(",")+1);
			
			allClass.add(classStr);
			
			if (scMap.containsKey(attrStr))
			{
				mcList.add(caseStr);
			}
			else
			{
				scMap.put(attrStr, caseStr);
				scClass.add(classStr);
			}
		}

		try 
		{
			String fn = arffFn.substring(0, arffFn.lastIndexOf("."));
			String scfn = fn + "_sc.arff";
			BufferedWriter scWriter = new BufferedWriter(new FileWriter(scfn));	
			scWriter.write(arffHeader.toString());
			scWriter.write("@data");
			scWriter.newLine();
			Iterator<String> scKeys = scMap.keySet().iterator();
			while ( scKeys.hasNext())
			{
				scWriter.write(scMap.get(scKeys.next()));
				scWriter.newLine();
			}
			scWriter.close();
			System.out.println("arff with single class(remove dup) :" + scfn);
			System.out.println("arff with single class data count : " + scMap.size());
			System.out.println("class count(all) : " + allClass.size());
			System.out.println("class count(sc) : " + scClass.size());
			//System.out.println(scClass);
			
			String dupfn = fn + "_dup.arff";
			BufferedWriter dupWriter = new BufferedWriter(new FileWriter(dupfn));	
			dupWriter.write(arffHeader.toString());
			dupWriter.write("@data");
			dupWriter.newLine();
			for ( int i = 0; i < mcList.size(); ++i)
			{
				dupWriter.write(mcList.get(i));
				dupWriter.newLine();
			}
			dupWriter.close();
			System.out.println("arff with dup :" + dupfn);
			System.out.println("arff with dup data count : " + mcList.size());
		}
		catch (Exception ex)
		{
			Logger.error(ex.getClass().getName() + ": " + ex.getMessage(), ex );
		}
	}
	
	/** arffHeader, arffData setting 
	 * 
	 * @param arffFn
	 */
	private void readArffString(String arffFn)
	{
		try
		{
			File aFile = new File(arffFn);
			FileReader fileReader = new FileReader(aFile);
			BufferedReader reader = new BufferedReader(fileReader);

			arffHeader = new StringBuilder();
			arffData = new HashMap<Integer, String>();
			
			int dataSeq = 1;
			String line = null;
			boolean isDataSection = false;
			while ( (line = reader.readLine()) != null )
			{
				if ( line.contains("@data") || line.contains("@DATA"))
				{
					//arffHeader = sb.toString() + System.getProperty("line.separator");
					isDataSection = true;
					continue;
				}
				
				if ( isDataSection) 
				{
					arffData.put( new Integer(dataSeq), line );
					dataSeq++;
				}
				else
				{
					arffHeader.append(line);
					arffHeader.append(System.getProperty("line.separator"));
				}
			}
			reader.close();
			
			System.out.println(dataSeq-1 + " data string loaded");
		}
		catch (Exception ex)
		{
			Logger.error(ex.getClass().getName() + ": " + ex.getMessage(), ex);
		}
		
		/**
		System.out.println("header : " + arffHeader);
		Iterator<Integer> keys = arffData.keySet().iterator();
		while ( keys.hasNext() )
		{
			Integer key = keys.next();
			System.out.println( key.intValue() + " : " + arffData.get(key) );
		}
		*/
	}
	
	/** read arff file, caseStructure, allCases setting
	 * 
	 * @param arffFn
	 * @throws Exception
	 */
	private void readArff(String arffFn) throws Exception
	{
		ConverterUtils.DataSource source = new ConverterUtils.DataSource(arffFn);
		
        if(source.getDataSet()!=null){
            Instances data = source.getDataSet();
            
            if (data.classIndex() == -1){
                data.setClassIndex(data.numAttributes() - 1);
            }
            
            // Getting case structure
            caseStructure = new CaseStructure();

            int arffAttrAmount = source.getStructure().numAttributes();
            for(int i=0; i<arffAttrAmount; i++) {
                Attribute arffAttr = source.getStructure().attribute(i);

                IAttribute convertedAttr = convertAttributeFromArffToRDRAttr(arffAttr);
                if(!caseStructure.isAttributeExist(convertedAttr)){
                    caseStructure.addAttribute(convertedAttr);
                }
            }

            int attrAmount = caseStructure.getAttrAmount();

            System.out.print("Case loading.");
            
            // Add cases
            int caseAmount = source.getDataSet().numInstances();
            for(int i=0; i<caseAmount; i++) {
                
            	Case rdrCase = new Case(caseStructure);
                
                {
                    Instance arffCase = source.getDataSet().instance(i);
                    String[] attrNames = caseStructure.getAttributeNameArray();
                    
                    //storing null value
                    rdrCase.initNullValue();
                    
                    //storing empty value
//                    for(int j=0; j<attrAmount; j++) {
//                        String attrName = attrNames[j];
//                        String attrType = caseStructure.getAttributeByName(attrName).getValueType().getTypeName();
//                            String valStr = "";
//                            switch (attrType) {
//                                case "CONTINUOUS":               
//                                    ValueType valType = new ValueType(ValueType.CONTINUOUS);
//                                    Value value = new Value(valType, "0.0");
//                                    rdrCase.setValue(attrName, value);
//                                break;
//
//                                case "CATEGORICAL":
//                                    valStr = "";
//
//                                    valType = new ValueType(ValueType.CATEGORICAL);
//                                    value = new Value(valType, valStr);
//                                    rdrCase.setValue(attrName, value);
//                                break;
//
//                                default:
//                                    valStr = "";
//
//                                    valType = new ValueType(ValueType.TEXT);
//                                    value = new Value(valType, valStr);
//                                    rdrCase.setValue(attrName, value);
//                                break;
//                            }
//                    }
                    
                    
                    for(int j=0; j<arffAttrAmount; j++) 
                    {                
                        String attrName = arffCase.attribute(j).name();
                        if(caseStructure.getAttributeByName(attrName)!=null)
                        {
                            String attrType = caseStructure.getAttributeByName(attrName).getValueType().getTypeName();
                            String valStr = "";
                            if(arffCase.isMissing(j)){
                                ValueType valType = new ValueType("NULL");
                                Value value = new Value(valType);
                                rdrCase.setValue(attrName, value);
                            }
                            else 
                            {
                                switch (attrType) 
                                {
                                    case "CONTINUOUS":
                                            double dVal = arffCase.value(j);
                                            valStr = String.valueOf(dVal);
                                        break;
                                    case "CATEGORICAL":
                                            valStr = arffCase.stringValue(j);
                                        break;
                                    default:
                                            valStr = arffCase.stringValue(j);
                                        break;
                                }
                                
                                if (RDRConfig.isNullValueString(valStr))
                                {
                                	ValueType valType = new ValueType(ValueType.NULL_TYPE);
                              		Value value = new Value(valType);
                              		rdrCase.setValue(attrName, value);
                                }
                                else
                                {
            	                    ValueType valType = caseStructure.getAttributeByName(attrName).getValueType();
            	                    Value value = new Value(valType, valStr);
            	                    rdrCase.setValue(attrName, value);
                                }
                            }
                        }
                    }
                }
                int caseId = i+1;
                rdrCase.setCaseId(caseId);

                //add case into allCaseSet
                allCases.addCase(rdrCase);
                System.out.print(".");
            }
            System.out.println("..");
            
            /**
            System.out.println("Total " + allCases.getCaseAmount() + " cases(s) loaded.");
            if(caseAmount==0){
                Logger.warn("There is no case can be imported");
            } else if(caseAmount==1){
            	Logger.info("Total 1 case imported");
            } else {
            	Logger.info("Total " + caseAmount + " cases imported");
            }
            */
        } else {
            
            // Construct case structure
            caseStructure = new CaseStructure();

            int attrAmount = source.getStructure().numAttributes();
            for(int i=0; i<attrAmount; i++) {
                Attribute arffAttr = source.getStructure().attribute(i);

                IAttribute convertedAttr = convertAttributeFromArffToRDRAttr(arffAttr);
                caseStructure.addAttribute(convertedAttr);
            }

        }
        
        System.out.println("case structure, attr count : " + caseStructure.getAttrAmount());
        System.out.println("data loaded count by weca : " + allCases.getCaseAmount());
	}
	
	
	private static IAttribute convertAttributeFromArffToRDRAttr(Attribute arffAttr){        
        String RDRAttrType = "";
        
        // check the attribute type of arff dataset
        if(arffAttr.isDate()){
            RDRAttrType = "Date";
        } else if(arffAttr.isNumeric()){
            RDRAttrType = "Continuous";
        } else if(arffAttr.isString()){
            RDRAttrType = "Text";
        } else if(arffAttr.isNominal()){
            RDRAttrType = "Categorical";            
        } 
        IAttribute convertedAttr = AttributeFactory.createAttribute(RDRAttrType);
                
        //if nominal type, add categories
        if(arffAttr.isNominal()){
            int valueAmount = arffAttr.numValues();            
            convertedAttr.addCategoricalValue("");
            for (int i=0; i<valueAmount; i++){
                // get single value of nominal values
                String valueInstance = arffAttr.value(i);
                // add categorical value
                convertedAttr.addCategoricalValue(valueInstance);
            }      
            if(convertedAttr.getCategoricalValues().size()==2){
                if(convertedAttr.getCategoricalValues().contains("true") && convertedAttr.getCategoricalValues().contains("false") ){
                    RDRAttrType = "Boolean";
                    convertedAttr = AttributeFactory.createAttribute(RDRAttrType);
                }
            }
        } 
        
        convertedAttr.setName(arffAttr.name());
        convertedAttr.setAttributeType("Case Attribute");
        convertedAttr.setValueType(new ValueType(RDRAttrType));
        
        
        return convertedAttr;
    }

	
}
