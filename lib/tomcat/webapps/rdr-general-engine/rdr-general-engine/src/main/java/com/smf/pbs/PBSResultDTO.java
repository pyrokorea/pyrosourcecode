
package com.smf.pbs;

import java.util.Objects;
import java.util.Date;
import rdr.utils.StringUtil;
import rdr.utils.RDRConstants;
import rdr.utils.DateUtil;

/**
 * @brief 검사결과정보 관리 DTO Class
 * @author Kim, Woo Cheol
 * @version 1.00
 * @date 2020.03.31
 * @section MODIFYINFO 수정정보 
 *  - 수정일/수정자 : 수정내역
 *  - 2020.03.31/Kim, Woo Cheol : 최초작성
 */
public class PBSResultDTO
{
    /** LMB코드 */
    private String  lmbCode;
    /** 접수일자 */
    private Integer receiptDate;
    /** 접수번호 */
    private Integer receiptNo;
    /** 검사코드 */
    private String  testCode;
    /** 부속코드 */
    private String  testSubCode;
    /** 검체유형코드 */
    private String  specimenCode;
    /** 검사명 */
    private String  testName;
    /** 검체명 */
    private String  specimenName;
    /** 작업명 */
    private String  workName;
    /** 검사학부코드 */
    private String  departmentCode;
    /** 검사학부명 */
    private String  departmentName;
    /** 검사담당자 */
    private String  testStaff;
    /** 장비숫자결과 */
    private Double  equipmentNumericResult;
    /** 숫자결과 */
    private Double  numericDefaultResult;
    /** 최종 숫자결과 */
    private Double  numericFinalResult;
    /** 문자결과 */
    private String  charDefaultResult;
    /** 최종 문자결과 */
    private String  charFinalResult;
    /** 결과 기준정보에 의해 자동 변경 유무 */
    private String  resultStatusFlag;
    /** 1차 판독결과(판독 결과 코드)숫자 */
    private Integer decisionNumric1;
    /** 1차 판독결과(판독 결과 코드)문자 */
    private String  decisionChar1;
    /** 2차 판독결과(판독 결과 코드)숫자 */
    private Integer decisionNumric2;
    /** 2차 판독결과(판독 결과 코드)문자 */
    private String  decisionChar2;
    /** 검사일자 */
    private Integer testDate;
    /** 작업번호 */
    private Integer workNo;
    /** 소견생성유무 */
    private String  commentFlag;
    /** 장비 Flag */
    private String  equipmentFlag;
    /** 진행상태 */
    private String  workStatus;
    /** 1차저장 User 정보 */
    private String  firstSaveUserInfo;
    /** 1차변경 Flag */
    private String  firstEditFlag;
    /** 2차Confirm User 정보 */
    private String  secondConfirmUserInfo;
    /** 2차변경 Flag */
    private String  secondEditFlag;
    /** 검사장비 */
    private String  equipmentCode;
    /** 생성일시 */
    private Date    creationDate;
    /** 수정일시 */
    private Date    updateDate;
    /** 사용자ID */
    private String  userId;

    /** default constructor */
    public PBSResultDTO()
    {
        this.lmbCode = "";
        this.receiptDate = null;
        this.receiptNo = null;
        this.testCode = "";
        this.testSubCode = "";
        this.specimenCode = "";
        this.testName = "";
        this.specimenName = "";
        this.workName = "";
        this.departmentCode = "";
        this.departmentName = "";
        this.testStaff = "";
        this.equipmentNumericResult = null;
        this.numericDefaultResult = null;
        this.numericFinalResult = null;
        this.charDefaultResult = "";
        this.charFinalResult = "";
        this.resultStatusFlag = "";
        this.decisionNumric1 = null;
        this.decisionChar1 = "";
        this.decisionNumric2 = null;
        this.decisionChar2 = "";
        this.testDate = null;
        this.workNo = null;
        this.commentFlag = "";
        this.equipmentFlag = "";
        this.workStatus = "";
        this.firstSaveUserInfo = "";
        this.firstEditFlag = "";
        this.secondConfirmUserInfo = "";
        this.secondEditFlag = "";
        this.equipmentCode = "";
        this.creationDate = null;
        this.updateDate = null;
        this.userId = "";
    }

