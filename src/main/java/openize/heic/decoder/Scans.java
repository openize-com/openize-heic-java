/*
 * Openize.HEIC 
 * Copyright (c) 2024-2025 Openize Pty Ltd. 
 *
 * This file is part of Openize.HEIC.
 *
 * Openize.HEIC is available under Openize license, which is
 * available along with Openize.HEIC sources.
 */

package openize.heic.decoder;


final class Scans
{
    public static byte[][][][] getScanOrder(){ return scanOrder; }
    private static byte[][][][] scanOrder;

    private static boolean _initialised = false;

    static synchronized void initialize()
    {
        if (_initialised) return;

        _initialised = true;
        scanOrderInitialize();
    }

    static void scanOrderInitialize()
    {
        scanOrder = new byte[6][][][];
        for (int log2BlockSize = 0; log2BlockSize < 6; log2BlockSize++)
        {
            scanOrder[log2BlockSize] = new byte[4][][];

            if (log2BlockSize < 4)
            {
                scanOrder[log2BlockSize][0] = diagonalScanInitialize((byte)(1 << log2BlockSize));
                scanOrder[log2BlockSize][1] = horizontalScanInitialize((byte)(1 << log2BlockSize));
                scanOrder[log2BlockSize][2] = verticalScanInitialize((byte)(1 << log2BlockSize));
            }

            if (log2BlockSize > 1)
                scanOrder[log2BlockSize][3] = traverseScanInitialize((byte)(1 << log2BlockSize));
        }
    }

    // 6.5.3 Up-right diagonal scan order array initialization process
    // Up-right diagonal scan order array [ sPos ][ sComp ].
    // The array index sPos specify the scan position ranging from 0 to(blkSize* blkSize) − 1.
    // The array index sComp equal to 0 specifies the horizontal component and
    // the array index sComp equal to 1 specifies the vertical component.
    static byte[][] diagonalScanInitialize(byte blkSize)
    {
        byte[][] diagScan = new byte[(blkSize & 0xFF) * (blkSize & 0xFF)][];
        int i = 0;
        int x = 0;
        int y = 0;
        boolean stopLoop = false;

        while (!stopLoop)
        {
            while (y >= 0)
            {
                if (x < (blkSize & 0xFF) && y < (blkSize & 0xFF))
                {
                    diagScan[i] = new byte[2];
                    diagScan[i][0] = (byte)x;
                    diagScan[i][1] = (byte)y;
                    i++;
                }
                y--;
                x++;
            }

            y = x;
            x = 0;

            if (i >= (blkSize & 0xFF) * (blkSize & 0xFF))
                stopLoop = true;
        }

        return diagScan;
    }

    // 6.5.4 Horizontal scan order array initialization process
    static byte[][] horizontalScanInitialize(byte blkSize)
    {
        byte[][] horScan = new byte[(blkSize & 0xFF) * (blkSize & 0xFF)][];

        int i = 0;
        for (byte y = 0; (y & 0xFF) < (blkSize & 0xFF); y++)
        {
            for (byte x = 0; (x & 0xFF) < (blkSize & 0xFF); x++)
            {
                horScan[i] = new byte[2];
                horScan[i][0] = x;
                horScan[i][1] = y;
                i++;
            }
        }

        return horScan;
    }

    // 6.5.5 Vertical scan order array initialization process
    static byte[][] verticalScanInitialize(byte blkSize)
    {
        byte[][] verScan = new byte[(blkSize & 0xFF) * (blkSize & 0xFF)][];

        int i = 0;
        for (byte x = 0; (x & 0xFF) < (blkSize & 0xFF); x++)
        {
            for (byte y = 0; (y & 0xFF) < (blkSize & 0xFF); y++)
            {
                verScan[i] = new byte[2];
                verScan[i][0] = x;
                verScan[i][1] = y;
                i++;
            }
        }

        return verScan;
    }

    // 6.5.6 Traverse scan order array initialization process
    static byte[][] traverseScanInitialize(byte blkSize)
    {
        byte[][] travScan = new byte[(blkSize & 0xFF) * (blkSize & 0xFF)][];

        int i = 0;
        for (byte y = 0; (y & 0xFF) < (blkSize & 0xFF); y++)
        {
            if ((y & 0xFF) % 2 == 0)
            {
                for (byte x = 0; (x & 0xFF) < (blkSize & 0xFF); x++)
                {
                    travScan[i] = new byte[2];
                    travScan[i][0] = x;
                    travScan[i][1] = y;
                    i++;
                }
            }
            else
            {
                for (int x = (blkSize & 0xFF) - 1; x >= 0; x--)
                {
                    travScan[i] = new byte[2];
                    travScan[i][0] = (byte)x;
                    travScan[i][1] = y;
                    i++;
                }
            }
        }

        return travScan;
    }
}
