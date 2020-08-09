package com.tridevmc.movingworld.common.chunk.mobilechunk.world;

import com.tridevmc.movingworld.common.chunk.mobilechunk.FakeChunk;
import com.tridevmc.movingworld.common.chunk.mobilechunk.MobileChunk;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.EmptyTickList;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A wrapper for MobileChunks, used to give blocks accurate information about it's neighbors.
 */
public class FakeWorld extends World {

    private MobileChunk mobileChunk;

    private FakeWorld(boolean remote, World parentWorld) {
        super((ISpawnWorldInfo) parentWorld.getWorldInfo(), parentWorld.func_234923_W_(), parentWorld.func_234922_V_(), parentWorld.func_230315_m_(), parentWorld::getProfiler, remote, parentWorld.func_234925_Z_(), 0); // no way to get the seed for some reason. very cool update.
    }

    public static FakeWorld getFakeWorld(MobileChunk chunk) {
        FakeWorld retVal = new FakeWorld(chunk.world.isRemote, chunk.world);
        retVal.setMobileChunk(chunk);
        return retVal;
    }

    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return this.getMobileChunk().getTileEntity(pos);
    }


    @Override
    public BlockState getBlockState(BlockPos pos) {
        return this.getMobileChunk().getBlockState(pos);
    }

    @Override
    public void playSound(@Nullable PlayerEntity player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
        Vector3d at = mobileChunk.getWorldPosForChunkPos(new Vector3d(x, y, z));
        mobileChunk.world.playSound(player, at.x, at.y, at.z, soundIn, category, volume, pitch);
    }

    @Override
    public void playMovingSound(@Nullable PlayerEntity p_217384_1_, Entity p_217384_2_, SoundEvent p_217384_3_, SoundCategory p_217384_4_, float p_217384_5_, float p_217384_6_) {
        mobileChunk.world.playMovingSound(p_217384_1_, p_217384_2_, p_217384_3_, p_217384_4_, p_217384_5_, p_217384_6_);
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state) {
        return this.getMobileChunk().setBlockState(pos, state);
    }

    @Override
    public void notifyBlockUpdate(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
        return this.getMobileChunk().setBlockState(pos, state, flags);
    }

    @Override
    public void setTileEntity(BlockPos pos, @Nullable TileEntity tileEntityIn) {
        this.getMobileChunk().setTileEntity(pos, tileEntityIn);
    }

    @Nullable
    @Override
    public Entity getEntityByID(int id) {
        return null;
    }

    @Override
    public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) {
        this.getMobileChunk().setChunkModified();
        if (this.getMobileChunk().side() == LogicalSide.SERVER) {
            this.getMobileChunk().markTileDirty(pos);
        }
    }

    private boolean isValidPosition(BlockPos pos) {
        return pos.getX() >= this.getMobileChunk().minX() && pos.getZ() >= this.getMobileChunk().minZ() && pos.getX() < this.getMobileChunk().maxX() && pos.getZ() < this.getMobileChunk().maxZ() && pos.getY() >= 0 && pos.getY() < this.getMobileChunk().maxY();
    }

    @Override
    public float getBrightness(BlockPos pos) {
        return this.getBlockState(pos).getLightValue(this.getMobileChunk(), pos);
    }

    @Override
    public long getGameTime() {
        return this.getParentWorld().getGameTime();
    }

    @Override
    public long getDayTime() {
        return this.getParentWorld().getDayTime();
    }

    @Override
    public int getNextMapId() {
        return mobileChunk.world.getNextMapId();
    }

    @Override
    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
        // NO-OP
    }

    @Override
    public Scoreboard getScoreboard() {
        return this.getParentWorld().getScoreboard();
    }

    @Override
    public RecipeManager getRecipeManager() {
        return this.getParentWorld().getRecipeManager();
    }

    @Override
    public NetworkTagManager getTags() {
        return this.getParentWorld().getTags();
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ) {
        return new FakeChunk(this.getMobileChunk(), new ChunkPos(chunkX, chunkZ), new Biome[]{this.mobileChunk.getCreationSpotBiome()});
    }

    public MobileChunk getMobileChunk() {
        return this.mobileChunk;
    }

    public void setMobileChunk(MobileChunk mobileChunk) {
        this.mobileChunk = mobileChunk;
    }

    public World getParentWorld() {
        return this.getMobileChunk().getWorld();
    }

    @Override
    public ITickList<Block> getPendingBlockTicks() {
        return new EmptyTickList<>();
    }

    @Override
    public ITickList<Fluid> getPendingFluidTicks() {
        return new EmptyTickList<>();
    }

    @Override
    public void playEvent(@Nullable PlayerEntity player, int type, BlockPos pos, int data) {
        // TODO: Offset and play in real world.
    }

    @Override
    public List<? extends PlayerEntity> getPlayers() {
        return this.mobileChunk.world.getPlayers();
    }

    @Override
    public MapData getMapData(String mapName) {
        return mobileChunk.world.getMapData(mapName);
    }

    @Override
    public void registerMapData(MapData mapDataIn) {
        mobileChunk.world.registerMapData(mapDataIn);
    }

    @Override
    public AbstractChunkProvider getChunkProvider() {
        return null;
    }

    @Override
    public float func_230487_a_(Direction p_230487_1_, boolean p_230487_2_) {
        return mobileChunk.world.func_230487_a_(p_230487_1_, p_230487_2_);
    }

    @Override
    public Biome getNoiseBiomeRaw(int x, int y, int z) {
        BlockPos pos = new BlockPos(mobileChunk.getWorldPosForChunkPos(new BlockPos(x, y, z)));
        return getParentWorld().getNoiseBiomeRaw(pos.getX(), pos.getY(), pos.getZ());
    }
}
