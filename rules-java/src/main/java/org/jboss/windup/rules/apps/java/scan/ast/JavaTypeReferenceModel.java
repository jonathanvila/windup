package org.jboss.windup.rules.apps.java.scan.ast;

import org.windup.rules.apps.model.FileLocationModel;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

/**
 * This references a particular location within a Java source file, as well as the contents of that location.
 */
@TypeValue(JavaTypeReferenceModel.TYPE)
public interface JavaTypeReferenceModel extends FileLocationModel
{

    public static final String TYPE = "JavaTypeReference";
    public static final String REFERENCE_TYPE = "referenceType";

    /**
     * Contains the {@link TypeReferenceLocation} location referred to by this {@link Vertex}.
     */
    @Property(REFERENCE_TYPE)
    public TypeReferenceLocation getReferenceLocation();

    /**
     * Contains the {@link TypeReferenceLocation} location referred to by this {@link Vertex}.
     */
    @Property(REFERENCE_TYPE)
    public void setReferenceLocation(TypeReferenceLocation type);
}
