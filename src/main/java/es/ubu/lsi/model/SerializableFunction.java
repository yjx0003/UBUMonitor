package model;

import java.io.Serializable;
import java.util.function.Function;


/**
 * Interfaz para poder serializar funciones de java
 * https://stackoverflow.com/a/45699173
 * @author Yi Peng Ji
 *
 * @param <T> 
 * @param <R>
 */
public interface SerializableFunction<T,R> extends Function<T,R>, Serializable {}