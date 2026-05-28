package dev.greencat.shimmer.mixin;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({Mouse.class})
public interface MouseAccessor {
}
