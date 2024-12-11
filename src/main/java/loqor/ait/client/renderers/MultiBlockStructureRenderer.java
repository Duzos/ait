package loqor.ait.client.renderers;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.BlockRenderView;

import loqor.ait.core.engine.block.multi.MultiBlockStructure;

public class MultiBlockStructureRenderer {
    private final MinecraftClient client;
    private final Profiler profiler;

    protected MultiBlockStructureRenderer(MinecraftClient client) {
        this.client = client;
        this.profiler = client.getProfiler();
    }
    private MultiBlockStructureRenderer() {
        this(MinecraftClient.getInstance());
    }

    public void render(MultiBlockStructure structure, BlockPos centre, BlockRenderView view, MatrixStack matrices, VertexConsumerProvider provider, boolean holographic) {
        profiler.push("multi_block_structure");
        profiler.push("iterate_offsets");
        structure.forEach(offset -> renderOffset(offset, centre, view, matrices, provider, holographic));
    }
    public void renderOffset(MultiBlockStructure.BlockOffset offset, BlockPos centre, BlockRenderView view, MatrixStack matrices, VertexConsumerProvider provider, boolean holographic) {
        BlockPos pos = centre.add(offset.offset());

        BlockPos diff = pos.subtract(centre);
        BlockState state = this.getBlock(offset.block(), client.getServer().getTicks()).getDefaultState();

        matrices.push();
        matrices.translate(diff.getX(), diff.getY(), diff.getZ());

        if (holographic) {
            matrices.scale(0.35f, 0.35f, 0.35f);
            matrices.translate(0.95, 1, 1);
        }

        renderBlock(state, pos, view, matrices, provider);
        matrices.pop();
    }
    private void renderBlock(BlockState state, BlockPos pos, BlockRenderView view, MatrixStack matrices, VertexConsumerProvider provider) {
        client.getBlockRenderManager().getModelRenderer().render(view, client.getBlockRenderManager().getModel(state), state, pos, matrices, provider.getBuffer(RenderLayers.getBlockLayer(state)), false, client.world.random, state.getRenderingSeed(pos), OverlayTexture.DEFAULT_UV);
    }
    private Block getBlock(MultiBlockStructure.AllowedBlocks block, int ticks) {
        return (Block) block.toArray()[0];
    }

    private static MultiBlockStructureRenderer INSTANCE;

    public static MultiBlockStructureRenderer instance() {
        if (INSTANCE == null) {
            INSTANCE = new MultiBlockStructureRenderer();
        }
        return INSTANCE;
    }
}
