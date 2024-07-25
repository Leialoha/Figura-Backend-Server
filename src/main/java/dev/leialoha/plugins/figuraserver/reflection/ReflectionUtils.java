package dev.leialoha.plugins.figuraserver.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class ReflectionUtils {
	
	private static final String CRAFTBUKKIT_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();

	public static Class<?> getOBCClass(String obcClassString) {
		String name = CRAFTBUKKIT_PACKAGE + "." + obcClassString;
		try {
			return Class.forName(name);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Class<?> getNMSClass(String nmsClass) {
		try {
			return Class.forName(nmsClass);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object getNMSEntity(Entity entity) {
		try {
			Method getHandle = entity.getClass().getMethod("getHandle");
			return getHandle.invoke(entity);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object getField(String field, Class<?> clazz, Object object) {
		try {
			Field f = clazz.getDeclaredField(field);
			f.setAccessible(true);
			return f.get(object);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void setField(String field, Class<?> clazz, Object object, Object toSet) {
		try {
			Field f = clazz.getDeclaredField(field);
			f.setAccessible(true);
			f.set(object, toSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setField(String field, Object object, Object toSet) {
		try {
			Field f = object.getClass().getDeclaredField(field);
			f.setAccessible(true);
			f.set(object, toSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean methodExists(String className, String methodName) {
		try {
			Class<?> clazz = Class.forName(className);
			return Arrays.asList(clazz.getDeclaredMethods()).stream()
				.anyMatch(method -> method.getName().equals(methodName));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public static Method getMethod(Class<?> clazz, String methodName, Class<?>... args) throws Exception {
		Method method = clazz.getDeclaredMethod(methodName, args);
		if (!method.isAccessible()) method.setAccessible(true);
		return method;
	}

	public static Object useMethod(Class<?> clazz, String methodName, Object object, Object... args) throws Exception {
		Class<?>[] argClasses = Stream.of(args).map(a -> a.getClass()).toList().toArray(new Class<?>[0]);
		return getMethod(clazz, methodName, argClasses).invoke(object, args);
	}

	public static Object useMethod(String methodName, Object object, Object... args) throws Exception {
		return useMethod(object.getClass(), methodName, object, args);
	}

	public static Object useMethodSafely(Class<?> clazz, String methodName, Object object, Object... args) {
		try {
			return useMethod(clazz, methodName, object, args);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object useMethodSafely(String methodName, Object object, Object... args) {
		return useMethodSafely(object.getClass(), methodName, object, args);
	}

}
