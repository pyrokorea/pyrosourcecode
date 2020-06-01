select LMB_CODE
      ,WORK_NO
      ,RECEIPT_DATE
      ,RECEIPT_NO
      ,HOSPITAL_CODE
      ,HOSPITAL_NAME
      ,PATIENT_NAME
      ,SEX
      ,BIRTH_DAY
      ,AGE_YEAR
      ,AGE_MONTH
      ,AGE_DAY
      ,STATUS
      ,BRANCH_CODE
      ,BRANCH_NAME
      ,PID_PRE
      ,PID_AFTER
      ,HOSPITAL_CHART_NO
      ,CREATION_DATE
      ,UPDATE_DATE
      ,USER_ID
 from TB_PBS_RECEIPT
WHERE LMB_CODE = ?
  AND RECEIPT_DATE = ?
  AND RECEIPT_NO = ?
