import { HardhatRuntimeEnvironment } from "hardhat/types";
import { DeployFunction } from "hardhat-deploy/types";

const func: DeployFunction = async function (hre: HardhatRuntimeEnvironment) {
  const { deployments, getNamedAccounts } = hre;
  const { deploy } = deployments;

  const { deployer } = await getNamedAccounts();

  await deploy("ZHMToken", {
    from: deployer,
    args: [], // ⚠️ 构造函数没有参数，所以这里是空数组
    log: true,
  });
};

func.tags = ["ERC20ZHM202330552162"];
export default func;

