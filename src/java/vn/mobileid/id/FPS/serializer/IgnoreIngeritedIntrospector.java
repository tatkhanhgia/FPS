/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.serializer;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import fps_core.objects.core.BasicFieldAttribute;

/**
 *
 * @author GiaTK
 */
public class IgnoreIngeritedIntrospector extends JacksonAnnotationIntrospector {

    @Override
    public boolean hasIgnoreMarker(final AnnotatedMember m) {
        return m.getDeclaringClass() == BasicFieldAttribute.class || super.hasIgnoreMarker(m);
    }
    
}
