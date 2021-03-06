package io.github.starwishsama.extra;

import io.papermc.lib.PaperLib;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.logging.Logger;

public class StarWishUtil {
    /**
     * 推荐你使用 Paper 服务端
     *
     * @param plugin
     */
    public static void suggestPaper(@Nonnull Plugin plugin) {
        if (PaperLib.isPaper()) {
            return;
        }
        final String benefitsProperty = "paperlib.shown-benefits";
        final String pluginName = plugin.getDescription().getName();
        final Logger logger = plugin.getLogger();
        logger.warning("====================================================");
        logger.warning(" " + pluginName + " 在 Paper 上会工作地更好 ");
        logger.warning(" 推荐你使用 Paper 运行" + pluginName + " ");
        if (System.getProperty(benefitsProperty) == null) {
            System.setProperty(benefitsProperty, "1");
            logger.warning("  ");
            logger.warning(" Paper 提供显而易见的性能优化,");
            logger.warning(" Bug 修复, 更好的安全性, 可选的部分特性");
            logger.warning(" 为服主们提升TA们的服务器体验.");
            logger.warning("  ");
            logger.warning(" Paper 内置了 Timings v2. 相比 v1 版本");
            logger.warning(" 能够更显著地诊断服务器卡顿.");
            logger.warning("  ");
            logger.warning(" 你原有的插件在更换后应该仍能正常使用.");
            logger.warning(" 如果遇到问题, Paper 社区很乐意帮助你解决你的问题.");
            logger.warning("  ");
            logger.warning(" 加入 Paper 社区 @ https://papermc.io");
        }
        logger.warning("====================================================");
    }
}