    /** constructor */
    public PBSResultDTO(final String pLmbCode, final Integer pReceiptDate, final Integer pReceiptNo, final String pTestCode, final String pTestSubCode, final String pSpecimenCode, final String pTestName, final String pSpecimenName, final String pWorkName, final String pDepartmentCode, final String pDepartmentName, final String pTestStaff, final Double pEquipmentNumericResult, final Double pNumericDefaultResult, final Double pNumericFinalResult, final String pCharDefaultResult, final String pCharFinalResult, final String pResultStatusFlag, final Integer pDecisionNumric1, final String pDecisionChar1, final Integer pDecisionNumric2, final String pDecisionChar2, final Integer pTestDate, final Integer pWorkNo, final String pCommentFlag, final String pEquipmentFlag, final String pWorkStatus, final String pFirstSaveUserInfo, final String pFirstEditFlag, final String pSecondConfirmUserInfo, final String pSecondEditFlag, final String pEquipmentCode, final Date pCreationDate, final Date pUpdateDate, final String pUserId)
    {
        this.lmbCode = pLmbCode;
        this.receiptDate = pReceiptDate;
        this.receiptNo = pReceiptNo;
        this.testCode = pTestCode;
        this.testSubCode = pTestSubCode;
        this.specimenCode = pSpecimenCode;
        this.testName = pTestName;
        this.specimenName = pSpecimenName;
        this.workName = pWorkName;
        this.departmentCode = pDepartmentCode;
        this.departmentName = pDepartmentName;
        this.testStaff = pTestStaff;
        this.equipmentNumericResult = pEquipmentNumericResult;
        this.numericDefaultResult = pNumericDefaultResult;
        this.numericFinalResult = pNumericFinalResult;
        this.charDefaultResult = pCharDefaultResult;
        this.charFinalResult = pCharFinalResult;
        this.resultStatusFlag = pResultStatusFlag;
        this.decisionNumric1 = pDecisionNumric1;
        this.decisionChar1 = pDecisionChar1;
        this.decisionNumric2 = pDecisionNumric2;
        this.decisionChar2 = pDecisionChar2;
        this.testDate = pTestDate;
        this.workNo = pWorkNo;
        this.commentFlag = pCommentFlag;
        this.equipmentFlag = pEquipmentFlag;
        this.workStatus = pWorkStatus;
        this.firstSaveUserInfo = pFirstSaveUserInfo;
        this.firstEditFlag = pFirstEditFlag;
        this.secondConfirmUserInfo = pSecondConfirmUserInfo;
        this.secondEditFlag = pSecondEditFlag;
        this.equipmentCode = pEquipmentCode;
        this.creationDate = pCreationDate;
        this.updateDate = pUpdateDate;
        this.userId = pUserId;
    }

    /** get 함수 : LMB코드 */
    public String getLmbCode()
    {
        return lmbCode;
    }

    /** get 함수 : 접수일자 */
    public Integer getReceiptDate()
    {
        return receiptDate;
    }

    /** get 함수 : 접수번호 */
    public Integer getReceiptNo()
    {
        return receiptNo;
    }

    /** get 함수 : 검사코드 */
    public String getTestCode()
    {
        return testCode;
    }

    /** get 함수 : 부속코드 */
    public String getTestSubCode()
    {
        return testSubCode;
    }

    /** get 함수 : 검체유형코드 */
    public String getSpecimenCode()
    {
        return specimenCode;
    }

    /** get 함수 : 검사명 */
    public String getTestName()
    {
        return testName;
    }

    /** get 함수 : 검체명 */
    public String getSpecimenName()
    {
        return specimenName;
    }

    /** get 함수 : 작업명 */
    public String getWorkName()
    {
        return workName;
    }

    /** get 함수 : 검사학부코드 */
    public String getDepartmentCode()
    {
        return departmentCode;
    }

    /** get 함수 : 검사학부명 */
    public String getDepartmentName()
    {
        return departmentName;
    }

    /** get 함수 : 검사담당자 */
    public String getTestStaff()
    {
        return testStaff;
    }

