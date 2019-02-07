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

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

import org.jocl.CLException;
import org.jocl.struct.CLTypes.cl_vector_type;
import org.jocl.struct.StructAccess.StructAccessor;
import org.jocl.struct.StructAccess.StructInfo;

/**
 * This class is the base class for all Java classes that should represent 
 * a native 'struct'. A struct class may be created by creating a <b>public</b> 
 * class that extends this Struct class, and which contains <b>public</b> 
 * fields. All public non-volatile fields will be considered as fields that 
 * correspond to the element of native struct. <br>
 * <br>
 * <u>Example:</u><br>
 * <br>
 * The class
 * <pre><code>
 * public class Particle extends Struct
 * {
 *     public float mass;
 *     public cl_float4 position;
 *     public cl_float4 velocity;
 * }
 * </code></pre>
 * Corresponds to a native struct like
 * <pre><code>
 * typedef struct Particle
 * {
 *     float mass;
 *     float4 position;
 *     float4 velocity;
 * } Particle;
 * </code></pre>
 * <br>
 * All fields of the derived class must either be primitive fields, one of 
 * the OpenCL vector types defined in {@link CLTypes}, other Structs or 
 * arrays of these types (except for boolean arrays). Structs containing 
 * instances of themself are not supported and will cause a CLException 
 * to be thrown during the initialization of the Struct. 
 * <br>
 */
public abstract class Struct extends Bufferable
{
    /**
     * Creates a new instance of the given type, wrapping
     * all possible exceptions into a CLException
     * 
     * @param type The type
     * @return The new instance
     */
    private static Object createObject(Class<?> type)
    {
        try
        {
            Constructor<?> constructor = type.getDeclaredConstructor();
            return constructor.newInstance();
        }
        catch (IllegalArgumentException e)
        {
            // This
            throw new CLException(e.getMessage(), e);
        }
        catch (IllegalAccessException e)
        {
            // may
            throw new CLException(e.getMessage(), e);
        }
        catch (InstantiationException e)
        {
            // go
            throw new CLException(e.getMessage(), e);
        }
        catch (NoSuchMethodException e)
        {
            // terribly
            throw new CLException(e.getMessage(), e);
        }
        catch (InvocationTargetException e)
        {
            // wrong ;-)
            throw new CLException(e.getMessage(), e);
        }
    }
    
    /**
     * Initialize the given object array. If the array contains
     * arrays, this method is called recursively on all contained
     * arrays. When the innermost arrays are reached, they are
     * filled with objects of the given type.<br>
     * <br>
     * The given type must provide a default constructor, none
     * of the arguments may be null, and the array may not 
     * contain null arrays.
     * 
     * @param array The array to fill
     * @param type The type of the objects
     * @throws CLException If the objects can not be instantiated
     */
    private static void initObjectArray(Object array, Class<?> type)
    {
        int length = Array.getLength(array);
        if (array.getClass().getComponentType().isArray())
        {
            for (int i=0; i<length; i++)
            {
                Object subArray = Array.get(array, i);
                initObjectArray(subArray, type);
            }
        }
        else
        {
            for (int i=0; i<length; i++)
            {
                Object object = createObject(type);
                Array.set(array, i, object);
            }
        }
    }
    
    
    /**
     * Creates a new instance of a struct.
     */
    protected Struct()
    {
        initFields();
    }
    
