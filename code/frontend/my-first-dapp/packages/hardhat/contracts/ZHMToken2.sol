// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

/**
 * @title ZHMToken
 * @dev 一个符合 ERC20 标准的代币合约（初始发行量为 0）
 * 名称: ZHMToken
 * 符号: ZHM
 * 精度: 18
 * 功能: mint（发行）与 burn（销毁）
 */
contract ZHMToken is ERC20, Ownable {
    constructor() ERC20("ZHMToken", "ZHM") Ownable(msg.sender) {
        // 初始发行量为 0，不进行任何 _mint
    }

    /**
     * @dev 发行新代币（仅限合约所有者）
     * @param to 接收地址
     * @param amount 发行数量（不含小数精度）
     */
    function mint(address to, uint256 amount) external onlyOwner {
        _mint(to, amount);
    }

    /**
     * @dev 销毁代币（仅限当前持币者）
     * @param amount 销毁数量（不含小数精度）
     */
    function burn(uint256 amount) external {
        _burn(msg.sender, amount);
    }
}

