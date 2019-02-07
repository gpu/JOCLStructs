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

import org.jocl.struct.CLTypes.*;
import org.jocl.*;

/**
 * A class that offers a functionality similar to the 'Pointer' class
 * of JOCL for the experimental 'struct' support.
 */
public class PointerStruct
{
    /**
     * Creates a new Pointer to the given vector type values.
     * The given value may not be null, must at least contain
     * one element, and may not contain null elements.<br>
     * <br> 
     * The data of this pointer may not be read on host side.
     * To pass data to a kernel which may later be written into
     * CL vector types, use the 
     * {@link Buffers#allocateBuffer(cl_vector_type...)} / 
     * {@link Buffers#readFromBuffer(ByteBuffer, cl_vector_type...)} /
     * {@link Buffers#writeToBuffer(ByteBuffer, cl_vector_type...)}
     * methods:
     * <pre><code>
     * ByteBuffer buffer = CLTypes.allocateBuffer(values);
     * Buffers.writeToBuffer(buffer, values);
     * passToKernel(Pointer.to(buffer));
     * Buffers.readFromBuffer(buffer, values);
     * </code></pre>
     * 
     * @param values The vector type values
     * @return A pointer to the vector 
     * @throws NullPointerException If the given values
     * array is null, has a length of 0, or contains null elements
     */
    @SafeVarargs
    public static <T extends cl_vector_type> Pointer to(T ... values)
    {
        ByteBuffer buffer = Buffers.allocateBuffer(values);
        Buffers.writeToBuffer(buffer, values);
        return Pointer.to(buffer);
    }
    
    
    /**
     * Creates a new Pointer to the given Structures. The array of
     * structures may not be null, must at least contain one
     * element, and may not contain null elements.<br>
     * <br>
     * The data of this pointer may not be read on host side.
     * To pass data to a kernel which may later be written into
     * Structs, use the 
     * {@link Buffers#allocateBuffer(Struct...)} / 
     * {@link Buffers#readFromBuffer(ByteBuffer, Struct...)} /
     * {@link Buffers#writeToBuffer(ByteBuffer, Struct...)}
     * methods:
     * <pre><code>
     * ByteBuffer buffer = Struct.allocateBuffer(structs);
     * Buffers.writeToBuffer(buffer, values);
     * passToKernel(Pointer.to(buffer));
     * Buffers.readFromBuffer(buffer, structs);
     * </code></pre>
     * 
     * @param structs The structures that the pointer will point to
     * @return The pointer
     * @throws IllegalArgumentException If the given array
     * is null, has a length of 0 or contains null objects
     */
    public static Pointer to(Struct ... structs)
    {
        ByteBuffer buffer = Buffers.allocateBuffer(structs);
        Buffers.writeToBuffer(buffer, structs);
        return Pointer.to(buffer);
    }

    /**
     * Private constructor to prevent instantiation
     */
    private PointerStruct()
    {
        // Private constructor to prevent instantiation
    }
    
}
