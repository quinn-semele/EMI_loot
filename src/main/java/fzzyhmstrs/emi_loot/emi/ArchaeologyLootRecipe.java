package fzzyhmstrs.emi_loot.emi;

import com.google.common.collect.ArrayListMultimap;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.EMILootClient;
import fzzyhmstrs.emi_loot.client.ClientArchaeologyLootTable;
import fzzyhmstrs.emi_loot.util.cleancode.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static fzzyhmstrs.emi_loot.util.FloatTrimmer.trimFloatString;

public class ArchaeologyLootRecipe implements EmiRecipe {
	private final ClientArchaeologyLootTable loot;
	private final ArrayListMultimap<Float, EmiStack> lootStacksSorted;
	private final int lootStacksSortedSize;
	private final List<EmiStack> outputs;
	private boolean isGuaranteedNonChance = false;
	private final Text title;
	private final float columns = 8f;

	public ArchaeologyLootRecipe(ClientArchaeologyLootTable loot) {
		this.loot = loot;
		if(loot.items.size() == 1) {
			if (loot.items.values().toFloatArray()[0] == 1f) {
				isGuaranteedNonChance = true;
			}
		}

		ArrayListMultimap<Float, EmiStack> map2 = ArrayListMultimap.create();
		List<EmiStack> outputsList = new LinkedList<>();
		loot.items.forEach((item, weight) -> {
			EmiStack stack = EmiStack.of(item);
			map2.put(weight, stack);
			outputsList.add(stack);
		});
		lootStacksSorted = map2;

		if(loot.items.size() > 48 || EMILoot.config.chestLootAlwaysStackSame) {
			this.lootStacksSortedSize = lootStacksSorted.keySet().size();
		} else {
			this.lootStacksSortedSize = loot.items.size();
		}

		outputs = outputsList;
		String key = "emi_loot.archaeology." + loot.id.toString();
		MutableComponent text = Text.translatable(key);
		MutableText rawTitle;
		if(Objects.equals(text.getString(), key)) {
			Optional<? extends ModContainer> modNameOpt = ModList.get().getModContainerById(loot.id.getNamespace());
			if(modNameOpt.isPresent()) {
				ModContainer modContainer = modNameOpt.get();
				String modName = modContainer.getModInfo().getDisplayName();
				rawTitle = Text.translatable("emi_loot.archaeology.unknown_archaeology", modName);
			} else {
				Text unknown = Text.translatable("emi_loot.archaeology.unknown");
				rawTitle = Text.translatable("emi_loot.archaeology.unknown_archaeology", unknown.getString());
			}
		} else {
			rawTitle = text;
		}

		Text dots = Text.literal("...");
		int dotsWidth = MinecraftClient.getInstance().textRenderer.getWidth(dots);
		if(MinecraftClient.getInstance().textRenderer.getWidth(rawTitle) > 138 - dotsWidth) {
			String trimmed = MinecraftClient.getInstance().textRenderer.trimToWidth(rawTitle.getString(), 138 - dotsWidth) + "...";
			title = Text.literal(trimmed);
		} else {
			title = rawTitle;
		}
	}



	@Override
	public EmiRecipeCategory getCategory() {
		return EmiClientPlugin.ARCHAEOLOGY_CATEGORY;
	}

	@Override
	public @Nullable Identifier getId() {
		return Identifier.of(EMILootClient.MOD_ID, "/" + getCategory().id.getPath() + "/" + loot.id.getNamespace() + "/" + loot.id.getPath());
	}

	@Override
	public List<EmiIngredient> getInputs() {
		EmiIngredient sand = EmiIngredient.of(Ingredient.ofItems(Items.SUSPICIOUS_SAND, Items.SUSPICIOUS_GRAVEL));
		EmiIngredient brush = EmiIngredient.of(Ingredient.ofItems(Items.BRUSH));
		return Arrays.asList(sand, brush);
	}

	@Override
	public List<EmiStack> getOutputs() {
		return outputs;
	}

	@Override
	public int getDisplayWidth() {
		return 144;
	}

	@Override
	public int getDisplayHeight() {
		int titleHeight = 11;
		int boxesHeight = ((int) Math.ceil(lootStacksSortedSize / columns) * (EMILoot.config.chestLootCompact ? 18 : 19)) - 1;
		return titleHeight + boxesHeight;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		final int titleSpace;
		final int finalRowHeight;
		if(widgets.getHeight() < getDisplayHeight()) {
			titleSpace = 9;
			finalRowHeight = (widgets.getHeight() - titleSpace) / ((int) Math.ceil(lootStacksSortedSize / 8.0));
		} else {
			titleSpace = 11;
			finalRowHeight = 18;
		}

		widgets.addText(title.asOrderedText(), 1, 0, 0x404040, false);
		AtomicInteger index = new AtomicInteger(lootStacksSortedSize);
		for (var entry : lootStacksSorted.asMap().entrySet()) {
			float weight = entry.getKey();
			Collection<EmiStack> items = entry.getValue();
			if ((loot.items.size() <= 48) && !EMILoot.config.chestLootAlwaysStackSame) {
				for (EmiStack stack : items) {
					int row = (int) Math.ceil(index.get() / columns) - 1;
					int column= (index.get() - 1) % (int) columns;
					index.getAndDecrement();
					String fTrim = trimFloatString(weight);
					SlotWidget slotWidget = new SlotWidget(stack, column * 18, titleSpace + row * finalRowHeight).recipeContext(this);
					widgets.add(slotWidget.appendTooltip(Text.translatable("emi_loot.percentage", fTrim)));
				}
			} else {
				int row = (int) Math.ceil(index.get() / columns) - 1;
				int column = (int)((index.get() - 1) % columns);
				index.getAndDecrement();
				EmiIngredient ingredient = EmiIngredient.of(items.stream().toList());
				String fTrim = trimFloatString(Math.max(weight / 100f, 0.01f), 2);
				SlotWidget slotWidget = new SlotWidget(ingredient, column * 18, titleSpace + row * finalRowHeight).recipeContext(this);
				widgets.add(slotWidget.appendTooltip(Text.translatable("emi_loot.rolls", fTrim).formatted(Formatting.ITALIC, Formatting.GOLD)));
			}
		}
	}

	@Override
	public boolean supportsRecipeTree() {
		return EmiRecipe.super.supportsRecipeTree() && isGuaranteedNonChance;
	}
}