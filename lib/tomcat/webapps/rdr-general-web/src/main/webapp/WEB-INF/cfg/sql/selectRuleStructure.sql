SELECT DOMAIN_NAME,
       RULE_ID,
       PARENT_RULE_ID,
       CONCLUSION_ID,
       CREATION_DATE,
       UPDATE_DATE,
       USER_ID
FROM TB_RULE_STRUCTURE
WHERE DOMAIN_NAME = ?
ORDER BY RULE_ID ASC
