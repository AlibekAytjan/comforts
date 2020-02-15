package top.theillusivec4.comforts.common.network;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import top.theillusivec4.comforts.Comforts;

public class ComfortsNetwork {

  private static final String PTC_VERSION = "1";

  public static SimpleChannel INSTANCE;

  private static int id = 0;

  public static void register() {
    INSTANCE = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(Comforts.MODID, "main"))
        .networkProtocolVersion(() -> PTC_VERSION).clientAcceptedVersions(PTC_VERSION::equals)
        .serverAcceptedVersions(PTC_VERSION::equals).simpleChannel();

    register(SPacketAutoSleep.class, SPacketAutoSleep::encode, SPacketAutoSleep::decode,
        SPacketAutoSleep::handle);
  }

  private static <M> void register(Class<M> messageType, BiConsumer<M, PacketBuffer> encoder,
      Function<PacketBuffer, M> decoder, BiConsumer<M, Supplier<Context>> messageConsumer) {
    INSTANCE.registerMessage(id++, messageType, encoder, decoder, messageConsumer);
  }
}
