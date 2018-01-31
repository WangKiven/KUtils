package com.kiven.kutils.tools;

import java.math.BigDecimal;
import java.util.Stack;

/**
 * 简单公式计算器如：KMath.calculator("1.0/3+1.7")//不要有空格
 * Created by wangk on 2018/1/31.
 */

public class KMath {

    private static final String OPERATOR = "+-*/()";

    private static final int[][] OPERATORCOMPARE = new int[][]{{0, 0, -1, -1, -1, 1},
            {0, 0, -1, -1, -1, 1}, {1, 1, 0, 0, -1, 1},
            {1, 1, 0, 0, -1, 1}, {1, 1, 1, 1, 0, 1},
            {-1, -1, -1, -1, -1, 0}};

    private static final char ENDCHAR = '\n';

    public static BigDecimal calculator(String solution) {
        //需要给表达式添加结束符
        solution += ENDCHAR;
        Stack<BigDecimal> digital = new Stack<BigDecimal>();
        Stack<Character> opeators = new Stack<Character>();
        int begin = 0;
        int length = 0;
        char current = 0;
        for (int i = 0; i < solution.length(); i++) {
            current = solution.charAt(i);
            //解析数字
            if (('0' <= current && current <= '9') || current == '.') {
                length++;
            } else {
                //解析的数字压入数字栈
                if (length > 0) {
                    digital.add(new BigDecimal(solution.substring(begin, begin + length)));
                    begin += length;
                    length = 0;
                }
                begin++;
                //如果扫描结束,但数字栈及符号栈还有数据需要计算,比如1+2+(1+3),不进行此步骤"[3, 4],[+]"为两个栈的元素
                if (ENDCHAR == current) {
                    while (!opeators.isEmpty()) {
                        operator(digital, opeators);
                    }
                } else {
                    //处理当前操作字符,无限循环,处理当前操作字符后跳出循环
                    while (true) {
                        if (opeators.isEmpty()) {
                            opeators.push(current);
                            break;
                        } else {
                            if (OPERATORCOMPARE[OPERATOR.indexOf(current)][OPERATOR
                                    .indexOf(opeators.lastElement())] > 0) {
                                opeators.push(current);
                                break;
                            } else if (opeators.lastElement() == '('
                                    && current == ')') {
                                opeators.pop();
                                break;
                            } else if (opeators.lastElement() == '('
                                    && current != ')') {
                                opeators.push(current);
                                break;
                            } else {
                                operator(digital, opeators);
                            }
                        }
                    }
                }

            }
        }
        return digital.pop();
    }

    private static void operator(Stack<BigDecimal> digital,
                                 Stack<Character> opeators) {
        BigDecimal number1 = digital.pop();
        BigDecimal number2 = digital.pop();
        switch (opeators.pop()) {
            case '+':
                digital.push(number2.add(number1));
                break;
            case '-':
                digital.push(number2.subtract(number1));
                break;
            case '*':
                digital.push(number2.multiply(number1));
                break;
            case '/':
                //
                digital.push(number2.divide(number1, 10, BigDecimal.ROUND_DOWN));
                break;
        }
    }
}
