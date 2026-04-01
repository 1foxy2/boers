package net.foxy.bores.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.foxy.bores.base.ModRegistries;
import net.foxy.bores.client.BoresClientConfig;
import net.foxy.bores.util.FixerRegistryFixedCodec;
import net.foxy.bores.util.Utils;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record BoreHead(Texture texture, float defaultMiningSpeed, int durability, List<Rule> miningRules, Optional<Vec3i> radius) {
    public static final Codec<BoreHead> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Texture.CODEC.fieldOf("texture").forGetter(BoreHead::texture),
                    Codec.FLOAT.fieldOf("default_mining_speed").forGetter(BoreHead::defaultMiningSpeed),
                    Codec.INT.fieldOf("durability").forGetter(BoreHead::durability),
                    Rule.CODEC.listOf().fieldOf("mining_rules").forGetter(BoreHead::miningRules),
                    Vec3i.CODEC.optionalFieldOf("radius").forGetter(BoreHead::radius)
            ).apply(instance, BoreHead::new)
    );
    public static final Codec<Holder<BoreHead>> ITEM_CODEC = FixerRegistryFixedCodec.create(ModRegistries.BORE_HEAD);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<BoreHead>> STREAM_CODEC = ByteBufCodecs.holderRegistry(ModRegistries.BORE_HEAD);

    public float getMiningSpeed(ItemStack stack, BlockState state) {
        for (Rule tool$rule : miningRules) {
            if (tool$rule.speed().isPresent() && state.is(tool$rule.blocks())) {
                if (tool$rule.maxSpeed.isPresent() && tool$rule.speedPerTick.isPresent()) {
                    return (Math.min(tool$rule.speed.get() + tool$rule.speedPerTick.get() * Utils.getUsedFor(stack), tool$rule.maxSpeed.get()));
                }
                return tool$rule.speed().get();
            }
        }

        return this.defaultMiningSpeed;
    }

    public boolean isCorrectForDrops(BlockState state) {
        for (Rule tool$rule : this.miningRules) {
            if (tool$rule.correctForDrops().isPresent() && state.is(tool$rule.blocks())) {
                return tool$rule.correctForDrops().get();
            }
        }

        return false;
    }

    public int getDamage(BlockState state) {
        for (Rule tool$rule : this.miningRules) {
            if (tool$rule.damagePerBlock().isPresent() && state.is(tool$rule.blocks())) {
                return tool$rule.damagePerBlock().get();
            }
        }

        return 1;
    }

    public BoreHead(ResourceLocation texture, float miningSpeed, int durability, HolderGetter<Block> blockGetter, TagKey<Block> miningLevel) {
        this(new Texture(ResourceLocation.fromNamespaceAndPath(texture.getNamespace(),
                        texture.getPath() + "_idle"), texture), 1.0f, durability,
                List.of(Rule.deniesDrops(blockGetter, miningLevel),
                        Rule.minesAndDrops(blockGetter, BlockTags.MINEABLE_WITH_PICKAXE, miningSpeed),
                        Rule.minesAndDrops(blockGetter, BlockTags.MINEABLE_WITH_SHOVEL, miningSpeed)
                ), Optional.empty());
    }

    public BoreHead(ResourceLocation texture, float miningSpeed, float maxSpeed, float speedPerTick, int durability, HolderGetter<Block> blockGetter, TagKey<Block> miningLevel) {
        this(new Texture(ResourceLocation.fromNamespaceAndPath(texture.getNamespace(),
                        texture.getPath() + "_idle"), texture), 1.0f, durability,
                List.of(Rule.deniesDrops(blockGetter, miningLevel),
                        Rule.minesAndDrops(blockGetter, BlockTags.MINEABLE_WITH_PICKAXE, miningSpeed, maxSpeed, speedPerTick),
                        Rule.minesAndDrops(blockGetter, BlockTags.MINEABLE_WITH_SHOVEL, miningSpeed, maxSpeed, speedPerTick)
                ), Optional.empty());
    }

    public BoreHead(ResourceLocation texture, float miningSpeed, float maxSpeed, float speedPerTick, int durability, HolderGetter<Block> blockGetter, TagKey<Block> miningLevel, Vec3i radius) {
        this(new Texture(ResourceLocation.fromNamespaceAndPath(texture.getNamespace(),
                        texture.getPath() + "_idle"), texture), 1.0f, durability,
                List.of(Rule.deniesDrops(blockGetter, miningLevel),
                        Rule.minesAndDrops(blockGetter, BlockTags.MINEABLE_WITH_PICKAXE, miningSpeed, maxSpeed, speedPerTick),
                        Rule.minesAndDrops(blockGetter, BlockTags.MINEABLE_WITH_SHOVEL, miningSpeed, maxSpeed, speedPerTick)
                ), Optional.of(radius));
    }

    public int getMaxAcceleration(ItemStack stack) {
        int max = 0;
        for (Rule tool$rule : miningRules) {
            if (tool$rule.speed().isPresent()) {
                if (tool$rule.maxSpeed.isPresent() && tool$rule.speedPerTick.isPresent()) {
                    float maxSpeed = tool$rule.maxSpeed.get() - tool$rule.speed.get();
                    int m = (int) Mth.lerp(Math.min(tool$rule.speedPerTick.get() * Utils.getUsedFor(stack), maxSpeed) / maxSpeed, 0, BoresClientConfig.CONFIG.MAX_BORE_HEATING.getAsInt());
                    if (m > max) {
                        max = m;
                    }
                }
            }
        }

        return max;
    }

    public record Texture(ResourceLocation idle, ResourceLocation active) {
        public static final Codec<Texture> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ResourceLocation.CODEC.fieldOf("idle").forGetter(Texture::idle),
                        ResourceLocation.CODEC.fieldOf("active").forGetter(Texture::active)
                ).apply(instance, Texture::new));
    }


    public record Rule(HolderSet<Block> blocks, Optional<Float> speed, Optional<Float> maxSpeed, Optional<Float> speedPerTick, Optional<Boolean> correctForDrops, Optional<Integer> damagePerBlock) {
        public static final Codec<Rule> CODEC = RecordCodecBuilder.create(
                p_337954_ -> p_337954_.group(
                                RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter(Rule::blocks),
                                ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("speed").forGetter(Rule::speed),
                                ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("max_speed").forGetter(Rule::maxSpeed),
                                ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("speed_per_tick").forGetter(Rule::speedPerTick),
                                Codec.BOOL.optionalFieldOf("correct_for_drops").forGetter(Rule::correctForDrops),
                                Codec.INT.optionalFieldOf("damage_per_block").forGetter(Rule::damagePerBlock)
                        )
                        .apply(p_337954_, Rule::new)
        );

        public static Rule minesAndDrops(List<Block> blocks, float speed) {
            return forBlocks(blocks, Optional.of(speed), Optional.empty(), Optional.empty(), Optional.of(true), Optional.empty());
        }

        public static Rule minesAndDrops(HolderGetter<Block> blockGetter, TagKey<Block> blocks, float speed) {
            return forTag(blockGetter, blocks, Optional.of(speed), Optional.empty(), Optional.empty(), Optional.of(true), Optional.empty());
        }

        public static Rule minesAndDrops(HolderGetter<Block> blockGetter, TagKey<Block> blocks, float speed, float maxSpeed, float speedPerTick) {
            return forTag(blockGetter, blocks, Optional.of(speed), Optional.of(maxSpeed), Optional.of(speedPerTick), Optional.of(true), Optional.empty());
        }

        public static Rule deniesDrops(HolderGetter<Block> blockGetter, TagKey<Block> blocks) {
            return forTag(blockGetter, blocks, Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(false), Optional.empty());
        }

        public static Rule overrideSpeed(HolderGetter<Block> blockGetter, TagKey<Block> blocks, float speed) {
            return forTag(blockGetter, blocks, Optional.of(speed), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        }

        public static Rule overrideSpeed(List<Block> blocks, float speed) {
            return forBlocks(blocks, Optional.of(speed), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        }

        private static Rule forTag(HolderGetter<Block> blockGetter, TagKey<Block> tag, Optional<Float> speed, Optional<Float> maxSpeed, Optional<Float> maxSpeedPerTick, Optional<Boolean> correctForDrops, Optional<Integer> damagePerBlock) {
            return new Rule(blockGetter.getOrThrow(tag), speed, maxSpeed, maxSpeedPerTick, correctForDrops, damagePerBlock);
        }

        private static Rule forBlocks(List<Block> blocks, Optional<Float> speed, Optional<Float> maxSpeed, Optional<Float> maxSpeedPerTick, Optional<Boolean> correctForDrops, Optional<Integer> damagePerBlock) {
            return new Rule(HolderSet.direct(blocks.stream().map(Block::builtInRegistryHolder).collect(Collectors.toList())), speed, maxSpeed, maxSpeedPerTick, correctForDrops, damagePerBlock);
        }
    }
}
