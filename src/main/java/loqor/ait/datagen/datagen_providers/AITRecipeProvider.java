package loqor.ait.datagen.datagen_providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

import net.minecraft.block.Block;
import net.minecraft.data.recipe.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import loqor.ait.AITMod;

public class AITRecipeProvider extends FabricRecipeProvider {
    public List<ShapelessRecipeJsonBuilder> shapelessRecipes = new ArrayList<>();
    public List<ShapedRecipeJsonBuilder> shapedRecipes = new ArrayList<>();
    public HashMap<SmithingTransformRecipeJsonBuilder, Identifier> smithingTransformRecipes = new HashMap<>();
    public HashMap<ShapelessRecipeJsonBuilder, Identifier> shapelessRecipesWithNameHashMap = new HashMap<>();
    public HashMap<SingleItemRecipeJsonBuilder, Identifier> stonecutting = new HashMap<>();

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter recipeExporter) {
        return new RecipeGenerator(wrapperLookup, recipeExporter) {
            @Override
            public void generate() {

            }
        };
    }
    public AITRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        for (ShapelessRecipeJsonBuilder shapelessRecipeJsonBuilder : shapelessRecipes) {
            shapelessRecipeJsonBuilder.offerTo(exporter);
        }
        for (ShapedRecipeJsonBuilder shapedRecipeJsonBuilder : shapedRecipes) {
            shapedRecipeJsonBuilder.offerTo(exporter);
        }
        shapelessRecipesWithNameHashMap.forEach((shapelessRecipeJsonBuilder, identifier) -> {
            shapelessRecipeJsonBuilder.offerTo(exporter, identifier);
        });
        smithingTransformRecipes.forEach((smithingTransformRecipeJsonBuilder, identifier) -> {
            smithingTransformRecipeJsonBuilder.offerTo(exporter, identifier);
        });

        stonecutting.forEach((stonecuttingRecipeJsonBuilder, identifier) -> {
            stonecuttingRecipeJsonBuilder.offerTo(exporter, identifier);
        });
    }

    public void addShapelessRecipe(ShapelessRecipeJsonBuilder builder) {
        if (!shapelessRecipes.contains(builder)) {
            shapelessRecipes.add(builder);
        }
    }

    public void addShapelessRecipeWithCustomname(ShapelessRecipeJsonBuilder builder, Identifier id) {
        shapelessRecipesWithNameHashMap.put(builder, id);
    }

    public void addSmithingTransformRecipe(SmithingTransformRecipeJsonBuilder builder, Identifier id) {
        smithingTransformRecipes.put(builder, id);
    }


    public void addShapedRecipe(ShapedRecipeJsonBuilder builder) {
        if (!shapedRecipes.contains(builder)) {
            shapedRecipes.add(builder);
        }
    }

    public void addStonecutting(Block in, Block out, int count) {
        Identifier id = getStonecuttingIdentifier(in, out);

        stonecutting.put(SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(in), RecipeCategory.BUILDING_BLOCKS, out, count).criterion("has_block", VanillaRecipeProvider.conditionsFromItem(in)), id);
    }
    public void addStonecutting(Block in, Block out) {
        addStonecutting(in, out, 1);
    }

    private Identifier getStonecuttingIdentifier(Block in, Block out) {
        return AITMod.id(fixupBlockKey(in.getTranslationKey()) + "_to_" + fixupBlockKey(out.getTranslationKey()) + "_stonecutting");
    }
    private String fixupBlockKey(String key) {
        return key.substring(key.lastIndexOf(".") + 1);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
        return null;
    }

    @Override
    public String getName() {
        return "AIT's Recipes";
    }
}
