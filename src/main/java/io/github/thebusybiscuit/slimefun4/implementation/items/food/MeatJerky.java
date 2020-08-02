package io.github.thebusybiscuit.slimefun4.implementation.items.food;

import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemConsumptionHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.inventory.ItemStack;

public class MeatJerky extends SimpleSlimefunItem<ItemConsumptionHandler> {

    private final ItemSetting<Integer> saturation = new ItemSetting<>("saturation-level", 6);

    public MeatJerky(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemSetting(saturation);
    }

    @Override
    public ItemConsumptionHandler getItemHandler() {
        return (e, p, item) -> p.setSaturation(p.getSaturation() + saturation.getValue());
    }

}