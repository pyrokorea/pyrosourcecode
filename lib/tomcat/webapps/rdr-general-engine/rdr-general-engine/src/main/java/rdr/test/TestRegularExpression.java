package rdr.test;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRegularExpression 
{
	final private static String pattern1 = "^[가-힣]*$";
    final private static String pattern2 = "^[a-zA-Z]*$";
    final private static String pattern3 = "^[0-9]*$";
    final private static String pattern4 = "^[a-zA-Z가-힣]*$";
    
	public static void main(String[] argrs) 
	{
		//Scanner scan=new Scanner(System.in);
		//reInput(scan);
		test();
	}
	
	private static void test()
	{
		String str = "아마 122일일거야";
		Pattern pattern = Pattern.compile("[0-9]*\\s*일", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
		boolean flag = matcher.find();
		
		if (flag) System.out.println("matched");
		else System.out.println("not matched");
		
		
		
	}

	private static void reInput(Scanner scan)
	{
		System.out.println("parameter을 입력해주세요.(한글 재입력시 숫자나 영어를 입력하고 지우고 입력해주세요. 바이트 문제로 인해 입력 불가능 문제");
		String parameter = scan.next();
		if(parameter.equals("exit"))
		{
			System.out.println("종료합니다.");
			return;
		}
		else 
		{
			for (int i = 0; i < 4; i++) 
			{
				validPattern(parameter, i);	
			}
			System.out.println("------------------------------");
			reInput(scan);
		}
	}

	private static boolean validPattern(String parameter, int patternNo) 
	{
        boolean validation = false;
        switch (patternNo) {
            case 0:
                validation = parameter.matches(pattern1);
                System.out.println("patternNo : "+patternNo+" : "+validation);
                break;
            case 1:
                validation = parameter.matches(pattern2);
                System.out.println("patternNo : "+patternNo+" : "+validation);
                break;
            case 2:
                validation = parameter.matches(pattern3);
                System.out.println("patternNo : "+patternNo+" : "+validation);
                break;
            case 3:
                validation = parameter.matches(pattern4);
                System.out.println("patternNo : "+patternNo+" : "+validation);
                break;
        }

        return validation;
    }



}
