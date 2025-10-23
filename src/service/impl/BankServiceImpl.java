package service.impl;

import domain.Account;
import repository.AccountRepository;
import service.BankService;

import java.util.UUID;

public class BankServiceImpl implements BankService {
    private final AccountRepository accountRepository = new AccountRepository();
    @Override
    public String openAccount(String name, String email, String accountType) {
        String customerId = UUID.randomUUID().toString();
        //Change Later -> exponential id =  10 + 1 = AC11
        int temp = accountRepository.findAll().size() + 1;
        String accountNumber = String.format("AC%06d", temp);
        Account account = new Account(accountNumber, customerId, (double) 0,  accountType);
        accountRepository.save(account);
        return "";
    }
}
