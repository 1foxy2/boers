package net.foxy.drills.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.foxy.drills.event.ModEvents;
import net.foxy.drills.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public record DrillHead(ResourceLocation id, Texture texture, float defaultMiningSpeed, int durability, List<Tool.Rule> miningRules) {
    public static final DrillHead DEFAULT = new DrillHead(Utils.rl("default"), Utils.rl("item/drill/default_drill_head"), 1, 200, BlockTags.NEEDS_IRON_TOOL);

    public static final Codec<DrillHead> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.optionalFieldOf("id").forGetter((t) -> Optional.ofNullable(t.id)),
                    Texture.CODEC.fieldOf("texture").forGetter(DrillHead::texture),
                    Codec.FLOAT.fieldOf("default_mining_speed").forGetter(DrillHead::defaultMiningSpeed),
                    Codec.INT.fieldOf("durability").forGetter(DrillHead::durability),
                    Tool.Rule.CODEC.listOf().fieldOf("mining_rules").forGetter(DrillHead::miningRules)
            ).apply(instance, (id1, texture1, miningSpeed1, durability1, miningLevel) ->
                    new DrillHead(id1.orElseGet(() -> Utils.rl("")),
                            texture1, miningSpeed1, durability1, miningLevel))
    );
    public static final Codec<Optional<net.neoforged.neoforge.common.conditions.WithConditions<DrillHead>>> CONDITIONAL_CODEC = net.neoforged.neoforge.common.conditions.ConditionalOps.createConditionalCodecWithConditions(CODEC);

    public static final Codec<DrillHead> ITEM_CODEC =
            ResourceLocation.CODEC.xmap(ModEvents.DRILL_MANAGER::getDrillHead, DrillHead::id);

    public static final StreamCodec<RegistryFriendlyByteBuf, DrillHead> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            DrillHead::id,
            Texture.STREAM_CODEC,
            DrillHead::texture,
            ByteBufCodecs.FLOAT,
            DrillHead::defaultMiningSpeed,
            ByteBufCodecs.VAR_INT,
            DrillHead::durability,
            Tool.Rule.STREAM_CODEC.apply(ByteBufCodecs.list()),
            DrillHead::miningRules,
            DrillHead::new
    );

    public float getMiningSpeed(BlockState state) {
        for (Tool.Rule tool$rule : miningRules) {
            if (tool$rule.speed().isPresent() && state.is(tool$rule.blocks())) {
                return tool$rule.speed().get();
            }
        }

        return this.defaultMiningSpeed;
    }

    public boolean isCorrectForDrops(BlockState state) {
        for (Tool.Rule tool$rule : this.miningRules) {
            if (tool$rule.correctForDrops().isPresent() && state.is(tool$rule.blocks())) {
                return tool$rule.correctForDrops().get();
            }
        }

        return false;
    }

    public DrillHead(ResourceLocation texture, float miningSpeed, int durability, TagKey<Block> miningLevel) {
        this(null, texture, miningSpeed, durability, miningLevel);
    }
    public DrillHead(ResourceLocation id, ResourceLocation texture, float miningSpeed, int durability, TagKey<Block> miningLevel) {
        this(id, new Texture(ResourceLocation.fromNamespaceAndPath(texture.getNamespace(), texture.getPath() + "_idle"), texture) , 1.0f, durability, List.of(Tool.Rule.deniesDrops(miningLevel), Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, miningSpeed)));
    }

    // TagKey.codec(Registries.BLOCK);

    public record Texture(ResourceLocation idle, ResourceLocation active) {
        public static StreamCodec<FriendlyByteBuf, Texture> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC,
                Texture::idle,
                ResourceLocation.STREAM_CODEC,
                Texture::active,
                Texture::new
        );

        public static final Codec<Texture> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ResourceLocation.CODEC.fieldOf("idle").forGetter(Texture::idle),
                        ResourceLocation.CODEC.fieldOf("active").forGetter(Texture::active)
                ).apply(instance, Texture::new));
    }
}
