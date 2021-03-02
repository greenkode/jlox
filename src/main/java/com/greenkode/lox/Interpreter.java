package com.greenkode.lox;

public class Interpreter implements Expr.Visitor<Object> {

    void interpret(Expr expresion) {
        try {
            Object value = evaluate(expresion);
            System.out.println(stringify(value));
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        var left = evaluate(expr.left);
        var right = evaluate(expr.right);

        switch (expr.operator.type) {
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return Double.parseDouble(left.toString()) > Double.parseDouble(right.toString());
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return Double.parseDouble(left.toString()) >= Double.parseDouble(right.toString());
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return Double.parseDouble(left.toString()) < Double.parseDouble(right.toString());
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return Double.parseDouble(left.toString()) <= Double.parseDouble(right.toString());
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return Double.parseDouble(left.toString()) - Double.parseDouble(right.toString());
            case PLUS:
                if (isDouble(left) && isDouble(right)) {
                    return Double.parseDouble(left.toString()) + Double.parseDouble(right.toString());
                }
                if (left instanceof String && right instanceof String) {
                    return left + right.toString();
                }
                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return Double.parseDouble(left.toString()) / Double.parseDouble(right.toString());
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return Double.parseDouble(left.toString()) * Double.parseDouble(right.toString());
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
        }
        return null;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        var right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double) right;
        }

        return null;
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;
        return true;
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (isDouble(operand)) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (isDouble(left) && isDouble(right)) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private Object evaluate(Expr expression) {
        return expression.accept(this);
    }

    private boolean isEqual(Object left, Object right) {
        if (left == null && right == null) return true;
        if (left == null) return false;

        return left.equals(right);
    }

    private String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

    private boolean isDouble(Object operand) {
        if (operand instanceof String) {
            try {
                Double.parseDouble(operand.toString());
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        } else return operand instanceof Double;
    }
}
