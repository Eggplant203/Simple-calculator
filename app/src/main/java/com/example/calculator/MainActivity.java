package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvResult;
    private String currentInput = "";
    private String operator = "";
    private double firstNumber = 0;
    private double result = 0;
    private boolean isOperatorClicked = false;
    private boolean isCalculationDone = false;
    private DecimalFormat formatter;
    private TextView tvOperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Formatter
        formatter = new DecimalFormat("#,###.########");
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(8);

        tvResult = findViewById(R.id.tvResult);
        tvOperation = findViewById(R.id.tvOperation);
        tvResult.setText("0");
        tvOperation.setText("");

        // onClick
        int[] numberIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot};

        for (int id : numberIds) {
            findViewById(id).setOnClickListener(this);
        }

        int[] operatorIds = {R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply,
                R.id.btnDivide, R.id.btnPercent};

        for (int id : operatorIds) {
            findViewById(id).setOnClickListener(this);
        }

        findViewById(R.id.btnEquals).setOnClickListener(this);
        findViewById(R.id.btnDel).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        // Xử lý nút số
        if (id == R.id.btn0 || id == R.id.btn1 || id == R.id.btn2 || id == R.id.btn3 ||
                id == R.id.btn4 || id == R.id.btn5 || id == R.id.btn6 || id == R.id.btn7 ||
                id == R.id.btn8 || id == R.id.btn9) {

            Button button = (Button) view;
            String digit = button.getText().toString();

            if (isOperatorClicked || isCalculationDone || currentInput.equals("0")) {
                currentInput = digit;
                isOperatorClicked = false;
                isCalculationDone = false;
            } else {
                currentInput += digit;
            }

            tvResult.setText(formatNumber(currentInput));
        }
        // Xử lý dấu chấm
        else if (id == R.id.btnDot) {
            if (isCalculationDone) {
                currentInput = "0.";
                isCalculationDone = false;
            } else if (isOperatorClicked) {
                currentInput = "0.";
                isOperatorClicked = false;
            } else if (!currentInput.contains(".")) {
                if (currentInput.isEmpty()) {
                    currentInput = "0.";
                } else {
                    currentInput += ".";
                }
            }
            tvResult.setText(formatNumber(currentInput));
        }
        else if (id == R.id.btnPlus || id == R.id.btnMinus || id == R.id.btnMultiply ||
                id == R.id.btnDivide) {

            Button button = (Button) view;

            // Nếu có phép tính trước đó thực hiện phép tính đó
            if (!operator.isEmpty() && !isOperatorClicked && !currentInput.isEmpty()) {
                calculateResult();
            } else if (currentInput.isEmpty() || currentInput.equals("-")) {
                // Nhập số âm nếu nhấn dấu trừ đầu tiên
                if (id == R.id.btnMinus) {
                    currentInput = "-";
                    tvResult.setText(currentInput);
                    return;
                } else {
                    // Không có số nào được nhập, sử dụng kết quả trước đó
                    firstNumber = result;
                }
            } else {
                // Lưu số đầu tiên
                try {
                    firstNumber = Double.parseDouble(currentInput);
                    result = firstNumber;
                } catch (NumberFormatException e) {
                    tvResult.setText("Error");
                    resetCalculator();
                    return;
                }
            }

            operator = button.getText().toString();
            isOperatorClicked = true;
            isCalculationDone = false;

            tvOperation.setText(operator);
        }

        // Xử lý bằng
        else if (id == R.id.btnEquals) {
            if (!operator.isEmpty() && !currentInput.isEmpty() && !isOperatorClicked) {
                calculateResult();
                isCalculationDone = true;
                operator = "";
                tvOperation.setText("");
            }
        }

        // Xử lý DEL
        else if (id == R.id.btnDel) {
            if (isCalculationDone) {
                resetCalculator();
            } else if (!currentInput.isEmpty()) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
                if (currentInput.isEmpty() || currentInput.equals("-")) {
                    currentInput = "0";
                }
                tvResult.setText(formatNumber(currentInput));
            } else {
                resetCalculator();
            }
        }
        // Xử lý %
        else if (id == R.id.btnPercent) {
            if (!currentInput.isEmpty() && !currentInput.equals("-")) {
                try {
                    double value = Double.parseDouble(currentInput);
                    value = value / 100;
                    currentInput = String.valueOf(value);
                    tvResult.setText(formatNumber(currentInput));
                    isCalculationDone = true;
                } catch (NumberFormatException e) {
                    tvResult.setText("Error");
                    resetCalculator();
                }
            }
        }
    }

    private void calculateResult() {
        if (currentInput.isEmpty()) return;

        double secondNumber;
        try {
            secondNumber = Double.parseDouble(currentInput);
        } catch (NumberFormatException e) {
            tvResult.setText("Error");
            resetCalculator();
            return;
        }

        switch (operator) {
            case "+":
                result = firstNumber + secondNumber;
                break;
            case "-":
                result = firstNumber - secondNumber;
                break;
            case "X":
                result = firstNumber * secondNumber;
                break;
            case "/":
                if (secondNumber != 0) {
                    result = firstNumber / secondNumber;
                } else {
                    tvResult.setText("Không thể chia cho 0");
                    tvOperation.setText("");
                    isCalculationDone = true;
                    return;
                }
                break;
            case "%":
                if (secondNumber != 0) {
                    result = firstNumber % secondNumber;
                } else {
                    tvResult.setText("Không thể chia cho 0");
                    tvOperation.setText("");
                    isCalculationDone = true;
                    return;
                }
                break;
        }

        // Kiểm tra kết quả có phải là vô cùng hoặc NaN không
        if (Double.isInfinite(result) || Double.isNaN(result)) {
            tvResult.setText("Lỗi phép tính");
            tvOperation.setText("");
            isCalculationDone = true;
            return;
        }

        // Hiển thị kết quả
        currentInput = String.valueOf(result);
        tvResult.setText(formatNumber(currentInput));
        tvOperation.setText("");
        firstNumber = result;
    }

    private void resetCalculator() {
        currentInput = "0";
        operator = "";
        firstNumber = 0;
        result = 0;
        isOperatorClicked = false;
        isCalculationDone = false;
        tvResult.setText("0");
        tvOperation.setText("");
    }

    private String formatNumber(String number) {
        try {
            if (number.equals("-")) return "-";

            double value = Double.parseDouble(number);
            return formatter.format(value);
        } catch (NumberFormatException e) {
            return number;
        }
    }
}
