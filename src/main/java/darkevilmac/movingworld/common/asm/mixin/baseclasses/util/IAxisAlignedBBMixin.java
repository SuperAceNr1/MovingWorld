package darkevilmac.movingworld.common.asm.mixin.baseclasses.util;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;

public interface IAxisAlignedBBMixin {

    public AxisAlignedBB rotate(int angle);

    public AxisAlignedBB rotate(int angle, EnumFacing.Axis axis);

}
