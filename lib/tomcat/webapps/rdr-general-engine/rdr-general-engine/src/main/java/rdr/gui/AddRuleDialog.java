/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.gui;

import java.awt.event.KeyEvent;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import rdr.apps.Main;
import rdr.cases.Case;
import rdr.cases.CaseLoader;
import rdr.cases.CornerstoneCase;
import rdr.cases.CornerstoneCaseSet;
import rdr.learner.Learner;
import rdr.model.IAttribute;
import rdr.model.Value;
import rdr.model.ValueType;
import rdr.rules.Conclusion;
import rdr.rules.ConclusionSet;
import rdr.rules.Condition;
import rdr.rules.ConditionSet;
import rdr.rules.Operator;
import rdr.rules.Rule;
import rdr.rules.RuleBuilder;
import rdr.rules.RuleSet;

/**
 * This class is used to present GUI for adding a new rule
 * 
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class AddRuleDialog extends javax.swing.JDialog {
    
    private static Case currentCase;
    private CornerstoneCase validatingCase;
    
    private static int kaMode;
    private Condition newCondition;
    private static ConditionSet preDefinedConditionSet;
    
    private Conclusion newConclusion = new Conclusion();
    private ConclusionSet tempConclusionSet = new ConclusionSet();
    
    private ConclusionSet validatingCaseConclusionSet = new ConclusionSet();
    private boolean isValidatingCase = false;
    
    private boolean isNewConclusionDefined = false;
    private static Conclusion wrongConclusion;
    private static ConditionSet usedConditionSet;
    
    boolean validatingCaseAscending = true;

    /**
     * Creates new form AddRuleDialog
     */
    public AddRuleDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        
        initComponents();
        learnerInit();
    }
    
    private void learnerInit(){   
        //clear wrong conclusion in workbench
        Main.workbench.deleteWrongConclusion();
        
        //create empty rule for new rule
        Rule newRule = new Rule();
        //set cornerstone case for new rule
        CornerstoneCase aCornerstoneCase = new CornerstoneCase(currentCase);                
        newRule.setCornerstoneCase(aCornerstoneCase);
        
        Main.workbench.setCurrentCornerstoneCase(aCornerstoneCase);
        //put the new rule into the workbench
        Main.workbench.setNewRule(newRule);
        //set ka mode for knowledge acquisition
        Main.workbench.setKaMode(kaMode);
        
        // set the possible conclusion into conclusion set
        if(kaMode==Learner.KA_NEW_MODE) {
            // if KA mode is new, set the wrong conclusion as null
            tempConclusionSet.setConclusionSet(Main.KB.getConclusionSet());
            
            // disable 
            updateUsedConditionTableWithNodata();
            usedConditionListTable.setEnabled(false);
            wrongConclusionTextarea.setText("You are currently in adding new conclusion mode.");
            wrongConclusionTextarea.setEnabled(false);
            mainSplitPanel.setDividerLocation(0.25);

            setEnableWizardTab(0);
            
        } else if(kaMode==Learner.KA_EXCEPTION_MODE) {
            // if KA mode is edit (refining), set the wrong conclusion.
            Main.workbench.setWrongConclusion(wrongConclusion);
            
            tempConclusionSet.setConclusionSet(Main.KB.getConclusionSet());
            
            //update wrong conclusion text area with the defined wrong conclusion
            updateWrongConclusionTextArea(wrongConclusion);
            
            usedConditionSet = getUsedConditionSet(currentCase, wrongConclusion);
            
            //update used conditions for the defined wrong conclusion
            updateUsedConditionTable(usedConditionSet);

            setEnableWizardTab(0);
            
        } else if(kaMode==Learner.KA_ALTER_MODE) {
            // if KA mode is add (alter), set the wrong conclusion as null.
            wrongConclusion = null;
            Main.workbench.setWrongConclusion(wrongConclusion);  
            
            tempConclusionSet.setConclusionSet(Main.KB.getConclusionSet());
            
            //update wrong conclusion text area with the defined wrong conclusion
            updateWrongConclusionTextArea(wrongConclusion);
            
            usedConditionSet = getUsedConditionSet(currentCase, wrongConclusion);
            
            //update used conditions for the defined wrong conclusion
            updateUsedConditionTable(usedConditionSet);

            setEnableWizardTab(0);
            
        } else if(kaMode==Learner.KA_STOPPING_MODE) {
            // if KA mode is stopping (deleting), set the wrong conclusion
            Main.workbench.setWrongConclusion(wrongConclusion);   

            //isNewWrongConclusion set
            tempConclusionSet = new ConclusionSet();
            tempConclusionSet.addConclusion(Main.KB.getRootRule().getConclusion());
            
            newConclusion = Main.KB.getRootRule().getConclusion();
            
            Main.workbench.setNewRuleConclusion(newConclusion);
            isNewConclusionDefined = true;
            
            updateNewConclusionTextarea(newConclusion);
            
            //update wrong conclusion text area with the defined wrong conclusion
            updateWrongConclusionTextArea(wrongConclusion);
            
            updateNewConclusionSelectionDialog(Learner.KA_STOPPING_MODE, null);
            
            Main.workbench.setCaseForInference(currentCase);
            
            Main.workbench.inference();
            Main.workbench.getInferenceResult();
            
            ConditionSet aConditionSet = getUsedConditionSet(currentCase, wrongConclusion);
            
            //update used conditions for the defined wrong conclusion
            updateUsedConditionTable(aConditionSet);

            setEnableWizardTab(1);
        }  
        //if there is predefined condition set update condition set
        if(preDefinedConditionSet != null){
            setPreDefinedConditionSet(preDefinedConditionSet);
        }
        
        //updateNewConclusionSelection
        updateNewConclusionSelection();
        
        //update current case table
        updateCurrentCaseTable(false, currentCase, null);
        
        //update attribute name array
        updateAttrComboBox();
    }
    
    private void setEnableWizardTab(int index) {
        if(index == 3){
            isValidatingCase = true;
        } else {
            isValidatingCase = false;
        }
        //set the current tab as condition tab (index 0)
        wizardTabbedPanel.setSelectedIndex(index);
        //disable other tabs
        for(int i=0; i<4; i++){
            if(i == index){
                wizardTabbedPanel.setEnabledAt(i, true);
            } else {
                wizardTabbedPanel.setEnabledAt(i, false);
            }
        }
    }
    
    private void setPreDefinedConditionSet(ConditionSet preDefinedConditionSet) {    
        Main.workbench.getLearner().setConditionSetToNewRule(preDefinedConditionSet);
        updateNewConditionListTable();
    }
    
    private void updateWrongConclusionTextArea(Conclusion aConclusion) {
        if(aConclusion != null) {            
            wrongConclusionTextarea.setText(aConclusion.toString());
        } else {
            wrongConclusionTextarea.setText("NULL");
        }
    }
    
    private ConditionSet getUsedConditionSet(Case aCase, Conclusion aConclusion) {
        ConditionSet aConditionSet = new ConditionSet();
        
        Main.workbench.setCaseForInference(aCase);
        Main.workbench.inference();
        
        if(Main.domain.isMCRDR()){
            RuleSet inferenceResult = ((RuleSet) Main.workbench.getInferenceResult()).getRuleSetbyConclusion(aConclusion);
            aConditionSet = inferenceResult.getRulePathConditionSet(false);
            
        } else if(Main.domain.isSCRDR()){
            Rule inferenceResult = (Rule) Main.workbench.getInferenceResult();
            aConditionSet = inferenceResult.getPathRuleConditionSet(false);
        }
        return aConditionSet;
    }
    
    private void updateUsedConditionTable(ConditionSet aConditionSet){

        int conditionAmount = aConditionSet.getConditionAmount();
        
        String[] columnNames = new String[]{"Attribute", "Operator", "Value"};
        Object[][] tempArray = aConditionSet.toObjectForGUI(conditionAmount);

        TableModel newModel = new DefaultTableModel(
                tempArray,columnNames
         ){
            boolean[] canEdit = new boolean []{false, false, false};    

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];                
            }
         };

        usedConditionListTable.setModel(newModel);
        usedConditionListTable.getTableHeader().setReorderingAllowed(false);

        if (usedConditionListTable.getColumnModel().getColumnCount() > 0) {
            usedConditionListTable.getColumnModel().getColumn(0).setPreferredWidth(90);
            usedConditionListTable.getColumnModel().getColumn(1).setPreferredWidth(90);
            usedConditionListTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        }
    }
    
    private void updateUsedConditionTableWithNodata(){
        String[] columnNames = new String[]{""};
        Object[][] tempArray = new Object[1][1];
        tempArray[0][0] = "No conditions (You are currently in adding new conclusion mode.)";
        
        TableModel newModel = new DefaultTableModel(
                tempArray,columnNames
         ){
            boolean[] canEdit = new boolean []{false};    

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];                
            }
         };

        usedConditionListTable.setModel(newModel);
    }
    
    private void updateNewConclusionSelectionDialog(int kaMode, ConclusionSet aConclusionSet){
        if(kaMode==Learner.KA_STOPPING_MODE) {
//            selectNewConclusionButton.setEnabled(false);
            
        } else {
            
        }
    }
    
    private void updateCurrentCaseTable(boolean missingFiltered, Case aCase, String textFilter) {
        String[] columnNames = new String[]{"Attribute", "Type", "Value"};
        Object[][] tempArray;
        
        if(missingFiltered) {
            if(textFilter == null){
                String[] filterArray = Main.missingValueArray;
                tempArray = aCase.getRemovingValueFilteredCase(filterArray).toSortedObjectForGUIRowWithType();
            } else {
                String[] filterArray = Main.missingValueArray;
                tempArray = aCase.getRemovingValueFilteredCase(filterArray).getShowingAttrNameFilteredCase(textFilter).toSortedObjectForGUIRowWithType();
            }
        } else {
            if(textFilter == null){
                tempArray = aCase.toSortedObjectForGUIRowWithType();
            } else {
                tempArray = aCase.getShowingAttrNameFilteredCase(textFilter).toSortedObjectForGUIRowWithType();
            }
        }
        
        TableModel newModel = new DefaultTableModel(
                tempArray,columnNames
         ){
            boolean[] canEdit = new boolean []{false, false, false};    
            
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];                
            }
         };
        
        currentCaseTable.setModel(newModel);
        currentCaseTable.getTableHeader().setReorderingAllowed(false);
        
        if (currentCaseTable.getColumnModel().getColumnCount() > 0) {
            currentCaseTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            currentCaseTable.getColumnModel().getColumn(1).setPreferredWidth(90);
            currentCaseTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        }
    }
    
    private void updateNewConclusionSelection(){
        updateNewConclusionTable(tempConclusionSet, null);
    }
    
    private void updateNewConclusionTable(ConclusionSet aConclusionSet, String conclusionFilter){
        String[] columnNames = new String[]{"Type", "Value"};
        Object[][] tempArray = aConclusionSet.toObjectsForGUIWithFilter(conclusionFilter);
        
        TableModel newModel = new DefaultTableModel(
                tempArray,columnNames
         ){
            boolean[] canEdit = new boolean []{false, false, false};    
            
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];                
            }
         };
        
        newConclusionTable.setModel(newModel);
        newConclusionTable.getTableHeader().setReorderingAllowed(false);
        if (newConclusionTable.getColumnModel().getColumnCount() > 0) {
            newConclusionTable.getColumnModel().getColumn(0).setMaxWidth(80);
        }
    }
    
    private void selectConclusion() {
        int selectedRow = newConclusionTable.getSelectedRow();
        if(selectedRow>-1){
            Value selectedValue = (Value) newConclusionTable.getValueAt(selectedRow, 1);

            newConclusion = tempConclusionSet.getConclusionByName(selectedValue.toString());

            updateNewConclusionTextarea(newConclusion);

            Main.workbench.setNewRuleConclusion(newConclusion);
            isNewConclusionDefined = true;

            //set the current tab as condition tab (index 1)
            setEnableWizardTab(1);
            
            
        } else {
            showMessageDialog(null, "Please select the conclusion.");
        }
    }
    
    private void addConclusion() {
        String conclusionType = (String) conclusionTypeComboBox.getSelectedItem();
        String conclusionName = conclusionValueField.getText();
        
        if(conclusionName.length() > 512){
            showMessageDialog(null, "This conclusion is too long.");
        } else if(conclusionName.length() == 0){
            showMessageDialog(null, "Please enter the conclusion.");
        } else {
            Value value = new Value(new ValueType(conclusionType), conclusionName);
            newConclusion = new Conclusion(value);            

            if(tempConclusionSet.isExist(newConclusion)){
                showMessageDialog(null, "This conclusion name is already used.");
            } else {
                tempConclusionSet.addConclusion(newConclusion);
                
                Main.workbench.setNewRuleConclusion(newConclusion);
                isNewConclusionDefined = true;
                
                updateNewConclusionTable(tempConclusionSet, null);
                updateNewConclusionTextarea(newConclusion);
            
                //set the current tab as condition tab (index 1)
                setEnableWizardTab(1);

            }
        }
    }
    
    private void updateNewConclusionTextarea(Conclusion aConclusion) {
//        String valueTypeName = aConclusion.getConclusionValue().getValueType().getTypeName();
//        newConclusionTextareaLabel.setText("Selected conclusion (" + valueTypeName + ")");
        selectedNewConclusionTextarea.setText(aConclusion.getConclusionName());
        selectedNewConclusionTextarea2.setText(aConclusion.getConclusionName());
        selectedNewConclusionTextarea.setEnabled(true);
    }
    
    
    private void updateConditionFields(Case aCase, String attrName) {
        IAttribute selectedAttr = aCase.getCaseStructure().getAttributeByName(attrName);
        //set potential operators
        String[] operatorsList = selectedAttr.getPotentialOperators();

        conAttrComboBox.setSelectedItem(attrName);
        conOperComboBox.setModel(new javax.swing.DefaultComboBoxModel(operatorsList));
        
        if(selectedAttr.getValueType().getTypeName().equals("CATEGORICAL")){
            conValField.setText(aCase.getValue(attrName).toString());
            conValField.setEditable(false);
        } else if (aCase.getValue(selectedAttr).getValueType().getTypeCode() == ValueType.NULL_TYPE){
            operatorsList = new String[]{"MISSING"};
            conOperComboBox.setModel(new javax.swing.DefaultComboBoxModel(operatorsList));
            conValField.setText("");
            conValField.setEditable(false);
        }else {
            conValField.setText(aCase.getValue(attrName).toString());
            conValField.setEditable(true);
        }
    }
    
    
    private boolean constructNewCondition() {        
        boolean isValid = false;
        String newConAttrStr = (String) conAttrComboBox.getSelectedItem();
        String newConOperStr = (String) conOperComboBox.getSelectedItem();
        String newConValStr = conValField.getText();
        
        newCondition = RuleBuilder.buildRuleCondition(currentCase.getCaseStructure(), newConAttrStr, newConOperStr, newConValStr);
        // check whether the new condition is valid for this case
        isValid = newCondition.isSatisfied(currentCase);
         
        return isValid;
    }
    
    private void addCondition() {
        //check whether the new condition value field is empty
        String newConVal = conValField.getText();
        if(!newConVal.equals("") && newConVal!=null){            
            // construct new condition and check whether the condition is valid for this case            
            if(constructNewCondition()) {
                // add condition and check whether there is duplicating one
                if(Main.workbench.addConditionToNewRule(newCondition)){
                    // if the condition is valid to add, then update condition table
                    updateNewConditionListTable();                
                } else {                    
                    showMessageDialog(null, "This condition is already added.");
                }
            } else {
                showMessageDialog(null, "Condition does not satisfy with the current case.");
            }
            
        } else {
            if(conOperComboBox.getSelectedItem().equals("MISSING") || conOperComboBox.getSelectedItem().equals("NOT MISSING")){
                if(constructNewCondition()) {
                // add condition and check whether there is duplicating one
                if(Main.workbench.addConditionToNewRule(newCondition)){
                    // if the condition is valid to add, then update condition table
                    updateNewConditionListTable();                
                } else {                    
                    showMessageDialog(null, "This condition is already added.");
                }
            } else {
                showMessageDialog(null, "Condition does not satisfy with the current case.");
            }
            } else {
                showMessageDialog(null, "Please enter condition value");
            }
        }
        
    }
    
    private void deleteCondition() {
        int[] selectedRows = newConditionTable.getSelectedRows();
        int selectedAmount = newConditionTable.getSelectedRowCount();
        for (int i=0; i<selectedAmount; i++){
            
            String conditionAttrName = ((IAttribute) newConditionTable.getValueAt(selectedRows[i], 0)).getName();
            String conditionOper = ((Operator) newConditionTable.getValueAt(selectedRows[i], 1)).getOperatorName();
            String conditionVal = ((Value) newConditionTable.getValueAt(selectedRows[i], 2)).toString();
                        
            Condition deletingCondition = RuleBuilder.buildRuleCondition(currentCase.getCaseStructure(), conditionAttrName, conditionOper, conditionVal);
            
            if(!Main.workbench.getLearner().deleteConditionFromNewRule(deletingCondition)){
                showMessageDialog(null, "This condition cannot be deleted.");
            } 
            
        }        
        updateNewConditionListTable();
    }
    
    private void updateReAddingConditionFields(String caseType, Case aCase, String attrName) {
        if(caseType.equals("validating")){
            IAttribute selectedAttr = aCase.getCaseStructure().getAttributeByName(attrName);
            //set potential operators
            String[] operatorsList = selectedAttr.getPotentialOperators();

            reAddingConAttrComboBox.setSelectedItem(attrName);
            reAddingConOperComboBox1.setModel(new javax.swing.DefaultComboBoxModel(operatorsList));
            reAddingConOperComboBox1.setSelectedItem("!=");

            if(selectedAttr.getValueType().getTypeName().equals("CATEGORICAL")){
                reAddingConValField1.setText(aCase.getValue(attrName).toString());
                reAddingConValField1.setEditable(false);
            } else if (aCase.getValue(selectedAttr).getValueType().getTypeCode() == ValueType.NULL_TYPE){
                operatorsList = new String[]{"NOT MISSING"};
                reAddingConOperComboBox1.setModel(new javax.swing.DefaultComboBoxModel(operatorsList));
                reAddingConValField1.setText("");
                reAddingConValField1.setEditable(false);
            } else {
                reAddingConValField1.setText(aCase.getValue(attrName).toString());
                reAddingConValField1.setEditable(true);
            }
            
        } else if(caseType.equals("current")){

            IAttribute selectedAttr = aCase.getCaseStructure().getAttributeByName(attrName);
            //set potential operators
            String[] operatorsList = selectedAttr.getPotentialOperators();

            reAddingConAttrComboBox.setSelectedItem(attrName);
            reAddingConOperComboBox1.setModel(new javax.swing.DefaultComboBoxModel(operatorsList));

            if(selectedAttr.getValueType().getTypeName().equals("CATEGORICAL")){
                reAddingConValField1.setText(aCase.getValue(attrName).toString());
                reAddingConValField1.setEditable(false);
            } else if (aCase.getValue(selectedAttr).getValueType().getTypeCode() == ValueType.NULL_TYPE){
                operatorsList = new String[]{"MISSING"};
                reAddingConOperComboBox1.setModel(new javax.swing.DefaultComboBoxModel(operatorsList));
                reAddingConValField1.setText("");
                reAddingConValField1.setEditable(false);
            }else {
                reAddingConValField1.setText(aCase.getValue(attrName).toString());
                reAddingConValField1.setEditable(true);
            }
        }
    }
    
    
    private boolean constructNewConditionFromReAdding() {        
        boolean isValid = false;
        String newConAttrStr = (String) reAddingConAttrComboBox.getSelectedItem();
        String newConOperStr = (String) reAddingConOperComboBox1.getSelectedItem();
        String newConValStr = reAddingConValField1.getText();
        
        newCondition = RuleBuilder.buildRuleCondition(currentCase.getCaseStructure(), newConAttrStr, newConOperStr, newConValStr);
        
        // check whether the new condition is valid for this case
        if(!newCondition.isSatisfied(currentCase)){
            showMessageDialog(null, "Condition does not satisfy with the current case.");
        } else {
            isValid = true;
        }
        return isValid;
    }
    
    private void addConditionForReAdding() {
        //check whether the new condition value field is empty
        String newConVal = reAddingConValField1.getText();
        if(!newConVal.equals("") && newConVal!=null){
            
            // construct new condition and check whether the condition is valid for this case            
            if(constructNewConditionFromReAdding()) {
                // add condition and check whether there is duplicating one
                if(Main.workbench.addConditionToNewRule(newCondition)){
                    if(Main.workbench.getNewRule().isSatisfied(validatingCase)){
                        showMessageDialog(null, "Condition still satisfies with the validating case.");
                    }                    
                    // if the condition is valid to add, then update condition table
                    updateNewConditionListTable();                
                } else {                    
                    showMessageDialog(null, "This condition is already added.");
                }
            }
        } else {
             if(reAddingConOperComboBox1.getSelectedItem().equals("MISSING") || reAddingConOperComboBox1.getSelectedItem().equals("NOT MISSING") ){
                // construct new condition and check whether the condition is valid for this case            
                if(constructNewConditionFromReAdding()) {
                    // add condition and check whether there is duplicating one
                    if(Main.workbench.addConditionToNewRule(newCondition)){
                        if(Main.workbench.getNewRule().isSatisfied(validatingCase)){
                            showMessageDialog(null, "Condition still satisfies with the validating case.");
                        }                    
                        // if the condition is valid to add, then update condition table
                        updateNewConditionListTable();                
                    } else {                    
                        showMessageDialog(null, "This condition is already added.");
                    }
                }
            } else {
                showMessageDialog(null, "Please enter condition value");
            }
        }
        
    }
    
    private void deleteConditionFromReadding() {
        int[] selectedRows = newReAddingConditionTable.getSelectedRows();
        int selectedAmount = newReAddingConditionTable.getSelectedRowCount();
        for (int i=0; i<selectedAmount; i++){
            String conditionAttrName = ((IAttribute) newReAddingConditionTable.getValueAt(selectedRows[i], 0)).getName();
            String conditionOper = ((Operator) newReAddingConditionTable.getValueAt(selectedRows[i], 1)).getOperatorName();
            String conditionVal = ((Value) newReAddingConditionTable.getValueAt(selectedRows[i], 2)).toString();
                        
            Condition deletingCondition = RuleBuilder.buildRuleCondition(currentCase.getCaseStructure(), conditionAttrName, conditionOper, conditionVal);
            
            if(!Main.workbench.getLearner().deleteConditionFromNewRule(deletingCondition)){
                showMessageDialog(null, "This condition cannot be deleted.");
            } 
            
        }        
        updateNewConditionListTable();
    }
    
    private void updateNewConditionListTable() {        
        int conditionAmount = Main.workbench.getLearner().getConditionAmountFromNewRule();
        String[] columnNames = new String[]{"Attribute", "Operator", "Value"};
        
        Object[][] tempArray = Main.workbench.getLearner().getConditionSetFromNewRule().toObjectForGUI(conditionAmount);
        
        TableModel newModel = new DefaultTableModel(
                tempArray,columnNames
         ){
            boolean[] canEdit = new boolean []{false, false, false};    
            
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];                
            }
         };
        
        newConditionTable.setModel(newModel);
        leftNewConditionTable.setModel(newModel);
        newReAddingConditionTable.setModel(newModel);
        newConditionTable.getTableHeader().setReorderingAllowed(false);
        newReAddingConditionTable.getTableHeader().setReorderingAllowed(false);
        
