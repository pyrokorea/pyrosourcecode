UPDATE TB_CASE_STRUCTURE
   SET ATTRIBUTE_NAME = ?,
       UPDATE_DATE = CURRENT_TIMESTAMP,
       USER_ID = ?
 WHERE DOMAIN_NAME = ?
   AND ATTRIBUTE_NAME = ?