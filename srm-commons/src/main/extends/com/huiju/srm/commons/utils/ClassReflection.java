package com.huiju.srm.commons.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 类反射工具
 * 
 * @author WANGLQ
 * 
 */
public class ClassReflection {

    /**
     * @param class1
     *            用于赋值的实体类
     * @param class1
     *            需要待赋值的实体类
     * @author ym
     * @CreateTime 2012-11-22下午03:23:23 描述：反射实体类赋值
     */
    @SuppressWarnings("rawtypes")
    public static void reflectionAttr(Object class1, Object class2) throws Exception {
        Class clazz1 = Class.forName(class1.getClass().getName());
        Class clazz2 = Class.forName(class2.getClass().getName());
        Class clazz3 = Class.forName(class1.getClass().getSuperclass().getName());
        Class clazz4 = Class.forName(class2.getClass().getSuperclass().getName());
        // 获取两个实体类的所有属性
        Field[] fields1 = clazz1.getDeclaredFields();
        Field[] fields2 = clazz2.getDeclaredFields();
        Field[] fields3 = clazz3.getDeclaredFields();
        Field[] fields4 = clazz4.getDeclaredFields();
        ClassReflection cr = new ClassReflection();
        // 遍历class1Bean，获取逐个属性值，然后遍历class2Bean查找是否有相同的属性，如有相同则赋值
        for (Field f1 : fields1) {
            // System.out.println(f1.getName());
            if (f1.getName().equals("serialVersionUID") || f1.getName().equals("trade") || f1.getName().equals("area") || f1.getName().equals("companyType")
                    || f1.getName().equals("currency") || f1.getName().equals("sendType") || f1.getName().equals("payType")
                    || f1.getName().equals("comproCompanys") || f1.getName().equals("bankCompanys")
                    || f1.getName().equals("companyExtraInfo")
                    || f1.getName().equals("corporationCompanys")
                    // get方法带的参数
                    || f1.getName().equals("_persistence_primaryKey") || f1.getName().equals("_persistence_listener")
                    || f1.getName().equals("_persistence_fetchGroup") || f1.getName().equals("_persistence_session")
                    || f1.getName().equals("_persistence_shouldRefreshFetchGroup"))
                continue;
            Object value = cr.invokeGetMethod(class1, f1.getName(), null);
            if (value != null) {
                for (Field f2 : fields2) {
                    if (f1.getName().equals(f2.getName())) {
                        Object[] obj = new Object[1];
                        if (!value.equals("")) {
                            obj[0] = value;
                            cr.invokeSetMethod(class2, f2.getName(), obj);
                        }
                    }
                }
            }
        }

        for (Field f3 : fields3) {
            // System.out.println(f3.getName());
            if (f3.getName().equals("serialVersionUID") || f3.getName().equals("groupType") || f3.getName().equals("clientTypeGroupInfos")
                    || f3.getName().equals("_persistence_primaryKey") || f3.getName().equals("_persistence_listener")
                    || f3.getName().equals("_persistence_fetchGroup") || f3.getName().equals("_persistence_session")
                    || f3.getName().equals("_persistence_shouldRefreshFetchGroup"))
                continue;
            Object value = cr.invokeSuperGetMethod(class1, f3.getName(), null);
            if (value != null) {
                for (Field f4 : fields4) {
                    if (f3.getName().equals(f4.getName())) {
                        Object[] obj = new Object[1];
                        if (!value.equals("")) {
                            obj[0] = value;
                            cr.invokeSuperSetMethod(class2, f4.getName(), obj);
                        }
                    }
                }
            }
        }

    }

