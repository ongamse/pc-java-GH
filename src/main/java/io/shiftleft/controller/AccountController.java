package io.shiftleft.controller;

import io.shiftleft.data.DataLoader;
import io.shiftleft.exception.AccessDeniedException;
import io.shiftleft.model.Account;
import io.shiftleft.model.Customer;
import io.shiftleft.repository.CustomerRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.shiftleft.repository.AccountRepository;

import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * Admin checks login
 */

@RestController
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private static Logger log = LoggerFactory.getLogger(DataLoader.class);

    /**
     * Verifies that the authenticated user owns the account identified by accountId.
     * Throws AccessDeniedException if principal is null or does not own the account.
     */
    private void verifyAccountOwnership(Principal principal, long accountId) {
        if (principal == null) {
            throw new AccessDeniedException();
        }
        Customer owner = customerRepository.findByAccountsId(accountId);
        if (owner == null || !principal.getName().equals(owner.getCustomerId())) {
            throw new AccessDeniedException();
        }
    }
    
    @GetMapping("/account")
    public Iterable<Account> getAccountList(HttpServletResponse response, HttpServletRequest request) {
        response.addHeader("test-header-detection", new Account().toString());
        log.info("Account Data is {}", this.accountRepository.findOne(1l).toString());
        return this.accountRepository.findAll();
    }

    @PostMapping("/account")
    public Account createAccount(Account account) {
        this.accountRepository.save(account);
        log.info("Account Data is {}", account.toString());
        return account;
    }

    @GetMapping("/account/{accountId}")
    public Account getAccount(@PathVariable long accountId, Principal principal) {
        verifyAccountOwnership(principal, accountId);
        log.info("Account Data is {}", this.accountRepository.findOne(1l).toString());
        return this.accountRepository.findOne(accountId);
    }

    @PostMapping("/account/{accountId}/deposit")
    public Account depositIntoAccount(@RequestParam double amount, @PathVariable long accountId, Principal principal) {
        verifyAccountOwnership(principal, accountId);
        Account account = this.accountRepository.findOne(accountId);
        log.info("Account Data is {}", account.toString());
        account.deposit(amount);
        this.accountRepository.save(account);
        return account;
    }

    @PostMapping("/account/{accountId}/withdraw")
    public Account withdrawFromAccount(@RequestParam double amount, @PathVariable long accountId, Principal principal) {
        verifyAccountOwnership(principal, accountId);
        Account account = this.accountRepository.findOne(accountId);
        account.withdraw(amount);
        this.accountRepository.save(account);
        log.info("Account Data is {}", account.toString());
        return account;
    }

    @PostMapping("/account/{accountId}/addInterest")
    public Account addInterestToAccount(@RequestParam double amount, @PathVariable long accountId, Principal principal) {
        verifyAccountOwnership(principal, accountId);
        Account account = this.accountRepository.findOne(accountId);
        account.addInterest();
        this.accountRepository.save(account);
        log.info("Account Data is {}", account.toString());
        return account;
    }

}
