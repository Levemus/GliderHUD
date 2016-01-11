package com.levemus.gliderhud.Messages;

import java.util.HashSet;

/**
 * Created by markcarter on 16-01-05.
 */
public interface IPayload<E, F> {

    HashSet<E> keys();

    F get(E key) throws java.lang.UnsupportedOperationException;
}
