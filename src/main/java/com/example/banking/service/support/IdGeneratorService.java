package com.example.banking.service.support;

import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class IdGeneratorService {
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public IdGeneratorService(CustomerRepository customerRepository, AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    public synchronized String nextCustomerId() {
        return nextPrefixed(customerRepository.findMaxCustomerId(), "CUST", 6);
    }

    public synchronized String nextAccountNumber() {
        return nextPrefixed(accountRepository.findMaxAccountNumber(), "AC", 6);
    }

    private String nextPrefixed(String maxValue, String prefix, int width) {
        int next = 1;
        if (maxValue != null && maxValue.startsWith(prefix)) {
            String num = maxValue.substring(prefix.length());
            next = Integer.parseInt(num) + 1;
        }
        return prefix + String.format("%0" + width + "d", next);
    }
}
