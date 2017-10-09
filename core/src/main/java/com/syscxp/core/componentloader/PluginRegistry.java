package com.syscxp.core.componentloader;

import com.syscxp.utils.function.Function;

import java.util.List;

public interface PluginRegistry {
    List<PluginExtension> getExtensionByInterfaceName(String interfaceName);

    <T> List<T> getExtensionList(Class<T> clazz);

    <T, K> void saveExtensionAsMap(Class<T> clazz, Function<K, T> func);

    <T> T getExtensionFromMap(Object key, Class<T> clazz);

    <T, K> void saveExtensionListAsMap(Class<T> clazz, Function<K, T> func);

    <T> List getExtensionListFromMap(Object key, Class<T> clazz);

    void defineDynamicExtension(Class interfaceClass, Object instance);

    String PLUGIN_REGISTRY_BEAN_NAME = "syscxp.PluginRegistry";
    String PLUGIN_REGISTRYIMPL_PLUGINS_FIELD_NAME = "extensions";
}
