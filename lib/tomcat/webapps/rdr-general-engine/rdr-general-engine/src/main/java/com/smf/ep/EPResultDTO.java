
package com.smf.ep;

import java.util.Objects;
import java.util.Date;
import rdr.utils.StringUtil;
import rdr.utils.DateUtil;

/**
 * @brief 검사결과정보 관리 DTO Class
 * @author Kim, Woo Cheol
 * @version 1.00
 * @date 2019.09.18
 * @section MODIFYINFO 수정정보
 *  - 수정일/수정자 : 수정내역
 *  - 2019.09.18/Kim, Woo Cheol : 최초작성
 */
public class EPResultDTO
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
    /** 면적F600NU1 */
    private Double  numArea;
    /** 농도F600NU2 */
    private Double  numConc;
    /** 장비결과F600NU3 */
    private Double  numFromEqpt;
    /** 환산 농도 */
    private Double  numMathConc;
    /** 문자결과 */
    private String  chrResult;
    /** M SPIKE 개수 */
    private Integer mspikeCnt;
    /** 결과 상태(L/H) */
    private String  resultStatus;
    /** 판정코드 */
    private String  decisionCode;
    /** 검사일자 */
    private Integer testDate;
    /** 장비 Flag */
    private String  equipmentFlag;
    /** 검사장비 */
    private String  equipmentCode;
    /** 생성일시 */
    private Date    creationDate;
    /** 수정일시 */
    private Date    updateDate;
    /** 사용자ID */
    private String  userId;

    /** default constructor */
    public EPResultDTO()
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
        this.numArea = null;
        this.numConc = null;
        this.numFromEqpt = null;
        this.numMathConc = null;
        this.chrResult = "";
        this.mspikeCnt = null;
        this.resultStatus = "";
        this.decisionCode = "";
        this.testDate = null;
        this.equipmentFlag = "";
        this.equipmentCode = "";
        this.creationDate = null;
        this.updateDate = null;
        this.userId = "";
    }

    /** constructor */
    public EPResultDTO(final String pLmbCode, final Integer pReceiptDate, final Integer pReceiptNo, final String pTestCode, final String pTestSubCode, final String pSpecimenCode, final String pTestName, final String pSpecimenName, final String pWorkName, final String pDepartmentCode, final String pDepartmentName, final String pTestStaff, final Double pNumArea, final Double pNumConc, final Double pNumFromEqpt, final Double pNumMathConc, final String pChrResult, final Integer pMspikeCnt, final String pResultStatus, final String pDecisionCode, final Integer pTestDate, final String pEquipmentFlag, final String pEquipmentCode, final Date pCreationDate, final Date pUpdateDate, final String pUserId)
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
        this.numArea = pNumArea;
        this.numConc = pNumConc;
        this.numFromEqpt = pNumFromEqpt;
        this.numMathConc = pNumMathConc;
        this.chrResult = pChrResult;
        this.mspikeCnt = pMspikeCnt;
        this.resultStatus = pResultStatus;
        this.decisionCode = pDecisionCode;
        this.testDate = pTestDate;
        this.equipmentFlag = pEquipmentFlag;
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

    /** get 함수 : 면적F600NU1 */
    public Double getNumArea()
    {
        return numArea;
    }

    /** get 함수 : 농도F600NU2 */
    public Double getNumConc()
    {
        return numConc;
    }

    /** get 함수 : 장비결과F600NU3 */
    public Double getNumFromEqpt()
    {
        return numFromEqpt;
    }

    /** get 함수 : 환산 농도 */
    public Double getNumMathConc()
    {
        return numMathConc;
    }

    /** get 함수 : 문자결과 */
    public String getChrResult()
    {
        return chrResult;
    }

    /** get 함수 : M SPIKE 개수 */
    public Integer getMspikeCnt()
    {
        return mspikeCnt;
    }

    /** get 함수 : 결과 상태(L/H) */
    public String getResultStatus()
    {
        return resultStatus;
    }

    /** get 함수 : 판정코드 */
    public String getDecisionCode()
    {
        return decisionCode;
    }

    /** get 함수 : 검사일자 */
    public Integer getTestDate()
    {
        return testDate;
    }

    /** get 함수 : 장비 Flag */
    public String getEquipmentFlag()
    {
        return equipmentFlag;
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

    /** set 함수 : 면적F600NU1 */
    public void setNumArea(final Double pNumArea)
    {
        this.numArea = pNumArea;
    }

    /** set 함수 : 농도F600NU2 */
    public void setNumConc(final Double pNumConc)
    {
        this.numConc = pNumConc;
    }

    /** set 함수 : 장비결과F600NU3 */
    public void setNumFromEqpt(final Double pNumFromEqpt)
    {
        this.numFromEqpt = pNumFromEqpt;
    }

    /** set 함수 : 환산 농도 */
    public void setNumMathConc(final Double pNumMathConc)
    {
        this.numMathConc = pNumMathConc;
    }

    /** set 함수 : 문자결과 */
    public void setChrResult(final String pChrResult)
    {
        this.chrResult = pChrResult;
    }

    /** set 함수 : M SPIKE 개수 */
    public void setMspikeCnt(final Integer pMspikeCnt)
    {
        this.mspikeCnt = pMspikeCnt;
    }

    /** set 함수 : 결과 상태(L/H) */
    public void setResultStatus(final String pResultStatus)
    {
        this.resultStatus = pResultStatus;
    }

    /** set 함수 : 판정코드 */
    public void setDecisionCode(final String pDecisionCode)
    {
        this.decisionCode = pDecisionCode;
    }

    /** set 함수 : 검사일자 */
    public void setTestDate(final Integer pTestDate)
    {
        this.testDate = pTestDate;
    }

    /** set 함수 : 장비 Flag */
    public void setEquipmentFlag(final String pEquipmentFlag)
    {
        this.equipmentFlag = pEquipmentFlag;
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
        hash = 31 * hash + Objects.hashCode(this.numArea);
        hash = 31 * hash + Objects.hashCode(this.numConc);
        hash = 31 * hash + Objects.hashCode(this.numFromEqpt);
        hash = 31 * hash + Objects.hashCode(this.numMathConc);
        hash = 31 * hash + Objects.hashCode(this.chrResult);
        hash = 31 * hash + Objects.hashCode(this.mspikeCnt);
        hash = 31 * hash + Objects.hashCode(this.resultStatus);
        hash = 31 * hash + Objects.hashCode(this.decisionCode);
        hash = 31 * hash + Objects.hashCode(this.testDate);
        hash = 31 * hash + Objects.hashCode(this.equipmentFlag);
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
             + seperator + (this.numArea == null ? "" : this.numArea)
             + seperator + (this.numConc == null ? "" : this.numConc)
             + seperator + (this.numFromEqpt == null ? "" : this.numFromEqpt)
             + seperator + (this.numMathConc == null ? "" : this.numMathConc)
             + seperator + (this.chrResult == null ? "" : this.chrResult)
             + seperator + (this.mspikeCnt == null ? "" : this.mspikeCnt)
             + seperator + (this.resultStatus == null ? "" : this.resultStatus)
             + seperator + (this.decisionCode == null ? "" : this.decisionCode)
             + seperator + (this.testDate == null ? "" : this.testDate)
             + seperator + (this.equipmentFlag == null ? "" : this.equipmentFlag)
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

        final EPResultDTO other = (EPResultDTO)obj;
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
             + seperator + "NUM_AREA"
             + seperator + "NUM_CONC"
             + seperator + "NUM_FROM_EQPT"
             + seperator + "NUM_MATH_CONC"
             + seperator + "CHR_RESULT"
             + seperator + "MSPIKE_CNT"
             + seperator + "RESULT_STATUS"
             + seperator + "DECISION_CODE"
             + seperator + "TEST_DATE"
             + seperator + "EQUIPMENT_FLAG"
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
        this.numArea = StringUtil.parseDouble(buffer[i++]);
        this.numConc = StringUtil.parseDouble(buffer[i++]);
        this.numFromEqpt = StringUtil.parseDouble(buffer[i++]);
        this.numMathConc = StringUtil.parseDouble(buffer[i++]);
        this.chrResult = buffer[i++];
        this.mspikeCnt = StringUtil.parseInteger(buffer[i++]);
        this.resultStatus = buffer[i++];
        this.decisionCode = buffer[i++];
        this.testDate = StringUtil.parseInteger(buffer[i++]);
        this.equipmentFlag = buffer[i++];
        this.equipmentCode = buffer[i++];
        this.creationDate = DateUtil.convert(buffer[i++]);
        this.updateDate = DateUtil.convert(buffer[i++]);
        this.userId = buffer[i++];
    }

    /** bind object for insert sql */
    public Object[] getBindObject()
    {
        Object bindObj[] = new Object[26];

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
        bindObj[12] = this.numArea;
        bindObj[13] = this.numConc;
        bindObj[14] = this.numFromEqpt;
        bindObj[15] = this.numMathConc;
        bindObj[16] = this.chrResult;
        bindObj[17] = this.mspikeCnt;
        bindObj[18] = this.resultStatus;
        bindObj[19] = this.decisionCode;
        bindObj[20] = this.testDate;
        bindObj[21] = this.equipmentFlag;
        bindObj[22] = this.equipmentCode;
        bindObj[23] = this.creationDate;
        bindObj[24] = this.updateDate;
        bindObj[25] = this.userId;

        return bindObj;
    }
}