    /**
     * Initialize all non-primitive (public and non-volatile) 
     * fields of this instance. 
     */
    private void initFields()
    {
        StructInfo structInfo = getStructInfo();
        StructAccessor structAccessors[] = structInfo.getStructAccessors();
        for (int i=0; i<structAccessors.length; i++)
        {
            Field field = structAccessors[i].getField();
            if (!field.getType().isPrimitive())
            {
                if (this.getClass().isAssignableFrom(field.getType()))
                {
                    // This has already been checked during the 
                    // initialization of the StructAccessors,
                    // so should never happen here.
                    throw new CLException(
                        "Struct may not contain instances of itself: "+field);
                }
                
                if (CLTypes.cl_vector_type.class.isAssignableFrom(
                    field.getType()))
                {
                    initCLVectorTypeField(field);
                }
                else if (field.getType().isArray())
                {
                    initArrayField(field);
                }
                else if (Struct.class.isAssignableFrom(field.getType()))
                {
                    initStructField(field);
                }
                else
                {
                    // This has already been checked during the 
                    // initialization of the StructAccessors,
                    // so should never happen here.
                    throw new CLException(
                        "Invalid type in struct: "+field);
                }
            }
        }
    }
    
    
    /**
     * Initialize the given field, which must be an OpenCL vector type field.
     * This method will create an instance of this type and assign it to the
     * specified field in this struct.
     * 
     * @param clVectorTypeField The field to initialize
     * @throws CLException If the initialization went wrong...
     */
    private void initCLVectorTypeField(Field clVectorTypeField) 
        throws CLException
    {
        Class<?> type = clVectorTypeField.getType(); 
        cl_vector_type element = (cl_vector_type)createObject(type);
        try
        {
            clVectorTypeField.set(this, element);
        }
        catch (IllegalArgumentException e)
        {
            throw new CLException(e.getMessage(), e);
        }
        catch (IllegalAccessException e)
        {
            throw new CLException(e.getMessage(), e);
        }
    }
    
    
    /**
     * Initialize the given field, which must be an array type field.
     * This method will create an instance of the array and assign 
     * it to the specified field in this struct. The size of the
     * array that is created is taken from the {@link ArrayLength}
     * annotation of the field. If this annotation is not present,
     * a CLException is thrown.
     * 
     * @param arrayField The field to initialize
     * @throws CLException If the field has no ArrayLength 
     * annotation, or the initialization went wrong...
     */
    private void initArrayField(Field arrayField)
    {
        // This has already been checked during the 
        // initialization of the StructAccessors,
        // so should never happen here.
        if (!arrayField.isAnnotationPresent(ArrayLength.class)) 
        {
            throw new CLException(
                "Field "+arrayField+" has no ArrayLength annotation");
        }
        
        // Create the array and assign it to 'this' via the field
        int arrayLengths[] = 
            arrayField.getAnnotation(ArrayLength.class).value();
        Class<?> componentType = 
            StructAccess.getBaseComponentType(arrayField.getType());
        Object array = Array.newInstance(componentType, arrayLengths);
        try
        {
            arrayField.set(this, array);
        }
        catch (IllegalArgumentException e)
        {
            throw new CLException(e.getMessage(), e);
        }
        catch (IllegalAccessException e)
        {
            throw new CLException(e.getMessage(), e);
        }
        
        // Non-primitive arrays will be filled with objects
        if (!componentType.isPrimitive())
        {
            if (cl_vector_type.class.isAssignableFrom(componentType))
            {
                initObjectArray(array, componentType);
            }
            else if (Struct.class.isAssignableFrom(componentType))
            {
                initObjectArray(array, componentType);
            }
            else
            {
                throw new CLException("Invalid type in array: "+componentType);
            }
        }
        
    }
    
    
    /**
     * Initialize the given field, which must be a Struct type field.
     * This method will create an instance of this type and assign 
     * it to the specified field in this struct.
     * 
     * @param structField The field to initialize
     * @throws CLException If the initialization went wrong...
     */
    private void initStructField(Field structField)
    {
        try
        {
            structField.set(this, createObject(structField.getType()));
        }
        catch (IllegalArgumentException e)
        {
            throw new CLException(e.getMessage(), e);
        }
        catch (IllegalAccessException e)
        {
            throw new CLException(e.getMessage(), e);
        }
    }
    
    
    
    /**
     * Returns the StructInfo instance that is associated with
     * the class of this struct. If this StructInfo does not
     * yet exist, it is created and stored for later access.
     * 
     * @return The StructInfo for this struct class.
     */
    private StructInfo getStructInfo()
    {
        return StructAccess.obtainStructInfo(getClass());
    }

    
    /**
     * Return the size of this struct, including paddings that are
     * inserted to obey OpenCL alignment requirements.
     * 
     * @return The size of this struct
     */
    int getSize()
    {
        return getStructInfo().getSize();
    }
    
    
    @Override
    void writeThisToBuffer(ByteBuffer targetBuffer)
    {
        int initialPosition = targetBuffer.position();
        StructAccessor structAccessors[] = 
            getStructInfo().getStructAccessors();
        for (int i=0; i<structAccessors.length; i++)
        {
            StructAccessor structAccessor = structAccessors[i];
            try
            {
                int position = 
                    initialPosition + structAccessor.getOffset();
                
                //System.out.println("For writing " + structAccessor.getField()
                //    + " position at " + position);

                targetBuffer.position(position);
                structAccessor.writeToBuffer(this, targetBuffer);
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
                throw new CLException(
                    "Could not access field "+structAccessor.getField()+
                    " of structure "+this);
            }
            catch (IllegalAccessException e)
            {
                throw new CLException(
                    "Could not access field "+structAccessor.getField()+
                    " of structure "+this);
            }
        }
        targetBuffer.position(initialPosition+getSize());
    }


