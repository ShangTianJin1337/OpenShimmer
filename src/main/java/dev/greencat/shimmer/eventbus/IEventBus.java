package dev.greencat.shimmer.eventbus;

public interface IEventBus {
   void register(Object var1);

   void unregister(Object var1);

   void post(ShimmerEvent var1);
}
