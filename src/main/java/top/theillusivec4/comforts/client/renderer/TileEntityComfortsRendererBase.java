/*
 * Copyright (C) 2017-2019  C4
 *
 * This file is part of Comforts, a mod made for Minecraft.
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.comforts.client.renderer;

import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.comforts.Comforts;
import top.theillusivec4.comforts.client.model.ModelComfortsBase;
import top.theillusivec4.comforts.common.tileentity.TileEntityComfortsBase;

import java.util.Arrays;
import java.util.Comparator;

public abstract class TileEntityComfortsRendererBase<T extends TileEntityComfortsBase> extends TileEntityRenderer<T> {

    private final ResourceLocation[] textures;
    private final ModelComfortsBase model;
    private final float height;

    public TileEntityComfortsRendererBase(String textureName, ModelComfortsBase model, float height) {
        this.textures = Arrays
                .stream(EnumDyeColor.values())
                .sorted(Comparator.comparingInt(EnumDyeColor::getId))
                .map((color) -> new ResourceLocation(Comforts.MODID, "textures/entity/" + textureName + "/" + color.getTranslationKey() + ".png"))
                .toArray(ResourceLocation[]::new);
        this.model = model;
        this.height = height;
    }

    @Override
    public void render(T tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {

        if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4.0F, 4.0F, 1.0F);
            GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        } else {
            ResourceLocation resourcelocation = textures[tileEntityIn.getColor().getId()];

            if (resourcelocation != null) {
                this.bindTexture(resourcelocation);
            }
        }

        if (tileEntityIn.hasWorld()) {
            IBlockState iblockstate = tileEntityIn.getBlockState();
            this.func_199343_a(iblockstate.get(BlockBed.PART) == BedPart.HEAD, x, y, z, iblockstate.get(BlockBed.HORIZONTAL_FACING));
        } else {
            this.func_199343_a(true, x, y, z, EnumFacing.SOUTH);
            this.func_199343_a(false, x, y, z - 1.0D, EnumFacing.SOUTH);
        }

        if (destroyStage >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }

    }

    private void func_199343_a(boolean p_199343_1_, double p_199343_2_, double p_199343_4_, double p_199343_6_, EnumFacing p_199343_8_) {
        this.model.preparePiece(p_199343_1_);
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)p_199343_2_, (float)p_199343_4_ + height, (float)p_199343_6_);
        GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translatef(0.5F, 0.5F, 0.5F);
        GlStateManager.rotatef(180.0F + p_199343_8_.getHorizontalAngle(), 0.0F, 0.0F, 1.0F);
        GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
        GlStateManager.enableRescaleNormal();
        this.model.render();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
