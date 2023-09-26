package com.shepherdmoney.interviewproject.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String issuanceBank;

    private String number;

    // TODO: Credit card's owner. For detailed hint, please see User class

    // TODO: Credit card's balance history. It is a requirement that the dates in
    // the balanceHistory
    // list must be in chronological order, with the most recent date appearing
    // first in the list.
    // Additionally, the first object in the list must have a date value that
    // matches today's date,
    // since it represents the current balance of the credit card. For example:
    // [
    // {date: '2023-04-13', balance: 1500},
    // {date: '2023-04-12', balance: 1200},
    // {date: '2023-04-11', balance: 1000},
    // {date: '2023-04-10', balance: 800}
    // ]

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "creditCard", cascade = CascadeType.ALL)
    private List<BalanceHistory> balanceHistory = new ArrayList<>();

    public void updateBalance(Instant date, double amount) {
        BalanceHistory balance = new BalanceHistory();
        balance.setDate(date);
        balance.setBalance(amount);
        balance.setCreditCard(this);
        balanceHistory.add(0, balance);
    }

    public double getBalanceAt(Instant date) {
        return balanceHistory.stream()
                .filter(bh -> bh.getDate().isBefore(date) || bh.getDate().equals(date))
                .mapToDouble(BalanceHistory::getBalance)
                .sum();
    }

}
