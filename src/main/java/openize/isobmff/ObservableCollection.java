/*
 * FileFormat.IsoBmff
 * Copyright (c) 2024-2026 Openize Pty Ltd.
 *
 * This file is part of FileFormat.IsoBmff.
 *
 * FileFormat.IsoBmff is available under MIT license, which is
 * available along with FileFormat.IsoBmff sources.
 */

package openize.isobmff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * <p>
 *     A simple ArrayList in fact.
 * </p>
 * @param <T> The type of storing elements.
 */
public class ObservableCollection<T> extends ArrayList<T>
{
    public ObservableCollection(int initialCapacity)
    {
        super(initialCapacity);
    }

    public ObservableCollection()
    {
        super();
    }

    public ObservableCollection(Collection<? extends T> c)
    {
        super(c);
    }

    public ObservableCollection(T[] args)
    {
        super(args.length);
        Collections.addAll(this, args);
    }
}
