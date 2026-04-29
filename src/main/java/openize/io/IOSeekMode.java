/*
 * Openize.HEIC
 * Copyright (c) 2024-2026 Openize Pty Ltd.
 *
 * This file is part of Openize.HEIC.
 *
 * Openize.HEIC is available under Openize license, which is
 * available along with Openize.HEIC sources.
 */

package openize.io;

/**
 * Specifies the position in a stream to use for seeking.
 */
public enum IOSeekMode
{
    /**
     * Specifies the beginning of a stream.
     */
    BEGIN,
    /**
     * Specifies the current position within a stream.
     */
    CURRENT,
    /**
     * Specifies the end of a stream.
     */
    END
}
