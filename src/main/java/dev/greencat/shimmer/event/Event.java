package dev.greencat.shimmer.event;

import dev.greencat.shimmer.eventbus.ShimmerEvent;
import net.minecraft.client.MinecraftClient;

public class Event extends ShimmerEvent {
   public MinecraftClient mc = MinecraftClient.getInstance();
}