    @SuppressWarnings("rawtypes")
    public static void reflectionAttrNoSuper(Object class1, Object class2) throws Exception {
        Class clazz1 = Class.forName(class1.getClass().getName());
        Class clazz2 = Class.forName(class2.getClass().getName());
        //Class clazz3 = Class.forName(class1.getClass().getSuperclass().getName());
        // Class clazz4 = Class.forName(class2.getClass().getSuperclass().getName());
        // 获取两个实体类的所有属性
        Field[] fields1 = clazz1.getDeclaredFields();
        Field[] fields2 = clazz2.getDeclaredFields();
        //Field[] fields3 = clazz3.getDeclaredFields();
        //Field[] fields4 = clazz4.getDeclaredFields();
        ClassReflection cr = new ClassReflection();
        // 遍历class1Bean，获取逐个属性值，然后遍历class2Bean查找是否有相同的属性，如有相同则赋值
        for (Field f1 : fields1) {
            // System.out.println(f1.getName());
            if (f1.getName().equals("serialVersionUID") || f1.getName().equals("trade") || f1.getName().equals("area") || f1.getName().equals("companyType")
                    || f1.getName().equals("currency") || f1.getName().equals("sendType") || f1.getName().equals("payType")
                    || f1.getName().equals("comproCompanys") || f1.getName().equals("bankCompanys") || f1.getName().equals("companyExtraInfo")
                    || f1.getName().equals("corporationCompanys") || f1.getName().equals("groupInfoCompanyPK") || f1.getName().equals("company")
                    || f1.getName().equals("groupType") || f1.getName().equals("clientTypeGroupInfos") || f1.getName().equals("groupInfo")
                    || f1.getName().equals("clientType") || f1.getName().equals("_persistence_primaryKey") || f1.getName().equals("_persistence_listener")
                    || f1.getName().equals("_persistence_fetchGroup") || f1.getName().equals("_persistence_session")
                    || f1.getName().equals("_persistence_shouldRefreshFetchGroup"))
                continue;
            Object value = cr.invokeGetMethod(class1, f1.getName(), null);
            if (value != null) {
                for (Field f2 : fields2) {
                    if (f1.getName().equals(f2.getName())) {
                        Object[] obj = new Object[1];
                        if (!value.equals("")) {
                            obj[0] = value;
                            cr.invokeSetMethod(class2, f2.getName(), obj);
                        }
                    }
                }
            }
        }
    }

    /**
     * 
     * 执行某个Field的getField方法
     * 
     * @param clazz
     *            类
     * @param fieldName
     *            类的属性名称
     * @param args
     *            参数，默认为null
     * @return
     */
    private Object invokeGetMethod(Object clazz, String fieldName, Object[] args) {
        String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Method method = null;
        try {
            method = Class.forName(clazz.getClass().getName()).getDeclaredMethod("get" + methodName);
            return method.invoke(clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private Object invokeSuperGetMethod(Object clazz, String fieldName, Object[] args) {
        String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Method method = null;
        try {
            method = Class.forName(clazz.getClass().getSuperclass().getName()).getDeclaredMethod("get" + methodName);
            return method.invoke(clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 
     * 执行某个Field的setField方法
     * 
     * @param clazz
     *            类
     * @param fieldName
     *            类的属性名称
     * @param args
     *            参数，默认为null
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object invokeSetMethod(Object clazz, String fieldName, Object[] args) {
        String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Method method = null;
        try {
            Class[] parameterTypes = new Class[1];
            Class c = Class.forName(clazz.getClass().getName());
            Field field = c.getDeclaredField(fieldName);
            parameterTypes[0] = field.getType();
            method = c.getDeclaredMethod("set" + methodName, parameterTypes);
            return method.invoke(clazz, args);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object invokeSuperSetMethod(Object clazz, String fieldName, Object[] args) {
        String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Method method = null;
        try {
            Class[] parameterTypes = new Class[1];
            Class c = Class.forName(clazz.getClass().getSuperclass().getName());
            Field field = c.getDeclaredField(fieldName);
            parameterTypes[0] = field.getType();
            method = c.getDeclaredMethod("set" + methodName, parameterTypes);
            return method.invoke(clazz, args);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}