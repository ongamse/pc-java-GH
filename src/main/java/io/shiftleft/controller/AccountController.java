package io.shiftleft.controller;

import io.shiftleft.data.DataLoader;
import io.shiftleft.exception.UnauthorizedAccessException;
import io.shiftleft.model.Account;
import io.shiftleft.model.Customer;
import io.shiftleft.repository.CustomerRepository;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

import io.shiftleft.repository.AccountRepository;

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
     * Verify the authenticated user owns the given account.
     */
    private void authorizeAccountAccess(Principal principal, long accountId) {
        if (principal == null) {
            throw new UnauthorizedAccessException();
        }
        Customer customer = customerRepository.findByCustomerId(principal.getName());
        if (customer == null || customer.getAccounts() == null
                || customer.getAccounts().stream().noneMatch(a -> a.getId() == accountId)) {
            throw new UnauthorizedAccessException();
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
        authorizeAccountAccess(principal, accountId);
        log.info("Account Data is {}", this.accountRepository.findOne(1l).toString());
        return this.accountRepository.findOne(accountId);
    }

    @PostMapping("/account/{accountId}/deposit")
    public Account depositIntoAccount(@RequestParam double amount, @PathVariable long accountId, Principal principal) {
        authorizeAccountAccess(principal, accountId);
        Account account = this.accountRepository.findOne(accountId);
        log.info("Account Data is {}", account.toString());
        account.deposit(amount);
        this.accountRepository.save(account);
        return account;
    }

    @PostMapping("/account/{accountId}/withdraw")
    public Account withdrawFromAccount(@RequestParam double amount, @PathVariable long accountId, Principal principal) {
        authorizeAccountAccess(principal, accountId);
        Account account = this.accountRepository.findOne(accountId);
        account.withdraw(amount);
        this.accountRepository.save(account);
        log.info("Account Data is {}", account.toString());
        return account;
    }

    @PostMapping("/account/{accountId}/addInterest")
    public Account addInterestToAccount(@RequestParam double amount, @PathVariable long accountId, Principal principal) {
        authorizeAccountAccess(principal, accountId);
        Account account = this.accountRepository.findOne(accountId);
        account.addInterest();
        this.accountRepository.save(account);
        log.info("Account Data is {}", account.toString());
        return account;
    }

}
