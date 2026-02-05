package com.example.banking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(name = "beneficiaries", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"owner_account_number", "beneficiary_account_number"})
})
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_account_number", nullable = false)
    private Account ownerAccount;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "beneficiary_account_number", nullable = false)
    private Account beneficiaryAccount;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Beneficiary() {
    }

    public Beneficiary(Account ownerAccount, Account beneficiaryAccount, String nickname, LocalDateTime createdAt) {
        this.ownerAccount = ownerAccount;
        this.beneficiaryAccount = beneficiaryAccount;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Account getOwnerAccount() {
        return ownerAccount;
    }

    public Account getBeneficiaryAccount() {
        return beneficiaryAccount;
    }

    public String getNickname() {
        return nickname;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
