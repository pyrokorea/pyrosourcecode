
package com.smf.ep;

import java.util.Objects;
import java.util.Date;
import rdr.utils.StringUtil;
import rdr.utils.RDRConstants;
import rdr.utils.DateUtil;

/**
 * @brief 검사접수정보 관리 DTO Class
 * @author Kim, Woo Cheol
 * @version 1.00
 * @date 2019.09.18
 * @section MODIFYINFO 수정정보
 *  - 수정일/수정자 : 수정내역
 *  - 2019.09.18/Kim, Woo Cheol : 최초작성
 */
public class EPReceiptDTO
{
    /** LMB코드 */
    private String  lmbCode;
    /** 접수일자 */
    private Integer receiptDate;
    /** 접수번호 */
    private Integer receiptNo;
    /** 병원코드 */
    private String  hospitalCode;
    /** 병원명 */
    private String  hospitalName;
    /** 환자명 */
    private String  patientName;
    /** 성별 */
    private String  sex;
    /** 연령_년 */
    private Integer ageYear;
    /** 연령_일 */
    private Integer ageDay;
    /** 진행상태 */
    private String  status;
    /** 지점코드 */
    private String  branchCode;
    /** 지점명 */
    private String  branchName;
    /** 검체채취량 */
    private Double  totalVolume;
    /** 주민번호앞자리 */
    private String  pidPre;
    /** 주민번호뒷자리 */
    private String  pidAfter;
    /** 병원차트번호 */
    private String  hospitalChartNo;
    /** 생성일시 */
    private Date    creationDate;
    /** 수정일시 */
    private Date    updateDate;
    /** 사용자ID */
    private String  userId;

    /** default constructor */
    public EPReceiptDTO()
    {
        this.lmbCode = "";
        this.receiptDate = null;
        this.receiptNo = null;
        this.hospitalCode = "";
        this.hospitalName = "";
        this.patientName = "";
        this.sex = "";
        this.ageYear = null;
        this.ageDay = null;
        this.status = "";
        this.branchCode = "";
        this.branchName = "";
        this.totalVolume = null;
        this.pidPre = "";
        this.pidAfter = "";
        this.hospitalChartNo = "";
        this.creationDate = null;
        this.updateDate = null;
        this.userId = "";
    }

