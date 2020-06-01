/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.test;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.log4j.PropertyConfigurator;
import static rdr.apps.Main.KB;
import static rdr.apps.Main.allCaseSet;
import static rdr.apps.Main.domain;
import static rdr.apps.Main.testingCaseSet;

import rdr.apps.Main;
import rdr.cases.Case;
import rdr.cases.CaseLoader;
import rdr.cases.CaseSet;
import rdr.cases.CaseStructure;
import rdr.cases.CornerstoneCase;
import rdr.domain.Domain;
import rdr.domain.DomainLoader;
import rdr.gui.DomainEditorFrame;
import rdr.gui.StartupFrame;
import rdr.model.IAttribute;
import rdr.model.Value;
import rdr.rules.Rule;
import rdr.rules.RuleBuilder;
import rdr.rules.RuleSet;
import rdr.db.RDRDBManager;
import rdr.workbench.Workbench;

/**
 *
 * @author hchung
 */
public class checkCornerstoneCases {
    
    public static void main(String[] args) {
        System.out.println("Welcome. This is RDR engine ver1.2");
                
        String log4jConfPath = "./log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);
        
        //Define doamin
        String domainName = "seeGene";
                    
        domain = new Domain (domainName, "", "");
        allCaseSet = new CaseSet();
        testingCaseSet = new CaseSet();
        KB = new RuleSet();
        Rule rootRule = RuleBuilder.buildRootRule();
        KB.setRootRule(rootRule);

        Main.workbench = new Workbench(Domain.MCRDR);
        
        try {
            DomainLoader.openDomainFile(domainName, true);
            
            Main.workbench.setRuleSet(Main.KB);
            
//            findCornerstoneCasesUsingInference();
            
            findCornerstoneCasesUsingMissingRuleIds();
        } catch (Exception ex) {
            Logger.getLogger(checkCornerstoneCases.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    private static void findCornerstoneCasesUsingInference() {
        
        try {
            int caseAmount = CaseLoader.getCaseAmount();
            
            RuleSet totalInferenceResult = new RuleSet();
            
            for(int i=1; i<=caseAmount; i++){
                System.out.println("finding cornerstone case" + i);
//                Case currentCase = Main.allCaseSet.getCaseById(i);
                Case currentCase = CaseLoader.caseLoad(i, null);
                CornerstoneCase cornerstoneCase = new CornerstoneCase(currentCase);
                
                Main.workbench.setCaseForInference(currentCase);
                Main.workbench.inference();
                
                RuleSet inferenceResult = (RuleSet) Main.workbench.getInferenceResult();
                
                cornerstoneCase.setWrongRuleSet(inferenceResult);
                
                Set rules = inferenceResult.getBase().entrySet();
                Iterator ruleIterator = rules.iterator();
                while (ruleIterator.hasNext()) {
                    Map.Entry me = (Map.Entry) ruleIterator.next();
                    Rule rule = (Rule)me.getValue();
                    if(!totalInferenceResult.isRuleExist(rule)){
                        totalInferenceResult.addRule(rule);
                        
                        RDRDBManager.getInstance().insertRuleCornerstone(rule.getRuleId(), cornerstoneCase.getCaseId());
                        
                        if(!Main.allCornerstoneCaseSet.isCaseIdExist(cornerstoneCase.getCaseId())){
                            RDRDBManager.getInstance().insertCornerstoneValue(cornerstoneCase);
                            
                            Main.allCornerstoneCaseSet.addCornerstoneCase(null, cornerstoneCase);
                        }
                    }
                    
                }
                         
            }
            
        } catch (Exception ex) {
            Logger.getLogger(checkCornerstoneCases.class.getName()).log(Level.SEVERE, null, ex);
        }
            

    }
    
    private static void findCornerstoneCasesUsingMissingRuleIds() throws SQLException{
//        //getting rule ids that could not cover using current dataset.
//        String sql = "SELECT `rule_id`, `parentrule_id`, `conclusion_id`, `creation_date` FROM `tb_rule_structure` WHERE `rule_id` NOT IN (SELECT `rule_id` FROM tb_rule_cornerstones WHERE 1)";
//        ResultSet rs = RDRDBManager.getInstance().executeSelectQuery(sql, null);
//        
//        while(rs.next()) {
//            boolean found = false;
//            int ruleId = rs.getInt(1);
//            
//            System.out.println("checking rule " + ruleId);
//            
//            Rule missingRule = Main.KB.getRuleById(ruleId);
//            System.out.println(missingRule);
//            
//            try {
//                Set cornerstoneCases = Main.allCornerstoneCaseSet.getBase().entrySet();
//                // Get an iterator
//                Iterator cornerstoneCaseIterator = cornerstoneCases.iterator();
//                while(cornerstoneCaseIterator.hasNext()){
//                    Map.Entry me = (Map.Entry) cornerstoneCaseIterator.next();
//                    CornerstoneCase cornerstoneCase = (CornerstoneCase) me.getValue();
//                    System.out.println("checking case " + cornerstoneCase.getCaseId());
//                    if(missingRule.isSatisfied(cornerstoneCase)){
//                        System.out.println("found valid case =" + cornerstoneCase.getCaseId());
//                        RDRDBManager.getInstance().insertRuleCornerstone(missingRule.getRuleId(), cornerstoneCase.getCaseId());
//                        
//                        if(!Main.allCornerstoneCaseSet.isCaseIdExist(cornerstoneCase.getCaseId())){
//                            RDRDBManager.getInstance().insertCornerstoneValue(cornerstoneCase);
//                            Main.allCornerstoneCaseSet.addCornerstoneCase(null, cornerstoneCase);
//                        }
//                        found = true;
//                        break;
//                    }
//                }
//                
//                if(!found){
//                    Set cases = Main.allCaseSet.getBase().entrySet();
//                    // Get an iterator
//                    Iterator caseIterator = cases.iterator();
//                    while(caseIterator.hasNext()){
//                        Map.Entry me = (Map.Entry) caseIterator.next();
//                        Case currentCase = (Case) me.getValue();
//                        CornerstoneCase cornerstoneCase = new CornerstoneCase(currentCase);
//                        cornerstoneCase.setCaseId(Main.allCornerstoneCaseSet.getNewCornerstoneCaseId());
//                        if(missingRule.isSatisfied(cornerstoneCase)){
//                            System.out.println("found valid case =" + cornerstoneCase.getCaseId());
//                            RDRDBManager.getInstance().insertRuleCornerstone(missingRule.getRuleId(), cornerstoneCase.getCaseId());
//
//                            if(!Main.allCornerstoneCaseSet.isCaseIdExist(cornerstoneCase.getCaseId())){
//                                RDRDBManager.getInstance().insertCornerstoneValue(cornerstoneCase);
//                                Main.allCornerstoneCaseSet.addCornerstoneCase(null, cornerstoneCase);
//                            }
//                            found = true;
//                            break;
//                        }
//                    }
//                }
//                
//                if(!found){
//                    int caseAmount = CaseLoader.getCaseAmount();
//                    
//                    for(int i=435; i<=caseAmount; i++){
//                        System.out.println("finding cornerstone case " + i);
//                        Case currentCase = CaseLoader.caseLoad(i, null);
//                        Main.allCaseSet.addCase(currentCase);
//                        CornerstoneCase cornerstoneCase = new CornerstoneCase(currentCase);
//                        cornerstoneCase.setCaseId(Main.allCornerstoneCaseSet.getNewCornerstoneCaseId());
//                        
//                        if(missingRule.isSatisfied(currentCase)){
//                            System.out.println("found valid case =" + i);
//                            RDRDBManager.getInstance().insertRuleCornerstone(missingRule.getRuleId(), cornerstoneCase.getCaseId());
//
//                            if(!Main.allCornerstoneCaseSet.isCaseIdExist(cornerstoneCase.getCaseId())){
//                                RDRDBManager.getInstance().insertCornerstoneValue(cornerstoneCase);
//                                Main.allCornerstoneCaseSet.addCornerstoneCase(null, cornerstoneCase);
//                            }
//                            break;
//                        }
//                    }
//                }
//                
//            } catch (Exception ex) {
//                Logger.getLogger(checkCornerstoneCases.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            
//        }
//        
    }
}
