/*
 * Openize.HEIC
 * Copyright (c) 2024-2025 Openize Pty Ltd.
 *
 * This file is part of Openize.HEIC.
 *
 * Openize.HEIC is available under Openize license, which is
 * available along with Openize.HEIC sources.
 */

package openize.io;

/**
 * Signals that an I/O exception has occurred.
 * Intended for throwing the uncontrollable exception
 */
public class IOException extends RuntimeException
{
    /**
     * Constructs an IOException with null as its error detail message.
     */
    public IOException()
    {
    }

    /**
     * Constructs an IOException with the specified detail message.
     * @param message The detail message (which is saved for later retrieval by the getMessage() method)
     */
    public IOException(String message)
    {
        super(message);
    }

    /**
     * Constructs an IOException with the specified detail message and cause.
     * Note that the detail message associated with cause is not automatically incorporated into this exception's detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the getMessage() method)
     * @param cause The cause (which is saved for later retrieval by the getCause() method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public IOException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
