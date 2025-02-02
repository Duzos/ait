package loqor.ait.core.likes;

import java.util.Optional;

import dev.pavatus.lib.register.datapack.SimpleDatapackRegistry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import loqor.ait.AITMod;

public class ItemOpinionRegistry extends SimpleDatapackRegistry<ItemOpinion> {
    private static final ItemOpinionRegistry instance = new ItemOpinionRegistry();


    public ItemOpinionRegistry() {
        super(ItemOpinion::fromInputStream, ItemOpinion.CODEC, "opinion_item", "opinion/item", true);
    }

    public static ItemOpinion LIKES_ENDER_EYE;

    @Override
    protected void defaults() {
        LIKES_ENDER_EYE = register(new ItemOpinion(AITMod.id("likes_ender_eye"), Items.ENDER_EYE.getDefaultStack(), 10));
    }

    @Override
    public ItemOpinion fallback() {
        return LIKES_ENDER_EYE;
    }

    public Optional<ItemOpinion> get(ItemStack stack) {
        return REGISTRY.values().stream().filter(opinion -> opinion.stack().equals(stack)).findFirst();
    }
    public Optional<ItemOpinion> get(Item item) {
        return REGISTRY.values().stream().filter(opinion -> opinion.stack().getItem().equals(item)).findFirst();
    }

    public static ItemOpinionRegistry getInstance() {
        return instance;
    }
}
