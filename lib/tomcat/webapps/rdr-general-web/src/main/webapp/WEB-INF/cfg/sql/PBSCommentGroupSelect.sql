select TEST_SUB_CODE, COMMENT_GROUP
from TB_PBS_TEST_CODE
where LMB_CODE = ?
  and TEST_CODE like ?
ORDER BY TEST_SUB_CODE