    /** get 함수 : 장비숫자결과 */
    public Double getEquipmentNumericResult()
    {
        return equipmentNumericResult;
    }

    /** get 함수 : 숫자결과 */
    public Double getNumericDefaultResult()
    {
        return numericDefaultResult;
    }

    /** get 함수 : 최종 숫자결과 */
    public Double getNumericFinalResult()
    {
        return numericFinalResult;
    }

    /** get 함수 : 문자결과 */
    public String getCharDefaultResult()
    {
        return charDefaultResult;
    }

    /** get 함수 : 최종 문자결과 */
    public String getCharFinalResult()
    {
        return charFinalResult;
    }

    /** get 함수 : 결과 기준정보에 의해 자동 변경 유무 */
    public String getResultStatusFlag()
    {
        return resultStatusFlag;
    }

    /** get 함수 : 1차 판독결과(판독 결과 코드)숫자 */
    public Integer getDecisionNumric1()
    {
        return decisionNumric1;
    }

    /** get 함수 : 1차 판독결과(판독 결과 코드)문자 */
    public String getDecisionChar1()
    {
        return decisionChar1;
    }

    /** get 함수 : 2차 판독결과(판독 결과 코드)숫자 */
    public Integer getDecisionNumric2()
    {
        return decisionNumric2;
    }

    /** get 함수 : 2차 판독결과(판독 결과 코드)문자 */
    public String getDecisionChar2()
    {
        return decisionChar2;
    }

    /** get 함수 : 검사일자 */
    public Integer getTestDate()
    {
        return testDate;
    }

    /** get 함수 : 작업번호 */
    public Integer getWorkNo()
    {
        return workNo;
    }

    /** get 함수 : 소견생성유무 */
    public String getCommentFlag()
    {
        return commentFlag;
    }

    /** get 함수 : 장비 Flag */
    public String getEquipmentFlag()
    {
        return equipmentFlag;
    }

    /** get 함수 : 진행상태 */
    public String getWorkStatus()
    {
        return workStatus;
    }

    /** get 함수 : 1차저장 User 정보 */
    public String getFirstSaveUserInfo()
    {
        return firstSaveUserInfo;
    }

    /** get 함수 : 1차변경 Flag */
    public String getFirstEditFlag()
    {
        return firstEditFlag;
    }

    /** get 함수 : 2차Confirm User 정보 */
    public String getSecondConfirmUserInfo()
    {
        return secondConfirmUserInfo;
    }

    /** get 함수 : 2차변경 Flag */
    public String getSecondEditFlag()
    {
        return secondEditFlag;
    }

    /** get 함수 : 검사장비 */
    public String getEquipmentCode()
    {
        return equipmentCode;
    }

    /** get 함수 : 생성일시 */
    public Date getCreationDate()
    {
        return creationDate;
    }

    /** get 함수 : 수정일시 */
    public Date getUpdateDate()
    {
        return updateDate;
    }

    /** get 함수 : 사용자ID */
    public String getUserId()
    {
        return userId;
    }

    /** set 함수 : LMB코드 */
    public void setLmbCode(final String pLmbCode)
    {
        this.lmbCode = pLmbCode;
    }

    /** set 함수 : 접수일자 */
    public void setReceiptDate(final Integer pReceiptDate)
    {
        this.receiptDate = pReceiptDate;
    }

    /** set 함수 : 접수번호 */
    public void setReceiptNo(final Integer pReceiptNo)
    {
        this.receiptNo = pReceiptNo;
    }

    /** set 함수 : 검사코드 */
    public void setTestCode(final String pTestCode)
    {
        this.testCode = pTestCode;
    }

    /** set 함수 : 부속코드 */
    public void setTestSubCode(final String pTestSubCode)
    {
        this.testSubCode = pTestSubCode;
    }

    /** set 함수 : 검체유형코드 */
    public void setSpecimenCode(final String pSpecimenCode)
    {
        this.specimenCode = pSpecimenCode;
    }

    /** set 함수 : 검사명 */
    public void setTestName(final String pTestName)
    {
        this.testName = pTestName;
    }

    /** set 함수 : 검체명 */
    public void setSpecimenName(final String pSpecimenName)
    {
        this.specimenName = pSpecimenName;
    }

