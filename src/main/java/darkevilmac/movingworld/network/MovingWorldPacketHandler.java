package darkevilmac.movingworld.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;

@ChannelHandler.Sharable
public class MovingWorldPacketHandler extends SimpleChannelInboundHandler<MovingWorldMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MovingWorldMessage msg) {
        try {
            INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
            EntityPlayer player = ((NetHandlerPlayServer) netHandler).playerEntity;

            switch (FMLCommonHandler.instance().getEffectiveSide()) {
                case CLIENT:
                    msg.handleClientSide(player);
                    break;
                case SERVER:
                    msg.handleServerSide(player);
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}