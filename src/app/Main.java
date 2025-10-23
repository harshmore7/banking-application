package app;

import domain.Customer;
import service.BankService;
import service.impl.BankServiceImpl;

import java.util.Scanner;

public class Main{
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BankService bankService = new BankServiceImpl();
        System.out.println("Welcome to Console Bank!");
        boolean running = true;
        while(running){
            System.out.println("""
            1. Open Account
            2. Deposit
            3. Withdraw
            4. Transfer
            5. Account Statement
            6. List Accounts
            7. Search Accounts by Customer Name
            0. EXIT
            """);
            System.out.println("CHOOSE : ");
            String choice =  scanner.nextLine().trim();
            switch (choice){
                case "1" -> openAccount(scanner, bankService);
                case "2" -> deposit(scanner);                
                case "3" -> withdraw(scanner);                
                case "4" -> transfer(scanner);                
                case "5" -> statement(scanner);                
                case "6" -> listAccounts(scanner);                
                case "7" -> searchAccounts(scanner);                
                case "0" -> running = false;
                default -> System.out.println("Choose Valid Option Please!");
            }
        }
    }

    private static void openAccount(Scanner scanner, BankService bankService) {
        System.out.println("Must Enter Customer Details to Open an Account : ");
        System.out.println("Customer Name: ");
        String name = scanner.nextLine().trim();
        System.out.println("Customer Email: ");
        String email = scanner.nextLine().trim();
        System.out.println("Account Type (SAVINGS/CURRENT): ");
        String accountType = scanner.nextLine().trim();
        System.out.println("Initial Deposit (Optional, Leave Blank for 0 Balance): ");
        String amountStr = scanner.nextLine().trim();
        Double initial = Double.valueOf(amountStr);
        bankService.openAccount(name, email, accountType);
    }
    private static void deposit(Scanner scanner) {

    }
    private static void withdraw(Scanner scanner) {

    }
    private static void transfer(Scanner scanner) {

    }

    private static void statement(Scanner scanner) {

    }

    private static void listAccounts(Scanner scanner) {

    }
    private static void searchAccounts(Scanner scanner) {

    }



}
