package com.wetech.demo.web3j.service;

import com.wetech.demo.web3j.contracts.erc20test.ERC20Test;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ERC20TestService {

    private final Web3j web3j;
    private final Credentials credentials;
    private final ContractGasProvider gasProvider;

    private ERC20Test contract;
    @Getter
    private String contractAddress;

    /**
     * 部署ERC20合约
     */
    public CompletableFuture<String> deployContract() {
        log.info("Deploying ERC20Test contract...");
        return ERC20Test.deploy(web3j, credentials, gasProvider)
                .sendAsync()
                .thenApply(deployedContract -> {
                    this.contract = deployedContract;
                    this.contractAddress = deployedContract.getContractAddress();
                    log.info("ERC20Test contract deployed to: {}", contractAddress);
                    return contractAddress;
                });
    }

    /**
     * 加载已部署的ERC20合约
     */
    public void loadContract(String contractAddress) {
        log.info("Loading ERC20Test contract from address: {}", contractAddress);
        this.contract = ERC20Test.load(contractAddress, web3j, credentials, gasProvider);
        this.contractAddress = contractAddress;
    }

    /**
     * 铸造代币
     */
    public CompletableFuture<TransactionReceipt> mint(String to, BigInteger amount) {
        validateContractLoaded();
        log.info("Minting {} tokens to {} (contract: {})", amount, to, contractAddress);
        return contract.mint(to, amount).sendAsync();
    }

    /**
     * 转账代币
     */
    public CompletableFuture<TransactionReceipt> transfer(String to, BigInteger amount) {
        validateContractLoaded();
        log.info("Transferring {} tokens to {} (contract: {})", amount, to, contractAddress);
        return contract.transfer(to, amount).sendAsync();
    }

    /**
     * 查询余额
     */
    public CompletableFuture<BigInteger> balanceOf(String account) {
        validateContractLoaded();
        log.info("Querying balance of {} (contract: {})", account, contractAddress);
        return contract.balanceOf(account).sendAsync();
    }

    /**
     * 授权额度
     */
    public CompletableFuture<TransactionReceipt> approve(String spender, BigInteger amount) {
        validateContractLoaded();
        log.info("Approving {} tokens to spender {} (contract: {})", amount, spender, contractAddress);
        return contract.approve(spender, amount).sendAsync();
    }

    /**
     * 授权转账
     */
    public CompletableFuture<TransactionReceipt> transferFrom(String from, String to, BigInteger amount) {
        validateContractLoaded();
        log.info("Transferring {} tokens from {} to {} (contract: {})", amount, from, to, contractAddress);
        return contract.transferFrom(from, to, amount).sendAsync();
    }

    /**
     * 验证合约是否已加载
     */
    private void validateContractLoaded() {
        if (contract == null) {
            throw new IllegalStateException("ERC20 contract not deployed or loaded");
        }
    }

    // 调用合约的 allowance 方法
    public CompletableFuture<BigInteger> allowance(String owner, String spender) {
        validateContractLoaded();
        log.info("Querying allowance from {} to {} (contract: {})", owner, spender, contractAddress);
        return contract.allowance(owner, spender).sendAsync();
    }
}