/*
 * JOCL - Java bindings for OpenCL
 *
 * Copyright (c) 2009-2019 Marco Hutter - http://www.jocl.org
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package org.jocl.struct;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.jocl.struct.CLTypes.cl_vector_type;

/**
 * A class containing methods for allocating buffers, and for 
 * reading and writing Struct and CLTypes.* objects from 
 * and to buffers.
 */
public class Buffers
{
    /*
     * Implementation note: The methods for allocating/reading/writing
     * buffers for Struct and cl_vector_type arrays are delegating to
     * the same internal methods. The fact that Struct and cl_vector_type
     * are both 'Bufferable' objects should be considered as an 
     * implementation detail, and is thus not visible for the users.
     */
    
    /**
     * Allocates a new ByteBuffer that has a size and alignment
     * that is sufficient to buffer the given objects. The
     * Buffer will have the default (native) byte order.
     * 
     * @param structs The objects for which the buffer should
     * be allocated
     * @return A newly allocated ByteBuffer
     */
    public static ByteBuffer allocateBuffer(Struct ... structs)
    {
        return Buffers.allocateBufferImpl(ByteOrder.nativeOrder(), structs);
    }

    /**
     * Allocates a new ByteBuffer that has a size and alignment
     * that is sufficient to buffer the given objects, and has
     * the given byte order.
     * 
     * @param byteOrder The byte order for the buffer.
     * @param structs The objects for which the buffer should
     * be allocated
     * @return A newly allocated ByteBuffer
     */
    public static ByteBuffer allocateBuffer(
        ByteOrder byteOrder, Struct ... structs)
    {
        return Buffers.allocateBufferImpl(byteOrder, structs);
    }
    
    /**
     * Writes the given objects to the given target buffer.
     * 
     * @param targetBuffer The buffer to write to
     * @param structs The objects to write
     * @throws IllegalArgumentException If the given objects array
     * is null, has a length of 0 or contains 'null' elements,
     * or if the given buffer is null or has not enough bytes 
     * remaining to write the given objects to it.
     */
    public static void writeToBuffer(
        ByteBuffer targetBuffer, Struct ... structs)
    {
        Buffers.writeToBufferImpl(targetBuffer, structs);
    }
    
    
    /**
     * Reads the given objects from the given source buffer.
     * 
     * @param sourceBuffer The buffer to read from
     * @param structs The objects to read
     * @throws IllegalArgumentException If the given objects array
     * is null, has a length of 0 or contains 'null' elements,
     * or if the given buffer is null or has not enough bytes 
     * remaining to read the given objects from it.
     */
    public static void readFromBuffer(
        ByteBuffer sourceBuffer, Struct ... structs)
    {
        Buffers.readFromBufferImpl(sourceBuffer, structs);
    }
    
    
    
    /**
     * Allocates a new ByteBuffer that has a size and alignment
     * that is sufficient to buffer the given objects. The
     * Buffer will have the default (native) byte order.
     * 
     * @param <T> The element type
     * 
     * @param elements The objects for which the buffer should
     * be allocated
     * @return A newly allocated ByteBuffer
     */
    @SafeVarargs
    public static <T extends cl_vector_type> ByteBuffer allocateBuffer(
        T ... elements)
    {
        return Buffers.allocateBufferImpl(ByteOrder.nativeOrder(), elements);
    }

    /**
     * Allocates a new ByteBuffer that has a size and alignment
     * that is sufficient to buffer the given objects, and has
     * the given byte order.
     * 
     * @param <T> The element type
     * 
     * @param byteOrder The byte order for the buffer.
     * @param elements The objects for which the buffer should
     * be allocated
     * @return A newly allocated ByteBuffer
     */
    @SafeVarargs
    public static <T extends cl_vector_type> ByteBuffer allocateBuffer(
        ByteOrder byteOrder, T ... elements)
    {
        return Buffers.allocateBufferImpl(byteOrder, elements);
    }
    
    /**
     * Writes the given objects to the given target buffer.
     * 
     * @param <T> The element type
     * 
     * @param targetBuffer The buffer to write to
     * @param elements The objects to write
     * @throws IllegalArgumentException If the given objects array
     * is null, has a length of 0 or contains 'null' elements,
     * or if the given buffer is null or has not enough bytes 
     * remaining to write the given objects to it.
     */
    @SafeVarargs
    public static <T extends cl_vector_type> void writeToBuffer(
        ByteBuffer targetBuffer, T ... elements)
    {
        Buffers.writeToBufferImpl(targetBuffer, elements);
    }

