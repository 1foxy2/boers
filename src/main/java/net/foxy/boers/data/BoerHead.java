package net.foxy.boers.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.foxy.boers.base.ModDataComponents;
import net.foxy.boers.base.ModRegistries;
import net.foxy.boers.client.BoersClientConfig;
import net.foxy.boers.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
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

public record BoerHead(Texture texture, float defaultMiningSpeed, int durability, List<Rule> miningRules, Optional<Vec3i> radius) {
    public static final Codec<BoerHead> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Texture.CODEC.fieldOf("texture").forGetter(BoerHead::texture),
                    Codec.FLOAT.fieldOf("default_mining_speed").forGetter(BoerHead::defaultMiningSpeed),
                    Codec.INT.fieldOf("durability").forGetter(BoerHead::durability),
                    Rule.CODEC.listOf().fieldOf("mining_rules").forGetter(BoerHead::miningRules),
                    Vec3i.CODEC.optionalFieldOf("radius").forGetter(BoerHead::radius)
            ).apply(instance, BoerHead::new)
    );
    public static final Codec<Holder<BoerHead>> ITEM_CODEC = RegistryFixedCodec.create(ModRegistries.BOER_HEAD);

    //public static final StreamCodec<RegistryFriendlyByteBuf, Holder<BoerHead>> STREAM_CODEC = ByteBufCodecs.holderRegistry(ModRegistries.BOER_HEAD);

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

    public BoerHead(ResourceLocation texture, float miningSpeed, int durability, TagKey<Block> miningLevel) {
        this(new Texture(ResourceLocation.fromNamespaceAndPath(texture.getNamespace(),
                        texture.getPath() + "_idle"), texture), 1.0f, durability,
                List.of(Rule.deniesDrops(miningLevel),
                        Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, miningSpeed),
                        Rule.minesAndDrops(BlockTags.MINEABLE_WITH_SHOVEL, miningSpeed)
                ), Optional.empty());
    }

    public BoerHead(ResourceLocation texture, float miningSpeed, float maxSpeed, float speedPerTick, int durability, TagKey<Block> miningLevel) {
        this(new Texture(ResourceLocation.fromNamespaceAndPath(texture.getNamespace(),
                        texture.getPath() + "_idle"), texture), 1.0f, durability,
                List.of(Rule.deniesDrops(miningLevel),
                        Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, miningSpeed, maxSpeed, speedPerTick),
                        Rule.minesAndDrops(BlockTags.MINEABLE_WITH_SHOVEL, miningSpeed, maxSpeed, speedPerTick)
                ), Optional.empty());
    }

    public BoerHead(ResourceLocation texture, float miningSpeed, float maxSpeed, float speedPerTick, int durability, TagKey<Block> miningLevel, Vec3i radius) {
        this(new Texture(ResourceLocation.fromNamespaceAndPath(texture.getNamespace(),
                        texture.getPath() + "_idle"), texture), 1.0f, durability,
                List.of(Rule.deniesDrops(miningLevel),
                        Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, miningSpeed, maxSpeed, speedPerTick),
                        Rule.minesAndDrops(BlockTags.MINEABLE_WITH_SHOVEL, miningSpeed, maxSpeed, speedPerTick)
                ), Optional.of(radius));
    }

    public int getMaxAcceleration(ItemStack stack) {
        int max = 0;
        for (Rule tool$rule : miningRules) {
            if (tool$rule.speed().isPresent()) {
                if (tool$rule.maxSpeed.isPresent() && tool$rule.speedPerTick.isPresent()) {
                    float maxSpeed = tool$rule.maxSpeed.get() - tool$rule.speed.get();
                    int m = (int) Mth.lerp(Math.min(tool$rule.speedPerTick.get() * Utils.getUsedFor(stack), maxSpeed) / maxSpeed, 0, BoersClientConfig.CONFIG.MAX_BOER_HEATING.getAsInt());
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

        public static Rule minesAndDrops(TagKey<Block> blocks, float speed) {
            return forTag(blocks, Optional.of(speed), Optional.empty(), Optional.empty(), Optional.of(true), Optional.empty());
        }

        public static Rule minesAndDrops(TagKey<Block> blocks, float speed, float maxSpeed, float speedPerTick) {
            return forTag(blocks, Optional.of(speed), Optional.of(maxSpeed), Optional.of(speedPerTick), Optional.of(true), Optional.empty());
        }

        public static Rule deniesDrops(TagKey<Block> blocks) {
            return forTag(blocks, Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(false), Optional.empty());
        }

        public static Rule overrideSpeed(TagKey<Block> blocks, float speed) {
            return forTag(blocks, Optional.of(speed), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        }

        public static Rule overrideSpeed(List<Block> blocks, float speed) {
            return forBlocks(blocks, Optional.of(speed), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        }

        private static Rule forTag(TagKey<Block> tag, Optional<Float> speed, Optional<Float> maxSpeed, Optional<Float> maxSpeedPerTick, Optional<Boolean> correctForDrops, Optional<Integer> damagePerBlock) {
            return new Rule(BuiltInRegistries.BLOCK.getOrCreateTag(tag), speed, maxSpeed, maxSpeedPerTick, correctForDrops, damagePerBlock);
        }

        private static Rule forBlocks(List<Block> blocks, Optional<Float> speed, Optional<Float> maxSpeed, Optional<Float> maxSpeedPerTick, Optional<Boolean> correctForDrops, Optional<Integer> damagePerBlock) {
            return new Rule(HolderSet.direct(blocks.stream().map(Block::builtInRegistryHolder).collect(Collectors.toList())), speed, maxSpeed, maxSpeedPerTick, correctForDrops, damagePerBlock);
        }
    }
}
