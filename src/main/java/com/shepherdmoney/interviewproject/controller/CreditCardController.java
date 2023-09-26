package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.model.CreditCard;
import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.CreditCardRepository;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.vo.request.AddCreditCardToUserPayload;
import com.shepherdmoney.interviewproject.vo.request.UpdateBalancePayload;
import com.shepherdmoney.interviewproject.vo.response.CreditCardView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class CreditCardController {

    // TODO: wire in CreditCard repository here (~1 line)
    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/credit-card")
    public ResponseEntity<Integer> addCreditCardToUser(@RequestBody AddCreditCardToUserPayload payload) {
        // TODO: Create a credit card entity, and then associate that credit card with
        // user with given userId
        // Return 200 OK with the credit card id if the user exists and credit card is
        // successfully associated with the user
        // Return other appropriate response code for other exception cases
        // Do not worry about validating the card number, assume card number could be
        // any arbitrary format and length
        Optional<User> optionalUser = userRepository.findById(payload.getUserId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            CreditCard creditCard = new CreditCard();
            creditCard.setIssuanceBank(payload.getCardIssuanceBank());
            creditCard.setNumber(payload.getCardNumber());
            user.addCreditCard(creditCard);
            userRepository.save(user);
            return ResponseEntity.ok(creditCard.getId());
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/credit-card:all")
    public ResponseEntity<List<CreditCardView>> getAllCardOfUser(@RequestParam int userId) {
        // TODO: return a list of all credit card associated with the given userId,
        // using CreditCardView class
        // if the user has no credit card, return empty list, never return null
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<CreditCardView> creditCardViews = user.getCreditCards().stream()
                    .map(CreditCardView::fromCreditCard)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(creditCardViews);
        } else {
            return ResponseEntity.badRequest().body(null);
        }

    }

    @GetMapping("/credit-card:user-id")
    public ResponseEntity<Integer> getUserIdForCreditCard(@RequestParam String creditCardNumber) {
        // TODO: Given a credit card number, efficiently find whether there is a user
        // associated with the credit card
        // If so, return the user id in a 200 OK response. If no such user exists,
        // return 400 Bad Request

        Optional<CreditCard> optionalCreditCard = creditCardRepository.findByNumber(creditCardNumber);
        if (optionalCreditCard.isPresent()) {
            CreditCard creditCard = optionalCreditCard.get();
            return ResponseEntity.ok(creditCard.getOwner().getId());
        } else {
            return ResponseEntity.badRequest().body(null);
        }

    }

    @PostMapping("/credit-card:update-balance")
    public ResponseEntity<String> updateBalance(@RequestBody UpdateBalancePayload[] payload) {
        // TODO: Given a list of transactions, update credit cards' balance history.
        // For example: if today is 4/12, a credit card's balanceHistory is [{date:
        // 4/12, balance: 110}, {date: 4/10, balance: 100}],
        // Given a transaction of {date: 4/10, amount: 10}, the new balanceHistory is
        // [{date: 4/12, balance: 120}, {date: 4/11, balance: 110}, {date: 4/10,
        // balance: 110}]
        // Return 200 OK if update is done and successful, 400 Bad Request if the given
        // card number
        // is not associated with a card.

        for (UpdateBalancePayload updateBalancePayload : payload) {
            Optional<CreditCard> optionalCreditCard = creditCardRepository
                    .findByNumber(updateBalancePayload.getCreditCardNumber());
            if (optionalCreditCard.isPresent()) {
                CreditCard creditCard = optionalCreditCard.get();
                Instant date = updateBalancePayload.getTransactionTime();
                double amount = updateBalancePayload.getTransactionAmount();
                creditCard.updateBalance(date, amount);
                creditCardRepository.save(creditCard);
            } else {
                return ResponseEntity.badRequest().body("Credit card not found");
            }
        }
        return ResponseEntity.ok("Balance updated successfully");
    }

}