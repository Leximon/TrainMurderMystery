package dev.doctor4t.trainmurdermystery.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.trainmurdermystery.client.util.AlwaysVisibleFrustum;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "method_52816", at = @At(value = "RETURN"), cancellable = true)
    private static void method_52816(Frustum frustum, CallbackInfoReturnable<Frustum> cir) {
        cir.setReturnValue(new AlwaysVisibleFrustum(frustum));
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;setupTerrain(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/Frustum;ZZ)V"))
    public void render(WorldRenderer instance, Camera camera, Frustum frustum, boolean hasForcedFrustum, boolean spectator, Operation<Void> original) {
        original.call(instance, camera, frustum, hasForcedFrustum, spectator);
    }

    @Inject(method = "renderLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/ShaderProgram;bind()V", shift = At.Shift.AFTER), cancellable = true)
    private void render(RenderLayer renderLayer, double x, double y, double z, Matrix4f matrix4f, Matrix4f positionMatrix, CallbackInfo ci, @Local(name = "bl2") boolean bl2, @Local ObjectListIterator<ChunkBuilder.BuiltChunk> objectListIterator, @Local ShaderProgram shaderProgram) {
        GlUniform glUniform = shaderProgram.chunkOffset;

        while (bl2 ? objectListIterator.hasNext() : objectListIterator.hasPrevious()) {
            boolean tooFar = false;

            ChunkBuilder.BuiltChunk builtChunk2 = bl2 ? objectListIterator.next() : objectListIterator.previous();
            if (!builtChunk2.getData().isEmpty(renderLayer)) {
                VertexBuffer vertexBuffer = builtChunk2.getBuffer(renderLayer);
                BlockPos blockPos = builtChunk2.getOrigin();

                if (glUniform != null) {
                    boolean trainSection = ChunkSectionPos.getSectionCoord(blockPos.getY()) >= 4;
                    float trainSpeed = 130; // in kmh

                    MinecraftClient client = MinecraftClient.getInstance();
                    ChunkPos chunkPos = new ChunkPos(client.cameraEntity.getBlockPos());
                    client.chunkCullingEnabled = false;

                    float v1 = (float) ((double) blockPos.getX() - x);
                    float v2 = (float) ((double) blockPos.getY() - y);
                    float v3 = (float) ((double) blockPos.getZ() - z);

                    int chunkSize = 16;
                    int zSection = blockPos.getZ() / chunkSize - chunkPos.z;
                    int tileWidth = 15 * chunkSize;
                    int height = 61;
                    int tileLength = 32 * chunkSize;
                    int tileSize = tileLength * 3;

                    int time = client.player.age; // TODO: replace with a proper time

                    float finalX = v1;
                    float finalY = v2;
                    float finalZ = v3;

                    if (zSection <= -8) {
                        finalX = ((v1 - tileLength + ((time + client.getRenderTickCounter().getTickDelta(true)) / 73.8f * trainSpeed)) % tileSize - tileSize / 2f);
                        finalY = (v2 + height);
                        finalZ = v3 + tileWidth;
                    } else if (zSection >= 8) {
                        finalX = ((v1 + tileLength + ((time + client.getRenderTickCounter().getTickDelta(true)) / 73.8f * trainSpeed)) % tileSize - tileSize / 2f);
                        finalY = (v2 + height);
                        finalZ = v3 - tileWidth;
                    } else if (!trainSection) {
                        finalX = ((v1 + ((time + client.getRenderTickCounter().getTickDelta(true)) / 73.8f * trainSpeed)) % tileSize - tileSize / 2f);
                        finalY = (v2 + height);
                        finalZ = v3;
                    }

                    if (Math.abs(finalX) < 256) {
                        glUniform.set(
                                finalX,
                                finalY,
                                finalZ
                        );
                        glUniform.upload();
                    } else {
                        tooFar = true;
                    }
                }

                if (!tooFar) {
                    vertexBuffer.bind();
                    vertexBuffer.draw();
                }
            }
        }

        if (glUniform != null) {
            glUniform.set(0.0F, 0.0F, 0.0F);
        }

        shaderProgram.unbind();
        VertexBuffer.unbind();
        this.client.getProfiler().pop();
        renderLayer.endDrawing();

        ci.cancel();
    }
}