//        newConditionListTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        newConditionTable.getTableHeader().setReorderingAllowed(false);
        newReAddingConditionTable.getTableHeader().setReorderingAllowed(false);
        
        if (newConditionTable.getColumnModel().getColumnCount() > 0) {
            newConditionTable.getColumnModel().getColumn(0).setPreferredWidth(90);
            newConditionTable.getColumnModel().getColumn(1).setPreferredWidth(90);
            newConditionTable.getColumnModel().getColumn(2).setPreferredWidth(120);
            newReAddingConditionTable.getColumnModel().getColumn(0).setPreferredWidth(90);
            newReAddingConditionTable.getColumnModel().getColumn(1).setPreferredWidth(90);
            newReAddingConditionTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        }
        if(newConditionTable.getRowCount()==0){
            validationButton.setEnabled(false);
        } else {
            validationButton.setEnabled(true);
        }
    }
    
    private void validateRule() {
        // clear cornerstonce case set (but the current case must be remained)
        CornerstoneCase cornerstoneCase = new CornerstoneCase(currentCase);     
        Main.workbench.setCurrentCornerstoneCase(cornerstoneCase);
        Main.workbench.getNewRule().setCornerstoneCase(cornerstoneCase);
        Main.workbench.getLearner().retrieveValidatingCaseSet(kaMode);            
        CornerstoneCaseSet validatingCaseSet = Main.workbench.getLearner().getValidatingCaseSet();
        
        if(validatingCaseSet.getCaseAmount()==0){
            int confirmed = JOptionPane.showConfirmDialog(validationDialog,
                    "There is no validation cases. Do you want to add the rule?", "Confirm Add Rule",
                    JOptionPane.YES_NO_OPTION);
            //Close if user confirmed
            if (confirmed == JOptionPane.YES_OPTION)
            {
               addRule();
            }
        } else {
            validatingCase = validatingCaseSet.getFirstCornerstoneCase();

            validateCornerstoneCase();

            updateValCaseSetTable(validatingCaseSet);
            
            boolean valCaseFilteredCheck =  refineCaseHideMissingCheckbox.isSelected();
            String textFilter = refineAttrNameFilterTextField.getText();
            updateValCaseTable(valCaseFilteredCheck, validatingCase, textFilter);
            validatingCaseConclusionSet = validatingCase.getConclusionSet();

            validationAcceptButton.setEnabled(true);
//                generateDiffButton.setEnabled(true);
            if(validatingCaseSet.getCaseAmount()>1){;
//                valCasePrevButton.setEnabled(false);
//                valCaseNextButton.setEnabled(true);
            }

            //set the current tab as condition tab (index 2)
            setEnableWizardTab(2);

        }        
    }
    
    private void validateCornerstoneCase() {
        Main.workbench.setValidatingCase(validatingCase);
        Main.workbench.inferenceForValidation();
    }
    
    private void updateValCaseSetTable(CornerstoneCaseSet caseSet){
        int attributeAmount = validatingCase.getCaseStructure().getAttrAmount();
        // +1 for case id
        int columnCount = attributeAmount+1;
        
        String filter = valSearchAttrNameTextField.getText();
        String[] columnNames = validatingCase.getCaseStructure().getFilteredSortedAttributeNameArrayWithOtherName(filter, "Cornerstone Case ID");
        
        // for table cell editable stting
        boolean[] tempCanEdit = new boolean [columnCount];
        
        
        for(int i=0;i<columnCount;i++){
            tempCanEdit[i] = false;
        }
        
        // case amount
        int caseAmount = caseSet.getCaseAmount();
        
        // case id plus attribute amount 
        int colAmount = validatingCase.getCaseStructure().getAttrAmount() + 1;
        
        Object[][] tempArray = caseSet.toFilteredSortedObjectForGUI(filter, caseAmount, colAmount);
        
        
        TableModel newModel = new DefaultTableModel(
                tempArray,columnNames
         ){
            boolean[] canEdit = tempCanEdit;
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
         };
        
        valCaseListTable.setModel(newModel);
        
        if(valCaseListTable.getColumnCount()>6){
            valCaseListTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        }
    }
    
     private void updateCurrentCaseTableInValidation(boolean missingFiltered, Case aCase, String textFilter) {
        String[] columnNames = new String[]{"Attribute", "Type", "Value"};
        Object[][] tempArray;
        
        if(missingFiltered) {
            if(textFilter == null){
                String[] filterArray = Main.missingValueArray;
                tempArray = aCase.getRemovingValueFilteredCase(filterArray).toSortedObjectForGUIRowWithType();
            } else {
                String[] filterArray = Main.missingValueArray;
                tempArray = aCase.getRemovingValueFilteredCase(filterArray).getShowingAttrNameFilteredCase(textFilter).toSortedObjectForGUIRowWithType();
            }
        } else {
            if(textFilter == null){
                tempArray = aCase.toSortedObjectForGUIRowWithType();
            } else {
                tempArray = aCase.getShowingAttrNameFilteredCase(textFilter).toSortedObjectForGUIRowWithType();
            }
        }
        
        TableModel newModel = new DefaultTableModel(
                tempArray,columnNames
         ){
            boolean[] canEdit = new boolean []{false, false, false};    
            
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];                
            }
         };
        
        reAddingCurrentCaseTable.setModel(newModel);
        reAddingCurrentCaseTable.getTableHeader().setReorderingAllowed(false);
        
        if (reAddingCurrentCaseTable.getColumnModel().getColumnCount() > 0) {
            reAddingCurrentCaseTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            reAddingCurrentCaseTable.getColumnModel().getColumn(1).setPreferredWidth(90);
            reAddingCurrentCaseTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        }
    }
    
    private void updateValCaseTable(boolean missingFiltered, Case aCase, String textFilter){
        String[] columnNames = new String[]{"Attribute", "Type", "Value"};
        
        Object[][] tempArray;
        if(missingFiltered) {
            if(textFilter == null){
                String[] filterArray = Main.missingValueArray;
                tempArray = aCase.getRemovingValueFilteredCase(filterArray).toSortedObjectForGUIRowWithType();
            } else {
                String[] filterArray = Main.missingValueArray;
                tempArray = aCase.getRemovingValueFilteredCase(filterArray).getShowingAttrNameFilteredCase(textFilter).toSortedObjectForGUIRowWithType();
            }
        } else {
            if(textFilter == null){
                tempArray = aCase.toSortedObjectForGUIRowWithType();
            } else {
                tempArray = aCase.getShowingAttrNameFilteredCase(textFilter).toSortedObjectForGUIRowWithType();
            }
        }
        
        TableModel newModel = new DefaultTableModel(
                tempArray,columnNames
         ){
            boolean[] canEdit = new boolean []{false, false, false};    
            
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];                
            }
         };
        
        valCaseLabel.setText("Affected case (" + aCase.getCaseId() + ")");
        valCaseTable.setModel(newModel);
        valCaseTable.getTableHeader().setReorderingAllowed(false);
        
