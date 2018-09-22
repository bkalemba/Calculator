package pl.coderslab.app;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "AdvancedCalculator", urlPatterns = "/calc")
public class AdvancedCalculator extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String equation = request.getParameter("equation");
        response.getWriter().println("Wprowadzono: " + equation);
        equation = cleanEquation(equation);
        Pattern pattern = Pattern.compile("\\([^()]+\\)");
        Matcher matcher = pattern.matcher(equation);
        while (matcher.find()) {
            String left = equation.substring(0, matcher.start());
            String right = equation.substring(matcher.end());
            String subequation = matcher.group(0).substring(1, matcher.group(0).length() - 1);
            subequation = firstOrder(subequation);
            subequation = secondOrder(subequation);
            equation = left + subequation + right;
            matcher = pattern.matcher(equation);
        }
        equation = firstOrder(equation);
        equation = secondOrder(equation);
        response.getWriter().println("Wynik: " + equation);
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("/");
    }

    protected static String cleanEquation(String toJoin) {
        String[] parts = toJoin.split("\\s");
        return String.join("", parts);
    }

    protected static String addAndReturn(String equation, int signIndex) {
        String left = equation.substring(0, signIndex);
        String right = equation.substring(signIndex + 1);
        String firstStr = null;
        String secondStr = null;
        Pattern pattern = Pattern.compile("[\\-]?[0-9]*\\.?[0-9]+");
        Matcher matcher1 = pattern.matcher(left);
        Matcher matcher2 = pattern.matcher(right);
        while (matcher1.find()) {
            firstStr = left.substring(matcher1.start(), matcher1.end());
        }
        matcher2.find();
        secondStr = right.substring(matcher2.start(), matcher2.end());
        double sum = 0;
        if (equation.substring(signIndex, signIndex + 1).equals("+")) {
            sum = Double.parseDouble(firstStr) + Double.parseDouble(secondStr);
        } else {
            sum = Double.parseDouble(firstStr) - Double.parseDouble(secondStr);
        }
        StringBuilder newEquation = new StringBuilder();
        newEquation.append(left.substring(0, left.length() - firstStr.length()));
        newEquation.append(sum);
        newEquation.append(right.substring(secondStr.length()));
        return newEquation.toString();

    }

    protected static String multiplyAndReturn(String equation, int signIndex) {
        String left = equation.substring(0, signIndex);
        String right = equation.substring(signIndex + 1);
        String firstStr = null;
        String secondStr = null;
        Pattern pattern = Pattern.compile("[\\-]?[0-9]*\\.?[0-9]+");
        Matcher matcher1 = pattern.matcher(left);
        Matcher matcher2 = pattern.matcher(right);
        while (matcher1.find()) {
            firstStr = left.substring(matcher1.start(), matcher1.end());
        }
        matcher2.find();
        secondStr = right.substring(matcher2.start(), matcher2.end());
        double product = 0;
        if (equation.substring(signIndex, signIndex + 1).equals("*")) {
            product = Double.parseDouble(firstStr) * Double.parseDouble(secondStr);
        } else {
            product = Double.parseDouble(firstStr) / Double.parseDouble(secondStr);
        }
        StringBuilder newEquation = new StringBuilder();
        newEquation.append(left.substring(0, left.length() - firstStr.length()));
        newEquation.append(product);
        newEquation.append(right.substring(secondStr.length()));
        return newEquation.toString();

    }

    protected static String firstOrder(String equation) {
        Pattern pattern1 = Pattern.compile("[0-9]([*/])[\\-]?[0-9]");
        Matcher matcher1 = pattern1.matcher(equation);
        while (matcher1.find()) {
            String operation = matcher1.group(1);
            equation = multiplyAndReturn(equation, matcher1.start(1));
            matcher1 = pattern1.matcher(equation);
        }
        return equation;
    }

    protected static String secondOrder(String equation) {
        Pattern pattern2 = Pattern.compile("[0-9]([+\\-])[\\-]?[0-9]");
        Matcher matcher2 = pattern2.matcher(equation);
        while (matcher2.find()) {
            String operation = matcher2.group(1);
            equation = addAndReturn(equation, matcher2.start(1));
            matcher2 = pattern2.matcher(equation);
        }
        return equation;
    }
}

