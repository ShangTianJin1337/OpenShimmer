package dev.greencat.shimmer.eventbus;

import java.lang.reflect.Method;
import java.util.function.Consumer;

public final class Listener {
   private final Object listenerClass;
   private final Method method;
   private final Consumer<ShimmerEvent> lambda;

   public Listener(Object listenerClass, Method method, Consumer<ShimmerEvent> lambda) {
      this.listenerClass = listenerClass;
      this.method = method;
      this.lambda = lambda;
   }

   public Listener(Object listenerClass, Method method) {
      this(listenerClass, method, null);
   }

   public Method getMethod() {
      return this.method;
   }

   public Consumer<ShimmerEvent> getLambda() {
      return this.lambda;
   }

   public Object getListenerClass() {
      return this.listenerClass;
   }
}
