package fr.theorozier.webstreamer.display.render;

import fr.theorozier.webstreamer.WebStreamerClientMod;
import fr.theorozier.webstreamer.display.DisplayBlockEntity;
import fr.theorozier.webstreamer.display.url.DisplayUrl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
public class DisplayBlockEntityRenderer implements BlockEntityRenderer<DisplayBlockEntity> {

    private final GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;

    @SuppressWarnings("unused")
    public DisplayBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    
    }

    @Override
    public void render(DisplayBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    
        DisplayRenderData renderData = (DisplayRenderData) entity.getRenderData();
        
        DisplayUrl url = renderData.getUrl();
        if (url != null) {
    
            DisplayLayer layer = WebStreamerClientMod.DISPLAY_LAYERS.forSource(url);

            if (layer != null) {

                matrices.push();

                MatrixStack.Entry entry = matrices.peek();
                Matrix4f positionMatrix = entry.getPositionMatrix();

                VertexConsumer buffer = vertexConsumers.getBuffer(layer);

                BlockPos pos = entity.getPos();
                float audioDistance = entity.getAudioDistance();
                float audioVolume = entity.getAudioVolume();
                layer.pushAudioSource(pos, pos.getManhattanDistance(this.gameRenderer.getCamera().getBlockPos()), audioDistance, audioVolume);

                // Width/Height start coords
                float ws = renderData.getWidthOffset();
                float hs = renderData.getHeightOffset();
                // Width/Height end coords
                float we = ws + entity.getWidth();
                float he = hs + entity.getHeight();

                switch (entity.getCachedState().get(Properties.HORIZONTAL_FACING)) {
                    case NORTH -> {
                        buffer.vertex(positionMatrix, we, hs, 0.95f).texture(0, 1).next();
                        buffer.vertex(positionMatrix, ws, hs, 0.95f).texture(1, 1).next();
                        buffer.vertex(positionMatrix, ws, he, 0.95f).texture(1, 0).next();
                        buffer.vertex(positionMatrix, we, he, 0.95f).texture(0, 0).next();
                    }
                    case SOUTH -> {
                        buffer.vertex(positionMatrix, ws, hs, 0.05f).texture(0, 1).next();
                        buffer.vertex(positionMatrix, we, hs, 0.05f).texture(1, 1).next();
                        buffer.vertex(positionMatrix, we, he, 0.05f).texture(1, 0).next();
                        buffer.vertex(positionMatrix, ws, he, 0.05f).texture(0, 0).next();
                    }
                    case EAST -> {
                        buffer.vertex(positionMatrix, 0.05f, hs, we).texture(0, 1).next();
                        buffer.vertex(positionMatrix, 0.05f, hs, ws).texture(1, 1).next();
                        buffer.vertex(positionMatrix, 0.05f, he, ws).texture(1, 0).next();
                        buffer.vertex(positionMatrix, 0.05f, he, we).texture(0, 0).next();
                    }
                    case WEST -> {
                        buffer.vertex(positionMatrix, 0.95f, hs, ws).texture(0, 1).next();
                        buffer.vertex(positionMatrix, 0.95f, hs, we).texture(1, 1).next();
                        buffer.vertex(positionMatrix, 0.95f, he, we).texture(1, 0).next();
                        buffer.vertex(positionMatrix, 0.95f, he, ws).texture(0, 0).next();
                    }
                }

                matrices.pop();

            }
    
        }

    }

}
