SELECT DOMAIN_NAME,
       CONCLUSION_ID,
       VALUE_TYPE_ID,
       CONCLUSION_NAME,
       CREATION_DATE,
       UPDATE_DATE,
       USER_ID
FROM TB_RULE_CONCLUSION
WHERE DOMAIN_NAME = ?
