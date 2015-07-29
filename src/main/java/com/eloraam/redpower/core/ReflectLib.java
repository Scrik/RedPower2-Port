package com.eloraam.redpower.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectLib {

   public static void callClassMethod(String className, String method, Class[] def, Object ... params) {
      Class cl;
      try {
         cl = Class.forName(className);
      } catch (ClassNotFoundException var10) {
         return;
      }

      Method mth;
      try {
         mth = cl.getDeclaredMethod(method, def);
      } catch (NoSuchMethodException var9) {
         return;
      }

      try {
         mth.invoke((Object)null, params);
      } catch (IllegalAccessException var7) {
         ;
      } catch (InvocationTargetException var8) {
         ;
      }
   }

   public static Object getStaticField(String classname, String var, Class varcl) {
      Class cl;
      try {
         cl = Class.forName(classname);
      } catch (ClassNotFoundException var10) {
         return null;
      }

      Field fld;
      try {
         fld = cl.getDeclaredField(var);
      } catch (NoSuchFieldException var9) {
         return null;
      }

      Object ob;
      try {
         ob = fld.get((Object)null);
      } catch (IllegalAccessException var7) {
         return null;
      } catch (NullPointerException var8) {
         return null;
      }

      return !varcl.isInstance(ob)?null:ob;
   }

   public static Object getField(Object ob, String var, Class varcl) {
      Class cl = ob.getClass();

      Field fld;
      try {
         fld = cl.getDeclaredField(var);
      } catch (NoSuchFieldException var9) {
         return null;
      }

      Object ob2;
      try {
         ob2 = fld.get(ob);
      } catch (IllegalAccessException var7) {
         return null;
      } catch (NullPointerException var8) {
         return null;
      }

      return !varcl.isInstance(ob2)?null:ob2;
   }
}
