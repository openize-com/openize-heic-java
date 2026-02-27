/*
 * FileFormat.IsoBmff
 * Copyright (c) 2024-2025 Openize Pty Ltd.
 *
 * This file is part of FileFormat.IsoBmff.
 *
 * FileFormat.IsoBmff is available under MIT license, which is
 * available along with FileFormat.IsoBmff sources.
 */

package openize.isobmff;

import openize.isobmff.io.BitStreamReader;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * The metadata for a presentation is stored in the single Movie Box which occurs at the top‐level of a file.
 * </p>
 */
public class MovieBox extends Box
{
    /**
     * <p>
     * List of nested boxes.
     * </p>
     */
    private final List<Box> boxes;
    private ObservableCollection<Box> children;

    /**
     * <p>
     * Create the box object from the bitstream, box size and start position.
     * </p>
     *
     * @param stream   File stream.
     * @param size     Box size in bytes.
     * @param startPos Start position in bits.
     */
    public MovieBox(BitStreamReader stream, /*UInt64*/long size, /*UInt64*/long startPos)
    {
        super(BoxType.moov, size);

        boxes = new ArrayList<>((int) size);

        while (stream.getBitPosition() < startPos + size * 8)
        {
            Box box = Box.parseBox(stream);
            boxes.add(box);
        }

        setChildren(new ObservableCollection<>(boxes));
    }

    /**
     * <p>
     * Text summary of the box.
     * </p>
     */
    @Override
    public String toString()
    {
        return this.type.name();
    }

    /**
     * <p>
     * Observable collection of the nested boxes.
     * </p>
     */
    public final ObservableCollection<Box> getChildren()
    {
        return children;
    }

    /**
     * <p>
     * Observable collection of the nested boxes.
     * </p>
     */
    public final void setChildren(ObservableCollection<Box> value)
    {
        children = value;
    }
}