    /** set 함수 : 작업명 */
    public void setWorkName(final String pWorkName)
    {
        this.workName = pWorkName;
    }

    /** set 함수 : 검사학부코드 */
    public void setDepartmentCode(final String pDepartmentCode)
    {
        this.departmentCode = pDepartmentCode;
    }

    /** set 함수 : 검사학부명 */
    public void setDepartmentName(final String pDepartmentName)
    {
        this.departmentName = pDepartmentName;
    }

    /** set 함수 : 검사담당자 */
    public void setTestStaff(final String pTestStaff)
    {
        this.testStaff = pTestStaff;
    }

    /** set 함수 : 장비숫자결과 */
    public void setEquipmentNumericResult(final Double pEquipmentNumericResult)
    {
        this.equipmentNumericResult = pEquipmentNumericResult;
    }

    /** set 함수 : 숫자결과 */
    public void setNumericDefaultResult(final Double pNumericDefaultResult)
    {
        this.numericDefaultResult = pNumericDefaultResult;
    }

    /** set 함수 : 최종 숫자결과 */
    public void setNumericFinalResult(final Double pNumericFinalResult)
    {
        this.numericFinalResult = pNumericFinalResult;
    }

    /** set 함수 : 문자결과 */
    public void setCharDefaultResult(final String pCharDefaultResult)
    {
        this.charDefaultResult = pCharDefaultResult;
    }

    /** set 함수 : 최종 문자결과 */
    public void setCharFinalResult(final String pCharFinalResult)
    {
        this.charFinalResult = pCharFinalResult;
    }

    /** set 함수 : 결과 기준정보에 의해 자동 변경 유무 */
    public void setResultStatusFlag(final String pResultStatusFlag)
    {
        this.resultStatusFlag = pResultStatusFlag;
    }

    /** set 함수 : 1차 판독결과(판독 결과 코드)숫자 */
    public void setDecisionNumric1(final Integer pDecisionNumric1)
    {
        this.decisionNumric1 = pDecisionNumric1;
    }

    /** set 함수 : 1차 판독결과(판독 결과 코드)문자 */
    public void setDecisionChar1(final String pDecisionChar1)
    {
        this.decisionChar1 = pDecisionChar1;
    }

    /** set 함수 : 2차 판독결과(판독 결과 코드)숫자 */
    public void setDecisionNumric2(final Integer pDecisionNumric2)
    {
        this.decisionNumric2 = pDecisionNumric2;
    }

    /** set 함수 : 2차 판독결과(판독 결과 코드)문자 */
    public void setDecisionChar2(final String pDecisionChar2)
    {
        this.decisionChar2 = pDecisionChar2;
    }

    /** set 함수 : 검사일자 */
    public void setTestDate(final Integer pTestDate)
    {
        this.testDate = pTestDate;
    }

    /** set 함수 : 작업번호 */
    public void setWorkNo(final Integer pWorkNo)
    {
        this.workNo = pWorkNo;
    }

    /** set 함수 : 소견생성유무 */
    public void setCommentFlag(final String pCommentFlag)
    {
        this.commentFlag = pCommentFlag;
    }

    /** set 함수 : 장비 Flag */
    public void setEquipmentFlag(final String pEquipmentFlag)
    {
        this.equipmentFlag = pEquipmentFlag;
    }

    /** set 함수 : 진행상태 */
    public void setWorkStatus(final String pWorkStatus)
    {
        this.workStatus = pWorkStatus;
    }

    /** set 함수 : 1차저장 User 정보 */
    public void setFirstSaveUserInfo(final String pFirstSaveUserInfo)
    {
        this.firstSaveUserInfo = pFirstSaveUserInfo;
    }

    /** set 함수 : 1차변경 Flag */
    public void setFirstEditFlag(final String pFirstEditFlag)
    {
        this.firstEditFlag = pFirstEditFlag;
    }

    /** set 함수 : 2차Confirm User 정보 */
    public void setSecondConfirmUserInfo(final String pSecondConfirmUserInfo)
    {
        this.secondConfirmUserInfo = pSecondConfirmUserInfo;
    }

