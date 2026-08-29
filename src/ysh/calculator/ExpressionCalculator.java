
package ysh.calculator;

public class ExpressionCalculator {

    private String expression;
    private int position;

    public double calculate(String input) {

        expression = input.replaceAll("\\s+", "");
        position = 0;

        double result = parseExpression();

        if (position < expression.length()) {
            throw new IllegalArgumentException(
                "Invalid expression"
            );
        }

        return result;
    }

    private double parseExpression() {

        double result = parseTerm();

        while (position < expression.length()) {

            char operator =
                expression.charAt(position);

            if (operator == '+') {

                position++;
                result += parseTerm();

            } else if (operator == '-') {

                position++;
                result -= parseTerm();

            } else {

                break;
            }
        }

        return result;
    }

    private double parseTerm() {

        double result = parseFactor();

        while (position < expression.length()) {

            char operator =
                expression.charAt(position);

            if (operator == '*') {

                position++;
                result *= parseFactor();

            } else if (operator == '/') {

                position++;

                double divisor =
                    parseFactor();

                if (divisor == 0) {

                    throw new ArithmeticException(
                        "Division by zero"
                    );
                }

                result /= divisor;

            } else {

                break;
            }
        }

        return result;
    }

    private double parseFactor() {

        if (position >= expression.length()) {

            throw new IllegalArgumentException(
                "Invalid expression"
            );
        }

        char character =
            expression.charAt(position);

        if (character == '(') {

            position++;

            double result =
                parseExpression();

            if (position >= expression.length()
                    || expression.charAt(position)
                        != ')') {

                throw new IllegalArgumentException(
                    "Missing )"
                );
            }

            position++;

            return result;
        }

        if (character == '-') {

            position++;

            return -parseFactor();
        }

        return parseNumber();
    }

    private double parseNumber() {

        int start = position;

        while (position < expression.length()) {

            char character =
                expression.charAt(position);

            if (Character.isDigit(character)
                    || character == '.') {

                position++;

            } else {

                break;
            }
        }

        if (start == position) {

            throw new IllegalArgumentException(
                "Expected number"
            );
        }

        return Double.parseDouble(
            expression.substring(
                start,
                position
            )
        );
    }
}
