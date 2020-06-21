package com.socyno.base.bscmixutil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.ArrayUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.reinert.jjschema.Attributes;
import com.github.reinert.jjschema.JJSchemaUtil;
import com.github.reinert.jjschema.JJSchemaUtil.NamedTypeEntity;
import com.github.reinert.jjschema.v1.CustomAttributesProccessor;
import com.github.reinert.jjschema.v1.FieldOption;
import com.github.reinert.jjschema.v1.JsonSchemaFactory;
import com.github.reinert.jjschema.v1.JsonSchemaV4Factory;
import com.google.gson.reflect.TypeToken;
import com.github.reinert.jjschema.v1.FieldType.FieldOptionsType;
import com.socyno.base.bscexec.MessageException;
import com.socyno.base.bscexec.FormValidationException;


import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassUtil {
    
    @Data
    @Accessors(chain = true)
    public static class FieldAttribute {
        private String      field;
        private String      title;
        private boolean    editable;
        private boolean    required;
        private String      pattern;
        private Integer     position;
        private String      description;
        private Map<String, String> attributes;
        private List<? extends FieldOption> options;
    }
    
    @Getter
    public static class AttributeException extends MessageException {
        
        private static final long serialVersionUID = 1L;
        
        private final String clazz;
        
        public AttributeException(String clazz, Exception e) {
            super("", e);
            this.clazz = clazz;
        }
    }
    
    public static class AttributesProccessor extends CustomAttributesProccessor {
        
        private static final Type TYPE_FIELD_CUSTOM_DEFINITION
                    = new TypeToken<List<Map<String, String>>>() {}.getType();
        
        private static final Map<String, List<FieldAttribute>> CACHED_FORMS_ATTRIBUTES
                    = new ConcurrentHashMap<>();
        
        private static final ThreadLocal<String> CUSTOM_PREVIES_ATTRIBUTES = new ThreadLocal<>();
        
        public static String setContextPreviewAttributes(String form, String attrsForPreview) {
            String origin = CUSTOM_PREVIES_ATTRIBUTES.get();
            CUSTOM_PREVIES_ATTRIBUTES.set(String.format("%s/%s", form, attrsForPreview));
            return origin;
        }
        
        public static void resetContextPreviewAttributes(String attrsForPreview) {
            CUSTOM_PREVIES_ATTRIBUTES.set(attrsForPreview);
        }
        
        public static void setCustomFormAttributes(String form, List<FieldAttribute> attributes) {
            CACHED_FORMS_ATTRIBUTES.put(form, attributes);
        }
        
        public static void removeCustomFormAttributes(String form) {
            CACHED_FORMS_ATTRIBUTES.remove(form);
        }
        
        public static List<FieldAttribute> parseFieldAtributes(String clazz, String formAttrs) {
            if (StringUtils.isBlank(formAttrs)) {
                return Collections.emptyList();
            }
            List<Map<String, String>> attrs = null; try {
                attrs = JsonUtil.fromJson(formAttrs, TYPE_FIELD_CUSTOM_DEFINITION);
            } catch (Exception e) {
                throw new AttributeException(clazz, e);
            }
            if (attrs == null) {
                throw new AttributeException(clazz, new NullPointerException());
            }
            List<FieldAttribute> result = new ArrayList<>();
            for (Map<String,String> attr : attrs) {
                if (StringUtils.isBlank(attr.get("field"))) {
                    throw new AttributeException(clazz, new IllegalArgumentException("field name not found"));
                }
                result.add(new FieldAttribute()
                                .setField(attr.remove("field"))
                                .setTitle(attr.remove("title"))
                                .setPattern(attr.remove("pattern"))
                                .setDescription(attr.remove("description"))
                                .setPosition(CommonUtil.parseInteger(attr.remove("position")))
                                .setAttributes(attr));
            }
            return result;
        }
        
        private static FieldAttribute getFormCustomAttributes(String form, String field) {
            Map<String, List<FieldAttribute>> clonedAttrs = new HashMap<>(CACHED_FORMS_ATTRIBUTES);
            String previewAsCurrentForm;
            if (StringUtils.isNotBlank(previewAsCurrentForm = CUSTOM_PREVIES_ATTRIBUTES.get()) 
                    && previewAsCurrentForm.startsWith(String.format("%s/", form))) {
                List<FieldAttribute> previewAttrs = null;
                if ((previewAttrs = parseFieldAtributes(form, previewAsCurrentForm.substring(form.length() + 1))) != null) {
                    clonedAttrs.put(form, previewAttrs);
                }
            }
            if (StringUtils.isBlank(field)) {
                field = ":form";
            }
            List<FieldAttribute> formAttrs;
            if ((formAttrs = clonedAttrs.get(form)) == null) {
                return null;
            }
            FieldAttribute matched = null;
            for (FieldAttribute item : formAttrs) {
                String name;
                if (item == null || StringUtils.isBlank(name = item.getField())) {
                    continue;
                }
                if (field.equals(name)) {
                    return item;
                }
                if (name.endsWith("*") && field.startsWith(name.substring(0, name.length() - 1))
                        && (matched == null || matched.getField().length() < name.length())) {
                    matched = item;
                }
            }
            return matched;
        }
        
        public static void processCommonAttributes(ObjectNode jsonNode, Attributes attrs, Class<?> clazz, String field) {
            if (attrs != null) {
                /* 定制化筛选条件表单解析 */
                Class<?> filterFormClass;
                if ((filterFormClass = getSingltonInstance(attrs.type()).getDynamicFilterFormClass()) != null) {
                    jsonNode.put("dynamicFilterFormClass", classToJson(filterFormClass).toString());
                }
                /* 定制化表格行创建表单解析 */
                if ((filterFormClass = getSingltonInstance(attrs.type()).getListItemCreationFormClass()) != null) {
                    jsonNode.put("listItemCreationFormClass", classToJson(filterFormClass).toString());
                }
            }
            FieldAttribute fieldAttr;
            if (jsonNode == null || clazz == null || (fieldAttr = getFormCustomAttributes(clazz.getName(), field))
                                == null) {
                return;
            }
            if (StringUtils.isNotBlank(fieldAttr.getTitle())) {
                jsonNode.put("title", fieldAttr.getTitle());
            }
            if (StringUtils.isNotBlank(fieldAttr.getPattern())) {
                jsonNode.put("pattern", fieldAttr.getPattern());
            }
            if (fieldAttr.getPosition() != null) {
                jsonNode.put("position", fieldAttr.getPosition());
            }
            if (StringUtils.isNotBlank(fieldAttr.getDescription())) {
                jsonNode.put("description", fieldAttr.getDescription());
            }
            Map<String, String> customAttributes;
            if ((customAttributes = fieldAttr.getAttributes()) != null) {
                for (Map.Entry<String, String> c : customAttributes.entrySet()) {
                    jsonNode.put(String.format("custom%s", StringUtils.capitalize(c.getKey())), c.getValue());
                }
            }
        }
    }
    
    public static Type[] getActualParameterizedTypes(Class<?> sourceClazz, Class<?> targetClass) {
        if (sourceClazz == null) {
            return null;
        }
        Type superGenericType;
        Type[] nextActualTypes;
        Type[] currActualTypes = new Type[0];
        ParameterizedType superParameterizedType;
        targetClass = CommonUtil.ifNull(targetClass, Object.class);
        while (sourceClazz != null && !sourceClazz.equals(targetClass)) {
            superGenericType = sourceClazz.getGenericSuperclass();
//            System.out.println(clazz);
            if (superGenericType instanceof ParameterizedType) {
                superParameterizedType = (ParameterizedType)superGenericType;
//                System.out.println("\tSuperRawType = " + superRawType);
//                System.out.println("\tSuperGenericType = " + superGenericType);
                Type[] parameterTypes = superParameterizedType.getActualTypeArguments();
                nextActualTypes = new Type[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    Type currentType = parameterTypes[i];
//                    System.out.println("\tSuperParameterType[" + i + "] = " + currentType);
                    nextActualTypes[i] = currentType;
                }
                int startedIndex = 0;
                if (currActualTypes != null && currActualTypes.length  > 0) {
                    for (Type prevType : currActualTypes) {
                        for (int i = startedIndex; i < nextActualTypes.length; i++) {
//                            System.out.println("\tPrevoius = " + prevType);
//                            System.out.println("\tPosition = " + nextActualTypes[i]);
                            if (!nextActualTypes[i].toString().contains(".")) {
                                startedIndex = i + 1;
                                nextActualTypes[i] = prevType;
//                                System.out.println("\tReplaced[" + i + "] = " + prevType);
                                break;
                            }
                        }
                    }
                }
                currActualTypes = nextActualTypes;
//                for (Type xx : currActualTypes) {
//                    System.out.println("\tRESULT:" + xx);
//                }
            }
            sourceClazz = sourceClazz.getSuperclass();
        }
        return currActualTypes;
    }

    public static Type[] getActualParameterizedTypes(Class<?> sourceClazz) {
        return getActualParameterizedTypes(sourceClazz, null);
    }
    
    public static Type getActualParameterizedType(Class<?> sourceClazz, Class<?> targetClazz, int index) {
        Type[] types;
        if (index <0 || (types = getActualParameterizedTypes(sourceClazz, targetClazz))
                                        == null || index >= types.length) {
            return null;
        }
        return types[index];
    }

    public static Type getActualParameterizedType(Class<?> sourceClazz, int index) {
        Type[] types;
        if (index <0 || (types = getActualParameterizedTypes(sourceClazz)) == null
                                    || index >= types.length) {
            return null;
        }
        return types[index];
    }
    
    /**
     * 将 class 转换成 json schema
     */
    public static JsonNode classToJson(Class<?> clazz) {
        JsonSchemaFactory schemaFactory = new JsonSchemaV4Factory();
        schemaFactory.setAutoPutDollarSchema(false);
        Class<? extends CustomAttributesProccessor> originParser = JJSchemaUtil.getCustomAttributesParser();
        JJSchemaUtil.setCustomAttributesParser(AttributesProccessor.class);
        try {
            return schemaFactory.createSchema(clazz);
        } finally {
            JJSchemaUtil.setCustomAttributesParser(originParser);
        }
    }
    
    /**
     * 解析 Class 中的字段属性定义(通过 jjschema/Attributes 注解)
     */
    public static Collection<FieldAttribute> parseClassFields(@NonNull Class<?> clazz) {
        return parseClassFields(clazz, null).values();
    }
    
    private static Map<String, FieldAttribute> parseClassFields(@NonNull Class<?> clazz, Map<String, FieldAttribute> collector) {
        if (collector == null) {
            collector = new HashMap<>();
        }
        for (Field classField : clazz.getDeclaredFields()) {
            String field;
            if (collector.containsKey(field = classField.getName())) {
                continue;
            }
            boolean editable = true;
            boolean required = false;
            String title = classField.getName();
            String pattern = null;
            Integer position = null;
            String description = null;
            List<? extends FieldOption> options = null;
            Attributes fieldAttributes;
            if ((fieldAttributes = classField.getAnnotation(Attributes.class)) != null) {
                title = fieldAttributes.title();
                pattern = fieldAttributes.pattern();
                required = fieldAttributes.required();
                editable = !fieldAttributes.readonly();
                position = fieldAttributes.position();
                description = fieldAttributes.description();
                NamedTypeEntity nameTypeEntity = JJSchemaUtil.parseTypeAttributes(fieldAttributes);
                if (nameTypeEntity != null && FieldOptionsType.STATIC.equals(nameTypeEntity.getOptionsType())) {
                    options = nameTypeEntity.getStaticOptions();
                }
            }
            FieldAttribute customAttrs;
            if ((customAttrs = AttributesProccessor.getFormCustomAttributes(clazz.getName(), field))
                                != null) {
                if (StringUtils.isNotBlank(customAttrs.getTitle())) {
                    title = customAttrs.getTitle();
                }
                if (StringUtils.isNotBlank(customAttrs.getPattern())) {
                    pattern = customAttrs.getPattern();
                }
                if (customAttrs.getPosition() != null) {
                    position = customAttrs.getPosition();
                }
                if (StringUtils.isNotBlank(customAttrs.getDescription())) {
                    description = customAttrs.getDescription();
                }
            }
            collector.put(field, new FieldAttribute().setField(field).setTitle(title).setEditable(editable)
                    .setRequired(required).setPattern(pattern).setDescription(description)
                    .setPosition(position).setOptions(options));
        }
        if (Object.class.equals(clazz = clazz.getSuperclass())) {
            return collector;
        }
        return parseClassFields(clazz, collector);
    }
    /**
     * 根据字段的定义，检查给定对要是否符合规范
     */
    public static void checkFormRequiredAndOpValue(@NonNull Object instance, String... exclusions)
            throws FormValidationException {
        checkFormRequiredAndOpValue(instance, false, exclusions);
    }
    
    public static void checkFormRequiredAndOpValue(@NonNull Object instance, boolean skipReadOnly, String... exclusions)
            throws FormValidationException {
        try {
            Collection<FieldAttribute> fields;
            if ((fields = parseClassFields(instance.getClass())) == null) {
                return;
            }
            for (FieldAttribute field : fields) {
                String fieldName = field.getField();
                if (exclusions != null && ArrayUtils.contains(exclusions, fieldName)) {
                    continue;
                }
                if (skipReadOnly && !field.isEditable()) {
                    continue;
                }
                Object fieldValue = getFieldValue(instance.getClass(), instance, fieldName);
                if (field.isRequired()) {
                    if (fieldValue == null || StringUtils.isBlank(fieldValue.toString())
                            || (fieldValue.getClass().isArray() && ((Object[]) fieldValue).length <= 0)
                            || ((fieldValue instanceof Collection) && ((Collection<?>) fieldValue).isEmpty())) {
                        throw new FormValidationException(
                                String.format("字段（%s）的值被要求但未提供", field.getTitle()));
                    }
                }
                if (fieldValue == null || StringUtils.isBlank(fieldValue.toString())) {
                    continue;
                }
                List<? extends FieldOption> staticOptions;
                if ((staticOptions = field.getOptions()) != null && fieldValue != null) {
                    Object unknownFound = null;
                    Object[] arrayValues = null;
                    if (fieldValue instanceof Collection) {
                        arrayValues = ((Collection<?>) fieldValue).toArray();
                    } else if (fieldValue.getClass().isArray()) {
                        arrayValues = (Object[]) fieldValue;
                    } else {
                        arrayValues = new Object[] { fieldValue };
                    }
                    LOOP_TOP: for (Object av : arrayValues) {
                        if (av == null) {
                            continue;
                        }
                        for (FieldOption option : staticOptions) {
                            if (av.toString().equals(option.getOptionValue())) {
                                continue LOOP_TOP;
                            }
                        }
                        unknownFound = av;
                        break LOOP_TOP;
                    }
                    if (unknownFound != null) {
                        throw new FormValidationException(
                                String.format("字段（%s）的值(%s)不在可选范围", field.getTitle(), unknownFound));
                    }
                }
                if (StringUtils.isNotBlank(field.getPattern())) {
                    Pattern valueRegexp = null;
                    try {
                        valueRegexp = Pattern.compile(field.getPattern(), Pattern.DOTALL);
                    } catch (Exception e) {
                        throw new FormValidationException(
                                String.format("字段（%s）校验正则模式非法：%s", field.getTitle(), field.getPattern()));
                    }
                    if (!valueRegexp.matcher(fieldValue.toString()).find()) {
                        throw new FormValidationException(
                                String.format("字段（%s）校验正则模式未通过 -%s", field.getTitle(), field.getPattern()));
                    }
                }
            }
        } catch (MessageException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new FormValidationException("解析表单的字段定义信息失败");
        }
    }
    
    private static Object getFieldValue(@NonNull Class<?> clazz, Object instance, String feild) throws IllegalAccessException {
        try {
            Field field = clazz.getDeclaredField(feild);
            field.setAccessible(true);
            return field.get(instance);
        } catch (NoSuchFieldException e) {
            if ((clazz = clazz.getSuperclass()) == Object.class) {
                return null;
            }
            return getFieldValue(clazz, instance, feild);
        }
    }
    
    private static final Map<String, Class<?>> STATE_FORM_CACHED_CLASSES
                            = new ConcurrentHashMap<>();
    
    private static final Map<Class<?>, Object> STATE_FORM_CACHED_INSTANCES
                            = new ConcurrentHashMap<>();
    
    public static Class<?> loadClass(@NonNull String clazzPath) throws ClassNotFoundException {
        if (!STATE_FORM_CACHED_CLASSES.containsKey(clazzPath)) {
            synchronized(clazzPath.intern()) {
                if (!STATE_FORM_CACHED_CLASSES.containsKey(clazzPath)) {
                    try {
                        STATE_FORM_CACHED_CLASSES.put(clazzPath, Class.forName(clazzPath));
                    } catch (ClassNotFoundException | MessageException e ) {
                        throw e;
                    } catch (Exception ex) {
                        throw new MessageException(String.format("类加载失败: %s", clazzPath),
                                ex);
                    }
                }
            }
        }
        return STATE_FORM_CACHED_CLASSES.get(clazzPath);
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<? extends T> loadClass(@NonNull String clazzPath, Class<T> superClazz) throws ClassNotFoundException {
        Class<?> clazz = loadClass(clazzPath);
        if (superClazz != null && !superClazz.isAssignableFrom(clazz)) {
            throw new MessageException(
                    String.format("类继承关系不匹配: 要求 %s 必须实现或继承自 %s", clazz.getName(), superClazz.getName()));
        }
        return (Class<? extends T>) clazz;
    }
    
    public static <T> T getSingltonInstance(@NonNull String clazzPath, Class<T> superClazz)
            throws ClassNotFoundException {
        return (T) getSingltonInstance(loadClass(clazzPath, superClazz));
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T getSingltonInstance(@NonNull Class<T> clazz) {
        if (!STATE_FORM_CACHED_INSTANCES.containsKey(clazz)) {
            synchronized(clazz) {
                if (!STATE_FORM_CACHED_INSTANCES.containsKey(clazz)) {
                    T instance;
                    try {
                        instance = (T) clazz.getMethod("getInstance").invoke(null);
                    } catch (Throwable e) {
                        try {
                            instance = clazz.getDeclaredConstructor().newInstance();
                        } catch (Exception ex) {
                            throw new MessageException(String.format("类实例化失败: %s", clazz.getName(), ex),
                                    e);
                        }
                    }
                    STATE_FORM_CACHED_INSTANCES.put(clazz, instance);
                }
            }
        }
        return (T)STATE_FORM_CACHED_INSTANCES.get(clazz);
    }
}