    /** set 함수 : 2차변경 Flag */
    public void setSecondEditFlag(final String pSecondEditFlag)
    {
        this.secondEditFlag = pSecondEditFlag;
    }

    /** set 함수 : 검사장비 */
    public void setEquipmentCode(final String pEquipmentCode)
    {
        this.equipmentCode = pEquipmentCode;
    }

    /** set 함수 : 생성일시 */
    public void setCreationDate(final Date pCreationDate)
    {
        this.creationDate = pCreationDate;
    }

    /** set 함수 : 수정일시 */
    public void setUpdateDate(final Date pUpdateDate)
    {
        this.updateDate = pUpdateDate;
    }

    /** set 함수 : 사용자ID */
    public void setUserId(final String pUserId)
    {
        this.userId = pUserId;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.lmbCode);
        hash = 31 * hash + Objects.hashCode(this.receiptDate);
        hash = 31 * hash + Objects.hashCode(this.receiptNo);
        hash = 31 * hash + Objects.hashCode(this.testCode);
        hash = 31 * hash + Objects.hashCode(this.testSubCode);
        hash = 31 * hash + Objects.hashCode(this.specimenCode);
        hash = 31 * hash + Objects.hashCode(this.testName);
        hash = 31 * hash + Objects.hashCode(this.specimenName);
        hash = 31 * hash + Objects.hashCode(this.workName);
        hash = 31 * hash + Objects.hashCode(this.departmentCode);
        hash = 31 * hash + Objects.hashCode(this.departmentName);
        hash = 31 * hash + Objects.hashCode(this.testStaff);
        hash = 31 * hash + Objects.hashCode(this.equipmentNumericResult);
        hash = 31 * hash + Objects.hashCode(this.numericDefaultResult);
        hash = 31 * hash + Objects.hashCode(this.numericFinalResult);
        hash = 31 * hash + Objects.hashCode(this.charDefaultResult);
        hash = 31 * hash + Objects.hashCode(this.charFinalResult);
        hash = 31 * hash + Objects.hashCode(this.resultStatusFlag);
        hash = 31 * hash + Objects.hashCode(this.decisionNumric1);
        hash = 31 * hash + Objects.hashCode(this.decisionChar1);
        hash = 31 * hash + Objects.hashCode(this.decisionNumric2);
        hash = 31 * hash + Objects.hashCode(this.decisionChar2);
        hash = 31 * hash + Objects.hashCode(this.testDate);
        hash = 31 * hash + Objects.hashCode(this.workNo);
        hash = 31 * hash + Objects.hashCode(this.commentFlag);
        hash = 31 * hash + Objects.hashCode(this.equipmentFlag);
        hash = 31 * hash + Objects.hashCode(this.workStatus);
        hash = 31 * hash + Objects.hashCode(this.firstSaveUserInfo);
        hash = 31 * hash + Objects.hashCode(this.firstEditFlag);
        hash = 31 * hash + Objects.hashCode(this.secondConfirmUserInfo);
        hash = 31 * hash + Objects.hashCode(this.secondEditFlag);
        hash = 31 * hash + Objects.hashCode(this.equipmentCode);
        hash = 31 * hash + Objects.hashCode(this.creationDate);
        hash = 31 * hash + Objects.hashCode(this.updateDate);
        hash = 31 * hash + Objects.hashCode(this.userId);
        return hash;
    }

    @Override
    public String toString()
    {
        return toString(",");
    }

    public String toString(String seperator)
    {
        return (this.lmbCode == null ? "" : this.lmbCode)
             + seperator + (this.receiptDate == null ? "" : this.receiptDate)
             + seperator + (this.receiptNo == null ? "" : this.receiptNo)
             + seperator + (this.testCode == null ? "" : this.testCode)
             + seperator + (this.testSubCode == null ? "" : this.testSubCode)
             + seperator + (this.specimenCode == null ? "" : this.specimenCode)
             + seperator + (this.testName == null ? "" : this.testName)
             + seperator + (this.specimenName == null ? "" : this.specimenName)
             + seperator + (this.workName == null ? "" : this.workName)
             + seperator + (this.departmentCode == null ? "" : this.departmentCode)
             + seperator + (this.departmentName == null ? "" : this.departmentName)
             + seperator + (this.testStaff == null ? "" : this.testStaff)
             + seperator + (this.equipmentNumericResult == null ? "" : this.equipmentNumericResult)
             + seperator + (this.numericDefaultResult == null ? "" : this.numericDefaultResult)
             + seperator + (this.numericFinalResult == null ? "" : this.numericFinalResult)
             + seperator + (this.charDefaultResult == null ? "" : this.charDefaultResult)
             + seperator + (this.charFinalResult == null ? "" : this.charFinalResult)
             + seperator + (this.resultStatusFlag == null ? "" : this.resultStatusFlag)
             + seperator + (this.decisionNumric1 == null ? "" : this.decisionNumric1)
             + seperator + (this.decisionChar1 == null ? "" : this.decisionChar1)
             + seperator + (this.decisionNumric2 == null ? "" : this.decisionNumric2)
             + seperator + (this.decisionChar2 == null ? "" : this.decisionChar2)
             + seperator + (this.testDate == null ? "" : this.testDate)
             + seperator + (this.workNo == null ? "" : this.workNo)
             + seperator + (this.commentFlag == null ? "" : this.commentFlag)
             + seperator + (this.equipmentFlag == null ? "" : this.equipmentFlag)
             + seperator + (this.workStatus == null ? "" : this.workStatus)
             + seperator + (this.firstSaveUserInfo == null ? "" : this.firstSaveUserInfo)
             + seperator + (this.firstEditFlag == null ? "" : this.firstEditFlag)
             + seperator + (this.secondConfirmUserInfo == null ? "" : this.secondConfirmUserInfo)
             + seperator + (this.secondEditFlag == null ? "" : this.secondEditFlag)
             + seperator + (this.equipmentCode == null ? "" : this.equipmentCode)
             + seperator + (this.creationDate == null ? "" : this.creationDate)
             + seperator + (this.updateDate == null ? "" : this.updateDate)
             + seperator + (this.userId == null ? "" : this.userId)
         ;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }

        final PBSResultDTO other = (PBSResultDTO)obj;
        if (!Objects.equals(this.lmbCode, other.lmbCode)) { return false; }
        if (!Objects.equals(this.receiptDate, other.receiptDate)) { return false; }
        if (!Objects.equals(this.receiptNo, other.receiptNo)) { return false; }
        if (!Objects.equals(this.testCode, other.testCode)) { return false; }
        if (!Objects.equals(this.testSubCode, other.testSubCode)) { return false; }
        if (!Objects.equals(this.specimenCode, other.specimenCode)) { return false; }
        return true;
    }

    /** get header for csv file */
    public String header(String seperator)
    {
        return "LMB_CODE"
             + seperator + "RECEIPT_DATE"
             + seperator + "RECEIPT_NO"
             + seperator + "TEST_CODE"
             + seperator + "TEST_SUB_CODE"
             + seperator + "SPECIMEN_CODE"
             + seperator + "TEST_NAME"
             + seperator + "SPECIMEN_NAME"
             + seperator + "WORK_NAME"
             + seperator + "DEPARTMENT_CODE"
             + seperator + "DEPARTMENT_NAME"
             + seperator + "TEST_STAFF"
             + seperator + "EQUIPMENT_NUMERIC_RESULT"
             + seperator + "NUMERIC_DEFAULT_RESULT"
             + seperator + "NUMERIC_FINAL_RESULT"
             + seperator + "CHAR_DEFAULT_RESULT"
             + seperator + "CHAR_FINAL_RESULT"
             + seperator + "RESULT_STATUS_FLAG"
             + seperator + "DECISION_NUMRIC1"
             + seperator + "DECISION_CHAR1"
             + seperator + "DECISION_NUMRIC2"
             + seperator + "DECISION_CHAR2"
             + seperator + "TEST_DATE"
             + seperator + "WORK_NO"
             + seperator + "COMMENT_FLAG"
             + seperator + "EQUIPMENT_FLAG"
             + seperator + "WORK_STATUS"
             + seperator + "FIRST_SAVE_USER_INFO"
             + seperator + "FIRST_EDIT_FLAG"
             + seperator + "SECOND_CONFIRM_USER_INFO"
             + seperator + "SECOND_EDIT_FLAG"
             + seperator + "EQUIPMENT_CODE"
             + seperator + "CREATION_DATE"
             + seperator + "UPDATE_DATE"
             + seperator + "USER_ID"
         ;
    }

    /** set attributes from buffer array */
    public void setAttributes(String buffer[]) throws Exception
    {
        int i = 0;
        this.lmbCode = buffer[i++];
        this.receiptDate = StringUtil.parseInteger(buffer[i++]);
        this.receiptNo = StringUtil.parseInteger(buffer[i++]);
        this.testCode = buffer[i++];
        this.testSubCode = buffer[i++];
        this.specimenCode = buffer[i++];
        this.testName = buffer[i++];
        this.specimenName = buffer[i++];
        this.workName = buffer[i++];
        this.departmentCode = buffer[i++];
        this.departmentName = buffer[i++];
        this.testStaff = buffer[i++];
        this.equipmentNumericResult = StringUtil.parseDouble(buffer[i++]);
        this.numericDefaultResult = StringUtil.parseDouble(buffer[i++]);
        this.numericFinalResult = StringUtil.parseDouble(buffer[i++]);
        this.charDefaultResult = buffer[i++];
        this.charFinalResult = buffer[i++];
        this.resultStatusFlag = buffer[i++];
        this.decisionNumric1 = StringUtil.parseInteger(buffer[i++]);
        this.decisionChar1 = buffer[i++];
        this.decisionNumric2 = StringUtil.parseInteger(buffer[i++]);
        this.decisionChar2 = buffer[i++];
        this.testDate = StringUtil.parseInteger(buffer[i++]);
        this.workNo = StringUtil.parseInteger(buffer[i++]);
        this.commentFlag = buffer[i++];
        this.equipmentFlag = buffer[i++];
        this.workStatus = buffer[i++];
        this.firstSaveUserInfo = buffer[i++];
        this.firstEditFlag = buffer[i++];
        this.secondConfirmUserInfo = buffer[i++];
        this.secondEditFlag = buffer[i++];
        this.equipmentCode = buffer[i++];
        this.creationDate = DateUtil.convert(buffer[i++]);
        this.updateDate = DateUtil.convert(buffer[i++]);
        this.userId = buffer[i++];
    }

    /** bind object for insert sql */
    public Object[] getBindObject()
    {
        Object bindObj[] = new Object[35];

        bindObj[0] = this.lmbCode;
        bindObj[1] = this.receiptDate;
        bindObj[2] = this.receiptNo;
        bindObj[3] = this.testCode;
        bindObj[4] = this.testSubCode;
        bindObj[5] = this.specimenCode;
        bindObj[6] = this.testName;
        bindObj[7] = this.specimenName;
        bindObj[8] = this.workName;
        bindObj[9] = this.departmentCode;
        bindObj[10] = this.departmentName;
        bindObj[11] = this.testStaff;
        bindObj[12] = this.equipmentNumericResult;
        bindObj[13] = this.numericDefaultResult;
        bindObj[14] = this.numericFinalResult;
        bindObj[15] = this.charDefaultResult;
        bindObj[16] = this.charFinalResult;
        bindObj[17] = this.resultStatusFlag;
        bindObj[18] = this.decisionNumric1;
        bindObj[19] = this.decisionChar1;
        bindObj[20] = this.decisionNumric2;
        bindObj[21] = this.decisionChar2;
        bindObj[22] = this.testDate;
        bindObj[23] = this.workNo;
        bindObj[24] = this.commentFlag;
        bindObj[25] = this.equipmentFlag;
        bindObj[26] = this.workStatus;
        bindObj[27] = this.firstSaveUserInfo;
        bindObj[28] = this.firstEditFlag;
        bindObj[29] = this.secondConfirmUserInfo;
        bindObj[30] = this.secondEditFlag;
        bindObj[31] = this.equipmentCode;
        bindObj[32] = this.creationDate;
        bindObj[33] = this.updateDate;
        bindObj[34] = this.userId;

        return bindObj;
    }
}
