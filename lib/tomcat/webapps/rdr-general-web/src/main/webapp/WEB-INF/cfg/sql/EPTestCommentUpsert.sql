INSERT INTO TB_EP_TEST_COMMENT
(LMB_CODE
,RECEIPT_DATE
,RECEIPT_NO
,TEST_CODE
,SPECIMEN_CODE
,COMMENT_CODE
,COMMENT
,CREATION_DATE
,UPDATE_DATE
,USER_ID)
VALUES
(?
,?
,?
,?
,?
,?
,?
,CURRENT_TIMESTAMP
,NULL
,?)
ON DUPLICATE KEY UPDATE
COMMENT_CODE = ?
,UPDATE_DATE = CURRENT_TIMESTAMP
,USER_ID = ?
