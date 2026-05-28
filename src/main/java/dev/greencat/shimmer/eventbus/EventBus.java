package dev.greencat.shimmer.eventbus;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventBus implements IEventBus {
   private final Map<Class<? extends ShimmerEvent>, CopyOnWriteArrayList<Listener>> listeners = new ConcurrentHashMap();

   @Override
   public void register(Object registerClass) {
      Arrays.stream(registerClass.getClass().getMethods())
         .filter(method -> method.isAnnotationPresent(ShimmerSubscribe.class))
         .filter(method -> method.getParameterCount() == 1)
         .forEach(method -> {
            if (!method.canAccess(registerClass)) {
               method.setAccessible(true);
            }

            Class<?> parameterType = method.getParameterTypes()[0];
            if (!ShimmerEvent.class.isAssignableFrom(parameterType)) {
               return;
            }

            Class<? extends ShimmerEvent> event = parameterType.asSubclass(ShimmerEvent.class);
            Consumer<ShimmerEvent> lambda = null;
            if (((ShimmerSubscribe)method.getDeclaredAnnotation(ShimmerSubscribe.class)).lambda()) {
               lambda = this.getLambda(registerClass, method, event);
            }

            if (!this.listeners.containsKey(event)) {
               this.listeners.put(event, new CopyOnWriteArrayList());
            }

            ((CopyOnWriteArrayList)this.listeners.get(event)).add(new Listener(registerClass, method, lambda));
         });
   }

   @Override
   public void unregister(Object registerClass) {
      this.listeners.values().forEach(arrayList -> arrayList.removeIf(listener -> listener.getListenerClass().equals(registerClass)));
   }

   @Override
   public void post(ShimmerEvent event) {
      List<Listener> listenersList = (List<Listener>)this.listeners.get(event.getClass());
      if (listenersList != null) {
         for (Listener listener : listenersList) {
            if (event.isCancelled()) {
               return;
            }

            if (listener.getLambda() != null) {
               listener.getLambda().accept(event);
            } else {
               try {
                  listener.getMethod().invoke(listener.getListenerClass(), event);
               } catch (InvocationTargetException | IllegalAccessException var6) {
               }
            }
         }
      }
   }

   protected Consumer<ShimmerEvent> getLambda(Object object, Method method, Class<? extends ShimmerEvent> event) {
      Consumer<ShimmerEvent> eventLambda = null;

      try {
         MethodHandles.Lookup lookup = MethodHandles.lookup();
         MethodType subscription = MethodType.methodType(void.class, event);
         MethodHandle target = lookup.findVirtual(object.getClass(), method.getName(), subscription);
         CallSite site = LambdaMetafactory.metafactory(
            lookup, "accept", MethodType.methodType(Consumer.class, object.getClass()), subscription.changeParameterType(0, Object.class), target, subscription
         );
         MethodHandle factory = site.getTarget();
         eventLambda = (Consumer)factory.bindTo(object).invokeExact();
      } catch (Throwable var10) {
      }

      return eventLambda;
   }
}
