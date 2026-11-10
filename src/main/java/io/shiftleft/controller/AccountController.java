package io.shiftleft.controller;

import io.shiftleft.data.DataLoader;
import io.shiftleft.exception.CustomerNotFoundException;
import io.shiftleft.exception.ForbiddenException;
import io.shiftleft.model.Account;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    private static Logger log = LoggerFactory.getLogger(DataLoader.class);
    
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
    public Account getAccount(@PathVariable long accountId, HttpServletRequest request) {
        log.info("Account Data is {}", this.accountRepository.findOne(1l).toString());
        return findAuthorizedAccount(accountId, request);
    }

    @PostMapping("/account/{accountId}/deposit")
    public Account depositIntoAccount(@RequestParam double amount, @PathVariable long accountId,
                                      HttpServletRequest request) {
        Account account = findAuthorizedAccount(accountId, request);
        log.info("Account Data is {}", account.toString());
        account.deposit(amount);
        this.accountRepository.save(account);
        return account;
    }

    @PostMapping("/account/{accountId}/withdraw")
    public Account withdrawFromAccount(@RequestParam double amount, @PathVariable long accountId,
                                       HttpServletRequest request) {
        Account account = findAuthorizedAccount(accountId, request);
        account.withdraw(amount);
        this.accountRepository.save(account);
        log.info("Account Data is {}", account.toString());
        return account;
    }

    @PostMapping("/account/{accountId}/addInterest")
    public Account addInterestToAccount(@RequestParam double amount, @PathVariable long accountId,
                                        HttpServletRequest request) {
        Account account = findAuthorizedAccount(accountId, request);
        account.addInterest();
        this.accountRepository.save(account);
        log.info("Account Data is {}", account.toString());
        return account;
    }

    /**
     * Looks up an account by a client-supplied id and enforces authorization.
     * The app has no per-customer login; the only authenticated principal is the HMAC-verified
     * admin session (see AdminController), so access to a record addressed by id is limited to it.
     * Fails closed: 403 when not authorized, 404 when the account does not exist.
     */
    private Account findAuthorizedAccount(long accountId, HttpServletRequest request) {
        if (!AdminController.isAdminSession(request)) {
            throw new ForbiddenException();
        }
        Account account = this.accountRepository.findOne(accountId);
        if (account == null) {
            throw new CustomerNotFoundException();
        }
        return account;
    }

}
