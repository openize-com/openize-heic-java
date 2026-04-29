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

import openize.isobmff.io.BitStreamReader;


/**
 * <p>
 * The Protection Scheme Information Box contains all the information required both to understand the
 * encryption transform applied and its parameters, and also to find other information such as the kind
 * and location of the key management system.
 * </p>
 */
public class ProtectionSchemeInfoBox extends Box
{
    public ProtectionSchemeInfoBox(BitStreamReader stream)
    {
        super(stream, BoxType.sinf);

        throw new UnsupportedOperationException();

        //OriginalFormatBox(fmt) original_format;
        //SchemeTypeBox scheme_type_box; // optional
        //SchemeInformationBox info; // optional
    }
}
