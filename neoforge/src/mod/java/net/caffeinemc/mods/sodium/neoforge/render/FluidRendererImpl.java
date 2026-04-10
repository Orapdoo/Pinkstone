package net.caffeinemc.mods.sodium.neoforge.render;

import net.caffeinemc.mods.sodium.client.model.color.ColorProvider;
import net.caffeinemc.mods.sodium.client.model.color.ColorProviderRegistry;
import net.caffeinemc.mods.sodium.client.model.light.LightPipelineProvider;
import net.caffeinemc.mods.sodium.client.model.quad.blender.BlendedColorProvider;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.DefaultFluidRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.mods.sodium.client.services.FluidRendererFactory;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jspecify.annotations.Nullable;

public class FluidRendererImpl extends FluidRenderer {
    private final ColorProviderRegistry colorProviderRegistry;
    private final DefaultFluidRenderer defaultRenderer;

    public FluidRendererImpl(ColorProviderRegistry colorProviderRegistry, LightPipelineProvider lighters) {
        this.colorProviderRegistry = colorProviderRegistry;
        this.defaultRenderer = new DefaultFluidRenderer(lighters);
    }

    @Override
    public void render(LevelSlice level, BlockState blockState, FluidState fluidState, BlockPos blockPos, BlockPos offset, TranslucentGeometryCollector collector, ChunkBuildBuffers buffers) {
        var material = DefaultMaterials.forFluidState(fluidState);
        var meshBuilder = buffers.get(material);
        IClientFluidTypeExtensions handler = IClientFluidTypeExtensions.of(fluidState);
        var fallbackConsumer = meshBuilder.asFallbackVertexConsumer(material, collector);

        if (handler.renderFluid(fluidState, level, blockPos, fallbackConsumer, blockState)) {
            return;
        }

        this.defaultRenderer.render(level, blockState, fluidState, blockPos, offset, collector, meshBuilder, material,
                getColorProvider(fluidState.getType(), handler), getFluidSprites(handler, level, blockPos, fluidState));
    }

    private ColorProvider<FluidState> getColorProvider(Fluid fluid, IClientFluidTypeExtensions handler) {
        var override = this.colorProviderRegistry.getColorProvider(fluid);

        if (override != null) {
            return override;
        }

        return ForgeColorProviders.adapt(handler);
    }

    private TextureAtlasSprite[] getFluidSprites(IClientFluidTypeExtensions handler, LevelSlice level, BlockPos pos, FluidState state) {
        TextureAtlas atlas = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS);
        TextureAtlasSprite missingSprite = atlas.missingSprite();

        TextureAtlasSprite stillSprite = getSprite(atlas, handler.getStillTexture(state, level, pos), missingSprite);
        TextureAtlasSprite flowingSprite = getSprite(atlas, handler.getFlowingTexture(state, level, pos), stillSprite);
        TextureAtlasSprite overlaySprite = getSprite(atlas, handler.getOverlayTexture(state, level, pos), stillSprite);

        return new TextureAtlasSprite[] { stillSprite, flowingSprite, overlaySprite };
    }

    private static TextureAtlasSprite getSprite(TextureAtlas atlas, @Nullable Identifier textureId, TextureAtlasSprite fallback) {
        if (textureId == null) {
            return fallback;
        }

        TextureAtlasSprite sprite = atlas.getSprite(textureId);
        return sprite != null ? sprite : fallback;
    }

    public static class ForgeFactory implements FluidRendererFactory {
        @Override
        public FluidRenderer createPlatformFluidRenderer(ColorProviderRegistry colorRegistry, LightPipelineProvider lightPipelineProvider) {
            return new FluidRendererImpl(colorRegistry, lightPipelineProvider);
        }

        @Override
        public BlendedColorProvider<FluidState> getWaterColorProvider() {
            return new BlendedColorProvider<>() {
                @Override
                protected int getColor(LevelSlice slice, FluidState state, BlockPos pos) {
                    return IClientFluidTypeExtensions.of(state).getTintColor(state, slice, pos);
                }
            };
        }

        @Override
        public BlendedColorProvider<BlockState> getWaterBlockColorProvider() {
            return new BlendedColorProvider<>() {
                @Override
                protected int getColor(LevelSlice slice, BlockState state, BlockPos pos) {
                    FluidState fluidState = state.getFluidState().isEmpty() ? Fluids.WATER.defaultFluidState() : state.getFluidState();
                    return IClientFluidTypeExtensions.of(fluidState).getTintColor(fluidState, slice, pos);
                }
            };
        }
    }
}
