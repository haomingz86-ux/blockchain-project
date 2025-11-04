// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

contract ERC20Test {
    // 状态变量：存储余额、授权额度、总供应量及代币信息
    mapping(address => uint256) private _balances;
    mapping(address => mapping(address => uint256)) private _allowances;
    uint256 private _totalSupply;
    string private _name;
    string private _symbol;

    // 事件定义（ERC20标准要求）
    event Transfer(address indexed from, address indexed to, uint256 value);
    event Approval(address indexed owner, address indexed spender, uint256 value);

    /**
     * @dev 构造函数：初始化代币名称和符号
     */
    constructor() {
        _name = unicode"WZYToken";
        _symbol = unicode"WZY";
    }

    /**
     * @dev 返回代币名称
     */
    function name() public view returns (string memory) {
        return _name;
    }

    /**
     * @dev 返回代币符号
     */
    function symbol() public view returns (string memory) {
        return _symbol;
    }

    /**
     * @dev 返回代币精度
     */
    function decimals() public pure returns (uint8) {
        return 18;
    }

    /**
     * @dev 返回代币总供应量
     */
    function totalSupply() public view returns (uint256) {
        return _totalSupply;
    }

    /**
     * @dev 返回指定地址的代币余额
     */
    function balanceOf(address account) public view returns (uint256) {
        return _balances[account];
    }


    /**
     * @dev 从调用者地址向to转账value数量的代币
     */
    function transfer(address to, uint256 value) public returns (bool) {
        address owner = msg.sender; // 调用者为转账发起者
        _transfer(owner, to, value);
        return true;
    }

    /**
     * @dev 查询spender可使用的owner的代币授权额度
     */
    function allowance(address owner, address spender) public view returns (uint256) {
        return _allowances[owner][spender];
    }

    /**
     * @dev 授权spender使用调用者的value数量代币
     */
    function approve(address spender, uint256 value) public returns (bool) {
        address owner = msg.sender; // 调用者为授权者
        _approve(owner, spender, value);
        return true;
    }

    /**
     * @dev 从from地址向to地址转账value数量代币
     */
    function transferFrom(address from, address to, uint256 value) public returns (bool) {
        address spender = msg.sender; // 调用者为被授权者
        _spendAllowance(from, spender, value); // 消耗授权额度
        _transfer(from, to, value); // 执行转账
        return true;
    }

    /**
     * @dev 内部函数：处理代币转账逻辑
     */
    function _transfer(address from, address to, uint256 value) internal {
        // 检查转账地址有效性
        require(from != address(0), unicode"ERC20: 发送地址不能为0");
        require(to != address(0), unicode"ERC20: 接收地址不能为0");

        _update(from, to, value); // 调用核心更新逻辑
    }

    /**
     * @dev 核心更新函数：处理转账、mint和burn的底层逻辑
     */
    function _update(address from, address to, uint256 value) internal {
        if (from == address(0)) {
            // from为0地址：发行代币
            _totalSupply += value;
        } else {
            // from为普通地址：检查余额是否充足
            uint256 fromBalance = _balances[from];
            require(fromBalance >= value, unicode"ERC20: 余额不足");
            _balances[from] = fromBalance - value; // 扣减发送者余额
        }

        if (to == address(0)) {
            // to为0地址：销毁代币（总供应量减少）
            _totalSupply -= value;
        } else {
            // to为普通地址：增加接收者余额
            _balances[to] += value;
        }

        emit Transfer(from, to, value); // 触发转账事件
    }

    /**
     * @dev 发行代币：向account铸造value数量代币（公开调用）
     */
    function mint(address account, uint256 value) public {
        require(account != address(0), unicode"ERC20: 接收地址不能为0"); // 中文提示加unicode
        _update(address(0), account, value); // 从0地址向account转账（发行）
    }

    /**
     * @dev 销毁代币：从调用者地址销毁value数量代币（公开调用）
     */
    function burn(uint256 value) public {
        address account = msg.sender;
        require(account != address(0), unicode"ERC20: 销毁地址不能为0");
        _update(account, address(0), value); // 从account向0地址转账（销毁）
    }

    /**
     * @dev 内部函数：设置授权额度
     */
    function _approve(address owner, address spender, uint256 value) internal {
        require(owner != address(0), unicode"ERC20: 授权者不能为0");
        require(spender != address(0), unicode"ERC20: 被授权者不能为0");
        _allowances[owner][spender] = value; // 更新授权额度
        emit Approval(owner, spender, value); // 触发授权事件
    }

    /**
     * @dev 内部函数：消耗授权额度（transferFrom时使用）
     */
    function _spendAllowance(address owner, address spender, uint256 value) internal {
        uint256 currentAllowance = allowance(owner, spender);
        require(currentAllowance >= value, unicode"ERC20: 授权额度不足");

        // 扣减授权额度
        _allowances[owner][spender] = currentAllowance - value;
    }
}