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

import java.lang.annotation.*;


/**
 * This annotation may be used to specify the length of arrays inside
 * a {@link Struct}. <br>
 * <br>
 * Example:
 * <code><pre>
 * public class Data extends Struct
 * {
 *     // A 3D position
 *     &#064;ArrayLength(3)
 *     public float position[];
 *     
 *     // A 3x3 matrix
 *     &#064;ArrayLength({3,3})
 *     public float matrix[][];
 * }
 * </pre></code>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ArrayLength 
{
    /**
     * The values given as the array dimensions
     * 
     * @return The array dimensions
     */
    int[] value(); 
}
