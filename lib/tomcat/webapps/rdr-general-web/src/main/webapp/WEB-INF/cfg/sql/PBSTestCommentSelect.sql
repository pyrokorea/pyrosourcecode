SELECT COMMENT_CODE
      ,concat('[A]=======================','\n',
			  ifnull(COMMENT_A,'\n'),'\n',
              '[B]=======================','\n',
              ifnull(COMMENT_B,'\n'),'\n',
              '[C]=======================','\n',
              ifnull(COMMENT_C,'\n'))
 FROM TB_PBS_TEST_COMMENT_MASTER
WHERE COMMENT_CODE LIKE ?
