SELECT COUNT(*)
  FROM TB_RULE_CONDITIONS
 WHERE DOMAIN_NAME =  ?
   AND ATTRIBUTE_ID IN (SELECT ATTRIBUTE_ID
                          FROM TB_CASE_STRUCTURE
                         WHERE DOMAIN_NAME = ?
                           AND ATTRIBUTE_NAME = ?)
