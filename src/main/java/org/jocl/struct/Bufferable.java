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

import java.nio.*;

/**
 * Package-private abstract base class for Structs and the
 * cl_vector_types. This class summarizes the methods that
 * are required for reading or writing an object from or
 * to a byte buffer.<br>
 * <br>
 * This should be considered as an implementation detail,
 * and thus is NOT visible through a public interface.
 */
abstract class Bufferable
{
    /**
     * Writes this object to the given target buffer.
     * 
     * @param targetBuffer The target buffer
     */
    abstract void writeThisToBuffer(ByteBuffer targetBuffer);

    /**
     * Reads the contents of this object from the given buffer.
     * 
     * @param sourceBuffer The source buffer.
     */
    abstract void readThisFromBuffer(ByteBuffer sourceBuffer);
    
    /**
     * Returns the size of this object, in bytes
     * 
     * @return The size of this object, in bytes
     */
    abstract int getSize();
}
