package com.example.sample;

import java.util.Scanner;

public class CalculationRequestReader {

    public CalculationRequest read() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter two numbers and an orperator (e.g. 1 + 2: )");
        String result = scanner.nextLine();
        String[] parts = result.split(" ");

        return new CalculationRequest(parts);
    }
}
