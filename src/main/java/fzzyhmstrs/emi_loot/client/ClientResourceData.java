package fzzyhmstrs.emi_loot.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.cleancode.Identifier;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = EMILoot.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientResourceData {

    public static final Object2IntMap<EntityType<?>> MOB_OFFSETS = new Object2IntOpenHashMap<>();
    public static final Object2FloatMap<EntityType<?>> MOB_SCALES = new Object2FloatOpenHashMap<>();
    public static final Map<EntityType<?>, Vector3f> MOB_ROTATIONS = new HashMap<>();

    @SubscribeEvent
    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event){
        event.registerReloadListener(new EntityOffsetsReloadListener());
    }


        private static class EntityOffsetsReloadListener implements ResourceManagerReloadListener {

        @Override
        public void onResourceManagerReload(ResourceManager manager) {
            MOB_OFFSETS.clear();
            MOB_SCALES.clear();
            MOB_ROTATIONS.clear();
            manager.listResources("entity_fixers",path -> path.getPath().endsWith(".json")).forEach(this::load);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info(MOB_OFFSETS.toString());
            if (EMILoot.DEBUG) EMILoot.LOGGER.info(MOB_ROTATIONS.toString());
            if (EMILoot.DEBUG) EMILoot.LOGGER.info(MOB_SCALES.toString());
        }

        private void load(ResourceLocation fileId, Resource resource){
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("Reading entity fixers from file: " + fileId.toString());
            try {
                BufferedReader reader = resource.openAsReader();
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                json.entrySet().forEach((entry)->{
                    JsonElement element = entry.getValue();
                    ResourceLocation mobId = Identifier.of(entry.getKey());
                    if (BuiltInRegistries.ENTITY_TYPE.containsKey(mobId)) {
                        if (element.isJsonObject()) {
                            JsonObject object = element.getAsJsonObject();
                            JsonElement offset = object.get("offset");
                            if (offset != null && offset.isJsonPrimitive()) {

                                MOB_OFFSETS.put(BuiltInRegistries.ENTITY_TYPE.get(mobId), offset.getAsInt());

                            }
                            JsonElement scaling = object.get("scale");
                            if (scaling != null && scaling.isJsonPrimitive()) {
                                    MOB_SCALES.put(BuiltInRegistries.ENTITY_TYPE.get(mobId), scaling.getAsFloat());
                            }
                            float x = 0f;
                            float y = 0f;
                            float z = 0f;
                            JsonElement xEl = object.get("x");
                            if (xEl != null && xEl.isJsonPrimitive()) {
                                x = xEl.getAsFloat();
                            }
                            JsonElement yEl = object.get("y");
                            if (yEl != null && yEl.isJsonPrimitive()) {
                                y = yEl.getAsFloat();
                            }
                            JsonElement zEl = object.get("z");
                            if (zEl != null && zEl.isJsonPrimitive()) {
                                z = zEl.getAsFloat();
                            }
                            if (x != 0f || y != 0f || z != 0f) {
                                MOB_ROTATIONS.put(BuiltInRegistries.ENTITY_TYPE.get(mobId), new Vector3f(x, y, z));
                            }

                        } else {
                            throw new IllegalArgumentException("Element in mobfixer " + fileId + "not properly formatted");
                        }
                    }
                });
            } catch (Exception e){
                EMILoot.LOGGER.error("Failed to open or read Entity Offsets file: " + fileId);
            }
        }
    }

}
