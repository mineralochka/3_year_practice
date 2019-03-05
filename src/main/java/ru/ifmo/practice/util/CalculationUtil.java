package ru.ifmo.practice.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.ifmo.practice.exception.CalculationException;

import java.math.BigDecimal;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
import java.util.function.BiFunction;

/**
 * Performs calculations - applies the shunting-yard algorithm which translates the input into
 * reverse Polish notation and then computes the result
 *
 * @author r.goroshko
 */
@Slf4j
public final class CalculationUtil {
    private static final List<String> OPERATORS = List.of("+", "-", "*", "/", "^");

    private CalculationUtil() {

    }

    /**
     * @param input expresion to calculate
     * @return calculation result
     * @throws UnsupportedOperationException on invalid input
     */
    public static String calculate(String input) throws CalculationException {
        if (StringUtils.isBlank(input)) {
            throw new CalculationException();
        }

        try {
            return calculateRPN(shuntingYard(input)).toString();
        } catch (EmptyStackException e) {
            throw new CalculationException("Faulty input, check for errors", e);
        } catch (RuntimeException | AssertionError e) {
            throw new CalculationException("Internal error, please contact the developer", e);
        }
    }

    /**
     * Calculates reverse Polish notation expression on a stack
     *
     * @param rpnList list of tokens in reverse Polish notation
     * @return calculation result
     */
    private static BigDecimal calculateRPN(List<Token> rpnList) {
        Stack<BigDecimal> stack = new Stack<>();
        for (Token token : rpnList) {
            if (!TokenType.OPERATOR.equals(token.getTokenType())) {
                stack.add(new BigDecimal(token.getValue()));
            } else {
                BiFunction<BigDecimal, BigDecimal, BigDecimal> function;
                switch (token.getValue()) {
                    case "^":
                        function = (n1, n2) -> n1.pow(n2.intValue());
                        break;
                    case "+":
                        function = BigDecimal::add;
                        break;
                    case "-":
                        function = BigDecimal::subtract;
                        break;
                    case "*":
                        function = BigDecimal::multiply;
                        break;
                    case "/":
                        function = BigDecimal::divide;
                        break;
                    default:
                        throw new RuntimeException();
                }
                BigDecimal n2 = stack.pop();
                stack.add(function.apply(stack.pop(), n2));
            }
        }
        assert stack.size() == 1;
        return stack.pop();
    }

    /**
     * Applies the shunting-yard algorithm to the input - removes parentheses and puts tokens in correct order
     *
     * @param input mathematical expression
     * @return a list of tokens in reverse Polish notation
     */
    private static List<Token> shuntingYard(String input) {
        final Stack<Token> stack = new Stack<>();
        final Stack<Token> output = new Stack<>();
        for (String token : input.replaceAll("[+\\-*/^()]", " $0 ").split("\\s")) {
            if (StringUtils.isBlank(token)) {
                continue;
            }

            if (StringUtils.isNumeric(token)) {
                output.add(Token.number(token));
            } else if (OPERATORS.contains(token)) {
                Token newToken = Token.operator(token);
                while (!stack.empty() && TokenType.OPERATOR.equals(stack.peek().getTokenType()) &&
                        stack.peek().priority() > newToken.priority()) {
                    output.add(stack.pop());
                }
                stack.add(newToken);
            } else if ("(".equals(token)) {
                stack.add(new Token(TokenType.PARENTHESIS, token));
            } else {
                while (!TokenType.PARENTHESIS.equals(stack.peek().getTokenType())) {
                    output.add(stack.pop());
                }
                stack.pop();
            }
        }
        while (!stack.empty()) {
            output.add(stack.pop());
        }
        return output;
    }

    /**
     * A mathematical symbol - number, operator, parenthesis
     */
    @Data
    @AllArgsConstructor
    private static class Token {
        private TokenType tokenType;
        private String value;

        int priority() {
            assert TokenType.OPERATOR.equals(tokenType);
            return OPERATORS.indexOf(value) / 2;
        }

        /**
         * Factory method, produces numbers
         *
         * @param value number to wrap
         * @return number token
         */
        static Token number(String value) {
            return new Token(TokenType.NUMBER, value);
        }

        /**
         * Factory method, produces operators
         *
         * @param value operator to wrap
         * @return operator token
         */
        static Token operator(String value) {
            return new Token(TokenType.OPERATOR, value);
        }
    }

    private enum TokenType {
        OPERATOR,
        NUMBER,
        PARENTHESIS
    }
}