    /**
     * Reads the given objects from the given source buffer.
     * 
     * @param <T> The element type
     * 
     * @param sourceBuffer The buffer to read from
     * @param elements The objects to read
     * @throws IllegalArgumentException If the given objects array
     * is null, has a length of 0 or contains 'null' elements,
     * or if the given buffer is null or has not enough bytes 
     * remaining to read the given objects from it.
     */
    @SafeVarargs
    public static <T extends cl_vector_type> void readFromBuffer(
        ByteBuffer sourceBuffer, T ... elements)
    {
        Buffers.readFromBufferImpl(sourceBuffer, elements);
    }
    
    /**
     * Helper method to check the given arguments for validity: Will
     * throw an IllegalArgumentException when the given argument is
     * null, has a length of 0 or of the first element of the array
     * is 'null'.
     * 
     * @param bufferables The argument to check
     * @throws IllegalArgumentException If the arguments are invalid
     */
    private static void checkArgs(Bufferable ... bufferables)
    {
        if (bufferables == null)
        {
            throw new IllegalArgumentException(
                "Array argument is null");
        }
        if (bufferables.length == 0)
        {
            throw new IllegalArgumentException(
                "Array contains zero elements");
        }
        if (bufferables[0] == null)
        {
            throw new IllegalArgumentException(
                "Array contains null element");
        }
    }
    
    
    /**
     * Allocates a new ByteBuffer that has a size and alignment
     * that is sufficient to buffer the given objects.
     * 
     * @param byteOrder The byte order which should be used
     * for the buffer
     * @param bufferables The object for which the buffer should
     * be allocated
     * @return A newly allocated ByteBuffer
     */
    private static ByteBuffer allocateBufferImpl(
        ByteOrder byteOrder, Bufferable ... bufferables)
    {
        checkArgs(bufferables);
        int bufferableSize = bufferables[0].getSize();
        int totalSize = bufferables.length * bufferableSize;
        ByteBuffer buffer = ByteBuffer.allocateDirect(totalSize);
        //ByteBuffer buffer = CL.allocateAligned(totalSize, bufferableSize);
        buffer.order(byteOrder);
        return buffer;
    }

    /**
     * Writes the given objects to the given target buffer.
     * 
     * @param targetBuffer The buffer to write to
     * @param bufferables The objects to write
     * @throws IllegalArgumentException If the given objects array
     * is null, has a length of 0 or contains 'null' elements,
     * or if the given buffer is null or has not enough bytes 
     * remaining to write the given objects to it.
     */
    private static void writeToBufferImpl(
        ByteBuffer targetBuffer, Bufferable ... bufferables)
    {
        checkArgs(bufferables);
        if (targetBuffer == null)
        {
            throw new IllegalArgumentException(
                "May not read from null buffer");
        }
        int bufferableSize = bufferables[0].getSize();
        int totalSize = bufferables.length * bufferableSize;
        if (targetBuffer.remaining() < totalSize)
        {
            throw new IllegalArgumentException(
                "May not write "+bufferables.length+" elements with a " +
                "total size of "+totalSize+" bytes to a buffer with " +
                targetBuffer.remaining()+" bytes remaining");
        }
        for (int i=0; i<bufferables.length; i++)
        {
            Bufferable bufferable = bufferables[i];
            if (bufferable == null)
            {
                throw new IllegalArgumentException(
                    "Bufferables array contains null element");
            }
            bufferable.writeThisToBuffer(targetBuffer);
        }
    }
    
    
    /**
     * Reads the given objects from the given source buffer.
     * 
     * @param targetBuffer The buffer to read from
     * @param bufferables The objects to read
     * @throws IllegalArgumentException If the given objects array
     * is null, has a length of 0 or contains 'null' elements,
     * or if the given buffer is null or has not enough bytes 
     * remaining to read the given objects from it.
     */
    private static void readFromBufferImpl(
        ByteBuffer sourceBuffer, Bufferable ... bufferables)
    {
        checkArgs(bufferables);
        if (sourceBuffer == null)
        {
            throw new IllegalArgumentException(
                "May not read from null buffer");
        }
        int bufferableSize = bufferables[0].getSize();
        int totalSize = bufferables.length * bufferableSize;
        if (sourceBuffer.remaining() < totalSize)
        {
            throw new IllegalArgumentException(
                "May not read "+bufferables.length+" elements with a " +
                "total size of "+totalSize+" bytes from a buffer with " +
                sourceBuffer.remaining()+" bytes remaining");
        }

        for (int i=0; i<bufferables.length; i++)
        {
            Bufferable bufferable = bufferables[i];
            if (bufferable == null)
            {
                throw new IllegalArgumentException(
                    "Bufferables array contains null element");
            }
            bufferable.readThisFromBuffer(sourceBuffer);
        }
        
    }

    /**
     * Private constructor to prevent instantiation
     */
    private Buffers()
    {
        // Private constructor to prevent instantiation
    }

}
