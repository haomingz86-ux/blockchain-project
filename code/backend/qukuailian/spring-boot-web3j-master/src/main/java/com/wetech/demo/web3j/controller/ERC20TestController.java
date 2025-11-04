package com.wetech.demo.web3j.controller;

import com.wetech.demo.web3j.service.ERC20TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/erc20")
@RequiredArgsConstructor
public class ERC20TestController {

    private final ERC20TestService erc20Service;

    /**
     * 部署ERC20合约
     */
    @PostMapping("/deploy")
    public CompletableFuture<ResponseEntity<Map<String, String>>> deployContract() {
        return erc20Service.deployContract()
                .thenApply(address -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("contractAddress", address);
                    response.put("message", "ERC20 contract deployed successfully");
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * 加载已部署的合约
     */
    @PostMapping("/load")
    public ResponseEntity<Map<String, String>> loadContract(@RequestParam String address) {
        erc20Service.loadContract(address);
        Map<String, String> response = new HashMap<>();
        response.put("message", "ERC20 contract loaded successfully");
        response.put("contractAddress", address);
        return ResponseEntity.ok(response);
    }

    /**
     * 铸造代币
     */
    @PostMapping("/mint")
    public CompletableFuture<ResponseEntity<Map<String, String>>> mint(
            @RequestParam String to,
            @RequestParam String amount) {
        BigInteger value = new BigInteger(amount);
        return erc20Service.mint(to, value)
                .thenApply(receipt -> createTransactionResponse(receipt, "Mint successful"));
    }

    /**
     * 转账代币
     */
    @PostMapping("/transfer")
    public CompletableFuture<ResponseEntity<Map<String, String>>> transfer(
            @RequestParam String to,
            @RequestParam String amount) {
        BigInteger value = new BigInteger(amount);
        return erc20Service.transfer(to, value)
                .thenApply(receipt -> createTransactionResponse(receipt, "Transfer successful"));
    }

    /**
     * 查询余额
     */
    @GetMapping("/balance")
    public CompletableFuture<ResponseEntity<Map<String, String>>> balanceOf(@RequestParam String account) {
        return erc20Service.balanceOf(account)
                .thenApply(balance -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("account", account);
                    response.put("balance", balance.toString());
                    response.put("contractAddress", erc20Service.getContractAddress());
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * 授权额度
     */
    @PostMapping("/approve")
    public CompletableFuture<ResponseEntity<Map<String, String>>> approve(
            @RequestParam String spender,
            @RequestParam String amount) {
        BigInteger value = new BigInteger(amount);
        return erc20Service.approve(spender, value)
                .thenApply(receipt -> createTransactionResponse(receipt, "Approval successful"));
    }

    /**
     * 授权转账
     */
    @PostMapping("/transferFrom")
    public CompletableFuture<ResponseEntity<Map<String, String>>> transferFrom(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String amount) {
        BigInteger value = new BigInteger(amount);
        return erc20Service.transferFrom(from, to, value)
                .thenApply(receipt -> createTransactionResponse(receipt, "TransferFrom successful"));
    }

    /**
     * 获取当前加载的合约地址
     */
    @GetMapping("/address")
    public ResponseEntity<Map<String, String>> getContractAddress() {
        String address = erc20Service.getContractAddress();
        Map<String, String> response = new HashMap<>();
        if (address != null) {
            response.put("contractAddress", address);
        } else {
            response.put("message", "No ERC20 contract loaded");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 生成统一的交易响应格式
     */
    private ResponseEntity<Map<String, String>> createTransactionResponse(TransactionReceipt receipt, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        response.put("transactionHash", receipt.getTransactionHash());
        response.put("blockNumber", receipt.getBlockNumber().toString());
        response.put("gasUsed", receipt.getGasUsed().toString());
        response.put("status", receipt.getStatus());
        response.put("contractAddress", erc20Service.getContractAddress());
        return ResponseEntity.ok(response);
    }

    // 查询授权额度
    @GetMapping("/allowance")
    public CompletableFuture<ResponseEntity<Map<String, String>>> getAllowance(
            @RequestParam String owner,  // from 地址
            @RequestParam String spender) {
        return erc20Service.allowance(owner, spender)
                .thenApply(allowance -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("owner", owner);
                    response.put("spender", spender);
                    response.put("allowance", allowance.toString()); // 以 wei 为单位
                    response.put("contractAddress", erc20Service.getContractAddress());
                    return ResponseEntity.ok(response);
                });
    }
}