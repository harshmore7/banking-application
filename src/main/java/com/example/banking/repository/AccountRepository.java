package com.example.banking.repository;

import com.example.banking.model.Account;
import com.example.banking.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findByCustomerFullNameContainingIgnoreCaseOrderByAccountNumber(String name);

    List<Account> findByCustomerOrderByAccountNumber(Customer customer);

    @Query("select max(a.accountNumber) from Account a")
    String findMaxAccountNumber();
}