//        currentCaseTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        valCaseTable.getTableHeader().setReorderingAllowed(false);
        
        if (valCaseTable.getColumnModel().getColumnCount() > 0) {
            valCaseTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            valCaseTable.getColumnModel().getColumn(1).setPreferredWidth(90);
            valCaseTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        }
        valCaseListTable.setRowSelectionInterval(0, 0);
    }
        
    private void updateAttrComboBox() {
        String[] attrNames = currentCase.getCaseStructure().getAttributeNameArray();
        conAttrComboBox.setModel(new javax.swing.DefaultComboBoxModel(attrNames));
        reAddingConAttrComboBox.setModel(new javax.swing.DefaultComboBoxModel(attrNames));
    }

    private void valModeSetEnabled(boolean bool){
        valCaseTable.setEnabled(bool);
        validationAcceptButton.setEnabled(bool);
//        generateDiffButton.setEnabled(bool);
    }
    
    private void checkValidationCaseInterface(){
        CornerstoneCaseSet validatingCaseSet = Main.workbench.getLearner().getValidatingCaseSet();
        validationAcceptButton.setEnabled(true);
//        generateDiffButton.setEnabled(true);
        
        if(validatingCaseSet.hasNextCornerstoneCase(validatingCase)){
        } else {
        }
        if(!validatingCaseSet.isFirstCornerstoneCase(validatingCase)){
        } else {
        }
    }
    
    private void updateOtherConclusionList(ConclusionSet conclusionSet){
        otherConclusionList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = conclusionSet.toStringArrayForGUIWithoutAddConclusion();
            @Override
            public int getSize() { return strings.length; }
            @Override
            public Object getElementAt(int i) { return strings[i]; }
        });
    }
    
    private void acceptSelectedValidatingCase() {
        int selectedRow = valCaseListTable.getSelectedRow();
        if(selectedRow >= 0) {
            int selectedValidatingCaseId = (int) valCaseListTable.getValueAt(selectedRow, 0);
            CornerstoneCase selectedValidatingCase = Main.workbench.getLearner().getValidatingCaseSet().getCornerstoneCaseById(selectedValidatingCaseId);

            Main.workbench.getLearner().getValidatingCaseSet().deleteCornerstoneCase(selectedValidatingCase);

            CornerstoneCaseSet validatingCaseSet = Main.workbench.getLearner().getValidatingCaseSet();

            if(validatingCaseSet.getCaseAmount()==0){
                int confirmed = JOptionPane.showConfirmDialog(validationDialog,
                        "There is no validation cases. Do you want to add the rule?", "Confirm Add Rule",
                        JOptionPane.YES_NO_OPTION);
                //Close if user confirmed
                if (confirmed == JOptionPane.YES_OPTION)
                {
                   addRule();
                }
            } else {
                validatingCase = Main.workbench.getLearner().getValidatingCaseSet().getFirstCornerstoneCase();
                updateValCaseSetTable(validatingCaseSet);
                validateCornerstoneCase();
                boolean valCaseFilteredCheck = refineCaseHideMissingCheckbox.isSelected();
                String textFilter = refineAttrNameFilterTextField.getText();
                updateValCaseTable(valCaseFilteredCheck, validatingCase, textFilter);

                checkValidationCaseInterface();
            }
        } else {
            showMessageDialog(null, "Please select the case.");
        }
    }
        
    private void addRule(){
    	StringBuilder sb = new StringBuilder();
        Main.workbench.executeAddingRule(sb);
        Main.KB.setRuleSet(Main.workbench.getRuleSet());
        Main.KB.setRootRuleTree();

        //dispose validationDialog && AddRuleDialog
        this.setModal(false);
        this.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        conditionControlPopup = new javax.swing.JPopupMenu();
        deleteSelectedItem = new javax.swing.JMenuItem();
        validationDialog = new javax.swing.JDialog();
        jPanel5 = new javax.swing.JPanel();
        validateCasePopupMenu = new javax.swing.JPopupMenu();
        addingNewConditionOption = new javax.swing.JMenuItem();
        reAddingConditionControlPopup = new javax.swing.JPopupMenu();
        reAddingDeleteSelectedItem = new javax.swing.JMenuItem();
        addNewConclusionDialog = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        conclusionTypeComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        conclusionValueField = new javax.swing.JTextField();
        addConclusionButton = new javax.swing.JButton();
        newConclusionLabel1 = new javax.swing.JLabel();
        mainSplitPanel = new javax.swing.JSplitPane();
        jPanel9 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane12 = new javax.swing.JScrollPane();
        leftNewConditionTable = new javax.swing.JTable();
        valCaseListLabel3 = new javax.swing.JLabel();
        conclusionLabel2 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        selectedNewConclusionTextarea = new javax.swing.JTextArea();
        wizardPanel = new javax.swing.JPanel();
        wizardTabbedPanel = new javax.swing.JTabbedPane();
        conclusionSelectionPanel = new javax.swing.JPanel();
        newConclusionPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        newConclusionTable = new javax.swing.JTable();
        newConclusionLabel = new javax.swing.JLabel();
        currentCaseLabel3 = new javax.swing.JLabel();
        conclusionNameFilterTextField = new javax.swing.JTextField();
        wrongConclusionPanel = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        wrongConclusionTextarea = new javax.swing.JTextArea();
        wrongConclusionLabel = new javax.swing.JLabel();
        addNewConclusionButton = new javax.swing.JButton();
        selectConclusionButton = new javax.swing.JButton();
        conditionSelectionPanel = new javax.swing.JPanel();
        validationButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        usedConditionPanel = new javax.swing.JPanel();
        jSplitPane4 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        currentCaseTable = new javax.swing.JTable();
        currentCaseHideMissingCheckbox = new javax.swing.JCheckBox();
        currentCaseLabel = new javax.swing.JLabel();
        attrNameFilterTextField = new javax.swing.JTextField();
        currentCaseLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        usedConditionListTable = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        newConditionPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        newConditionTable = new javax.swing.JTable();
        conditionLabel = new javax.swing.JLabel();
        conAddButton = new javax.swing.JButton();
        conAttrComboBox = new javax.swing.JComboBox();
        conValField = new javax.swing.JTextField();
        conOperComboBox = new javax.swing.JComboBox();
        newConditionLabel = new javax.swing.JLabel();
        validateCornerstoneCasesPanel = new javax.swing.JPanel();
        validateCornerstoneCaseTopPanel = new javax.swing.JPanel();
        validatingCaseListPanel = new javax.swing.JPanel();
        valCaseListLabel = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        valCaseListTable = new javax.swing.JTable();
        conclusionLabel = new javax.swing.JLabel();
        conclusionLabel1 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        otherConclusionList = new javax.swing.JList();
        jScrollPane13 = new javax.swing.JScrollPane();
        selectedNewConclusionTextarea2 = new javax.swing.JTextArea();
        validationAcceptButton = new javax.swing.JButton();
        validationAcceptButton1 = new javax.swing.JButton();
        validationAcceptAllButton = new javax.swing.JButton();
        valSearchAttrNameTextField = new javax.swing.JTextField();
        valCaseListLabel1 = new javax.swing.JLabel();
        validateRulePanel = new javax.swing.JPanel();
        checkAgainButton = new javax.swing.JButton();
        checkAgainButton1 = new javax.swing.JButton();
        refineRuleLabel = new javax.swing.JLabel();
        jSplitPane3 = new javax.swing.JSplitPane();
        jPanel8 = new javax.swing.JPanel();
        refineCaseHideMissingCheckbox = new javax.swing.JCheckBox();
        currentCaseLabel5 = new javax.swing.JLabel();
        refineAttrNameFilterTextField = new javax.swing.JTextField();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        reAddingCurrentCaseTable = new javax.swing.JTable();
        currentCaseLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        valCaseTable = new javax.swing.JTable();
        valCaseLabel = new javax.swing.JLabel();
        newConditionPanel1 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        newReAddingConditionTable = new javax.swing.JTable();
        conditionLabel1 = new javax.swing.JLabel();
        reAddingConAddButton = new javax.swing.JButton();
        reAddingConAttrComboBox = new javax.swing.JComboBox();
        reAddingConValField1 = new javax.swing.JTextField();
        reAddingConOperComboBox1 = new javax.swing.JComboBox();

        deleteSelectedItem.setText("Delete Selected");
        deleteSelectedItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSelectedItemActionPerformed(evt);
            }
        });
        conditionControlPopup.add(deleteSelectedItem);

        validationDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        validationDialog.setTitle("Validating Case");
        validationDialog.setMinimumSize(new java.awt.Dimension(800, 620));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 151, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 576, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout validationDialogLayout = new javax.swing.GroupLayout(validationDialog.getContentPane());
        validationDialog.getContentPane().setLayout(validationDialogLayout);
        validationDialogLayout.setHorizontalGroup(
            validationDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 788, Short.MAX_VALUE)
            .addGroup(validationDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(validationDialogLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        validationDialogLayout.setVerticalGroup(
            validationDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
            .addGroup(validationDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(validationDialogLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        addingNewConditionOption.setText("Except this case (add more condition)");
        addingNewConditionOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addingNewConditionOptionActionPerformed(evt);
            }
        });
        validateCasePopupMenu.add(addingNewConditionOption);

        reAddingDeleteSelectedItem.setText("Delete Selected");
        reAddingDeleteSelectedItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reAddingDeleteSelectedItemActionPerformed(evt);
            }
        });
        reAddingConditionControlPopup.add(reAddingDeleteSelectedItem);

        addNewConclusionDialog.setTitle("Add New Conclusion");
        addNewConclusionDialog.setMinimumSize(new java.awt.Dimension(535, 160));
        addNewConclusionDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                addNewConclusionDialogWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                addNewConclusionDialogWindowOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("New conclusion value");

        conclusionTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TEXT" }));

        jLabel1.setText("New conclusion type");

        conclusionValueField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conclusionValueFieldActionPerformed(evt);
            }
        });

        addConclusionButton.setText("Add");
        addConclusionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addConclusionButtonActionPerformed(evt);
            }
        });

        newConclusionLabel1.setFont(new java.awt.Font("굴림", 1, 12)); // NOI18N
        newConclusionLabel1.setText("Add new conclusion");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(conclusionValueField, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addConclusionButton))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(conclusionTypeComboBox, 0, 352, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(newConclusionLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(newConclusionLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(conclusionTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addConclusionButton)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(conclusionValueField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)))
                .addContainerGap())
        );

        javax.swing.GroupLayout addNewConclusionDialogLayout = new javax.swing.GroupLayout(addNewConclusionDialog.getContentPane());
        addNewConclusionDialog.getContentPane().setLayout(addNewConclusionDialogLayout);
        addNewConclusionDialogLayout.setHorizontalGroup(
            addNewConclusionDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addNewConclusionDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        addNewConclusionDialogLayout.setVerticalGroup(
            addNewConclusionDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addNewConclusionDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add New Rule");

        jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        leftNewConditionTable.setAutoCreateRowSorter(true);
        leftNewConditionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Please select condition"}
            },
            new String [] {
                ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        leftNewConditionTable.setMinimumSize(new java.awt.Dimension(45, 200));
        jScrollPane12.setViewportView(leftNewConditionTable);

        valCaseListLabel3.setFont(new java.awt.Font("굴림", 1, 12)); // NOI18N
        valCaseListLabel3.setText("Conditions");

        conclusionLabel2.setFont(new java.awt.Font("굴림", 1, 12)); // NOI18N
        conclusionLabel2.setText("New conclusion");

        selectedNewConclusionTextarea.setEditable(false);
        selectedNewConclusionTextarea.setColumns(20);
        selectedNewConclusionTextarea.setFont(new java.awt.Font("굴림", 0, 12)); // NOI18N
        selectedNewConclusionTextarea.setForeground(new java.awt.Color(255, 51, 0));
        selectedNewConclusionTextarea.setLineWrap(true);
        selectedNewConclusionTextarea.setRows(2);
        selectedNewConclusionTextarea.setText("(Please select conclusion)");
        selectedNewConclusionTextarea.setWrapStyleWord(true);
        selectedNewConclusionTextarea.setEnabled(false);
        jScrollPane10.setViewportView(selectedNewConclusionTextarea);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
            .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(conclusionLabel2)
            .addComponent(valCaseListLabel3)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(valCaseListLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(conclusionLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("New rule details", jPanel10);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        mainSplitPanel.setLeftComponent(jPanel9);

        newConclusionTable.setAutoCreateRowSorter(true);
        newConclusionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Type", "Value"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        newConclusionTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane4.setViewportView(newConclusionTable);
        if (newConclusionTable.getColumnModel().getColumnCount() > 0) {
            newConclusionTable.getColumnModel().getColumn(0).setMaxWidth(100);
        }

        newConclusionLabel.setFont(new java.awt.Font("굴림", 1, 12)); // NOI18N
        newConclusionLabel.setText("Select conclusion");
        newConclusionLabel.setToolTipText("Select conclusion for the new rule.");

        currentCaseLabel3.setText("Search conclusion");

        conclusionNameFilterTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                conclusionNameFilterTextFieldKeyReleased(evt);
            }
        });

        wrongConclusionPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        wrongConclusionTextarea.setEditable(false);
        wrongConclusionTextarea.setColumns(20);
        wrongConclusionTextarea.setFont(new java.awt.Font("굴림", 0, 12)); // NOI18N
        wrongConclusionTextarea.setForeground(new java.awt.Color(153, 153, 153));
        wrongConclusionTextarea.setLineWrap(true);
        wrongConclusionTextarea.setRows(2);
        wrongConclusionTextarea.setText("NULL");
        wrongConclusionTextarea.setWrapStyleWord(true);
        wrongConclusionTextarea.setMinimumSize(new java.awt.Dimension(104, 0));
        wrongConclusionTextarea.setName(""); // NOI18N
        jScrollPane9.setViewportView(wrongConclusionTextarea);

        wrongConclusionLabel.setFont(new java.awt.Font("굴림", 1, 12)); // NOI18N
        wrongConclusionLabel.setForeground(new java.awt.Color(153, 153, 153));
        wrongConclusionLabel.setText("Wrong (previous) conclusion");

        javax.swing.GroupLayout wrongConclusionPanelLayout = new javax.swing.GroupLayout(wrongConclusionPanel);
        wrongConclusionPanel.setLayout(wrongConclusionPanelLayout);
        wrongConclusionPanelLayout.setHorizontalGroup(
            wrongConclusionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wrongConclusionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(wrongConclusionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9)
                    .addGroup(wrongConclusionPanelLayout.createSequentialGroup()
                        .addComponent(wrongConclusionLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        wrongConclusionPanelLayout.setVerticalGroup(
            wrongConclusionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wrongConclusionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wrongConclusionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                .addContainerGap())
        );

        addNewConclusionButton.setText("Add New Conclusion");
        addNewConclusionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewConclusionButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout newConclusionPanelLayout = new javax.swing.GroupLayout(newConclusionPanel);
        newConclusionPanel.setLayout(newConclusionPanelLayout);
        newConclusionPanelLayout.setHorizontalGroup(
            newConclusionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newConclusionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(newConclusionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(wrongConclusionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, newConclusionPanelLayout.createSequentialGroup()
                        .addComponent(currentCaseLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(conclusionNameFilterTextField))
                    .addGroup(newConclusionPanelLayout.createSequentialGroup()
                        .addGroup(newConclusionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(addNewConclusionButton)
                            .addComponent(newConclusionLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        newConclusionPanelLayout.setVerticalGroup(
            newConclusionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newConclusionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(newConclusionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(wrongConclusionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(newConclusionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(currentCaseLabel3)
                    .addComponent(conclusionNameFilterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addNewConclusionButton)
                .addContainerGap())
        );

        selectConclusionButton.setText("Next");
        selectConclusionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectConclusionButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout conclusionSelectionPanelLayout = new javax.swing.GroupLayout(conclusionSelectionPanel);
        conclusionSelectionPanel.setLayout(conclusionSelectionPanelLayout);
        conclusionSelectionPanelLayout.setHorizontalGroup(
            conclusionSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(conclusionSelectionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(conclusionSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(newConclusionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, conclusionSelectionPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(selectConclusionButton)))
                .addContainerGap())
        );
        conclusionSelectionPanelLayout.setVerticalGroup(
            conclusionSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(conclusionSelectionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(newConclusionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectConclusionButton)
                .addContainerGap())
        );

        wizardTabbedPanel.addTab("Select conclusion", conclusionSelectionPanel);

        validationButton.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        validationButton.setText("Next");
        validationButton.setEnabled(false);
        validationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validationButtonActionPerformed(evt);
            }
        });

        jSplitPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.5);

        jSplitPane4.setBorder(null);
        jSplitPane4.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        currentCaseTable.setAutoCreateRowSorter(true);
        currentCaseTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Attribute", "Type", "Value"
            }
        ));
        currentCaseTable.setMinimumSize(new java.awt.Dimension(200, 200));
        currentCaseTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        currentCaseTable.getTableHeader().setReorderingAllowed(false);
        currentCaseTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                currentCaseTableMouseReleased(evt);
            }
        });
        currentCaseTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                currentCaseTableKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(currentCaseTable);

        currentCaseHideMissingCheckbox.setText("Hide missing");
        currentCaseHideMissingCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currentCaseHideMissingCheckboxActionPerformed(evt);
            }
        });

        currentCaseLabel.setFont(new java.awt.Font("굴림", 1, 12)); // NOI18N
        currentCaseLabel.setText("Current case");

        attrNameFilterTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                attrNameFilterTextFieldKeyReleased(evt);
            }
        });

        currentCaseLabel1.setText("Search attribute name");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(currentCaseLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(currentCaseHideMissingCheckbox))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(currentCaseLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(attrNameFilterTextField)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(currentCaseLabel)
                    .addComponent(currentCaseHideMissingCheckbox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(attrNameFilterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(currentCaseLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane4.setLeftComponent(jPanel2);

        usedConditionListTable.setAutoCreateRowSorter(true);
        usedConditionListTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"NULL", "NULL", "NULL"}
            },
            new String [] {
                "Attribute", "Operator", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane8.setViewportView(usedConditionListTable);

        jLabel5.setFont(new java.awt.Font("굴림", 1, 12)); // NOI18N
        jLabel5.setText("Current used conditions");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))
        );

        jSplitPane4.setRightComponent(jPanel4);

        javax.swing.GroupLayout usedConditionPanelLayout = new javax.swing.GroupLayout(usedConditionPanel);
        usedConditionPanel.setLayout(usedConditionPanelLayout);
        usedConditionPanelLayout.setHorizontalGroup(
            usedConditionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane4)
        );
        usedConditionPanelLayout.setVerticalGroup(
            usedConditionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, usedConditionPanelLayout.createSequentialGroup()
                .addComponent(jSplitPane4)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(usedConditionPanel);

        newConditionTable.setAutoCreateRowSorter(true);
        newConditionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Attribute", "Operator", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        newConditionTable.setMinimumSize(new java.awt.Dimension(45, 200));
        newConditionTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                newConditionTableMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(newConditionTable);

        conditionLabel.setFont(new java.awt.Font("굴림", 1, 12)); // NOI18N
        conditionLabel.setText("New rule conditions");

        conAddButton.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        conAddButton.setText("Add");
        conAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conAddButtonActionPerformed(evt);
            }
        });

        conAttrComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Attribute" }));
        conAttrComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conAttrComboBoxActionPerformed(evt);
            }
        });

        conValField.setMinimumSize(new java.awt.Dimension(40, 21));

        conOperComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<", ">", "==", "!=", ">=", "<=" }));
        conOperComboBox.setMaximumSize(new java.awt.Dimension(200, 32767));
        conOperComboBox.setMinimumSize(new java.awt.Dimension(60, 21));
        conOperComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conOperComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout newConditionPanelLayout = new javax.swing.GroupLayout(newConditionPanel);
        newConditionPanel.setLayout(newConditionPanelLayout);
        newConditionPanelLayout.setHorizontalGroup(
            newConditionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newConditionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(newConditionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE)
                    .addGroup(newConditionPanelLayout.createSequentialGroup()
                        .addComponent(conditionLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(newConditionPanelLayout.createSequentialGroup()
                        .addComponent(conAttrComboBox, 0, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(conOperComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(conValField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(conAddButton)))
                .addContainerGap())
        );
        newConditionPanelLayout.setVerticalGroup(
            newConditionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newConditionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(conditionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(newConditionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(conAttrComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(conOperComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(conValField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(conAddButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(newConditionPanel);

        newConditionLabel.setFont(new java.awt.Font("굴림", 1, 12)); // NOI18N
        newConditionLabel.setText("Select conditions");
        newConditionLabel.setToolTipText("Select conditions for the new rule.");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane1)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(newConditionLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(newConditionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 557, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout conditionSelectionPanelLayout = new javax.swing.GroupLayout(conditionSelectionPanel);
        conditionSelectionPanel.setLayout(conditionSelectionPanelLayout);
        conditionSelectionPanelLayout.setHorizontalGroup(
            conditionSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(conditionSelectionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(conditionSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(conditionSelectionPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(validationButton))
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        conditionSelectionPanelLayout.setVerticalGroup(
            conditionSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(conditionSelectionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(validationButton)
                .addContainerGap())
        );

        wizardTabbedPanel.addTab("Select conditions", conditionSelectionPanel);

        validatingCaseListPanel.setEnabled(false);

        valCaseListLabel.setFont(new java.awt.Font("굴림", 1, 12)); // NOI18N
        valCaseListLabel.setText("Validate cornerstone cases");
        valCaseListLabel.setToolTipText("Validate cornerstone cases that may be affected by new rule.");

        jScrollPane3.setAutoscrolls(true);

        valCaseListTable.setAutoCreateRowSorter(true);
        valCaseListTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Cornerstone Case ID", "Attr 1", "Attr 2"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        valCaseListTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        valCaseListTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                valCaseListTableMouseReleased(evt);
            }
        });
        valCaseListTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                valCaseListTableKeyReleased(evt);
            }
        });
        jScrollPane3.setViewportView(valCaseListTable);

        conclusionLabel.setFont(new java.awt.Font("굴림", 1, 12)); // NOI18N
        conclusionLabel.setText("New conclusion");

        conclusionLabel1.setFont(new java.awt.Font("굴림", 1, 12)); // NOI18N
        conclusionLabel1.setText("Other conclusions");

        jScrollPane7.setViewportView(otherConclusionList);

        selectedNewConclusionTextarea2.setEditable(false);
        selectedNewConclusionTextarea2.setColumns(20);
        selectedNewConclusionTextarea2.setFont(new java.awt.Font("굴림", 0, 12)); // NOI18N
        selectedNewConclusionTextarea2.setForeground(new java.awt.Color(255, 51, 0));
        selectedNewConclusionTextarea2.setLineWrap(true);
        selectedNewConclusionTextarea2.setRows(2);
        selectedNewConclusionTextarea2.setText("Please select conclusion");
        selectedNewConclusionTextarea2.setWrapStyleWord(true);
        selectedNewConclusionTextarea2.setEnabled(false);
        jScrollPane13.setViewportView(selectedNewConclusionTextarea2);

        validationAcceptButton.setText("Accept");
        validationAcceptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validationAcceptButtonActionPerformed(evt);
            }
        });

        validationAcceptButton1.setText("Except");
        validationAcceptButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validationAcceptButton1ActionPerformed(evt);
            }
        });

        validationAcceptAllButton.setText("Accept all");
        validationAcceptAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validationAcceptAllButtonActionPerformed(evt);
            }
        });

        valSearchAttrNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                valSearchAttrNameTextFieldKeyReleased(evt);
            }
        });

        valCaseListLabel1.setText("Search attribute name");
        valCaseListLabel1.setToolTipText("Validate cornerstone cases that may be affected by new rule.");

        javax.swing.GroupLayout validatingCaseListPanelLayout = new javax.swing.GroupLayout(validatingCaseListPanel);
        validatingCaseListPanel.setLayout(validatingCaseListPanelLayout);
        validatingCaseListPanelLayout.setHorizontalGroup(
            validatingCaseListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(validatingCaseListPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(validatingCaseListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane7)
                    .addComponent(jScrollPane13)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, validatingCaseListPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(validationAcceptButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(validationAcceptButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(validationAcceptAllButton))
                    .addGroup(validatingCaseListPanelLayout.createSequentialGroup()
                        .addGroup(validatingCaseListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(valCaseListLabel)
                            .addComponent(conclusionLabel)
                            .addComponent(conclusionLabel1)
                            .addGroup(validatingCaseListPanelLayout.createSequentialGroup()
                                .addComponent(valCaseListLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(valSearchAttrNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 554, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        validatingCaseListPanelLayout.setVerticalGroup(
            validatingCaseListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(validatingCaseListPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(valCaseListLabel)
                .addGap(18, 18, 18)
                .addGroup(validatingCaseListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(valSearchAttrNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(valCaseListLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(validatingCaseListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(validationAcceptButton)
                    .addComponent(validationAcceptButton1)
                    .addComponent(validationAcceptAllButton))
                .addGap(9, 9, 9)
                .addComponent(conclusionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(conclusionLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout validateCornerstoneCaseTopPanelLayout = new javax.swing.GroupLayout(validateCornerstoneCaseTopPanel);
        validateCornerstoneCaseTopPanel.setLayout(validateCornerstoneCaseTopPanelLayout);
        validateCornerstoneCaseTopPanelLayout.setHorizontalGroup(
            validateCornerstoneCaseTopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(validatingCaseListPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        validateCornerstoneCaseTopPanelLayout.setVerticalGroup(
            validateCornerstoneCaseTopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(validateCornerstoneCaseTopPanelLayout.createSequentialGroup()
                .addComponent(validatingCaseListPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout validateCornerstoneCasesPanelLayout = new javax.swing.GroupLayout(validateCornerstoneCasesPanel);
        validateCornerstoneCasesPanel.setLayout(validateCornerstoneCasesPanelLayout);
        validateCornerstoneCasesPanelLayout.setHorizontalGroup(
            validateCornerstoneCasesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(validateCornerstoneCasesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(validateCornerstoneCaseTopPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        validateCornerstoneCasesPanelLayout.setVerticalGroup(
            validateCornerstoneCasesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(validateCornerstoneCasesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(validateCornerstoneCaseTopPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        wizardTabbedPanel.addTab("Validate cornerstone cases", validateCornerstoneCasesPanel);

        checkAgainButton.setText("Check again");
        checkAgainButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkAgainButtonActionPerformed(evt);
            }
        });

        checkAgainButton1.setText("Back");
        checkAgainButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkAgainButton1ActionPerformed(evt);
            }
        });

        refineRuleLabel.setFont(new java.awt.Font("굴림", 1, 12)); // NOI18N
        refineRuleLabel.setText("Refine rule conditions");
        refineRuleLabel.setToolTipText("Refine new rule by adding more conditions.");

        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        refineCaseHideMissingCheckbox.setText("Hide missing");
        refineCaseHideMissingCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refineCaseHideMissingCheckboxActionPerformed(evt);
            }
        });

        currentCaseLabel5.setText("Search attribute name");

        refineAttrNameFilterTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                refineAttrNameFilterTextFieldKeyReleased(evt);
            }
        });

        reAddingCurrentCaseTable.setAutoCreateRowSorter(true);
        reAddingCurrentCaseTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Attribute", "Type", "Value"
            }
        ));
        reAddingCurrentCaseTable.setMinimumSize(new java.awt.Dimension(200, 200));
        reAddingCurrentCaseTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        reAddingCurrentCaseTable.getTableHeader().setReorderingAllowed(false);
        reAddingCurrentCaseTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                reAddingCurrentCaseTableMouseReleased(evt);
            }
        });
        reAddingCurrentCaseTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                reAddingCurrentCaseTableKeyReleased(evt);
            }
        });
        jScrollPane11.setViewportView(reAddingCurrentCaseTable);

        currentCaseLabel4.setFont(new java.awt.Font("굴림", 1, 12)); // NOI18N
        currentCaseLabel4.setText("Current case");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(currentCaseLabel4)
                        .addGap(0, 232, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(currentCaseLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane2.setLeftComponent(jPanel7);

        valCaseTable.setAutoCreateRowSorter(true);
        valCaseTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Attribute", "Type", "Value"
            }
        ));
        valCaseTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                valCaseTableMouseReleased(evt);
            }
        });
        valCaseTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                valCaseTableKeyReleased(evt);
            }
        });
        jScrollPane5.setViewportView(valCaseTable);

        valCaseLabel.setFont(new java.awt.Font("굴림", 1, 12)); // NOI18N
        valCaseLabel.setText("Validating (affected) case");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(valCaseLabel)
                        .addGap(0, 138, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(valCaseLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane2.setRightComponent(jPanel3);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSplitPane2)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(currentCaseLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(refineAttrNameFilterTextField)
                        .addGap(18, 18, 18)
                        .addComponent(refineCaseHideMissingCheckbox)))
                .addGap(8, 8, 8))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(refineCaseHideMissingCheckbox)
                    .addComponent(currentCaseLabel5)
                    .addComponent(refineAttrNameFilterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSplitPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jSplitPane3.setLeftComponent(jPanel8);

        newConditionPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        newReAddingConditionTable.setAutoCreateRowSorter(true);
        newReAddingConditionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Attribute", "Operator", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        newReAddingConditionTable.setMinimumSize(new java.awt.Dimension(45, 200));
        newReAddingConditionTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                newReAddingConditionTableMouseReleased(evt);
            }
        });
        jScrollPane6.setViewportView(newReAddingConditionTable);

        conditionLabel1.setFont(new java.awt.Font("굴림", 1, 12)); // NOI18N
        conditionLabel1.setText("New rule conditions");

        reAddingConAddButton.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        reAddingConAddButton.setText("Add");
        reAddingConAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reAddingConAddButtonActionPerformed(evt);
            }
        });

        reAddingConAttrComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Attribute" }));
        reAddingConAttrComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reAddingConAttrComboBoxActionPerformed(evt);
            }
        });

        reAddingConOperComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<", ">", "==", "!=", ">=", "<=" }));
        reAddingConOperComboBox1.setMaximumSize(new java.awt.Dimension(200, 32767));
        reAddingConOperComboBox1.setMinimumSize(new java.awt.Dimension(60, 21));
        reAddingConOperComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reAddingConOperComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout newConditionPanel1Layout = new javax.swing.GroupLayout(newConditionPanel1);
        newConditionPanel1.setLayout(newConditionPanel1Layout);
        newConditionPanel1Layout.setHorizontalGroup(
            newConditionPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newConditionPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(newConditionPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE)
                    .addGroup(newConditionPanel1Layout.createSequentialGroup()
                        .addComponent(conditionLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(newConditionPanel1Layout.createSequentialGroup()
                        .addComponent(reAddingConAttrComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reAddingConOperComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reAddingConValField1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reAddingConAddButton)))
                .addContainerGap())
        );
        newConditionPanel1Layout.setVerticalGroup(
            newConditionPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newConditionPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(conditionLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(newConditionPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reAddingConAttrComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reAddingConOperComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reAddingConValField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reAddingConAddButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jSplitPane3.setRightComponent(newConditionPanel1);

        javax.swing.GroupLayout validateRulePanelLayout = new javax.swing.GroupLayout(validateRulePanel);
        validateRulePanel.setLayout(validateRulePanelLayout);
        validateRulePanelLayout.setHorizontalGroup(
            validateRulePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(validateRulePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(validateRulePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, validateRulePanelLayout.createSequentialGroup()
                        .addComponent(checkAgainButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(checkAgainButton))
                    .addGroup(validateRulePanelLayout.createSequentialGroup()
                        .addComponent(refineRuleLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jSplitPane3))
                .addContainerGap())
        );
        validateRulePanelLayout.setVerticalGroup(
            validateRulePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(validateRulePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(refineRuleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(validateRulePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkAgainButton)
                    .addComponent(checkAgainButton1))
                .addContainerGap())
        );

        wizardTabbedPanel.addTab("Refine rule conditions", validateRulePanel);

        javax.swing.GroupLayout wizardPanelLayout = new javax.swing.GroupLayout(wizardPanel);
        wizardPanel.setLayout(wizardPanelLayout);
        wizardPanelLayout.setHorizontalGroup(
            wizardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, wizardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wizardTabbedPanel)
                .addContainerGap())
        );
        wizardPanelLayout.setVerticalGroup(
            wizardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, wizardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wizardTabbedPanel)
                .addContainerGap())
        );

        mainSplitPanel.setRightComponent(wizardPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainSplitPanel))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainSplitPanel))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void currentCaseTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_currentCaseTableMouseReleased
        int selectedRow = currentCaseTable.getSelectedRow();
        if(selectedRow>-1){
            String attrName = (String) currentCaseTable.getValueAt(selectedRow, 0);
            updateConditionFields(currentCase, attrName);
        }
    }//GEN-LAST:event_currentCaseTableMouseReleased

    private void currentCaseTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_currentCaseTableKeyReleased
        int selectedRow = currentCaseTable.getSelectedRow();
        if(selectedRow>-1){
            String attrName = (String) currentCaseTable.getValueAt(selectedRow, 0);
            updateConditionFields(currentCase, attrName);
        }
    }//GEN-LAST:event_currentCaseTableKeyReleased

    private void conAttrComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conAttrComboBoxActionPerformed
        String attrName = conAttrComboBox.getSelectedItem().toString();
        updateConditionFields(currentCase, attrName);

    }//GEN-LAST:event_conAttrComboBoxActionPerformed

    private void conAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conAddButtonActionPerformed
        if(isNewConclusionDefined){
            addCondition();
        } else {
            showMessageDialog(null, "Please select the conclusion first.");
        }
    }//GEN-LAST:event_conAddButtonActionPerformed

    private void conOperComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conOperComboBoxActionPerformed
        //if MISSING is selected,
        if(conOperComboBox.getSelectedItem()=="MISSING" || conOperComboBox.getSelectedItem()=="NOT MISSING"){
            conValField.setText("");
            conValField.setEditable(false);
        } else {
            conValField.setEditable(true);
        }
    }//GEN-LAST:event_conOperComboBoxActionPerformed

    private void newConditionTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_newConditionTableMouseReleased
        if(newConditionTable.getSelectedRowCount()>0){
            if (SwingUtilities.isRightMouseButton(evt) && evt.getClickCount() == 1) {
                if(evt.isPopupTrigger()){
                    conditionControlPopup.show(evt.getComponent(),evt.getX(),evt.getY());
                }

            }
        }
    }//GEN-LAST:event_newConditionTableMouseReleased

    private void validationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validationButtonActionPerformed
        validateRule();
    }//GEN-LAST:event_validationButtonActionPerformed

    private void validationAcceptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validationAcceptButtonActionPerformed
        acceptSelectedValidatingCase();
    }//GEN-LAST:event_validationAcceptButtonActionPerformed

    private void deleteSelectedItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSelectedItemActionPerformed
        deleteCondition();
    }//GEN-LAST:event_deleteSelectedItemActionPerformed

    private void currentCaseHideMissingCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_currentCaseHideMissingCheckboxActionPerformed
        String filterText = attrNameFilterTextField.getText();
        
        if(currentCaseHideMissingCheckbox.isSelected()){
            //filter missing value
            updateCurrentCaseTable(true,currentCase, filterText);
        } else {
            //unfilter missing value (show all values)
            updateCurrentCaseTable(false,currentCase, filterText);
        }
    }//GEN-LAST:event_currentCaseHideMissingCheckboxActionPerformed

    private void selectConclusionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectConclusionButtonActionPerformed
        isNewConclusionDefined = true;
        selectConclusion();
    }//GEN-LAST:event_selectConclusionButtonActionPerformed

    private void addConclusionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addConclusionButtonActionPerformed
        isNewConclusionDefined = true;
        addConclusion();
        addNewConclusionDialog.dispose();
    }//GEN-LAST:event_addConclusionButtonActionPerformed

    private void valCaseListTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_valCaseListTableMouseReleased
        if(valCaseListTable.getSelectedRowCount()>0){
            if (SwingUtilities.isRightMouseButton(evt) && evt.getClickCount() == 1) {
                if(evt.isPopupTrigger()){
                    validateCasePopupMenu.show(evt.getComponent(),evt.getX(),evt.getY());
                }
            } else {
                //update other conclusionList
                int selectedRow = valCaseListTable.getSelectedRow();
                int selectedValCaseId = (int) valCaseListTable.getValueAt(selectedRow, 0);
                CornerstoneCase aCornerstoneCase = Main.workbench.getLearner().getValidatingCaseSet().getCornerstoneCaseById(selectedValCaseId);
                Main.workbench.setValidatingCase(aCornerstoneCase);
                Main.workbench.inferenceForValidation();
                RuleSet inferenceResult = (RuleSet) Main.workbench.getInferenceResult();
                ConclusionSet conclusionSet = inferenceResult.getConclusionSet();
                updateOtherConclusionList(conclusionSet);
            }
        }
    }//GEN-LAST:event_valCaseListTableMouseReleased

    private void validationAcceptAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validationAcceptAllButtonActionPerformed
            addRule();        
    }//GEN-LAST:event_validationAcceptAllButtonActionPerformed

    private void newReAddingConditionTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_newReAddingConditionTableMouseReleased
        if(newReAddingConditionTable.getSelectedRowCount()>0){
            if (SwingUtilities.isRightMouseButton(evt) && evt.getClickCount() == 1) {
                if(evt.isPopupTrigger()){
                    reAddingConditionControlPopup.show(evt.getComponent(),evt.getX(),evt.getY());
                }
            }
        }
    }//GEN-LAST:event_newReAddingConditionTableMouseReleased

    private void reAddingConAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reAddingConAddButtonActionPerformed
        addConditionForReAdding();
    }//GEN-LAST:event_reAddingConAddButtonActionPerformed

    private void reAddingConAttrComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reAddingConAttrComboBoxActionPerformed
        String attrName = reAddingConAttrComboBox.getSelectedItem().toString();
        if(validatingCase != null){
            updateReAddingConditionFields("current", currentCase, attrName);
        }
    }//GEN-LAST:event_reAddingConAttrComboBoxActionPerformed

    private void reAddingConOperComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reAddingConOperComboBox1ActionPerformed
        //if MISSING is selected,
        if(reAddingConOperComboBox1.getSelectedItem()=="MISSING" || reAddingConOperComboBox1.getSelectedItem()=="NOT MISSING"){
            reAddingConValField1.setText("");
            reAddingConValField1.setEditable(false);
        } else {
            reAddingConValField1.setEditable(true);
        }
    }//GEN-LAST:event_reAddingConOperComboBox1ActionPerformed

    private void checkAgainButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkAgainButtonActionPerformed
        if(Main.workbench.getNewRule().isSatisfied(validatingCase)){
            showMessageDialog(null, "Condition still satisfies with the validating case.");
        } else {
            validateRule();
        }

    }//GEN-LAST:event_checkAgainButtonActionPerformed

    private void valCaseTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_valCaseTableKeyReleased
        int selectedRow = valCaseTable.getSelectedRow();
        if(selectedRow>-1){
            String attrName = (String) valCaseTable.getValueAt(selectedRow, 0);
            updateReAddingConditionFields("validating", validatingCase, attrName);
        }
    }//GEN-LAST:event_valCaseTableKeyReleased

    private void valCaseTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_valCaseTableMouseReleased
        int selectedRow = valCaseTable.getSelectedRow();
        if(selectedRow>-1){
            String attrName = (String) valCaseTable.getValueAt(selectedRow, 0);
            updateReAddingConditionFields("validating", validatingCase, attrName);
        }
    }//GEN-LAST:event_valCaseTableMouseReleased

    private void addingNewConditionOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addingNewConditionOptionActionPerformed
        if(valCaseListTable.getSelectedRowCount()>1){
            showMessageDialog(null, "Please select any case.");
        } else {
            int selectedCornerstoneCaseId = (int) valCaseListTable.getValueAt(valCaseListTable.getSelectedRow(), 0);
            validatingCase = Main.workbench.getLearner().getValidatingCaseSet().getCornerstoneCaseById(selectedCornerstoneCaseId);
            
            boolean valCaseFilteredCheck =  refineCaseHideMissingCheckbox.isSelected();
            String textFilter = refineAttrNameFilterTextField.getText();
            updateCurrentCaseTableInValidation(valCaseFilteredCheck, currentCase, textFilter);
            updateValCaseTable(valCaseFilteredCheck, validatingCase, textFilter);
            validatingCaseConclusionSet = validatingCase.getConclusionSet();

            //set active tab
            setEnableWizardTab(3);
        }
    }//GEN-LAST:event_addingNewConditionOptionActionPerformed

    private void reAddingDeleteSelectedItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reAddingDeleteSelectedItemActionPerformed
        deleteConditionFromReadding();
    }//GEN-LAST:event_reAddingDeleteSelectedItemActionPerformed

    private void checkAgainButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkAgainButton1ActionPerformed
        setEnableWizardTab(2);
    }//GEN-LAST:event_checkAgainButton1ActionPerformed

    private void attrNameFilterTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_attrNameFilterTextFieldKeyReleased
        String filterText = attrNameFilterTextField.getText();
        if(currentCaseHideMissingCheckbox.isSelected()){
            //filter missing value
            updateCurrentCaseTable(true,currentCase, filterText);
        } else {
            //unfilter missing value (show all values)
            updateCurrentCaseTable(false,currentCase, filterText);
        }
    }//GEN-LAST:event_attrNameFilterTextFieldKeyReleased

    private void reAddingCurrentCaseTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reAddingCurrentCaseTableMouseReleased
        int selectedRow = reAddingCurrentCaseTable.getSelectedRow();
        if(selectedRow>-1){
            String attrName = (String) reAddingCurrentCaseTable.getValueAt(selectedRow, 0);
            updateReAddingConditionFields("current", currentCase, attrName);
        }
    }//GEN-LAST:event_reAddingCurrentCaseTableMouseReleased

    private void reAddingCurrentCaseTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_reAddingCurrentCaseTableKeyReleased
        int selectedRow = reAddingCurrentCaseTable.getSelectedRow();
        if(selectedRow>-1){
            String attrName = (String) reAddingCurrentCaseTable.getValueAt(selectedRow, 0);
            updateReAddingConditionFields("current", currentCase, attrName);
        }
    }//GEN-LAST:event_reAddingCurrentCaseTableKeyReleased

    private void refineCaseHideMissingCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refineCaseHideMissingCheckboxActionPerformed
        String filterText = refineAttrNameFilterTextField.getText();
        if(refineCaseHideMissingCheckbox.isSelected()){
            //filter missing value
            updateCurrentCaseTableInValidation(true,currentCase, filterText);
            updateValCaseTable(true, validatingCase, filterText);
        } else {
            //unfilter missing value (show all values)
            updateCurrentCaseTableInValidation(false,currentCase, filterText);
            updateValCaseTable(false, validatingCase, filterText);
        }
    }//GEN-LAST:event_refineCaseHideMissingCheckboxActionPerformed

    private void refineAttrNameFilterTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_refineAttrNameFilterTextFieldKeyReleased
        String filterText = refineAttrNameFilterTextField.getText();
        if(refineCaseHideMissingCheckbox.isSelected()){
            //filter missing value
            updateCurrentCaseTableInValidation(true,currentCase, filterText);
            updateValCaseTable(true, validatingCase, filterText);
        } else {
            //unfilter missing value (show all values)
            updateCurrentCaseTableInValidation(false,currentCase, filterText);
            updateValCaseTable(false, validatingCase, filterText);
        }
    }//GEN-LAST:event_refineAttrNameFilterTextFieldKeyReleased

    private void addNewConclusionDialogWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_addNewConclusionDialogWindowClosed
        addNewConclusionDialog.setModal(false);
        this.setModal(true);
    }//GEN-LAST:event_addNewConclusionDialogWindowClosed

    private void validationAcceptButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validationAcceptButton1ActionPerformed
        if(valCaseListTable.getSelectedRowCount()>1){
            showMessageDialog(null, "Please select any case.");
        } else {
            int selectedCornerstoneCaseId = (int) valCaseListTable.getValueAt(valCaseListTable.getSelectedRow(), 0);
            validatingCase = Main.workbench.getLearner().getValidatingCaseSet().getCornerstoneCaseById(selectedCornerstoneCaseId);
            
            boolean valCaseFilteredCheck =  refineCaseHideMissingCheckbox.isSelected();
            String textFilter = refineAttrNameFilterTextField.getText();
            updateCurrentCaseTableInValidation(valCaseFilteredCheck, currentCase, textFilter);
            updateValCaseTable(valCaseFilteredCheck, validatingCase, textFilter);
            validatingCaseConclusionSet = validatingCase.getConclusionSet();

            //set active tab
            setEnableWizardTab(3);
        }
    }//GEN-LAST:event_validationAcceptButton1ActionPerformed

    private void addNewConclusionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewConclusionButtonActionPerformed
        this.setModal(false);
        addNewConclusionDialog.setModal(true);        
        addNewConclusionDialog.setVisible(true);
    }//GEN-LAST:event_addNewConclusionButtonActionPerformed

    private void conclusionNameFilterTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_conclusionNameFilterTextFieldKeyReleased
        String textFilter = conclusionNameFilterTextField.getText();
        updateNewConclusionTable(tempConclusionSet, textFilter);
    }//GEN-LAST:event_conclusionNameFilterTextFieldKeyReleased

    private void conclusionValueFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conclusionValueFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_conclusionValueFieldActionPerformed

    private void addNewConclusionDialogWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_addNewConclusionDialogWindowOpened
        conclusionValueField.requestFocusInWindow();
    }//GEN-LAST:event_addNewConclusionDialogWindowOpened

    private void valSearchAttrNameTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_valSearchAttrNameTextFieldKeyReleased
        updateValCaseSetTable(Main.workbench.getLearner().getValidatingCaseSet());
    }//GEN-LAST:event_valSearchAttrNameTextFieldKeyReleased

    private void valCaseListTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_valCaseListTableKeyReleased
        //update other conclusionList
        int selectedRow = valCaseListTable.getSelectedRow();
        int selectedValCaseId = (int) valCaseListTable.getValueAt(selectedRow, 0);
        CornerstoneCase aCornerstoneCase = Main.workbench.getLearner().getValidatingCaseSet().getCornerstoneCaseById(selectedValCaseId);
        Main.workbench.setValidatingCase(aCornerstoneCase);
        Main.workbench.inferenceForValidation();
        RuleSet inferenceResult = (RuleSet) Main.workbench.getInferenceResult();
        ConclusionSet conclusionSet = inferenceResult.getConclusionSet();
        updateOtherConclusionList(conclusionSet);
    }//GEN-LAST:event_valCaseListTableKeyReleased
    
    /**
     * * Knowledge Acquisition with pre-defined wrongConclusion (if not applicable, null) and conditionSet (if not applicable, null).
     * 
     * @param aMode
     * @param aCase
     * @param wrongConclusion 
     * @param preDefinedConditionSet 
     */
    public static void  execute(int aMode, Case aCase, Conclusion wrongConclusion, ConditionSet preDefinedConditionSet) {
        AddRuleDialog.wrongConclusion = wrongConclusion;
        AddRuleDialog.preDefinedConditionSet = preDefinedConditionSet;
        
        try {
            AddRuleDialog.currentCase = CaseLoader.caseLoad(aCase.getCaseId(), null);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(AddRuleDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        AddRuleDialog.kaMode = aMode;
        
        /* Set the Windows look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AddRuleDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddRuleDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddRuleDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddRuleDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AddRuleDialog dialog = new AddRuleDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {

                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addConclusionButton;
    private javax.swing.JButton addNewConclusionButton;
    private javax.swing.JDialog addNewConclusionDialog;
    private javax.swing.JMenuItem addingNewConditionOption;
    private javax.swing.JTextField attrNameFilterTextField;
    private javax.swing.JButton checkAgainButton;
    private javax.swing.JButton checkAgainButton1;
    private javax.swing.JButton conAddButton;
    private javax.swing.JComboBox conAttrComboBox;
    private javax.swing.JComboBox conOperComboBox;
    private javax.swing.JTextField conValField;
    private javax.swing.JLabel conclusionLabel;
    private javax.swing.JLabel conclusionLabel1;
    private javax.swing.JLabel conclusionLabel2;
    private javax.swing.JTextField conclusionNameFilterTextField;
    private javax.swing.JPanel conclusionSelectionPanel;
    private javax.swing.JComboBox conclusionTypeComboBox;
    private javax.swing.JTextField conclusionValueField;
    private javax.swing.JPopupMenu conditionControlPopup;
    private javax.swing.JLabel conditionLabel;
    private javax.swing.JLabel conditionLabel1;
    private javax.swing.JPanel conditionSelectionPanel;
    private javax.swing.JCheckBox currentCaseHideMissingCheckbox;
    private javax.swing.JLabel currentCaseLabel;
    private javax.swing.JLabel currentCaseLabel1;
    private javax.swing.JLabel currentCaseLabel3;
    private javax.swing.JLabel currentCaseLabel4;
    private javax.swing.JLabel currentCaseLabel5;
    private javax.swing.JTable currentCaseTable;
    private javax.swing.JMenuItem deleteSelectedItem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JSplitPane jSplitPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable leftNewConditionTable;
    private javax.swing.JSplitPane mainSplitPanel;
    private javax.swing.JLabel newConclusionLabel;
    private javax.swing.JLabel newConclusionLabel1;
    private javax.swing.JPanel newConclusionPanel;
    private javax.swing.JTable newConclusionTable;
    private javax.swing.JLabel newConditionLabel;
    private javax.swing.JPanel newConditionPanel;
    private javax.swing.JPanel newConditionPanel1;
    private javax.swing.JTable newConditionTable;
    private javax.swing.JTable newReAddingConditionTable;
    private javax.swing.JList otherConclusionList;
    private javax.swing.JButton reAddingConAddButton;
    private javax.swing.JComboBox reAddingConAttrComboBox;
    private javax.swing.JComboBox reAddingConOperComboBox1;
    private javax.swing.JTextField reAddingConValField1;
    private javax.swing.JPopupMenu reAddingConditionControlPopup;
    private javax.swing.JTable reAddingCurrentCaseTable;
    private javax.swing.JMenuItem reAddingDeleteSelectedItem;
    private javax.swing.JTextField refineAttrNameFilterTextField;
    private javax.swing.JCheckBox refineCaseHideMissingCheckbox;
    private javax.swing.JLabel refineRuleLabel;
    private javax.swing.JButton selectConclusionButton;
    private javax.swing.JTextArea selectedNewConclusionTextarea;
    private javax.swing.JTextArea selectedNewConclusionTextarea2;
    private javax.swing.JTable usedConditionListTable;
    private javax.swing.JPanel usedConditionPanel;
    private javax.swing.JLabel valCaseLabel;
    private javax.swing.JLabel valCaseListLabel;
    private javax.swing.JLabel valCaseListLabel1;
    private javax.swing.JLabel valCaseListLabel3;
    private javax.swing.JTable valCaseListTable;
    private javax.swing.JTable valCaseTable;
    private javax.swing.JTextField valSearchAttrNameTextField;
    private javax.swing.JPopupMenu validateCasePopupMenu;
    private javax.swing.JPanel validateCornerstoneCaseTopPanel;
    private javax.swing.JPanel validateCornerstoneCasesPanel;
    private javax.swing.JPanel validateRulePanel;
    private javax.swing.JPanel validatingCaseListPanel;
    private javax.swing.JButton validationAcceptAllButton;
    private javax.swing.JButton validationAcceptButton;
    private javax.swing.JButton validationAcceptButton1;
    private javax.swing.JButton validationButton;
    private javax.swing.JDialog validationDialog;
    private javax.swing.JPanel wizardPanel;
    private javax.swing.JTabbedPane wizardTabbedPanel;
    private javax.swing.JLabel wrongConclusionLabel;
    private javax.swing.JPanel wrongConclusionPanel;
    private javax.swing.JTextArea wrongConclusionTextarea;
    // End of variables declaration//GEN-END:variables
}
