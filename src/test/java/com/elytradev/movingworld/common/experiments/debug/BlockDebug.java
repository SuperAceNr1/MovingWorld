package com.elytradev.movingworld.common.experiments.debug;

import com.elytradev.movingworld.common.experiments.entity.EntityMobileRegion;
import com.elytradev.movingworld.common.experiments.newassembly.WorldReader;
import com.elytradev.movingworld.common.experiments.region.MobileRegion;
import com.elytradev.movingworld.common.experiments.region.RegionPool;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDebug extends Block {
    public BlockDebug(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
        setRegistryName("movingworld-experiments", "debug");
        setUnlocalizedName("debug");
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        RegionPool pool = RegionPool.getPool(worldIn.provider.getDimension(), false);

        if (worldIn == null
                || worldIn.isRemote
                || RegionPool.getPool(worldIn.provider.getDimension(), false) != null)
            return false;

        WorldReader reader = new WorldReader(pos, worldIn);

        reader.readAll();
        reader.cloneToSubworld();
        reader.cleanRealWorld();
        MobileRegion readerRegion = reader.out.getRegion();

        BlockPos spawnAt = new BlockPos(reader.min.getX() + ((reader.max.getX() - reader.min.getX()) / 2), 0,
                reader.min.getZ() + ((reader.max.getZ() - reader.min.getZ()) / 2));

        readerRegion.x = spawnAt.getX();
        readerRegion.y = 0;
        readerRegion.z = spawnAt.getZ();

        BlockPos shiftedMin = readerRegion.convertRegionPosToRealWorld(readerRegion.sizeMin);
        BlockPos shiftedMax = readerRegion.convertRegionPosToRealWorld(readerRegion.sizeMax);

        EntityMobileRegion entityMobileRegion = new EntityMobileRegion(worldIn, readerRegion, new AxisAlignedBB(shiftedMin.getX(), shiftedMin.getY(), shiftedMin.getZ(), shiftedMax.getX(), shiftedMax.getY(), shiftedMax.getZ()));
        entityMobileRegion.setPosition(spawnAt.getX(), 0, spawnAt.getZ());
        worldIn.spawnEntity(entityMobileRegion);

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }
}