    @Override
    void readThisFromBuffer(ByteBuffer sourceBuffer)
    {
        int initialPosition = sourceBuffer.position();
        StructAccessor structAccessors[] = 
            getStructInfo().getStructAccessors();
        for (int i=0; i<structAccessors.length; i++)
        {
            StructAccessor structAccessor = structAccessors[i];
            try
            {
                int position = 
                    initialPosition + structAccessor.getOffset();
                sourceBuffer.position(position);
                
                //System.out.println("For reading " + structAccessor.getField()
                //    + " position at " + position);
                
                structAccessor.readFromBuffer(this, sourceBuffer);
            }
            catch (IllegalArgumentException e)
            {
                throw new CLException(
                    "Could not access field "+structAccessor.getField()+
                    " of structure "+this);
            }
            catch (IllegalAccessException e)
            {
                throw new CLException(
                    "Could not access field "+structAccessor.getField()+
                    " of structure "+this);
            }
        }
        sourceBuffer.position(initialPosition+getSize());
    }
    
    

    
    
    
    
    

    /**
     * Debug function which prints the alignment layout of the given
     * struct class.<br>
     * <br>
     * <b><u>This function is for debugging purposes ONLY!</u></b> 
     *  
     * @param structClass The class
     */
    public static void showLayout(Class<? extends Struct> structClass)
    {
        System.out.println(createLayoutString(structClass));
    }
    
    /**
     * Create a formatted string showing the alignment and layout of
     * the given struct class.<br>
     * <br>
     * <b><u>This function is for debugging purposes ONLY!</u></b>
     * 
     * @param structClass The class
     * @return The string
     */
    public static String createLayoutString(Class<? extends Struct> structClass)
    {
        StructInfo structInfo = StructAccess.obtainStructInfo(structClass);
        StructAccessor structAccessors[] = structInfo.getStructAccessors();
        StringBuilder sb = new StringBuilder();
        if (structAccessors.length == 0)
        {
            sb.append("No relevant (public and non-volatile) " + "fields in "
                + structClass.getSimpleName());
            return sb.toString();
        }

        sb.append(
            "Aligned field layout for " + structClass.getSimpleName() + ":\n");
        int currentOffset = 0;
        for (int i = 0; i < structAccessors.length; i++)
        {
            StructAccessor structAccessor = structAccessors[i];
            for (int j = currentOffset; j < structAccessor.offset; j++)
            {
                sb.append("_");
                currentOffset++;
            }
            for (int j = 0; j < structAccessor.getSize(); j++)
            {
                sb.append(i);
                currentOffset++;
            }
        }
        while (currentOffset < structInfo.getSize())
        {
            sb.append("_");
            currentOffset++;
        }
        sb.append("\n");
        for (int i = 0; i < structAccessors.length; i++)
        {
            StructAccessor sa = structAccessors[i];
            Field f = sa.getField();
            sb.append(i + ": " 
                + f.getType().getSimpleName() + " " + f.getName()
                + ", offset " + sa.getOffset() 
                + ", size " + sa.getSize()
                + ", alignment " + sa.getAlignment() + "\n");

            int current = sa.getOffset() + sa.getSize();
            int end = 0;
            if (i == structAccessors.length - 1)
            {
                end = structInfo.getSize();
            }
            else
            {
                end = structAccessors[i + 1].getOffset();
            }
            if (current < end)
            {
                sb.append("   padding: " + (end - current) + " bytes\n");
            }
        }
        sb.append("Total size: " + structInfo.getSize() + "\n");
        return sb.toString();
    }
    
    



}