    /** constructor */
    public EPReceiptDTO(final String pLmbCode, final Integer pReceiptDate, final Integer pReceiptNo, final String pHospitalCode, final String pHospitalName, final String pPatientName, final String pSex, final Integer pAgeYear, final Integer pAgeDay, final String pStatus, final String pBranchCode, final String pBranchName, final Double pTotalVolume, final String pPidPre, final String pPidAfter, final String pHospitalChartNo, final Date pCreationDate, final Date pUpdateDate, final String pUserId)
    {
        this.lmbCode = pLmbCode;
        this.receiptDate = pReceiptDate;
        this.receiptNo = pReceiptNo;
        this.hospitalCode = pHospitalCode;
        this.hospitalName = pHospitalName;
        this.patientName = pPatientName;
        this.sex = pSex;
        this.ageYear = pAgeYear;
        this.ageDay = pAgeDay;
        this.status = pStatus;
        this.branchCode = pBranchCode;
        this.branchName = pBranchName;
        this.totalVolume = pTotalVolume;
        this.pidPre = pPidPre;
        this.pidAfter = pPidAfter;
        this.hospitalChartNo = pHospitalChartNo;
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

    /** get 함수 : 병원코드 */
    public String getHospitalCode()
    {
        return hospitalCode;
    }

    /** get 함수 : 병원명 */
    public String getHospitalName()
    {
        return hospitalName;
    }

    /** get 함수 : 환자명 */
    public String getPatientName()
    {
        return patientName;
    }

    /** get 함수 : 성별 */
    public String getSex()
    {
        return sex;
    }

    /** get 함수 : 연령_년 */
    public Integer getAgeYear()
    {
        return ageYear;
    }

    /** get 함수 : 연령_일 */
    public Integer getAgeDay()
    {
        return ageDay;
    }

    /** get 함수 : 진행상태 */
    public String getStatus()
    {
        return status;
    }

    /** get 함수 : 지점코드 */
    public String getBranchCode()
    {
        return branchCode;
    }

    /** get 함수 : 지점명 */
    public String getBranchName()
    {
        return branchName;
    }

    /** get 함수 : 검체채취량 */
    public Double getTotalVolume()
    {
        return totalVolume;
    }

    /** get 함수 : 주민번호앞자리 */
    public String getPidPre()
    {
        return pidPre;
    }

    /** get 함수 : 주민번호뒷자리 */
    public String getPidAfter()
    {
        return pidAfter;
    }

    /** get 함수 : 병원차트번호 */
    public String getHospitalChartNo()
    {
        return hospitalChartNo;
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

    /** set 함수 : 병원코드 */
    public void setHospitalCode(final String pHospitalCode)
    {
        this.hospitalCode = pHospitalCode;
    }

    /** set 함수 : 병원명 */
    public void setHospitalName(final String pHospitalName)
    {
        this.hospitalName = pHospitalName;
    }

    /** set 함수 : 환자명 */
    public void setPatientName(final String pPatientName)
    {
        this.patientName = pPatientName;
    }

    /** set 함수 : 성별 */
    public void setSex(final String pSex)
    {
        this.sex = pSex;
    }

    /** set 함수 : 연령_년 */
    public void setAgeYear(final Integer pAgeYear)
    {
        this.ageYear = pAgeYear;
    }

    /** set 함수 : 연령_일 */
    public void setAgeDay(final Integer pAgeDay)
    {
        this.ageDay = pAgeDay;
    }

    /** set 함수 : 진행상태 */
    public void setStatus(final String pStatus)
    {
        this.status = pStatus;
    }

    /** set 함수 : 지점코드 */
    public void setBranchCode(final String pBranchCode)
    {
        this.branchCode = pBranchCode;
    }

    /** set 함수 : 지점명 */
    public void setBranchName(final String pBranchName)
    {
        this.branchName = pBranchName;
    }

    /** set 함수 : 검체채취량 */
    public void setTotalVolume(final Double pTotalVolume)
    {
        this.totalVolume = pTotalVolume;
    }

    /** set 함수 : 주민번호앞자리 */
    public void setPidPre(final String pPidPre)
    {
        this.pidPre = pPidPre;
    }

    /** set 함수 : 주민번호뒷자리 */
    public void setPidAfter(final String pPidAfter)
    {
        this.pidAfter = pPidAfter;
    }

    /** set 함수 : 병원차트번호 */
    public void setHospitalChartNo(final String pHospitalChartNo)
    {
        this.hospitalChartNo = pHospitalChartNo;
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
        hash = 31 * hash + Objects.hashCode(this.hospitalCode);
        hash = 31 * hash + Objects.hashCode(this.hospitalName);
        hash = 31 * hash + Objects.hashCode(this.patientName);
        hash = 31 * hash + Objects.hashCode(this.sex);
        hash = 31 * hash + Objects.hashCode(this.ageYear);
        hash = 31 * hash + Objects.hashCode(this.ageDay);
        hash = 31 * hash + Objects.hashCode(this.status);
        hash = 31 * hash + Objects.hashCode(this.branchCode);
        hash = 31 * hash + Objects.hashCode(this.branchName);
        hash = 31 * hash + Objects.hashCode(this.totalVolume);
        hash = 31 * hash + Objects.hashCode(this.pidPre);
        hash = 31 * hash + Objects.hashCode(this.pidAfter);
        hash = 31 * hash + Objects.hashCode(this.hospitalChartNo);
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
             + seperator + (this.hospitalCode == null ? "" : this.hospitalCode)
             + seperator + (this.hospitalName == null ? "" : this.hospitalName)
             + seperator + (this.patientName == null ? "" : this.patientName)
             + seperator + (this.sex == null ? "" : this.sex)
             + seperator + (this.ageYear == null ? "" : this.ageYear)
             + seperator + (this.ageDay == null ? "" : this.ageDay)
             + seperator + (this.status == null ? "" : this.status)
             + seperator + (this.branchCode == null ? "" : this.branchCode)
             + seperator + (this.branchName == null ? "" : this.branchName)
             + seperator + (this.totalVolume == null ? "" : this.totalVolume)
             + seperator + (this.pidPre == null ? "" : this.pidPre)
             + seperator + (this.pidAfter == null ? "" : this.pidAfter)
             + seperator + (this.hospitalChartNo == null ? "" : this.hospitalChartNo)
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

        final EPReceiptDTO other = (EPReceiptDTO)obj;
        if (!Objects.equals(this.lmbCode, other.lmbCode)) { return false; }
        if (!Objects.equals(this.receiptDate, other.receiptDate)) { return false; }
        if (!Objects.equals(this.receiptNo, other.receiptNo)) { return false; }
        return true;
    }

    /** get header for csv file */
    public String header(String seperator)
    {
        return "LMB_CODE"
             + seperator + "RECEIPT_DATE"
             + seperator + "RECEIPT_NO"
             + seperator + "HOSPITAL_CODE"
             + seperator + "HOSPITAL_NAME"
             + seperator + "PATIENT_NAME"
             + seperator + "SEX"
             + seperator + "AGE_YEAR"
             + seperator + "AGE_DAY"
             + seperator + "STATUS"
             + seperator + "BRANCH_CODE"
             + seperator + "BRANCH_NAME"
             + seperator + "TOTAL_VOLUME"
             + seperator + "PID_PRE"
             + seperator + "PID_AFTER"
             + seperator + "HOSPITAL_CHART_NO"
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
        this.hospitalCode = buffer[i++];
        this.hospitalName = buffer[i++];
        this.patientName = buffer[i++];
        this.sex = buffer[i++];
        this.ageYear = StringUtil.parseInteger(buffer[i++]);
        this.ageDay = StringUtil.parseInteger(buffer[i++]);
        this.status = buffer[i++];
        this.branchCode = buffer[i++];
        this.branchName = buffer[i++];
        this.totalVolume = StringUtil.parseDouble(buffer[i++]);
        this.pidPre = buffer[i++];
        this.pidAfter = buffer[i++];
        this.hospitalChartNo = buffer[i++];
        this.creationDate = DateUtil.convert(buffer[i++]);
        this.updateDate = DateUtil.convert(buffer[i++]);
        this.userId = buffer[i++];
    }

    /** bind object for insert sql */
    public Object[] getBindObject()
    {
        Object bindObj[] = new Object[19];

        bindObj[0] = this.lmbCode;
        bindObj[1] = this.receiptDate;
        bindObj[2] = this.receiptNo;
        bindObj[3] = this.hospitalCode;
        bindObj[4] = this.hospitalName;
        bindObj[5] = this.patientName;
        bindObj[6] = this.sex;
        bindObj[7] = this.ageYear;
        bindObj[8] = this.ageDay;
        bindObj[9] = this.status;
        bindObj[10] = this.branchCode;
        bindObj[11] = this.branchName;
        bindObj[12] = this.totalVolume;
        bindObj[13] = this.pidPre;
        bindObj[14] = this.pidAfter;
        bindObj[15] = this.hospitalChartNo;
        bindObj[16] = this.creationDate;
        bindObj[17] = this.updateDate;
        bindObj[18] = this.userId;

        return bindObj;
    }
}
